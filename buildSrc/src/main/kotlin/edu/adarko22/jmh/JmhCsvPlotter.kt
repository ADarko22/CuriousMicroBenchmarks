package edu.adarko22.jmh

import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.XYChartBuilder
import org.knowm.xchart.style.markers.SeriesMarkers
import java.io.File

data class JmhCsvResult(
    val benchmark: String,
    val params: Map<String, String>,
    val score: Double,
    val error: Double,
    val unit: String
)

object JmhCsvPlotter {
    // "Benchmark","Mode","Threads","Samples","Score","Score Error (99.9%)","Unit","Param: X","Param: Y"
    fun parseCsv(file: File): List<JmhCsvResult> {
        val lines = file.readLines().map { it.replace("\"", "") }
        val paramColumns = lines.first().split(",").map { it.trim() }.filter { it.startsWith("Param:") }
            .map { it.replace("Param: ", "") }
        val header = lines.first().split(",").map { it.trim() }.map { it.replace("Param: ", "") }

        return lines.drop(1).mapNotNull { line ->
            val tokens = line.split(",")
            if (tokens.size < header.size) return@mapNotNull null

            val row = header.zip(tokens).toMap()
            val benchmark = row["Benchmark"] ?: return@mapNotNull null
            val score = row["Score"]?.toDoubleOrNull() ?: return@mapNotNull null
            val error = row["Score Error (99.9%) -> NaN"]?.toDoubleOrNull() ?: 0.0
            val unit = row["Unit"] ?: "?"

            val params = paramColumns.associateWith { row[it] ?: "?" }

            JmhCsvResult(benchmark, params, score, error, unit)
        }
    }

    fun plot(jmhCsvResults: List<JmhCsvResult>, xAxisParam: String, outputDir: File) {
        val groupedByBenchmark = jmhCsvResults.groupBy { it.benchmark }

        if (!outputDir.exists()) outputDir.mkdirs()

        groupedByBenchmark.forEach { (benchmarkName, benchmarkResults) ->
            val benchmarkNameParts = benchmarkName.split(".")
            val benchmarkClass = benchmarkNameParts[benchmarkNameParts.size - 2]
            val title = benchmarkNameParts.last()
            val unit = benchmarkResults.firstOrNull()?.unit ?: "units"

            // Group by all parameters except the x-axis parameter
            val otherParams = benchmarkResults.groupBy {
                it.params.filterKeys { key -> key != xAxisParam }
            }

            val chart = XYChartBuilder()
                .width(1000)
                .height(600)
                .title(title)
                .xAxisTitle(xAxisParam)
                .yAxisTitle(unit)
                .build()

            otherParams.entries.forEach { (paramGroup, groupResults) ->
                val xs = groupResults.map { it.params[xAxisParam]?.toDoubleOrNull() ?: 0.0 }
                val ys = groupResults.map { it.score }
                val errs = groupResults.map { it.error }

                val label = if (paramGroup.isEmpty()) {
                    "Default"
                } else {
                    paramGroup.entries.joinToString(", ") { "${it.key}=${it.value}" }
                }

                val series = chart.addSeries(label, xs, ys, errs)
                series.marker = SeriesMarkers.CIRCLE
            }

            val outputFile = File(outputDir, "$benchmarkClass-$title.png")

            BitmapEncoder.saveBitmap(chart, outputFile.absolutePath, BitmapEncoder.BitmapFormat.PNG)
            println("✅ Plot saved to: ${outputFile.absolutePath}")
        }
    }
}
