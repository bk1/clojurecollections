package net.itsky.java.sort.metric;

import net.itsky.java.sort.Metric;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Supplier;
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
            for (String x : list) {
                long xm = metric.metric(x);
                for (String y : list) {
                    long ym = metric.metric(y);
                    Supplier<String> msg =  () -> "x=" + x + " y=" + y + " x[0]=" + (int) ((x+"0").charAt(0)) + " y[0]=" + (int) ((y+"0").charAt(0)) + " xm=" + xm + " ym=" + ym;
                    if (xm < ym) {
                        assertTrue(x.compareTo(y) < 0, msg);
                    } else if (xm > ym) {
                        assertTrue(x.compareTo(y) > 0, msg);
                    }
                }
            }
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
 // ,
}