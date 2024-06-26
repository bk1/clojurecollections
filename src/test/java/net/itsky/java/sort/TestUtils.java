package net.itsky.java.sort;

import org.eclipse.collections.api.factory.list.ImmutableListFactory;
import org.eclipse.collections.impl.list.immutable.ImmutableListFactoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUtils {
    static <T> void assertBigListsEqual(List<T> l1, List<T> l2) {
        assertEquals(l1.size(), l2.size());
        int n = l1.size();
        int minIdx = -1;
        int maxIdx = -1;
        int count = 0;
        for (int i = 0; i < n; i++) {
            if (! l1.get(i).equals(l2.get(i))) {
                if (minIdx == -1) {
                    minIdx = i;
                }
                maxIdx = i;
                count++;
            }
        }
        if (count != 0) {
            System.out.println("count=" + count + " n=" + n + " minIdx=" + minIdx + " maxIdx=" + maxIdx + " l1[minIdx]=" + l1.get(minIdx) + " l2[minIdx]=" + l2.get(minIdx) + " l1[maxIdx]=" + l1.get(maxIdx) + " l2[maxIdx]=" + l2.get(maxIdx));
            assertEquals(0, count);
        }
    }

    static List<List<Integer>> createAllPermutations(int n) {
        if (n < 0 || n > 13) {
            throw new IllegalArgumentException("factorial of n fits into int for n=0,1,2,...13, but n=" + n);
        }
        long factorialL = LongStream.range(1L, n).reduce(1, (prod, i) -> prod * i);
        int factorial = (int) factorialL;
        if (factorialL != (long) factorial) {
            throw new IllegalArgumentException("factorial of n fits into int for n=0,1,2,...12, but n=" + n);
        }
        int[] indexes = new int[n];
        for (int i = 0; i < n; i++) {
            indexes[i] = 0;
        }
        List<List<Integer>> result = new ArrayList<>(factorial);
        ImmutableListFactory lf = new ImmutableListFactoryImpl();
        ListSwapper<Integer> swapper = new ListSwapper<>();
        List<Integer> elements = new ArrayList<>(IntStream.range(1, n + 1).boxed().toList());
        result.add(lf.<Integer>withAll(elements).castToList());
        int i = 0;
        while (i < n) {
            if (indexes[i] < i) {
                swapper.swap(elements, (i & 0x01) == 0 ? 0 : indexes[i], i);
                result.add(lf.withAll(elements).castToList());
                indexes[i]++;
                i = 0;
            } else {
                indexes[i] = 0;
                i++;
            }
        }
        return result;
    }

    public static void checkConsistencyLong(List<String> data, Metric<String> metric, int n) {
        IntStream.range(n, data.size() - n-1)
                .mapToObj(i -> IntStream.rangeClosed(-n, n).map(j->i+j).mapToObj(data::get).toList())
                .forEach(list->checkConsistency(list, metric));
    }

    public static void checkConsistency(List<String> data, Metric<String> metric) {

        if (data.size() > 2000) {
            data = data.subList(0, 2000);
        }
        for (String x: data) {
            int mx = metric.metric(x);
            assertTrue(mx >= 0L, () -> "x=" + x + " mx=" + mx);
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

    public static void checkMinMaxPairs(List<List<String>> minMaxPairs, Metric<String> metric, boolean strict) {
        for (List<String> pair : minMaxPairs) {
            checkConsistency(pair, metric);
            assertEquals(2, pair.size());
            String s1 = pair.getFirst();
            String s2 = pair.getLast();
            long m1 = metric.metric(s1);
            long m2 = metric.metric(s2);
            String msg = String.format("m1=%d=%016x m2=%d=%016x delta=%d rel_delta=%f s1=\"%s\" s2=\"%s\"", m1, m1, m2, m2, m2-m1, ((double) (m2-m1))/((double) m1 + (double) m2), s1, s2);
            System.out.println(msg);
            assertTrue(m1 <= m2, msg);
            if (strict) {
                assertTrue(m1 < m2, msg);
            }
        }
    }
}
