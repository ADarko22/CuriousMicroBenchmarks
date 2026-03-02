package edu.adarko22.dimensioning;

import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS) // We want to see ns per access
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@State(Scope.Thread)
@Fork(1)
public class DimensioningSystemsBenchmark {
    // Parameters sized on MacBook Pro M3 with 18GB Ram
    // 64KB (L1), 4MB (L2), 1GB (RAM), 18GB
    @Param({"65536", "4194304", "1073741824", "19327352832"})
    long sizeBytes;

    private Arena arena;
    private MemorySegment segment;
    private long[] randomIndices;
    private final int ACCESS_COUNT = 1_000_000; // Constant work per benchmark

    @Setup(Level.Trial)
    public void setup() throws IOException {
        arena = Arena.ofShared();
        Path tempFile = Files.createTempFile("hardware_test_", ".bin");

        // Memory-map the file (OS handles RAM vs Disk)
        try (RandomAccessFile raf = new RandomAccessFile(tempFile.toFile(), "rw")) {
            raf.setLength(sizeBytes);
            segment = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, sizeBytes, arena);
        }

        // Pre-generate random indices to avoid including Math.random() in the benchmark
        randomIndices = new long[ACCESS_COUNT];
        for (int i = 0; i < ACCESS_COUNT; i++) {
            randomIndices[i] = (long) (Math.random() * (sizeBytes - 1));
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        arena.close();
    }

    @Benchmark
    public void testSequential(org.openjdk.jmh.infra.Blackhole bh) {
        // Linear scan of 1 million points
        for (int i = 0; i < ACCESS_COUNT; i++) {
            // We use a small stride to ensure we touch new cache lines
            bh.consume(segment.get(ValueLayout.JAVA_BYTE, (long) i * 64 % sizeBytes));
        }
    }

    @Benchmark
    public void testRandom(org.openjdk.jmh.infra.Blackhole bh) {
        // Random access of 1 million points
        for (int i = 0; i < ACCESS_COUNT; i++) {
            bh.consume(segment.get(ValueLayout.JAVA_BYTE, randomIndices[i]));
        }
    }
}
