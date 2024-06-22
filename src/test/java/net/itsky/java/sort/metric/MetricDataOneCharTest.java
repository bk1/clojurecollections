package net.itsky.java.sort.metric;

import net.itsky.java.sort.metric.MetricDataOneChar;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static net.itsky.java.sort.TestUtils.checkMinMaxPairs;
import static net.itsky.java.sort.metric.MetricDataOneChar.ARR_SIZE;
import static net.itsky.java.sort.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MetricDataOneCharTest {

    private MetricDataOneChar metric = readUkrainianMetric();

    @Test
    void testWriteAndRead() throws IOException, InterruptedException {
        int[] arr = IntStream.range(0, ARR_SIZE).map(x-> {int xx = x - 32768;return xx*xx*xx - xx*xx + xx - 1;}).toArray();
        MetricDataOneChar source = new MetricDataOneChar(arr);
        MetricDataOneChar target = new MetricDataOneChar();
        try (PipedInputStream inputStream = new PipedInputStream()) {
            try (PipedOutputStream outputStream = new PipedOutputStream()) {
                outputStream.connect(inputStream);
                Thread sourceThread = new Thread(() -> source.write(outputStream));
                sourceThread.start();
                target.read(inputStream);
                sourceThread.join();
            }
        }
        for (int ci = 0; ci < ARR_SIZE; ci++) {
            String s = String.valueOf((char) ci);
            assertEquals(arr[ci], target.metric(s));
            assertEquals(arr[ci], source.metric(s));
        }
    }

    private MetricDataOneChar readUkrainianMetric() {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ukrainian-metric-1c.dat")) {
            MetricDataOneChar result = new MetricDataOneChar();
            result.read(stream);
            return result;
        } catch (IOException ioex) {
            throw new UncheckedIOException(ioex);
        }
    }

    private void checkConsistency(List<String> data) {
        if (data.size() > 2000) {
            data = data.subList(0, 2000);
        }
        for (String x: data) {
            long mx = metric.metric(x);
            for (String y:data) {
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
    void testClassMinMax() {
        checkMinMaxPairs(LIST_OF_CLASS_MIN_MAX, metric, false);
    }

}
