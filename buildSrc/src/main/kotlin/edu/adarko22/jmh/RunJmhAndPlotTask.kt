package edu.adarko22.jmh

import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * Runs the JMH benchmark and then plots the results using the shared logic.
 */
abstract class RunJmhAndPlotTask : AbstractJmhPlottingTask() {

    @get:Input
    abstract val benchmarkClass: org.gradle.api.provider.Property<String>

    @get:OutputFile
    val jmhOutputFile: Provider<RegularFile> = project.layout.buildDirectory.file(
        benchmarkClass.map { "reports/jmh-results/${it.replace('.', '_')}.csv" }
    )

    @Inject
    abstract fun getExecOperations(): ExecOperations

    @TaskAction
    fun runAndPlot() {
        val jmhJar = project.tasks.named("jmhJar").get().outputs.files.singleFile
        require(jmhJar.exists()) { "❌ jmhJar not found. Run `./gradlew jmhJar` first." }

        val resultFile = jmhOutputFile.get().asFile

        logger.quiet("🏃 Running JMH benchmark: ${benchmarkClass.get()}")
        getExecOperations().exec {
            commandLine(
                "java", "-cp", jmhJar.absolutePath,
                "org.openjdk.jmh.Main",
                benchmarkClass.get(),
                "-rf", "csv",
                "-rff", resultFile.absolutePath
            )
        }

        println("✅ JMH CSV output saved to: ${resultFile.absolutePath}")
        plot(resultFile)
    }
}
