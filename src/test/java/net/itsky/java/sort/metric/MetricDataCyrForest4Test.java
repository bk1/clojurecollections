package net.itsky.java.sort.metric;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static net.itsky.java.sort.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MetricDataCyrForest4Test {

    private MetricDataCyrForest4 metric = readUkrainianMetric();

    @Test
    void testWriteAndRead() throws IOException, InterruptedException {
        MetricDataCyrForest4 source = metric;
        MetricDataCyrForest4 target = new MetricDataCyrForest4();
        try (InputStream frequencyStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ukrainian-frequencies.dat")) {
            try (PipedInputStream inputStream = new PipedInputStream()) {
                try (PipedOutputStream outputStream = new PipedOutputStream()) {
                    outputStream.connect(inputStream);
                    Thread sourceThread = new Thread(() -> source.write(outputStream));
                    sourceThread.start();
                    Thread targetThread = new Thread(() -> target.read(inputStream, frequencyStream));
                    targetThread.start();
                    sourceThread.join();
                    targetThread.join();
                }
            }
        }
        assertEquals(source.keys(), target.keys());
        for (String s : source.keys()) {
            assertEquals(source.metric(s), target.metric(s));
        }
    }

    private MetricDataCyrForest4 readUkrainianMetric()  {
        MetricDataCyrForest4 result = new MetricDataCyrForest4();
        try (InputStream metricStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ukrainian-metric.dat")) {
            try (InputStream frequencyStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ukrainian-frequencies.dat")) {
                result.read(metricStream, frequencyStream);
            }
        } catch (IOException ioex) {
            throw new UncheckedIOException(ioex);
        }
        return result;
    }

    private void checkConsistency(List<String> data) {
        if (data.size() > 2000) {
            data = data.subList(0, 2000);
        }
        for (String x: data) {
            long mx = metric.metric(x);
            for (String y:data) {
                if (x.equals(y)) {
                    continue;
                }
                long my = metric.metric(y);
                String msg = "x=" + x + " y=" + y + " mx=" + mx + " my=" + my;
                if (mx < my) {
                    assertTrue(x.compareTo(y) < 0, msg);
                } else if (mx > my) {
                    assertTrue(x.compareTo(y) > 0, msg);
                }
                if (x.equals(y)) {
                    assertEquals(mx, my, msg);
                }
                if (x.compareTo(y) == 0) {
                    assertEquals(mx, my, msg);
                }
            }
        }
    }

    @Test
    void testConsistentcyOfKeys()  {
        List<String> keys = metric.keys();
        for (int i = 0; i < keys.size(); i++) {
            int lower = Math.max(0, i-1);
            int upper = Math.min(keys.size(), i+1);
            List<String> subList = keys.subList(lower, upper);
            checkConsistency(subList);
        }

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
    void testConsistencyNumbersAsiaticUnsorted() {
        checkConsistency(ASIATIC_UNSORTED);
    }

    @Test
    void testConsistencyUkrainianUnsorted() {
        checkConsistency(UKRAINIAN_WORDS);
    }

    @Test
    void testConsistencyCyrLatMix() {
        checkConsistency(CYR_LAT_MIX);
    }

    @Test
    void testConsistencyExtremes() {
        checkConsistency(EXTREMES);
    }

    @Test
    void testFunnyList() {
        checkConsistency(List.of("(1937)/голодовий", "(Львів"));
    }

    @Test
    void testConsistencyXy() {
        checkConsistency(List.of("\u0000", "ÿ"));
    }

}
