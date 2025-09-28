# Curious Micro Benchmarks

This Gradle project relies on the [jmh-gradle-plugin](https://github.com/melix/jmh-gradle-plugin) to leverage
the [jmh micro-benchmarking framework](https://openjdk.org/projects/code-tools/jmh/) to evaluate the performance of
well-known Data Structures and Algorithms, for the sake of curiosity and learning.

## Project Structure

### Code Sources

The [src/main](src/main) folder contains the Source code of custom implementation of Data Structures and Algorithms.
While the [src/test](src/test) folder contains the Test code for checking the correctness of the Source Code.

### JMH Source

The [src/jmh](src/jmh) folder contains the Benchmarks for the experiments,
which will evaluate Standard, Custom and 3rd Party implementations of Data Structures and Algorithms.

---

## Get Started

Feel free the tune the Benchmarks, available in [src/jmh/java/edu/adarko22](src/jmh/java/edu/adarko22),
with your parameters by changing the warm-up, the iterations and more configs.

### Run Standard JMH Benchmarks

Use the standard `me.champeau.jmh` Gradle Plugin to run all or a specific Benchmark:

- Run all the benchmarks: `./gradlew jmh`.
- Run a specific benchmark, i.e. `JavaBinarySearchBenchmark`:
  `./gradlew jmh -Pjmh.includes=edu.adarko22.JavaBinarySearchBenchmark`.

---

## Custom JMH Plotting Tasks

This project provides a **custom Gradle plugin** (`CustomJmhPlugin`) with tasks to automate running benchmarks and generating explanatory performance plots.

The resulting plots are saved to the **build/reports/jmh-plots** directory as PNG files.

### 1. Run JMH Benchmark & Generate a Plot

Use the **`runJmhAndPlot`** task (implemented by `RunJmhAndPlotTask`) to execute a single benchmark and immediately visualize the results. This task depends on `jmhJar`.

**Usage:**

```bash
./gradlew runJmhAndPlot -PbenchmarkClass=<fqcn> -PxAxisParameter=<param> [OPTIONS]
````

| Parameter | Required? | Description | Example                                  |
| :--- | :--- | :--- |:-----------------------------------------|
| **`-PbenchmarkClass`** | **Yes** | The **Fully Qualified Class Name (FQCN)** of the JMH benchmark class to run. | `edu.adarko22.JavaBinarySearchBenchmark` |
| **`-PxAxisParameter`** | **Yes** | The name of the benchmark parameter to use for the **X-axis** in the plot (typically a parameter that controls input size, e.g., `@Param("size")`). | `size`                                   |
| **`-PzAxisParameter`** | No | An **optional** parameter to use for splitting the plot into different **lines** or **colors** (Z-axis). | `target`                                 |
| **`-PsplitPlotsByBenchmark`** | No | Set to `true` to generate a **separate plot file for each benchmark method** in the class.  | `true`                                   |

**Example:**
To run the `JavaBinarySearchBenchmark` and plot the time taken versus the input `size`, using the `algorithm` for color separation:

```bash
./gradlew runJmhAndPlot \
  -PbenchmarkClass=edu.adarko22.JavaBinarySearchBenchmark \
  -PxAxisParameter=size \
  -PsplitPlotsByBenchmark=false \
  -PzAxisParameter=target
```

-----

### 2\. Plot Existing JMH Results File

Use the **`plotJmhCsvFile`** task (implemented by `JmhPlotTask`) to generate a plot from an existing JMH CSV output file, without rerunning the benchmark.

**Usage:**

```bash
./gradlew plotJmhCsvFile -PjmhCsvFile=<path> -PxAxisParameter=<param> [OPTIONS]
```

| Parameter | Required? | Description | Example                                                                                                                                                  |
| :--- | :--- | :--- |:---------------------------------------------------------------------------------------------------------------------------------------------------------|
| **`-PjmhCsvFile`** | **Yes** | The **absolute or relative path** to the JMH results CSV file. | `build/reports/jmh-results/edu_adarko22_JavaBinarySearchBenchmark.csv` |
| **`-PxAxisParameter`** | **Yes** | The name of the benchmark parameter to use for the **X-axis**. | `size`                                                                                                                                                   |
| **`-PzAxisParameter`** | No | An **optional** parameter to use for splitting the plot into different **lines** or **colors** (Z-axis). | `type`                                                                                                                                                   |
| **`-PsplitPlotsByBenchmark`** | No | Set to `true` to generate a **separate plot file for each benchmark method** in the class. Defaults to `false`. | `true`                                                                                                                                                   |

**Example:**
To plot a saved CSV file, using `size` as the X-axis parameter:

```bash
./gradlew plotJmhCsvFile \
  -PjmhCsvFile=build/reports/jmh-results/edu_adarko22_JavaBinarySearchBenchmark.csv \
  -PxAxisParameter=size \
  -PsplitPlotsByBenchmark=false 
```
