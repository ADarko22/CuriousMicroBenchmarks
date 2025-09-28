package edu.adarko22.jmh

import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.XYChartBuilder
import org.knowm.xchart.style.Styler
import org.knowm.xchart.style.markers.SeriesMarkers
import java.io.File

data class JmhCsvResult(
    val benchmark: String,
    val xParamData: Int,
    val zParamLabel: String?,
    val yData: Double,
    val yDataError: Double,
    val yDataUnit: String
)

object JmhCsvPlotter {

    fun parseCsv(file: File, xParamHeader: String, zParamHeader: String?): List<JmhCsvResult> {
        val lines = file.readLines().filter { it.isNotBlank() }
        if (lines.isEmpty()) return emptyList()

        val headers = lines.first().split(',').map { it.trim().removeSurrounding("\"") }
        return lines.drop(1).mapNotNull { line ->
            val tokens = line.split(',').map { it.trim().removeSurrounding("\"") }
            if (tokens.size != headers.size) return@mapNotNull null
            val row = headers.zip(tokens).toMap()

            val benchmark = row["Benchmark"] ?: return@mapNotNull null
            val score = row["Score"]?.toDoubleOrNull() ?: return@mapNotNull null
            val error = row["Score Error (99.9%)"]?.toDoubleOrNull() ?: 0.0
            val unit = row["Unit"] ?: "?"

            val xData = row["Param: $xParamHeader"]?.toIntOrNull() ?: 0
            val zLabel = zParamHeader?.let { if (it.startsWith("Param")) row[it] else row["Param: $it"] }

            JmhCsvResult(benchmark, xData, zLabel, score, error, unit)
        }
    }

    fun plot(
        jmhCsvResults: List<JmhCsvResult>,
        xParamHeader: String,
        zParamHeader: String?,
        outputDir: File,
        splitByBenchmark: Boolean
    ) {
        if (!outputDir.exists()) outputDir.mkdirs()

        if (splitByBenchmark) {
            jmhCsvResults.groupBy { it.benchmark }
                .forEach { (benchmarkName, benchmarkResults) ->
                    val fileName = generateFileName(benchmarkName)
                    val title = benchmarkName.split(".").lastOrNull() ?: "Benchmark"
                    createAndSaveChart(
                        results = benchmarkResults,
                        xParamHeader = xParamHeader,
                        zParamHeader = zParamHeader,
                        outputFile = File(outputDir, "$fileName.png"),
                        title = title
                    )
                }
        } else {
            if (jmhCsvResults.isEmpty()) return
            val benchmarkClass = jmhCsvResults.first().benchmark.split(".")
                .getOrNull(jmhCsvResults.first().benchmark.split(".").size - 2) ?: "UnknownClass"
            createAndSaveChart(
                results = jmhCsvResults,
                xParamHeader = xParamHeader,
                zParamHeader = zParamHeader,
                outputFile = File(outputDir, "$benchmarkClass-all.png"),
                title = "All Benchmarks"
            )
        }
    }

    private fun createAndSaveChart(
        results: List<JmhCsvResult>,
        xParamHeader: String,
        zParamHeader: String?,
        outputFile: File,
        title: String
    ) {
        if (results.isEmpty()) return

        val unit = results.first().yDataUnit
        val (width, height) = computeChartSize()

        val chart = XYChartBuilder()
            .width(width)
            .height(height)
            .title(title)
            .xAxisTitle(xParamHeader)
            .yAxisTitle(unit)
            .build()

        styleChart(chart)

        // Group by zParamLabel for series
        results.groupBy { generateSeriesLabel(it, zParamHeader) }
            .forEach { (seriesLabel, seriesData) ->
                val seriesXs = seriesData.map { it.xParamData }
                val seriesYs = seriesData.map { it.yData }
                val series = chart.addSeries(seriesLabel, seriesXs, seriesYs)
                series.marker = SeriesMarkers.CIRCLE
            }

        BitmapEncoder.saveBitmap(chart, outputFile.absolutePath, BitmapEncoder.BitmapFormat.PNG)
        println("✅ Plot saved to: ${outputFile.absolutePath}")
    }

    private fun generateFileName(benchmarkName: String): String {
        val parts = benchmarkName.split(".")
        val benchmarkClass = parts.getOrNull(parts.size - 2) ?: "UnknownClass"
        val title = parts.lastOrNull() ?: "Benchmark"
        return "$benchmarkClass-$title"
    }

    private fun generateSeriesLabel(result: JmhCsvResult, zParamHeader: String?): String {
        val zLabel = zParamHeader?.let { z ->
            result.zParamLabel?.let { "$z=$it" }
        }
        val benchmarkShort = result.benchmark.split(".").lastOrNull() ?: result.benchmark
        return if (zLabel != null) "$zLabel • $benchmarkShort" else benchmarkShort
    }

    private fun styleChart(chart: org.knowm.xchart.XYChart) {
        chart.styler.apply {
            isLegendVisible = true
            legendPosition = Styler.LegendPosition.InsideNW
            isYAxisLogarithmic = false
            isPlotGridLinesVisible = true
        }
    }

    private fun computeChartSize(): Pair<Int, Int> {
        return 800 to 600
    }
}
