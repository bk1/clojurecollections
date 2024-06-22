package net.itsky.java.sort.metric;

import net.itsky.java.sort.metric.MetricDataHash;
import org.eclipse.collections.api.factory.map.primitive.MutableObjectIntMapFactory;
import org.eclipse.collections.api.factory.map.primitive.MutableObjectLongMapFactory;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.impl.map.mutable.primitive.MutableObjectIntMapFactoryImpl;
import org.eclipse.collections.impl.map.mutable.primitive.MutableObjectLongMapFactoryImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;

import static net.itsky.java.sort.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
public class MetricDataHashTest {

    private static final MutableObjectIntMapFactory mapFactory = new MutableObjectIntMapFactoryImpl();

    @Test
    void testWriteAndRead() throws IOException, InterruptedException {
        MutableObjectIntMap<String> map = mapFactory.of("A", 1, "B", 2, "C", 3, "D", 4);
        MetricDataHash source = new MetricDataHash(map);
        MetricDataHash target = new MetricDataHash();
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

    private MetricDataHash readUkrainianMetric() {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ukrainian-metric.dat");
        MetricDataHash result = new MetricDataHash();
        result.read(stream);
        return result;
    }

    private void checkConsistency(List<String> data) {
        if (data.size() > 2000) {
            data = data.subList(0, 2000);
        }
        MetricDataHash metric = readUkrainianMetric();
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

    @Test
    void testConsistencyExtremes() {
        checkConsistency(EXTREMES);
    }

}
