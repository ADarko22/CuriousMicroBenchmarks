package edu.adarko22

import org.openjdk.jmh.annotations.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Is `Binary Search always O(logN)? Yes, if indexed-access if O(1)!
 *
 * This experiment highlights the importance of "paying attention to details":
 *      - LinkedList.get(i) is O(N) -> linear indexed-access time
 *      - ArrayList.get(i) is O(1) -> constant indexed-access time
 *
 * Performing Binary Search on a LinkedList vs an ArrayList has huge performance implications!
 * The Asymptotic time-complexity for Binary Search on a LinkedList is O(N*logN)! You're better running a linear scan!!
 *
 * By running the experiment you will discover that on a list of 10M integers:
 *      - Binary Search on LinkedList takes approximately 8 to 15 milliseconds
 *      - Binary Search on ArrayList takes approximately 2 to 22 nanoseconds
 *  In this specific setup, Binary Search on ArrayList is almost a 1 Million times faster than LinkedList!
 *
 * Run with `./gradlew jmh -Pjmh.includes=edu.adarko22.BinarySearchBenchmark`
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
open class BinarySearchBenchmark {

    private val size = 10_000_000L
    private lateinit var linkedListOfRandom: LinkedList<Long>
    private lateinit var arrayListOfRandom: ArrayList<Long>

    @Param("1", "5000000", "9999999")
    var target: Long = 0

    @Setup(Level.Trial)
    fun setup() {
        val sortedList = LongRange(1, size).toList()
        linkedListOfRandom = LinkedList(sortedList)
        arrayListOfRandom = ArrayList(sortedList)
    }

    @Benchmark
    fun binarySearchLinkedList() {
        val result = linkedListOfRandom.binarySearch(target)
        assert(result >= 0)
    }

    @Benchmark
    fun binarySearchArrayList() {
        val result = arrayListOfRandom.binarySearch(target)
        assert(result >= 0)
    }
}