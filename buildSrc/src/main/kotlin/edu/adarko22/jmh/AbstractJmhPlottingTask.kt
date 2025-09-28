package edu.adarko22.jmh

import edu.adarko22.jmh.JmhPlottingSupport.plotFromCsvFile
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import java.io.File

/**
 * Base class for JMH tasks that involve plotting.
 * Holds common properties and the shared plot execution logic.
 */
abstract class AbstractJmhPlottingTask : DefaultTask() {

    @get:Input
    abstract val xAxisParam: Property<String>

    @get:Optional
    @get:Input
    abstract val zAxisParam: Property<String>

    @get:Input
    abstract val splitPlotsByBenchmark: Property<Boolean>

    @get:OutputDirectory
    val plotOutputDir: Provider<Directory>  = project.layout.buildDirectory.dir("reports/jmh-plots")

    /**
     * Executes the plotting logic using a provided CSV file.
     * This method is intended to be called by concrete subclasses' @TaskAction.
     */
    protected fun plot(csvFile: File) {
        // Use get() or orNull for accessing lazy properties
        plotFromCsvFile(
            csvFile,
            xAxisParam.get(),
            zAxisParam.orNull,
            plotOutputDir.get().asFile,
            splitPlotsByBenchmark.get()
        )
    }
}

object JmhPlottingSupport {

    fun plotFromCsvFile(
        csvFile: File,
        xAxisParam: String,
        zAxisParam: String?,
        outputDir: File,
        splitPlotsByBenchmark: Boolean
    ) {
        require(csvFile.exists()) { "❌ JMH CSV file not found: $csvFile" }

        val results = JmhCsvPlotter.parseCsv(csvFile, xAxisParam, zAxisParam)
        require(results.isNotEmpty()) { "❌ No data in JMH CSV file: $csvFile" }

        outputDir.mkdirs()
        JmhCsvPlotter.plot(results, xAxisParam, zAxisParam, outputDir, splitPlotsByBenchmark)

        println("✅ Plots saved to: ${outputDir.absolutePath}")
    }
}