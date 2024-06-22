package net.itsky.java.sort.metric;

import net.itsky.java.sort.TestUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.Random;

import static net.itsky.java.sort.TestData.*;
import static net.itsky.java.sort.TestUtils.checkMinMaxPairs;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MetricDataCyrForestCachedTest {

    private MetricDataCyrForest metricRaw = readUkrainianMetricRaw();
    private MetricDataCyrForestCached metric = readUkrainianMetric();

    @Test
    void testWriteAndRead() throws IOException, InterruptedException {
        MetricDataCyrForestCached source = metric;
        MetricDataCyrForestCached target = new MetricDataCyrForestCached();
        try (PipedInputStream inputStream = new PipedInputStream()) {
            try (PipedOutputStream outputStream = new PipedOutputStream()) {
                outputStream.connect(inputStream);
                Thread sourceThread = new Thread(() -> source.write(outputStream));
                sourceThread.start();
                Thread targetThread = new Thread(() -> target.read(inputStream));
                targetThread.start();
                sourceThread.join();
                targetThread.join();
            }
        }
        List<String> sourceKeys = source.keys();
        System.out.println("sourceKeys.size=" + sourceKeys.size());
        assertEquals(sourceKeys, target.keys());
        int i = 0;
        for (String s : sourceKeys) {
            i++;
            if (i%997 == 0 || i < 1000 || i >= sourceKeys.size()-1000) {
                assertEquals(source.metric(s), target.metric(s), "i=" + i + " s=\"" + s + "\"");
            }
        }
    }

    private MetricDataCyrForestCached readUkrainianMetric() {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ukrainian-metric.dat");
        MetricDataCyrForestCached result = new MetricDataCyrForestCached();
        result.read(stream);
        return result;
    }

    private MetricDataCyrForest readUkrainianMetricRaw() {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ukrainian-metric.dat");
        MetricDataCyrForest result = new MetricDataCyrForest();
        result.read(stream);
        return result;
    }


    private void checkConsistencyLong(List<String> data, int n) {
        TestUtils.checkConsistencyLong(data, metric, n);
    }

    private void checkConsistency(List<String> data) {
        TestUtils.checkConsistency(data, metric);
    }

    @Test
    void testConsistencyOfKeysLong() {
        List<String> keys = metric.keys();
        int i = 0;
        for (String key : keys) {
            i++;
            if (i%1001 == 0 || i < 1000 || i > keys.size()-1000) {
                assertEquals(metricRaw.metric(key), metric.metric(key));
            }
        }
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
    void testConsistencyNumbersLongUnsortedLong() {
        checkConsistencyLong(NUMBERS_LONG_UNSORTED, 1);
    }

    @Test
    void testConsistencyNumbersPrefixedUnsorted() {
        checkConsistency(NUMBERS_PREFIXED_UNSORTED);
    }

    @Test
    void testConsistencyNumbersPrefixedUnsortedLong() {
        checkConsistencyLong(NUMBERS_PREFIXED_UNSORTED, 1);
    }

    @Test
    void testConsistencyNumbersMixedPrefixedUnsorted() {
        checkConsistency(NUMBERS_MIXED_PREFIXED_UNSORTED);
    }

    @Test
    void testConsistencyNumbersMixedPrefixedUnsortedLong() {
        checkConsistencyLong(NUMBERS_MIXED_PREFIXED_UNSORTED, 1);
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
    void testConsistencyUkrainianUnsortedLong() {
        checkConsistencyLong(UKRAINIAN_WORDS, 1);
    }

    @Test
    void testConsistencyCyrLatMix() {
        checkConsistency(CYR_LAT_MIX);
    }

    @Test
    void testConsistencyCyrLatMixLong() {
        checkConsistencyLong(CYR_LAT_MIX, 3);
    }

    @Test
    void testConsistencyExtremes() {
        checkConsistency(EXTREMES);
    }

    @Test
    void testClassMinMax() {
        checkMinMaxPairs(LIST_OF_CLASS_MIN_MAX, metric, false);
    }

}
