package net.itsky.java.sort;

import net.itsky.java.clojurecollections.util.MetricDataForest;
import net.itsky.java.clojurecollections.util.MetricDataTree;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.Map;

import static net.itsky.java.sort.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MetricDataForestTest {

    private MetricDataForest metric = readUkrainianMetric();

    @Test
    void testWriteAndRead() throws IOException, InterruptedException {
        MetricDataForest source = metric;
        MetricDataForest target = new MetricDataForest();
        PipedInputStream inputStream = new PipedInputStream();
        PipedOutputStream outputStream = new PipedOutputStream();
        outputStream.connect(inputStream);
        Thread sourceThread = new Thread(() -> source.write(outputStream));
        sourceThread.start();
        target.read(inputStream);
        sourceThread.join();
        assertEquals(source.keys(), target.keys());
        for (String s : source.keys()) {
            assertEquals(source.metric(s), target.metric(s));
        }
    }

    private MetricDataForest readUkrainianMetric() {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ukrainian-metric.dat");
        MetricDataForest result = new MetricDataForest();
        result.read(stream);
        return result;
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
    void testConsistencyNumbersAsiaticUnsorted() {
        checkConsistency(ASIATIC_UNSORTED);
    }

    @Test
    void testConsistencyUkrainianUnsorted() {
        checkConsistency(UKRAINIAN_WORDS);
    }

}
