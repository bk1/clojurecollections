package net.itsky.java.sort;

import net.itsky.java.clojurecollections.PersistentList;
import net.itsky.java.clojurecollections.TransientList;
import org.eclipse.collections.api.factory.list.ImmutableListFactory;
import org.eclipse.collections.impl.list.immutable.ImmutableListFactoryImpl;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static net.itsky.java.sort.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FlashSortTest {


    private final ListSwapper<String> listSwapper = new ListSwapper<>();
    private final PersistentListSwapper<String> persistentListSwapper = new PersistentListSwapper<>();

    private final TransientListSwapper<String> transientListSwapper = new TransientListSwapper<>();
    private final SortMetricized<String, List<String>> listSort = new FlashSort<>();
    private final SortMetricized<String, PersistentList<String>> plistSort = new FlashSort<>();

    private final SortMetricized<String, TransientList<String>> tlistSort = new FlashSort<>();

    private final Metric<String> defaultStringMetric = new DefaultStringMetric();

    private final ListSwapper<Integer> intListSwapper = new ListSwapper<>();
    private final PersistentListSwapper<Integer> persistentIntListSwapper = new PersistentListSwapper<>();

    private final TransientListSwapper<Integer> transientIntListSwapper = new TransientListSwapper<>();
    private final SortMetricized<Integer, List<Integer>> intListSort = new FlashSort<>();
    private final SortMetricized<Integer, PersistentList<Integer>> intPlistSort = new FlashSort<>();

    private final SortMetricized<Integer, TransientList<Integer>> intTlistSort = new FlashSort<>();

    private final Metric<Integer> defaultIntMetric = x -> x;


    @Test
    void testFlashSortAsiatic() {
        List<String> unsortedList = new ArrayList<>(UNSORTED_ASIATIC);
        PersistentList<String> unsortedP = new PersistentList<>(UNSORTED_ASIATIC);
        TransientList<String> unsortedT = new TransientList<>(UNSORTED_ASIATIC);

        List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), listSwapper, defaultStringMetric);
        assertEquals(SORTED_ASIATIC, sortedList);

        PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), persistentListSwapper, defaultStringMetric);
        assertEquals(SORTED_ASIATIC, sortedP);

        TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), transientListSwapper, defaultStringMetric);
        assertEquals(SORTED_ASIATIC, sortedT);
    }


    @Test
    void testFlashSort() {
        List<String> unsortedList = new ArrayList<>(UNSORTED);
        PersistentList<String> unsortedP = new PersistentList<>(UNSORTED);
        TransientList<String> unsortedT = new TransientList<>(UNSORTED);

        List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), listSwapper, defaultStringMetric);
        assertEquals(SORTED, sortedList);

        PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), persistentListSwapper, defaultStringMetric);
        assertEquals(SORTED, sortedP);

        TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), transientListSwapper, defaultStringMetric);
        assertEquals(SORTED, sortedT);
    }

    @Test
    void testFlashSortLong() {
        List<String> unsortedList = new ArrayList<>(LONG_UNSORTED);
        PersistentList<String> unsortedP = new PersistentList<>(LONG_UNSORTED);
        TransientList<String> unsortedT = new TransientList<>(LONG_UNSORTED);


        List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), listSwapper, defaultStringMetric);
        assertEquals(LONG_SORTED, sortedList);

        PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), persistentListSwapper, defaultStringMetric);
        assertEquals(LONG_SORTED, sortedP);

        TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), transientListSwapper, defaultStringMetric);
        assertEquals(LONG_SORTED, sortedT);
    }

    @Test
    void testFlashSortShort() {
        List<Integer> empty = new ArrayList<>();
        List<Integer> emptyS = intListSort.sort(empty, Comparator.naturalOrder(), intListSwapper, defaultIntMetric);
        assertTrue(emptyS.isEmpty());
        List<Integer> oneU = Collections.singletonList(1);
        List<Integer> oneS = intListSort.sort(new ArrayList<>(oneU), Comparator.naturalOrder(), intListSwapper, defaultIntMetric);
        assertEquals(oneU, oneS);
        List<List<Integer>> lists = List.of(List.of(1, 2), List.of(2, 1), List.of(1, 2, 3), List.of(2, 1, 3), List.of(1, 3, 2), List.of(3, 1, 2), List.of(2, 3, 1), List.of(3, 2, 1),
                List.of(1, 2, 3, 4), List.of(1, 1, 1, 1), List.of(2, 1, 3, 4), List.of(3, 2, 1, 4), List.of(2, 3, 1, 4), List.of(3, 1, 2, 4), List.of(1, 3, 2, 4));
        for (List<Integer> unsorted : lists) {
            List<Integer> sorted = intListSort.sort(new ArrayList<>(unsorted), Comparator.naturalOrder(), intListSwapper, defaultIntMetric);
            List<Integer> expected = new ArrayList<>(unsorted);
            Collections.sort(expected);
            assertEquals(expected, sorted);
        }
    }

    private List<List<Integer>> createAllPermutations(int n) {
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


    @Test
    public void testAllPermutations() {
        for (int n = 0; n <= 8; n++) {
            List<List<Integer>> permutations = createAllPermutations(n);
            List<Integer> sorted = permutations.getFirst();
            for (List<Integer> unsorted : permutations) {
                assertEquals(sorted, intListSort.sort(new ArrayList<>(unsorted), Comparator.naturalOrder(), intListSwapper, defaultIntMetric), () -> "unsorted=" + unsorted);
                assertEquals(sorted, intPlistSort.sort(new PersistentList<>(unsorted), Comparator.naturalOrder(), persistentIntListSwapper, defaultIntMetric), () -> "unsorted=" + unsorted);
                assertEquals(sorted, intTlistSort.sort(new TransientList<>(unsorted), Comparator.naturalOrder(), transientIntListSwapper, defaultIntMetric), () -> "unsorted=" + unsorted);
            }
        }
    }


    @Test
    public void testAllPermutationsOfSignedNumbers() {
        Function<Integer, Integer> function = x -> x - 2;
        for (int n = 0; n <= 8; n++) {
            List<List<Integer>> permutations = createAllPermutations(n);
            List<Integer> sorted = permutations.getFirst().stream().map(function).toList();
            for (List<Integer> permutation : permutations) {
                List<Integer> unsorted = permutation.stream().map(function).toList();
                assertEquals(sorted, intListSort.sort(new ArrayList<>(unsorted), Comparator.naturalOrder(), intListSwapper, defaultIntMetric), () -> "unsorted=" + unsorted);
                assertEquals(sorted, intPlistSort.sort(new PersistentList<>(unsorted), Comparator.naturalOrder(), persistentIntListSwapper, defaultIntMetric), () -> "unsorted=" + unsorted);
                assertEquals(sorted, intTlistSort.sort(new TransientList<>(unsorted), Comparator.naturalOrder(), transientIntListSwapper, defaultIntMetric), () -> "unsorted=" + unsorted);
            }
        }
    }


    @Test
    public void testNumbers() {
        ListSwapper<String> swapper = new ListSwapper<>();
        assertEquals(NUMBERS_SORTED, listSort.sort(new ArrayList<>(NUMBERS_UNSORTED), Comparator.naturalOrder(), listSwapper, defaultStringMetric), () -> "unsorted=" + NUMBERS_UNSORTED);
        assertEquals(NUMBERS_SORTED, plistSort.sort(new PersistentList<>(NUMBERS_UNSORTED), Comparator.naturalOrder(), persistentListSwapper, defaultStringMetric), () -> "unsorted=" + NUMBERS_UNSORTED);
        assertEquals(NUMBERS_SORTED, tlistSort.sort(new TransientList<>(NUMBERS_UNSORTED), Comparator.naturalOrder(), transientListSwapper, defaultStringMetric), () -> "unsorted=" + NUMBERS_UNSORTED);
    }

    @Test
    public void testLongNumbers() {
        ListSwapper<String> swapper = new ListSwapper<>();
        assertEquals(NUMBERS_LONG_SORTED, listSort.sort(new ArrayList<>(NUMBERS_LONG_UNSORTED), Comparator.naturalOrder(), listSwapper, defaultStringMetric));
        assertEquals(NUMBERS_LONG_SORTED, plistSort.sort(new PersistentList<>(NUMBERS_LONG_UNSORTED), Comparator.naturalOrder(), persistentListSwapper, defaultStringMetric));
        assertEquals(NUMBERS_LONG_SORTED, tlistSort.sort(new TransientList<>(NUMBERS_LONG_UNSORTED), Comparator.naturalOrder(), transientListSwapper, defaultStringMetric));
    }


    @Test
    public void testPrefixedNumbers() {
        ListSwapper<String> swapper = new ListSwapper<>();
        assertEquals(NUMBERS_PREFIXED_SORTED, listSort.sort(new ArrayList<>(NUMBERS_PREFIXED_UNSORTED), Comparator.naturalOrder(), listSwapper, defaultStringMetric));
        assertEquals(NUMBERS_PREFIXED_SORTED, plistSort.sort(new PersistentList<>(NUMBERS_PREFIXED_UNSORTED), Comparator.naturalOrder(), persistentListSwapper, defaultStringMetric));
        assertEquals(NUMBERS_PREFIXED_SORTED, tlistSort.sort(new TransientList<>(NUMBERS_PREFIXED_UNSORTED), Comparator.naturalOrder(), transientListSwapper, defaultStringMetric));
    }


    @Test
    public void testMixedPrefixedNumbers() {
        ListSwapper<String> swapper = new ListSwapper<>();
        assertEquals(NUMBERS_MIXED_PREFIXED_SORTED, listSort.sort(new ArrayList<>(NUMBERS_MIXED_PREFIXED_UNSORTED), Comparator.naturalOrder(), listSwapper, defaultStringMetric));
        assertEquals(NUMBERS_MIXED_PREFIXED_SORTED, plistSort.sort(new PersistentList<>(NUMBERS_MIXED_PREFIXED_UNSORTED), Comparator.naturalOrder(), persistentListSwapper, defaultStringMetric));
        assertEquals(NUMBERS_MIXED_PREFIXED_SORTED, tlistSort.sort(new TransientList<>(NUMBERS_MIXED_PREFIXED_UNSORTED), Comparator.naturalOrder(), transientListSwapper, defaultStringMetric));
    }

}