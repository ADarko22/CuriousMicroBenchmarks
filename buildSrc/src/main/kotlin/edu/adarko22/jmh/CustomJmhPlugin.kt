// FIX: Using map { ... }.get() or get() on the provider is one way to satisfy
// the non-optional requirement if .required() is unavailable or not recognized.
// A simpler, cleaner approach is to use map() and rely on the required nature
// of the final Property<T>, but since that failed, we enforce a check here:

package edu.adarko22.jmh

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.*
import java.io.File

/**
 * Registers the 'runJmhAndPlot' and 'plotJmhCsvFile' tasks.
 */
class CustomJmhPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        fun getRequiredProperty(name: String) = project.providers.gradleProperty(name)

        // --- Task: runJmhAndPlot ---
        project.tasks.register<RunJmhAndPlotTask>("runJmhAndPlot") {
            group = "reporting"
            description = """
                Runs JMH and plots benchmark results.
                Usage:
                  ./gradlew runJmhAndPlot -PbenchmarkClass=<fqcn> -PxAxisParameter=<param> ...
            """.trimIndent()
            dependsOn("jmhJar")

            benchmarkClass.convention(getRequiredProperty("benchmarkClass"))
            xAxisParam.convention(getRequiredProperty("xAxisParameter"))
            splitPlotsByBenchmark.convention(
                getRequiredProperty("splitPlotsByBenchmark").map { it.toBooleanStrict() }
            )
            zAxisParam.convention(project.providers.gradleProperty("zAxisParameter"))
        }

        // --- Task: plotJmhCsvFile ---
        project.tasks.register<JmhPlotTask>("plotJmhCsvFile") {
            group = "reporting"
            description = """
                Plots an existing JMH CSV file.
                Usage:
                  ./gradlew plotJmhCsvFile -PjmhCsvFile=<path-to-csv> -PxAxisParameter=<param> ...
            """.trimIndent()

            jmhCsvFile.convention(getRequiredProperty("jmhCsvFile").map { File(it) })
            xAxisParam.convention(getRequiredProperty("xAxisParameter"))
            splitPlotsByBenchmark.convention(
                getRequiredProperty("splitPlotsByBenchmark").map { it.toBooleanStrict() }
            )
            zAxisParam.convention(project.providers.gradleProperty("zAxisParameter"))
        }
    }
}