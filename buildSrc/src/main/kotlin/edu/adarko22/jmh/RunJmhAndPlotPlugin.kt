package edu.adarko22.jmh

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.register
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

class RunJmhAndPlotPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register<RunJmhAndPlot>("runJmhAndPlot") {
            group = "reporting"
            description = """
                Runs JMH and plots benchmark results.
                Usage:
                  ./gradlew runJmhAndPlot \
                    -PbenchmarkClass=<fqcn> \
                    -PxAxisParameter=<param>
            """.trimIndent()
            dependsOn("jmhJar")
        }
    }
}

abstract class RunJmhAndPlot : DefaultTask() {

    @get:OutputFile
    val jmhOutputFile: Provider<RegularFile> = project.layout.buildDirectory.file("reports/jmh-result.csv")

    @get:OutputDirectory
    val plotOutputFile: Provider<Directory> = project.layout.buildDirectory.dir("reports/jmh-plots")

    @get:Input
    val benchmarkClass: String
        get() = project.findProperty("benchmarkClass") as? String
            ?: error("❌ Must provide -PbenchmarkClass=<fqcn>")

    @get:Input
    val xAxisParam: String
        get() = project.findProperty("xAxisParameter") as? String
            ?: error("❌ Must provide -PxAxisParameter=<param>")

    @Inject
    abstract fun getExecOperations(): ExecOperations

    @TaskAction
    fun action() {
        val jmhJar = project.tasks.named("jmhJar").get().outputs.files.singleFile
        if (!jmhJar.exists()) error("❌ jmhJar not found. Run `./gradlew jmhJar` first.")

        val resultPath = jmhOutputFile.get().asFile.absolutePath

        getExecOperations().exec {
            commandLine(
                "java", "-cp", jmhJar.absolutePath,
                "org.openjdk.jmh.Main",
                benchmarkClass,
                "-rf", "csv",
                "-rff", resultPath
            )
        }

        println("✅ JMH CSV output saved to: $resultPath")

        val input = jmhOutputFile.get().asFile
        val outputDir = plotOutputFile.get().asFile

        // Make sure directory exists
        outputDir.mkdirs()

        if (!input.exists()) error("❌ JMH result file not found: $input")

        val results = JmhCsvPlotter.parseCsv(input)
        if (results.isEmpty()) error("❌ No data in JMH CSV file.")

        JmhCsvPlotter.plot(results, xAxisParam, outputDir)
    }
}
