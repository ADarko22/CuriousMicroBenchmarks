package edu.adarko22.jmh

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Plots an existing JMH CSV file using the shared logic.
 */
abstract class JmhPlotTask : AbstractJmhPlottingTask() {

    @get:Input
    abstract val jmhCsvFile: Property<File>

    @TaskAction
    fun plotOnly() {
        val file = jmhCsvFile.get()
        require(file.exists()) { "❌ Provided jmhCsvFile does not exist: ${file.absolutePath}" }
        plot(file)
    }
}