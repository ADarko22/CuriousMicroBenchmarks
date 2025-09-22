package edu.adarko22

import org.openjdk.jmh.annotations.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Run with `./gradlew jmh -Pjmh.includes=edu.adarko22.BinarySearchBenchmark`
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
open class BinarySearchBenchmark {

    private val size = 1_000_000
    private lateinit var linkedListOfRandom: LinkedList<Int>
    private lateinit var arrayListOfRandom: ArrayList<Int>

    @Param("1", "500000", "999999")
    var target: Int = 0

    @Setup(Level.Trial)
    fun setup() {
        val sortedList = IntRange(1, size).toList()
        linkedListOfRandom = LinkedList(sortedList)
        arrayListOfRandom = ArrayList(sortedList)
    }

    @Benchmark
    fun binarySearchLinkedListHit() {
        val result = linkedListOfRandom.binarySearch(target)
        assert(result >= 0)
    }

    @Benchmark
    fun binarySearchArrayListHit() {
        val result = arrayListOfRandom.binarySearch(target)
        assert(result >= 0)
    }
}