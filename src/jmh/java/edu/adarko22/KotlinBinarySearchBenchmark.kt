package edu.adarko22

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.infra.Blackhole
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
 * Run with `./gradlew jmh -Pjmh.includes=edu.adarko22.KotlinBinarySearchBenchmark`
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 15, time = 1, timeUnit = TimeUnit.SECONDS)
open class KotlinBinarySearchBenchmark {

    private lateinit var linkedListOfRandom: LinkedList<Long>
    private lateinit var arrayListOfRandom: ArrayList<Long>


    @Param("1000", "1000000", "10000000")
    var size = 0L

    @Param("0", "1", "5000000")
    var target = 0L

    @Setup(Level.Trial)
    fun setup() {
        val sortedList = LongRange(1, size).toList()
        linkedListOfRandom = LinkedList(sortedList)
        arrayListOfRandom = ArrayList(sortedList)
    }

    @Benchmark
    fun binarySearchLinkedList(blackhole: Blackhole) {
        val result = linkedListOfRandom.binarySearch(target)
        blackhole.consume(result)
    }

    @Benchmark
    fun binarySearchArrayList(blackhole: Blackhole) {
        val result = arrayListOfRandom.binarySearch(target)
        blackhole.consume(result)
    }
}