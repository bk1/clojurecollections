package net.itsky.java.sort;

import org.eclipse.collections.api.factory.list.ImmutableListFactory;
import org.eclipse.collections.impl.list.immutable.ImmutableListFactoryImpl;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class FlashSortArrayTest {


    private static final long[] NUMBERS_UNSORTED = LongStream.range(0, 1001).map(x -> (1L + x + x * x + x*x*x + x*x*x*x) % 10012002L).toArray();
    private static final long[] NUMBERS_SORTED = Arrays.stream(NUMBERS_UNSORTED).toArray();

    static {
        Arrays.sort(NUMBERS_SORTED);
    }


    @Test
    void testSortEmpty() {
        FlashSortArray.fsort(new long[0]);
    }

    @Test
    void testSortOne() {
        long[] array = LongStream.of(101202).toArray();
        FlashSortArray.fsort(array);
        assertEquals(101202, array[0]);
    }

    @Test
    void testSortNumbers() {
        long[] array = Arrays.stream(NUMBERS_UNSORTED).toArray();
        FlashSortArray.fsort(array);
        assertArrayEquals(NUMBERS_SORTED, array);
    }

    @Test
    void testSortShort() {
        List<long[]> lists = Stream.of(LongStream.of(1, 2), LongStream.of(2, 1), LongStream.of(1, 2, 3), LongStream.of(2, 1, 3), LongStream.of(1, 3, 2), LongStream.of(3, 1, 2), LongStream.of(2, 3, 1), LongStream.of(3, 2, 1),
                LongStream.of(1, 2, 3, 4), LongStream.of(1, 1, 1, 1), LongStream.of(2, 1, 3, 4), LongStream.of(3, 2, 1, 4), LongStream.of(2, 3, 1, 4), LongStream.of(3, 1, 2, 4), LongStream.of(1, 3, 2, 4)).map(LongStream::toArray).toList();
        for (long[] array : lists) {
            long[] expected = Arrays.stream(array).toArray();
            Arrays.sort(expected);
            FlashSortArray.fsort(array);
            assertArrayEquals(expected, array);
        }
    }

    private List<long[]> createAllPermutations(int n) {
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
        List<long[]> result = new ArrayList<>(factorial);
        long[] elements = LongStream.range(1, n + 1).toArray();
        result.add(Arrays.stream(elements).toArray());
        int i = 0;
        while (i < n) {
            if (indexes[i] < i) {
                int otherIdx = (i & 0x01) == 0 ? 0 : indexes[i];
                long x = elements[i];
                elements[i] = elements[otherIdx];
                elements[otherIdx] = x;
                result.add(Arrays.stream(elements).toArray());
                indexes[i]++;
                i = 0;
            } else {
                indexes[i] = 0;
                i++;
            }
        }
        return result;
    }


    @Test
    public void testAllPermutations() {
        for (int n = 0; n <= 8; n++) {
            List<long[]> permutations = createAllPermutations(n);
            long[] sorted = Arrays.stream(permutations.get(0)).toArray();
            for (long[] array : permutations) {
                FlashSortArray.fsort(array);
                assertArrayEquals(sorted, array);
            }
        }
    }

}
