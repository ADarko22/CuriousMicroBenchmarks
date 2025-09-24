# Curious Micro Benchmarks

This Gradle project relies on the [jmh-gradle-plugin](https://github.com/melix/jmh-gradle-plugin) to leverage
the [jmh micro-benchmarking framework](https://openjdk.org/projects/code-tools/jmh/) to evaluate the performance of
well-known Data Structures and Algorithms, for the sake of curiosity and learning.

## Project Structure

### Code Sources

The [src/main](src/main) folder contains the Source code of custom implementation of Data Structures and Algorithms.
While the [src/test](src/test) folder contains the Test code for checking the correctness of the Source Code

### JMH Source

The [src/jmh](src/jmh) folder contains the Benchmarks for the experiments,
which will evaluate Standard, Custom and 3rd Party implementations of Data Structures and Algorithms.

## Get Started

Run all the benchmarks `./gradlew jmh`.

Alternatively run a specific benchmark, for
example [JavaBinarySearchBenchmark](src/jmh/java/edu/adarko22/JavaBinarySearchBenchmark.java) with
`./gradlew jmh -Pjmh.includes=edu.adarko22.JavaBinarySearchBenchmark`.

Feel free the tune the Benchmarks with your parameters by changing the warm-up, the iterations and more configs.