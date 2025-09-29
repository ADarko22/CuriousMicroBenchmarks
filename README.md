# Curious Micro Benchmarks

This project uses **[JMH (Java Microbenchmark Harness)](https://openjdk.org/projects/code-tools/jmh/)** to evaluate the real-world performance 
of various Data Structures and Algorithms for learning and curiosity.


## Project Structure


* **[src/main](src/main)**: Source code for custom data structures and algorithms.
* **[src/test](src/test)**: Standard unit tests for correctness.
* **[src/jmh](src/jmh)**: **The Benchmarks** (JMH experiments) for evaluating performance.

---

## Running Benchmarks

All benchmark classes are located in [src/jmh/java/edu/adarko22](src/jmh/java/edu/adarko22),
with your parameters by changing the warm-up, the iterations and more configs.

### Standard JMH Run

You can run benchmarks using the standard JMH Gradle plugin:

* **Run All Benchmarks**:
    ```bash
    ./gradlew jmh
    ```
* **Run a Specific Benchmark** (e.g., `JavaBinarySearchBenchmark`):
    ```bash
    ./gradlew jmh -Pjmh.includes=edu.adarko22.JavaBinarySearchBenchmark
    ```


---
## 📈 Automated Plotting (Custom Plugin)

This project includes a **custom Gradle plugin** to automate running benchmarks 
and immediately generating performance plots (PNG files) in the **`build/reports/jmh-plots`** directory.


### 1. Run Benchmark & Plot Result (`runJmhAndPlot`)


| Parameter | Required? | Description |
| :--- | :--- | :--- |
| **`-PbenchmarkClass`** | **Yes** | The **Fully Qualified Class Name (FQCN)** of the JMH benchmark class to run. |
| **`-PxAxisParameter`** | **Yes** | The name of the benchmark parameter to use for the **X-axis** in the plot (typically a parameter that controls input size, e.g., `@Param("size")`). |
| **`-PzAxisParameter`** | No | An **optional** parameter to use for splitting the plot into different **lines** or **colors** (Z-axis). |
| **`-PsplitPlotsByBenchmark`** | No | Set to `true` to generate a **separate plot file for each benchmark method** in the class.  |

**Example:**
To run the `JavaBinarySearchBenchmark` and plot the time taken versus the `size`, using the `target` for color separation:

```bash
./gradlew runJmhAndPlot \
  -PbenchmarkClass=edu.adarko22.JavaBinarySearchBenchmark \
  -PxAxisParameter=size \
  -PsplitPlotsByBenchmark=false \
  -PzAxisParameter=target
```

-----

### 2. Plot Existing Results (plotJmhCsvFile)


| Parameter | Required? | Description |
| :--- | :--- | :--- |
| **`-PjmhCsvFile`** | **Yes** | The **absolute or relative path** to the JMH results CSV file.                                          |
| **`-PxAxisParameter`** | **Yes** | The name of the benchmark parameter to use for the **X-axis**.                                      |
| **`-PzAxisParameter`** | No | An **optional** parameter to use for splitting the plot into different **lines** or **colors** (Z-axis). |
| **`-PsplitPlotsByBenchmark`** | No | Set to `true` to generate a **separate plot file for each benchmark method** in the class.        |

**Example:**
To plot a saved CSV file, using `size` as the X-axis parameter:

```bash
./gradlew plotJmhCsvFile \
  -PjmhCsvFile=build/reports/jmh-results/edu_adarko22_JavaBinarySearchBenchmark.csv \
  -PxAxisParameter=size \
  -PsplitPlotsByBenchmark=false 
```
