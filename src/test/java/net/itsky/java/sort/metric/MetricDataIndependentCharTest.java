package net.itsky.java.sort.metric;

import net.itsky.java.sort.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static net.itsky.java.sort.TestData.*;
import static net.itsky.java.sort.TestUtils.checkMinMaxPairs;
import static net.itsky.java.sort.metric.MetricDataOneChar.ARR_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MetricDataIndependentCharTest {

    private static final long MAX_INT_PRIME = 2147483647L;

    private MetricDataIndependentChar[] metrics
            = IntStream.rangeClosed(1, 10)
    .mapToObj(this::readUkrainianMetric).toArray(MetricDataIndependentChar[]::new);

    private MetricDataOneChar metricDataOneChar = readUkrainianMetric1c();

    @Test
    void testWriteAndRead() throws IOException, InterruptedException {
        for (int i = 1; i <= 10; i++) {
            int[] arr = LongStream.range(0, ARR_SIZE + 1).mapToInt(x -> {
                long xx = x - 32768;
                return (int) Math.abs((xx * xx * xx - xx * xx + xx - 1) % MAX_INT_PRIME);
            }).toArray();
            MetricDataIndependentChar source = new MetricDataIndependentChar(i, arr);
            MetricDataIndependentChar target = new MetricDataIndependentChar(i);
            try (PipedInputStream inputStream = new PipedInputStream()) {
                try (PipedOutputStream outputStream = new PipedOutputStream()) {
                    outputStream.connect(inputStream);
                    Thread sourceThread = new Thread(() -> source.write(outputStream));
                    sourceThread.start();
                    target.read(inputStream);
                    sourceThread.join();
                }
            }
            if (i == 1) {
                for (int ci = 0; ci < ARR_SIZE; ci++) {
                    String s = String.valueOf((char) ci);
                    assertEquals(arr[ci], target.metric(s));
                    assertEquals(arr[ci], source.metric(s));
                }
            }
        }
    }

    private MetricDataIndependentChar readUkrainianMetric(int depth) {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ukrainian-metric-1c.dat")) {
            MetricDataIndependentChar result = new MetricDataIndependentChar(depth);
            result.read(stream);
            return result;
        } catch (IOException ioex) {
            throw new UncheckedIOException(ioex);
        }
    }

    private MetricDataOneChar readUkrainianMetric1c() {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ukrainian-metric-1c.dat")) {
            MetricDataOneChar result = new MetricDataOneChar();
            result.read(stream);
            return result;
        } catch (IOException ioex) {
            throw new UncheckedIOException(ioex);
        }
    }

    private void checkConsistency(List<String> data) {
        Arrays.stream(metrics).forEach(metric -> TestUtils.checkConsistency(data, metric));
    }

    @Test
    void testConsistencyOfTwo() {
        checkConsistency(List.of("0000000001", "0001010101"));
    }

    @Test
    void testConsistencyUnsorted() {
        checkConsistency(UNSORTED);
    }

    @Test
    void testConsistencyLongUnsorted() {
        checkConsistency(LONG_UNSORTED);
    }

    @Test
    void testConsistencyNumbersUnsorted() {
        checkConsistency(NUMBERS_UNSORTED);
    }

    @Test
    void testConsistencyNumbersLongUnsorted() {
        checkConsistency(NUMBERS_LONG_UNSORTED);
    }

    @Test
    void testConsistencyNumbersPrefixedUnsorted() {
        checkConsistency(NUMBERS_PREFIXED_UNSORTED);
    }

    @Test
    void testConsistencyNumbersMixedPrefixedUnsorted() {
        checkConsistency(NUMBERS_MIXED_PREFIXED_UNSORTED);
    }

    @Test
    void testConsistencyAsiaticUnsorted() {
        checkConsistency(ASIATIC_UNSORTED);
    }

    @Test
    void testConsistencyUkrainianUnsorted() {
        checkConsistency(UKRAINIAN_WORDS);
    }

    @Test
    void testConsistencyExtremesUnsorted() {
        checkConsistency(EXTREMES_UNSORTED);
    }

    @Test
    void testConsistency1000Lines() {
        checkConsistency(X_1000_LINES);
    }

    @Test
    void testOneChar1000Lines() {
        X_1000_LINES.forEach(
                s->assertEquals(metrics[0].metric(s), metricDataOneChar.metric(s), "s=" + s)
        );
    }

    @Test
    void testClassMinMax() {
        Arrays.stream(metrics).forEach(metric -> checkMinMaxPairs(LIST_OF_CLASS_MIN_MAX, metric, false));
    }

    @Test
    void testSpecial() {
        checkConsistency(List.of("ȀĀ", "ǿӿ"));
    }

}
