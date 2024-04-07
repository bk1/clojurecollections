package net.itsky.java.sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
