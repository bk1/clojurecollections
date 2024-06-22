package net.itsky.java.sort.metric;

import net.itsky.java.sort.TestData;
import net.itsky.java.sort.TestUtils;
import net.itsky.java.sort.metric.MetricDataTree;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.Map;

import static net.itsky.java.sort.TestData.*;
import static net.itsky.java.sort.TestUtils.checkMinMaxPairs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MetricDataTreeTest {

    private MetricDataTree metric = readUkrainianMetric();

    @Test
    void testWriteAndRead() throws IOException, InterruptedException {
        TreeSortedMap<String, Integer> map = new TreeSortedMap<>(Map.of("A", 1, "B", 2, "C", 3, "D", 4));
        MetricDataTree source = new MetricDataTree(map);
        MetricDataTree target = new MetricDataTree();
        PipedInputStream inputStream = new PipedInputStream();
        PipedOutputStream outputStream = new PipedOutputStream();
        outputStream.connect(inputStream);
        Thread sourceThread = new Thread(() -> source.write(outputStream));
        sourceThread.start();
        target.read(inputStream);
        sourceThread.join();
        for (String s : map.keySet()) {
            assertEquals(map.get(s), target.metric(s));
            assertEquals(map.get(s), source.metric(s));
        }
    }

    private MetricDataTree readUkrainianMetric() {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ukrainian-metric.dat");
        MetricDataTree result = new MetricDataTree();
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
    void testConsistencyAsiaticUnsorted() {
        checkConsistency(ASIATIC_UNSORTED);
    }

    @Test
    void testConsistencyExtremesUnsorted() {
        checkConsistency(EXTREMES_UNSORTED);
    }

    @Test
    void testZeroMetric() {
        checkConsistency(List.of("0", "0. г. (Фізично-математичні науки, Київ). Данілевський В. Я."));
    }

    @Test
    void testClassMinMax() {
        checkMinMaxPairs(LIST_OF_CLASS_MIN_MAX, metric, false);
    }


}
