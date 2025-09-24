package edu.adarko22;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

/**
 * Run with `./gradlew jmh -Pjmh.includes=edu.adarko22.JavaBinarySearchBenchmark`
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 15, time = 1, timeUnit = TimeUnit.SECONDS)
public class JavaBinarySearchBenchmark {
    private LinkedList<Long> linkedListOfRandom;
    private ArrayList<Long> arrayListOfRandom;

    @Param({"1000", "1000000", "10000000"})
    Long size = 0L;
    @Param(value = {"0", "1", "5000000"})
    Long target = 0L;

    @Setup(Level.Trial)
    public void setup() {
        var sortedList = LongStream.rangeClosed(1L, size).boxed().toList();
        linkedListOfRandom = new LinkedList<>(sortedList);
        arrayListOfRandom = new ArrayList<>(sortedList);
    }

    @Benchmark
    public void javaCollectionsBinarySearchLinkedList(Blackhole blackhole) {
        var result = Collections.binarySearch(linkedListOfRandom, target);
        blackhole.consume(result);
    }

    @Benchmark
    public void javaCollectionsBinarySearchArrayList(Blackhole blackhole) {
        var result = Collections.binarySearch(arrayListOfRandom, target);
        blackhole.consume(result);
    }
}