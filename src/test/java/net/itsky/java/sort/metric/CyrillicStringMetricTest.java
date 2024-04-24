package net.itsky.java.sort.metric;

import net.itsky.java.sort.Metric;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.itsky.java.sort.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CyrillicStringMetricTest {

    private static final Metric<String> metric = new CyrillicStringMetric();

    @Test
    void testMetric() {
        List<List<String>> listOfLists = List.of(UNSORTED, LONG_UNSORTED,NUMBERS_UNSORTED, ASIATIC_UNSORTED, EXTREMES);
        for (List<String> list : listOfLists) {
            for (String x : list) {
                long xm = metric.metric(x);
                for (String y : list) {
                    long ym = metric.metric(y);
                    if (xm < ym) {
                        assertTrue(x.compareTo(y) < 0, () -> "x=" + x + " y=" + y + " xm=" + xm + " ym=" + ym + " list=" + list);
                    } else if (xm > ym) {
                        assertTrue(x.compareTo(y) > 0, () -> "x=" + x + " y=" + y + " xm=" + xm + " ym=" + ym);
                    }
                }
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
    void testCoverageLatCyr() {
        Set<Long> set = IntStream.concat(IntStream.range(0x000,0x100), IntStream.range(0x400, 0x500)).mapToLong(ci -> metric.metric(String.valueOf((char) ci))).boxed().collect(Collectors.toSet());
        assertEquals(0x200, set.size());
    }


    @Test
    void testCoverage() {
        Set<Long> set = IntStream.range(0x000,0x500).mapToLong(ci -> metric.metric(String.valueOf((char) ci))).boxed().collect(Collectors.toSet());
        assertEquals(0x500, set.size());
    }
}