package edu.adarko22.jmh

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class JmhCsvPlotterTest {

    @Test
    fun `parseCsv should correctly parse valid CSV data`() {
        val expectedResults = listOf(
            JmhCsvResult("BinarySearch_ArrayList", mapOf("size" to "1000", "target" to "0"), 7.7, 0.0, "ns/op"),
            JmhCsvResult("BinarySearch_LinkedList", mapOf("size" to "10000000", "target" to "1"), 1825.2, 0.0, "ns/op"),
        )
        val csvContent = """
            "Benchmark","Mode","Threads","Samples","Score","Score Error (99.9%)","Unit","Param: size","Param: target"
            "BinarySearch_ArrayList","avgt",1,1,7.7,NaN,"ns/op",1000,0
            "BinarySearch_LinkedList","avgt",1,1,1825.2,NaN,"ns/op",10000000,1
        """.trimIndent()

        val tempFile = File.createTempFile("jmh", ".csv").apply {
            writeText(csvContent)
            deleteOnExit()
        }

        val results = JmhCsvPlotter.parseCsv(tempFile)
        assertEquals(expectedResults, results)
    }
}
