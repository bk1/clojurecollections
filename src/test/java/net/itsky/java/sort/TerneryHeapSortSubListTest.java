package net.itsky.java.sort;

import net.itsky.java.clojurecollections.PersistentList;
import net.itsky.java.clojurecollections.TransientList;
import org.eclipse.collections.api.factory.list.ImmutableListFactory;
import org.eclipse.collections.impl.list.immutable.ImmutableListFactoryImpl;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static net.itsky.java.sort.TestData.*;
import static org.junit.jupiter.api.Assertions.*;

public class TerneryHeapSortSubListTest {

    private final ListSwapper<String> listSwapper = new ListSwapper<>();
    private final PersistentListSwapper<String> persistentListSwapper = new PersistentListSwapper<>();

    private final TransientListSwapper<String> transientListSwapper = new TransientListSwapper<>();
    private final SortSubList<String, List<String>> listSort = new TerneryHeapSortSubList<>();
    private final SortSubList<String, PersistentList<String>> plistSort = new TerneryHeapSortSubList<>();

    private final SortSubList<String, TransientList<String>> tlistSort = new TerneryHeapSortSubList<>();

    private final ListSwapper<Integer> intListSwapper = new ListSwapper<>();
    private final PersistentListSwapper<Integer> persistentIntListSwapper = new PersistentListSwapper<>();

    private final TransientListSwapper<Integer> transientIntListSwapper = new TransientListSwapper<>();
    private final SortSubList<Integer, List<Integer>> intListSort = new TerneryHeapSortSubList<>();
    private final SortSubList<Integer, PersistentList<Integer>> intPlistSort = new TerneryHeapSortSubList<>();

    private final SortSubList<Integer, TransientList<Integer>> intTlistSort = new TerneryHeapSortSubList<>();


    @Test
    void testBeginNegative() {
        List<String> unsortedList = new ArrayList<>(UNSORTED);
        PersistentList<String> unsortedP = new PersistentList<>(UNSORTED);
        TransientList<String> unsortedT = new TransientList<>(UNSORTED);

        assertThrows(IllegalArgumentException.class, () -> listSort.sort(unsortedList, Comparator.naturalOrder(), listSwapper, -1, unsortedList.size()));
        assertThrows(IllegalArgumentException.class, () -> plistSort.sort(unsortedP, Comparator.naturalOrder(), persistentListSwapper, -1, unsortedP.size()));
        assertThrows(IllegalArgumentException.class, () -> tlistSort.sort(unsortedT, Comparator.naturalOrder(), transientListSwapper, -1, unsortedT.size()));
    }

    @Test
    void testBeginAfterEnd() {
        List<String> unsortedList = new ArrayList<>(UNSORTED);
        PersistentList<String> unsortedP = new PersistentList<>(UNSORTED);
        TransientList<String> unsortedT = new TransientList<>(UNSORTED);

        assertThrows(IllegalArgumentException.class, () -> listSort.sort(unsortedList, Comparator.naturalOrder(), listSwapper, 2, 1));
        assertThrows(IllegalArgumentException.class, () -> plistSort.sort(unsortedP, Comparator.naturalOrder(), persistentListSwapper, 2, 1));
        assertThrows(IllegalArgumentException.class, () -> tlistSort.sort(unsortedT, Comparator.naturalOrder(), transientListSwapper, 2, 1));
    }

    @Test
    void testBeginAfterEndAfterSize() {
        List<String> unsortedList = new ArrayList<>(UNSORTED);
        PersistentList<String> unsortedP = new PersistentList<>(UNSORTED);
        TransientList<String> unsortedT = new TransientList<>(UNSORTED);

        assertThrows(IllegalArgumentException.class, () -> listSort.sort(unsortedList, Comparator.naturalOrder(), listSwapper, 2, unsortedList.size() + 1));
        assertThrows(IllegalArgumentException.class, () -> plistSort.sort(unsortedP, Comparator.naturalOrder(), persistentListSwapper, 2, unsortedP.size() + 1));
        assertThrows(IllegalArgumentException.class, () -> tlistSort.sort(unsortedT, Comparator.naturalOrder(), transientListSwapper, 2, unsortedT.size() + 1));
    }

    @Test
    void testSort() {
        List<String> unsortedList = new ArrayList<>(UNSORTED);
        PersistentList<String> unsortedP = new PersistentList<>(UNSORTED);
        TransientList<String> unsortedT = new TransientList<>(UNSORTED);

        List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), listSwapper, 0, unsortedList.size());
        assertEquals(SORTED, sortedList);

        PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), persistentListSwapper, 0, unsortedP.size());
        assertEquals(SORTED, sortedP);

        TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), transientListSwapper, 0, unsortedT.size());
        assertEquals(SORTED, sortedT);
    }

    @Test
    void testSortEmptySubList() {
        List<String> unsortedList = new ArrayList<>(UNSORTED);
        PersistentList<String> unsortedP = new PersistentList<>(UNSORTED);
        TransientList<String> unsortedT = new TransientList<>(UNSORTED);

        for (int l = unsortedList.size(); l >= 0; l--) {
            List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), listSwapper, l, l);
            assertEquals(UNSORTED, sortedList);

            PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), persistentListSwapper, l, l);
            assertEquals(UNSORTED, sortedP);

            TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), transientListSwapper, l, l);
            assertEquals(UNSORTED, sortedT);
        }
    }

    @Test
    void testSortSingularSubList() {
        List<String> unsortedList = new ArrayList<>(UNSORTED);
        PersistentList<String> unsortedP = new PersistentList<>(UNSORTED);
        TransientList<String> unsortedT = new TransientList<>(UNSORTED);

        for (int l = unsortedList.size() - 1; l >= 0; l--) {
            List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), listSwapper, l, l + 1);
            assertEquals(UNSORTED, sortedList);

            PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), persistentListSwapper, l, l + 1);
            assertEquals(UNSORTED, sortedP);

            TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), transientListSwapper, l, l + 1);
            assertEquals(UNSORTED, sortedT);
        }
    }

    @Test
    void testSortAnySubList() {
        for (int lower = 0; lower <= UNSORTED.size(); lower++) {
            for (int upper = lower; upper <= UNSORTED.size(); upper++) {
                List<String> unsortedList = new ArrayList<>(UNSORTED);
                PersistentList<String> unsortedP = new PersistentList<>(UNSORTED);
                TransientList<String> unsortedT = new TransientList<>(UNSORTED);
                List<String> sorted = new ArrayList<>(UNSORTED);
                Collections.sort(sorted.subList(lower, upper));

                List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), listSwapper, lower, upper);
                assertEquals(sorted, sortedList);

                PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), persistentListSwapper, lower, upper);
                assertEquals(sorted, sortedP);

                TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), transientListSwapper, lower, upper);
                assertEquals(sorted, sortedT);

            }
        }
    }

    @Test
    void testSortAnySubListOfLong() {
        for (int lower = 0; lower <= LONG_UNSORTED.size(); lower++) {
            for (int upper = lower; upper <= LONG_UNSORTED.size(); upper++) {
                List<String> unsortedList = new ArrayList<>(LONG_UNSORTED);
                PersistentList<String> unsortedP = new PersistentList<>(LONG_UNSORTED);
                TransientList<String> unsortedT = new TransientList<>(LONG_UNSORTED);
                List<String> sorted = new ArrayList<>(LONG_UNSORTED);
                Collections.sort(sorted.subList(lower, upper));

                List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), listSwapper, lower, upper);
                assertEquals(sorted, sortedList);

                PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), persistentListSwapper, lower, upper);
                assertEquals(sorted, sortedP);

                TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), transientListSwapper, lower, upper);
                assertEquals(sorted, sortedT);

            }
        }
    }

    @Test
    void testSortSubList1to3() {
        int lower = 1;
        int upper = 3;
                List<String> unsortedList = new ArrayList<>(UNSORTED);
                PersistentList<String> unsortedP = new PersistentList<>(UNSORTED);
                TransientList<String> unsortedT = new TransientList<>(UNSORTED);
                List<String> sorted = new ArrayList<>(UNSORTED);
                Collections.sort(sorted.subList(lower, upper));

                List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), listSwapper, lower, upper);
                assertEquals(sorted, sortedList);

                PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), persistentListSwapper, lower, upper);
                assertEquals(sorted, sortedP);

                TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), transientListSwapper, lower, upper);
                assertEquals(sorted, sortedT);
    }

    @Test
    void testSortLong() {
        List<String> unsortedList = new ArrayList<>(LONG_UNSORTED);
        PersistentList<String> unsortedP = new PersistentList<>(LONG_UNSORTED);
        TransientList<String> unsortedT = new TransientList<>(LONG_UNSORTED);


        List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), listSwapper, 0, unsortedList.size());
        assertEquals(LONG_SORTED, sortedList);

        PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), persistentListSwapper, 0, unsortedP.size());
        assertEquals(LONG_SORTED, sortedP);

        TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), transientListSwapper, 0, unsortedT.size());
        assertEquals(LONG_SORTED, sortedT);
    }

    @Test
    void testSortShort() {
        List<Integer> empty = new ArrayList<>();
        List<Integer> emptyS = intListSort.sort(empty, Comparator.naturalOrder(), intListSwapper, 0, empty.size());
        assertTrue(emptyS.isEmpty());
        List<Integer> oneU = Collections.singletonList(1);
        List<Integer> oneS = intListSort.sort(new ArrayList<>(oneU), Comparator.naturalOrder(), intListSwapper, 0, oneU.size());
        assertEquals(oneU, oneS);
        List<List<Integer>> lists = List.of(List.of(1, 2), List.of(2, 1), List.of(1, 2, 3), List.of(2, 1, 3), List.of(1, 3, 2), List.of(3, 1, 2), List.of(2, 3, 1), List.of(3, 2, 1),
                List.of(1, 2, 3, 4), List.of(1, 1, 1, 1), List.of(2, 1, 3, 4), List.of(3, 2, 1, 4), List.of(2, 3, 1, 4), List.of(3, 1, 2, 4), List.of(1, 3, 2, 4));
        for (List<Integer> unsorted : lists) {
            List<Integer> sorted = intListSort.sort(new ArrayList<>(unsorted), Comparator.naturalOrder(), intListSwapper, 0, unsorted.size());
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
            List<Integer> sorted = permutations.get(0);
            for (List<Integer> unsorted : permutations) {
                assertEquals(sorted, intListSort.sort(new ArrayList<>(unsorted), Comparator.naturalOrder(), intListSwapper, 0, unsorted.size()), () -> "unsorted=" + unsorted);
                assertEquals(sorted, intPlistSort.sort(new PersistentList<>(unsorted), Comparator.naturalOrder(), persistentIntListSwapper, 0, unsorted.size()), () -> "unsorted=" + unsorted);
                assertEquals(sorted, intTlistSort.sort(new TransientList<>(unsorted), Comparator.naturalOrder(), transientIntListSwapper, 0, unsorted.size()), () -> "unsorted=" + unsorted);
            }
        }
    }


    @Test
    public void testNumbers() {
        ListSwapper<String> swapper = new ListSwapper<>();
        assertEquals(NUMBERS_SORTED, listSort.sort(new ArrayList<>(NUMBERS_UNSORTED), Comparator.naturalOrder(), listSwapper, 0, NUMBERS_UNSORTED.size()), () -> "unsorted=" + NUMBERS_UNSORTED);
        assertEquals(NUMBERS_SORTED, plistSort.sort(new PersistentList<>(NUMBERS_UNSORTED), Comparator.naturalOrder(), persistentListSwapper, 0, NUMBERS_UNSORTED.size()), () -> "unsorted=" + NUMBERS_UNSORTED);
        assertEquals(NUMBERS_SORTED, tlistSort.sort(new TransientList<>(NUMBERS_UNSORTED), Comparator.naturalOrder(), transientListSwapper, 0, NUMBERS_UNSORTED.size()), () -> "unsorted=" + NUMBERS_UNSORTED);
    }

    @Test
    public void testLongNumbers() {
        ListSwapper<String> swapper = new ListSwapper<>();
        assertEquals(NUMBERS_LONG_SORTED, listSort.sort(new ArrayList<>(NUMBERS_LONG_UNSORTED), Comparator.naturalOrder(), listSwapper, 0, NUMBERS_LONG_UNSORTED.size()));
        assertEquals(NUMBERS_LONG_SORTED, plistSort.sort(new PersistentList<>(NUMBERS_LONG_UNSORTED), Comparator.naturalOrder(), persistentListSwapper, 0, NUMBERS_LONG_UNSORTED.size()));
        assertEquals(NUMBERS_LONG_SORTED, tlistSort.sort(new TransientList<>(NUMBERS_LONG_UNSORTED), Comparator.naturalOrder(), transientListSwapper, 0, NUMBERS_LONG_UNSORTED.size()));
    }


    @Test
    public void testPrefixedNumbers() {
        ListSwapper<String> swapper = new ListSwapper<>();
        assertEquals(NUMBERS_PREFIXED_SORTED, listSort.sort(new ArrayList<>(NUMBERS_PREFIXED_UNSORTED), Comparator.naturalOrder(), listSwapper, 0, NUMBERS_PREFIXED_UNSORTED.size()));
        assertEquals(NUMBERS_PREFIXED_SORTED, plistSort.sort(new PersistentList<>(NUMBERS_PREFIXED_UNSORTED), Comparator.naturalOrder(), persistentListSwapper, 0, NUMBERS_PREFIXED_UNSORTED.size()));
        assertEquals(NUMBERS_PREFIXED_SORTED, tlistSort.sort(new TransientList<>(NUMBERS_PREFIXED_UNSORTED), Comparator.naturalOrder(), transientListSwapper, 0, NUMBERS_PREFIXED_UNSORTED.size()));
    }
}