package net.itsky.java.sort.metric;

import net.itsky.java.sort.Metric;
import net.itsky.java.sort.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.itsky.java.sort.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Latin1StringMetricTest {

    private static final Metric<String> metric = new Latin1StringMetric();

    @Test
    void testMetric() {
        List<List<String>> listOfLists = List.of(UNSORTED, LONG_UNSORTED,NUMBERS_UNSORTED, ASIATIC_UNSORTED, EXTREMES);
        for (List<String> list : listOfLists) {
            TestUtils.checkConsistency(list, metric);
        }
    }

    @Test
    void testMetricAboveLatin1() {
        long m = metric.metric("\u0400");
        for (int i = 0x0100; i <= 0xffff; i++) {
            String si = String.valueOf((char) i);
            assertEquals(m, metric.metric(si), () -> "si=" + si + " i=" + (int) si.charAt(0));
            for (int j = 0; j <= 0x01ff; j++) {
                String sj = String.valueOf((char) j);
                assertEquals(m, metric.metric(si+sj));
            }
        }
    }

    @Test
    void testMetricLongLists() {
        List<List<String>> listOfLists = List.of(NUMBERS_LONG_UNSORTED, NUMBERS_PREFIXED_UNSORTED, NUMBERS_MIXED_PREFIXED_UNSORTED);
        for (List<String> list : listOfLists) {
            int n = list.size();
            for (int i = 0; i < n; i++) {
                String x = list.get(i);
                long xm = metric.metric(x);
                List<String> otherList = IntStream.of(0, 1, 2, 3, i-5, i-4, i-3, i-2, i-1, i, i+1, i+2, i+3, i+4, i+5, n-4, n-3, n-2, n-1).filter(k-> 0 <= k && k < n).mapToObj(list::get).toList();
                for (String y : otherList) {
                    long ym = metric.metric(y);
                    if (xm < ym) {
                        assertTrue(x.compareTo(y) < 0, () -> "x=" + x + " y=" + y + " xm=" + xm + " ym=" + ym);
                    } else if (xm > ym) {
                        assertTrue(x.compareTo(y) > 0, () -> "x=" + x + " y=" + y + " xm=" + xm + " ym=" + ym);
                    }
                }
            }
        }

    }

    @Test
    void testCoverage() {
        Set<Long> set = IntStream.range(0x000,0x100).mapToLong(ci -> metric.metric(String.valueOf((char) ci))).boxed().collect(Collectors.toSet());
        assertEquals(0x100, set.size());
    }
}