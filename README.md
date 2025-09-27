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

Feel free the tune the Benchmarks, available in [src/jmh/java/edu/adarko22](src/jmh/java/edu/adarko22),
with your parameters by changing the warm-up, the iterations and more configs.

### Run jmh benchmarks

Use the `me.champeau.jmh` Gradle Plugin to run all or a specific Benchmark:

- Run all the benchmarks: `./gradlew jmh`.
- un a specific benchmark, i.e. [JavaBinarySearchBenchmark](src/jmh/java/edu/adarko22/JavaBinarySearchBenchmark.java):
  `./gradlew jmh -Pjmh.includes=edu.adarko22.JavaBinarySearchBenchmark`.

### Run jmh benchmark & Generate a Plot

Use the custom Gradle Plugin `runJmhAndPlot` by specifying the `benchmarkClass` and the `xAxisParameter`.

For example: `./gradlew runJmhAndPlot -PbenchmarkClass=edu.adarko22.JavaBinarySearchBenchmark -PxAxisParameter=size`

