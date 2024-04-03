package net.itsky.java.sort;

import net.itsky.java.clojurecollections.PersistentList;
import net.itsky.java.clojurecollections.TransientList;
import org.eclipse.collections.api.factory.list.ImmutableListFactory;
import org.eclipse.collections.impl.list.immutable.ImmutableListFactoryImpl;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SortTest {

    private static final List<String> UNSORTED = List.of("FL8YPS", "6DSNQQ", "U419AB", "X82QVN", "CU25H4", "ZZCRWY");
    private static final List<String> SORTED = new ArrayList<>(UNSORTED);
    ;

    private static final List<String> LONG_UNSORTED = List.of("0H9Y41BL8WUQ", "7WPFZU00FHJQ", "D0OBE9SD6DPE", "AGBYG6MMCC9B", "E9SPYKWAITU9", "1VT6J5VSDMX9", "WSW1YJI40W8H", "378PPNCN6OW8", "JL819RD3CFTS", "C2YGBBGXXCBV", "KAGQZ7DY82WS", "C6N3W0RU4N5M", "XSJ9V3MN73TA", "8OPVKYG8JHYA", "UN0P5TKDEA05", "XP0KKYPUSTSF", "JS308V9PAJ2Z", "09NYKUUCTM5U", "AFID27K03CXA", "EPS16MESPLM7", "4FSSP0UJB6Q6", "FJG8FS1F2QXB", "L06RQWNPRFLJ", "TW9GAH01TK02", "2BSNA7IBHTRH", "H7IQKOMZ388X", "Y4B7SEEE5QMP", "C22P8R9UBPS7", "KVJE98AOILZ8", "LJLDM4AEA0AD", "1EVSCZGQ55MV", "E1LN8FRI5VNQ", "32NYE2DD9MBO", "OCKJPM3NKQ7P", "0IFOJ8OAY4H9", "5ERNL1PBOKOC", "76I2GY4KQOXP", "8EKLG4FQFU8K", "VP0J12B839EU", "94D3IKF9BITJ");
    private static final List<String> LONG_SORTED = new ArrayList<>(LONG_UNSORTED);

    private static final List<String> NUMBERS_UNSORTED = IntStream.range(0, 1001).map(x -> (1 + x + x * x) % 1001).boxed().map(x -> String.format("%04d", x)).toList();
    private static final List<String> NUMBERS_SORTED = new ArrayList<>(NUMBERS_UNSORTED);

    static {
        Collections.sort(SORTED);
        Collections.sort(LONG_SORTED);
        Collections.sort(NUMBERS_SORTED);
    }

    private <T, L extends List<T>> Sort<T, L> insertionSort() {
        return (L list, Comparator<T> comparator, Swapper<T, L> swapper) -> {
            int n = list.size();
            for (int i = 0; i < n; i++) {
                T t = list.get(i);
                int j = 0;
                for (j = 0; j < i; j++) {
                    if (comparator.compare(list.get(j), list.get(i)) >= 0) {
                        break;
                    }
                }
                for (int k = i; k > j; k--) {
                    list = swapper.swap(list, k, k - 1);
                }
            }
            return list;
        };
    }

    @Test
    void testInsertionSort() {
        List<String> unsortedList = new ArrayList<>(UNSORTED);
        PersistentList<String> unsortedP = new PersistentList<>(UNSORTED);
        TransientList<String> unsortedT = new TransientList<>(UNSORTED);

        Sort<String, List<String>> listSort = insertionSort();
        List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), new ListSwapper<>());
        assertEquals(SORTED, sortedList);

        Sort<String, PersistentList<String>> plistSort = insertionSort();
        PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), new PersistentListSwapper<String>());
        assertEquals(SORTED, sortedP);

        Sort<String, TransientList<String>> tlistSort = insertionSort();
        TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), new TransientListSwapper<String>());
        assertEquals(SORTED, sortedT);
    }

    @Test
    void testInsertionSortLong() {
        List<String> unsortedList = new ArrayList<>(LONG_UNSORTED);
        PersistentList<String> unsortedP = new PersistentList<>(LONG_UNSORTED);
        TransientList<String> unsortedT = new TransientList<>(LONG_UNSORTED);

        Sort<String, List<String>> listSort = insertionSort();
        List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), new ListSwapper<>());
        assertEquals(LONG_SORTED, sortedList);

        Sort<String, PersistentList<String>> plistSort = insertionSort();
        PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), new PersistentListSwapper<String>());
        assertEquals(LONG_SORTED, sortedP);

        Sort<String, TransientList<String>> tlistSort = insertionSort();
        TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), new TransientListSwapper<String>());
        assertEquals(LONG_SORTED, sortedT);
    }

    private <T, L extends List<T>> L bubbleSort(L list, Comparator<T> comparator, Swapper<T, L> swapper) {
        int n = list.size();
        for (int i = n - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (comparator.compare(list.get(j), list.get(j + 1)) > 0) {
                    list = swapper.swap(list, j, j + 1);
                }
            }
        }
        return list;
    }

    @Test
    void testBubbleSort() {
        List<String> unsortedList = new ArrayList<>(UNSORTED);
        PersistentList<String> unsortedP = new PersistentList<>(UNSORTED);
        TransientList<String> unsortedT = new TransientList<>(UNSORTED);

        Sort<String, List<String>> listSort = this::bubbleSort;
        List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), new ListSwapper<>());
        assertEquals(SORTED, sortedList);

        Sort<String, PersistentList<String>> plistSort = this::bubbleSort;
        PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), new PersistentListSwapper<String>());
        assertEquals(SORTED, sortedP);

        Sort<String, TransientList<String>> tlistSort = this::bubbleSort;
        TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), new TransientListSwapper<String>());
        assertEquals(SORTED, sortedT);
    }

    @Test
    void testBubbleSortLong() {
        List<String> unsortedList = new ArrayList<>(LONG_UNSORTED);
        PersistentList<String> unsortedP = new PersistentList<>(LONG_UNSORTED);
        TransientList<String> unsortedT = new TransientList<>(LONG_UNSORTED);

        Sort<String, List<String>> listSort = this::bubbleSort;
        List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), new ListSwapper<>());
        assertEquals(LONG_SORTED, sortedList);

        Sort<String, PersistentList<String>> plistSort = this::bubbleSort;
        PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), new PersistentListSwapper<String>());
        assertEquals(LONG_SORTED, sortedP);

        Sort<String, TransientList<String>> tlistSort = this::bubbleSort;
        TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), new TransientListSwapper<String>());
        assertEquals(LONG_SORTED, sortedT);
    }

    @Test
    void testQuickSortShort() {
        Sort<Integer, List<Integer>> quickSort = new QuickSort<>();
        List<Integer> empty = new ArrayList<>();
        ListSwapper<Integer> listSwapper = new ListSwapper<>();
        List<Integer> emptyS = quickSort.sort(empty, Comparator.naturalOrder(), listSwapper);
        assertTrue(emptyS.isEmpty());
        List<Integer> oneU = Collections.singletonList(1);
        List<Integer> oneS = quickSort.sort(new ArrayList<>(oneU), Comparator.naturalOrder(), listSwapper);
        assertEquals(oneU, oneS);
        List<List<Integer>> lists = List.of(List.of(1, 2), List.of(2, 1), List.of(1, 2, 3), List.of(2, 1, 3), List.of(1, 3, 2), List.of(3, 1, 2), List.of(2, 3, 1), List.of(3, 2, 1),
                List.of(1, 2, 3, 4), List.of(1, 1, 1, 1), List.of(2, 1, 3, 4), List.of(3, 2, 1, 4), List.of(2, 3, 1, 4), List.of(3, 1, 2, 4), List.of(1, 3, 2, 4));
        for (List<Integer> unsorted : lists) {
            List<Integer> sorted = quickSort.sort(new ArrayList<>(unsorted), Comparator.naturalOrder(), listSwapper);
            List<Integer> expected = new ArrayList<>(unsorted);
            Collections.sort(expected);
            assertEquals(expected, sorted);
        }
    }

    @Test
    void testQuickSort() {
        List<String> unsortedList = new ArrayList<>(UNSORTED);
        PersistentList<String> unsortedP = new PersistentList<>(UNSORTED);
        TransientList<String> unsortedT = new TransientList<>(UNSORTED);

        Sort<String, List<String>> listSort = new QuickSort<>();
        List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), new ListSwapper<>());
        assertEquals(SORTED, sortedList);

        Sort<String, PersistentList<String>> plistSort = new QuickSort<>();
        PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), new PersistentListSwapper<String>());
        assertEquals(SORTED, sortedP);

        Sort<String, TransientList<String>> tlistSort = new QuickSort<>();
        TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), new TransientListSwapper<String>());
        assertEquals(SORTED, sortedT);
    }

    @Test
    void testQuickSortLong() {
        List<String> unsortedList = new ArrayList<>(LONG_UNSORTED);
        PersistentList<String> unsortedP = new PersistentList<>(LONG_UNSORTED);
        TransientList<String> unsortedT = new TransientList<>(LONG_UNSORTED);

        Sort<String, List<String>> listSort = new QuickSort<>();
        List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), new ListSwapper<>());
        assertEquals(LONG_SORTED, sortedList);

        Sort<String, PersistentList<String>> plistSort = new QuickSort<>();
        PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), new PersistentListSwapper<String>());
        assertEquals(LONG_SORTED, sortedP);

        Sort<String, TransientList<String>> tlistSort = new QuickSort<>();
        TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), new TransientListSwapper<String>());
        assertEquals(LONG_SORTED, sortedT);
    }


    @Test
    void testParallelQuckSort() {
        List<String> unsortedList = new ArrayList<>(UNSORTED);
        PersistentList<String> unsortedP = new PersistentList<>(UNSORTED);
        TransientList<String> unsortedT = new TransientList<>(UNSORTED);

        Sort<String, List<String>> listSort = new ParallelQuickSort<>();
        List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), new ListSwapper<>());
        assertEquals(SORTED, sortedList);

        Sort<String, PersistentList<String>> plistSort = new ParallelQuickSort<>();
        PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), new PersistentListSwapper<String>());
        assertEquals(SORTED, sortedP);

        Sort<String, TransientList<String>> tlistSort = new ParallelQuickSort<>();
        TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), new TransientListSwapper<String>());
        assertEquals(SORTED, sortedT);
    }

    @Test
    void testParallelQuckSortLong() {
        List<String> unsortedList = new ArrayList<>(LONG_UNSORTED);
        PersistentList<String> unsortedP = new PersistentList<>(LONG_UNSORTED);
        TransientList<String> unsortedT = new TransientList<>(LONG_UNSORTED);

        Sort<String, List<String>> listSort = new ParallelQuickSort<>();
        List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), new ListSwapper<>());
        assertEquals(LONG_SORTED, sortedList);

        Sort<String, PersistentList<String>> plistSort = new ParallelQuickSort<>();
        PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), new PersistentListSwapper<String>());
        assertEquals(LONG_SORTED, sortedP);

        Sort<String, TransientList<String>> tlistSort = new ParallelQuickSort<>();
        TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), new TransientListSwapper<String>());
        assertEquals(LONG_SORTED, sortedT);
    }


    @Test
    void testHeapSort() {
        List<String> unsortedList = new ArrayList<>(UNSORTED);
        PersistentList<String> unsortedP = new PersistentList<>(UNSORTED);
        TransientList<String> unsortedT = new TransientList<>(UNSORTED);

        Sort<String, List<String>> listSort = new HeapSort<>();
        List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), new ListSwapper<>());
        assertEquals(SORTED, sortedList);

        Sort<String, PersistentList<String>> plistSort = new HeapSort<>();
        PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), new PersistentListSwapper<String>());
        assertEquals(SORTED, sortedP);

        Sort<String, TransientList<String>> tlistSort = new HeapSort<>();
        TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), new TransientListSwapper<String>());
        assertEquals(SORTED, sortedT);
    }

    @Test
    void testHeapSortLong() {
        List<String> unsortedList = new ArrayList<>(LONG_UNSORTED);
        PersistentList<String> unsortedP = new PersistentList<>(LONG_UNSORTED);
        TransientList<String> unsortedT = new TransientList<>(LONG_UNSORTED);

        Sort<String, List<String>> listSort = new HeapSort<>();
        List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), new ListSwapper<>());
        assertEquals(LONG_SORTED, sortedList);

        Sort<String, PersistentList<String>> plistSort = new HeapSort<>();
        PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), new PersistentListSwapper<String>());
        assertEquals(LONG_SORTED, sortedP);

        Sort<String, TransientList<String>> tlistSort = new HeapSort<>();
        TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), new TransientListSwapper<String>());
        assertEquals(LONG_SORTED, sortedT);
    }

    @Test
    void testTernaryHeapSort() {
        List<String> unsortedList = new ArrayList<>(UNSORTED);
        PersistentList<String> unsortedP = new PersistentList<>(UNSORTED);
        TransientList<String> unsortedT = new TransientList<>(UNSORTED);

        Sort<String, List<String>> listSort = new TerneryHeapSort<>();
        List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), new ListSwapper<>());
        assertEquals(SORTED, sortedList);

        Sort<String, PersistentList<String>> plistSort = new TerneryHeapSort<>();
        PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), new PersistentListSwapper<String>());
        assertEquals(SORTED, sortedP);

        Sort<String, TransientList<String>> tlistSort = new TerneryHeapSort<>();
        TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), new TransientListSwapper<String>());
        assertEquals(SORTED, sortedT);
    }

    @Test
    void testTernaryHeapSortLong() {
        List<String> unsortedList = new ArrayList<>(LONG_UNSORTED);
        PersistentList<String> unsortedP = new PersistentList<>(LONG_UNSORTED);
        TransientList<String> unsortedT = new TransientList<>(LONG_UNSORTED);

        Sort<String, List<String>> listSort = new TerneryHeapSort<>();
        List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), new ListSwapper<>());
        assertEquals(LONG_SORTED, sortedList);

        Sort<String, PersistentList<String>> plistSort = new TerneryHeapSort<>();
        PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), new PersistentListSwapper<String>());
        assertEquals(LONG_SORTED, sortedP);

        Sort<String, TransientList<String>> tlistSort = new TerneryHeapSort<>();
        TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), new TransientListSwapper<String>());
        assertEquals(LONG_SORTED, sortedT);
    }

    @Test
    void testParentChild() {
        HeapSort<String, List<String>> heapSort = new HeapSort<>();
        for (int i = 0; i < 1000; i++) {
            int parent = i;
            int left = heapSort.leftChild(parent);
            int right = heapSort.rightChild(parent);
            assertTrue(parent < left);
            assertTrue(left < right);
            assertEquals(right, left + 1);
            assertEquals(parent, heapSort.parent(left));
            assertEquals(parent, heapSort.parent(right));
        }
        assertEquals(heapSort.parent(1), 0);
        assertEquals(heapSort.parent(2), 0);
        assertEquals(heapSort.parent(3), 1);
        assertEquals(heapSort.parent(4), 1);
        assertEquals(heapSort.parent(5), 2);
        assertEquals(heapSort.parent(6), 2);

        Set<Integer> set = IntStream.range(0, 1000).flatMap(i -> IntStream.of(heapSort.leftChild(i), heapSort.rightChild(i))).boxed().collect(Collectors.toSet());
        assertEquals(2000, set.size());
    }

    @Test
    void testHeapify() {
        List<List<String>> lists = List.of(new ArrayList<>(UNSORTED),
                new ArrayList<>(SORTED),
                new ArrayList<>(LONG_UNSORTED),
                new ArrayList<>(LONG_SORTED));
        HeapSort<String, List<String>> heapSort = new HeapSort<>();
        for (List<String> list : lists) {
            list = heapSort.heapify(list, list.size(), Comparator.naturalOrder(), new ListSwapper<>());
            for (int i = 0; i < list.size(); i++) {
                int parent = i;
                int left = heapSort.leftChild(parent);
                int right = heapSort.rightChild(parent);
                String parentStr = list.get(parent);
                if (left < list.size()) {
                    String leftStr = list.get(left);
                    assertTrue(parentStr.compareTo(leftStr) >= 0, "parent=" + parent + " left=" + left + " right=" + right + " parentStr=" + parentStr + " leftStr=" + leftStr + " list=" + list);
                }
                if (right < list.size()) {
                    String rightStr = list.get(right);
                    assertTrue(parentStr.compareTo(rightStr) >= 0, "parent=" + parent + " left=" + left + " right=" + right + " parentStr=" + parentStr + " rightStr=" + rightStr + " list=" + list);
                }
            }
        }
    }

    @Test
    void testSiftDown() {
        HeapSort<String, List<String>> heapSort = new HeapSort<>();
        List<String> orig = LONG_UNSORTED;
        for (int i = 0; i < orig.size(); i++) {
            List<String> list = new ArrayList<>(orig);
            heapSort.siftDown(list, 0, i, Comparator.naturalOrder(), new ListSwapper<>());
            for (int j = i + 1; j < list.size(); j++) {
                assertEquals(orig.get(j), list.get(j), "i=" + i + " j=" + j + " list=" + list);
            }
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
        Sort<Integer, List<Integer>> heapSort = new HeapSort<>();
        Sort<Integer, List<Integer>> ternaryHeapSort = new TerneryHeapSort<>();
        Sort<Integer, List<Integer>> quickSort = new QuickSort<>();
        Sort<Integer, List<Integer>> parallelQuickSort = new ParallelQuickSort<>();
        ListSwapper<Integer> swapper = new ListSwapper<>();
        for (int n = 0; n <= 8; n++) {
            List<List<Integer>> permutations = createAllPermutations(n);
            List<Integer> sorted = permutations.get(0);
            for (List<Integer> unsorted : permutations) {
                assertEquals(sorted, heapSort.sort(new ArrayList<>(unsorted), Comparator.naturalOrder(), swapper), () -> "unsorted=" + unsorted);
                assertEquals(sorted, ternaryHeapSort.sort(new ArrayList<>(unsorted), Comparator.naturalOrder(), swapper), () -> "unsorted=" + unsorted);
                assertEquals(sorted, quickSort.sort(new ArrayList<>(unsorted), Comparator.naturalOrder(), swapper), () -> "unsorted=" + unsorted);
                assertEquals(sorted, parallelQuickSort.sort(new ArrayList<>(unsorted), Comparator.naturalOrder(), swapper), () -> "unsorted=" + unsorted);
            }
        }
    }


    @Test
    public void testNumbers() {
        ListSwapper<String> swapper = new ListSwapper<>();
        Sort<String, List<String>> heapSort = new HeapSort<>();
        Sort<String, List<String>> ternaryHeapSort = new TerneryHeapSort<>();
        Sort<String, List<String>> quickSortMedian3 = new QuickSort<>(QuickSort.PivotStyle.MEDIAN3);
        Sort<String, List<String>> quickSortMedian5 = new QuickSort<>(QuickSort.PivotStyle.MEDIAN5);
        Sort<String, List<String>> quickSortFirst = new QuickSort<>(QuickSort.PivotStyle.FIRST);
        Sort<String, List<String>> quickSortMiddle = new QuickSort<>(QuickSort.PivotStyle.MIDDLE);
        Sort<String, List<String>> quickSortLast = new QuickSort<>(QuickSort.PivotStyle.LAST);
        Sort<String, List<String>> quickSortRandom = new QuickSort<>(QuickSort.PivotStyle.RANDOM);
        Sort<String, List<String>> parallelQuickSort = new ParallelQuickSort<>();
        Sort<String, List<String>> insertionSort = new InsertionSort<>();
        List<Sort<String, List<String>>> sorters = List.of(heapSort, ternaryHeapSort, quickSortMedian3, quickSortMedian5, quickSortFirst, quickSortMiddle, quickSortLast, quickSortRandom, parallelQuickSort, insertionSort);
        for (Sort<String, List<String>> sorter : sorters) {
            System.out.println("sort");
            assertEquals(NUMBERS_SORTED, sorter.sort(new ArrayList<>(NUMBERS_UNSORTED), Comparator.naturalOrder(), swapper));
        }
    }

}
