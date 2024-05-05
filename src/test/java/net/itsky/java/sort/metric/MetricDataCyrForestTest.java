package net.itsky.java.sort.metric;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;

import static net.itsky.java.sort.TestData.*;
import static net.itsky.java.sort.TestUtils.checkMinMaxPairs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MetricDataCyrForestTest {

    private MetricDataCyrForest metric = readUkrainianMetric();

    @Test
    void testWriteAndRead() throws IOException, InterruptedException {
        MetricDataCyrForest source = metric;
        MetricDataCyrForest target = new MetricDataCyrForest();
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
        assertEquals(source.keys(), target.keys());
        for (String s : source.keys()) {
            assertEquals(source.metric(s), target.metric(s));
        }
    }

    private MetricDataCyrForest readUkrainianMetric() {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("ukrainian-metric.dat");
        MetricDataCyrForest result = new MetricDataCyrForest();
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

    @Test
    void testConsistencyBigGroups() {
        List<List<String>> listOfLists
                = List.of(List.of("0", "0. г. (Фізично-математичні науки, Київ). Данілевський В. Я."),
                List.of("—", "—\000", "— ", "—Z", "—\377", "—■ Гав!"),
                List.of("20", "20 maxT=20—22 III. 1904 р."),
                List.of("Leh Palych", "LehBot"),
                List.of("proofread-index", "prop: 'revisions',"),
                List.of("tex1zqdsqtrztoscf25taq9woumj9ff", "teymo4uflev3ulicsb3pgkjhzi6e2at"),
                List.of("wiki", "will be able to receive him."),
                List.of("Стор", "Стояв економ високий, сердитий. Хлопець одійшов кілька ступнів у бік і мовчки з підлоб'я дивився на його, не теряючи самоповаги, як то й годиться чарівникові."),
                List.of("—", "― Алилуя заспіваймо,"),
                List.of("", ""));
        listOfLists.forEach(this::checkConsistency);
        long m1 = metric.metric("—");
        long m2 = metric.metric("—\001");
        assertTrue(m1 < m2, String.format("c=%x m1=%d=%x m2=%d=%x", (int) '—', m1, m1, m2, m2));
    }

    @Test
    void testClassMinMax() {
        checkMinMaxPairs(LIST_OF_CLASS_MIN_MAX, metric, false);
    }

}
