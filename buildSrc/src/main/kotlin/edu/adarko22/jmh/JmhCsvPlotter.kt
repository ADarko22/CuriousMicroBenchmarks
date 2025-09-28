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

    // Parses JMH CSV data into a list of results
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

    // Plots benchmark results into PNG charts
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
                    val parts = benchmarkName.split(".")
                    val benchmarkClass = parts.getOrNull(parts.size - 2) ?: "UnknownClass"
                    val title = parts.lastOrNull() ?: "Benchmark"
                    val unit = benchmarkResults.firstOrNull()?.yDataUnit ?: "units"

                    val xs = benchmarkResults.map { it.xParamData.toDouble() }
                    val ys = benchmarkResults.map { it.yData }
                    val isLogScale = shouldUseLogScale(ys)

                    val chart = XYChartBuilder()
                        .width(computeChartSize(xs, isLogScale))
                        .height(computeChartSize(ys, isLogScale, isYAxis = true))
                        .title(title)
                        .xAxisTitle(xParamHeader)
                        .yAxisTitle(unit)
                        .build()

                    styleChart(chart, benchmarkResults.size, isLogScale, zParamHeader != null)

                    // Plot each zParam group as a separate series
                    benchmarkResults.groupBy { it.zParamLabel }
                        .forEach { (zLabel, groupedJmhCsvResults) ->
                            val xsGroup = groupedJmhCsvResults.map { it.xParamData }
                            val ysGroup = groupedJmhCsvResults.map { it.yData }
                            val label = zLabel?.let { "$zParamHeader=$it" } ?: "Default"
                            val series = chart.addSeries(label, xsGroup, ysGroup)
                            series.marker = SeriesMarkers.CIRCLE
                        }

                    val outputFile = File(outputDir, "$benchmarkClass-$title.png")
                    BitmapEncoder.saveBitmap(chart, outputFile.absolutePath, BitmapEncoder.BitmapFormat.PNG)
                    println("✅ Plot saved to: ${outputFile.absolutePath}")
                }
        } else {
            val benchmarkClass = jmhCsvResults.first().benchmark.split(".")
                .let { it.getOrNull(it.size - 2) ?: "UnknownClass" }
            val unit = jmhCsvResults.firstOrNull()?.yDataUnit ?: "units"

            val xs = jmhCsvResults.map { it.xParamData.toDouble() }
            val ys = jmhCsvResults.map { it.yData }
            val isLogScale = shouldUseLogScale(ys)

            val chart = XYChartBuilder()
                .width(computeChartSize(xs, isLogScale))
                .height(computeChartSize(ys, isLogScale, isYAxis = true))
                .xAxisTitle(xParamHeader)
                .yAxisTitle(unit)
                .build()

            styleChart(chart, jmhCsvResults.size, isLogScale, true)

            jmhCsvResults.groupBy { it.benchmark }
                .forEach { (benchmarkName, benchmarkResults) ->
                    val benchmarkFunction = jmhCsvResults.first().benchmark.split(".")
                        .let { it.getOrNull(it.size - 2) ?: "UnknownFunction" }

                    val xsGroup = benchmarkResults.map { it.xParamData }
                    val ysGroup = benchmarkResults.map { it.yData }
                    val ysErrors = benchmarkResults.map { it.yDataError }
                    val series = chart.addSeries(benchmarkFunction, xsGroup, ysGroup, ysErrors)
                    series.marker = SeriesMarkers.CIRCLE
                }

            val outputFile = File(outputDir, "$benchmarkClass-all.png")
            BitmapEncoder.saveBitmap(chart, outputFile.absolutePath, BitmapEncoder.BitmapFormat.PNG)
            println("✅ Plot saved to: ${outputFile.absolutePath}")
        }
    }

    // Styles the chart to improve readability and visual appeal
    private fun styleChart(
        chart: org.knowm.xchart.XYChart,
        dataSize: Int,
        useLogY: Boolean,
        showLegend: Boolean
    ) {
        chart.styler.apply {
            isLegendVisible = showLegend
            legendPosition = Styler.LegendPosition.InsideNE
            markerSize = if (dataSize > 30) 4 else 6
            decimalPattern = "###,###.##"
            isYAxisLogarithmic = useLogY
            isPlotGridLinesVisible = true
            isPlotBorderVisible = false
            yAxisTickMarkSpacingHint = if (useLogY) 60 else 80
        }
    }

    // Determines if a logarithmic scale is suitable for Y-axis
    private fun shouldUseLogScale(dataPoints: List<Double>): Boolean {
        val min = dataPoints.minOrNull() ?: 1.0
        val max = dataPoints.maxOrNull() ?: 1.0
        val ratio = max / min
        return ratio > 100 && min > 0
    }

    // Computes dynamic chart size based on data range
    private fun computeChartSize(
        data: List<Double>,
        logScale: Boolean,
        isYAxis: Boolean = false,
        base: Int = if (isYAxis) 600 else 800,
        multiplier: Double = if (isYAxis) 50.0 else 10.0,
        minSize: Int = 400,
        maxSize: Int = 2000
    ): Int {
        val safeData = data.filter { it > 0.0 }
        val range = if (safeData.isEmpty()) 1.0 else {
            val min = safeData.minOrNull() ?: 1.0
            val max = safeData.maxOrNull() ?: 1.0
            if (logScale) kotlin.math.log10(max / min).coerceAtLeast(1.0)
            else (max - min).coerceAtLeast(1.0)
        }
        return (base + range * multiplier).toInt().coerceIn(minSize, maxSize)
    }
}
