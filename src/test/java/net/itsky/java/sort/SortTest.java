package net.itsky.java.sort;

import clojure.lang.IFn;
import net.itsky.java.clojurecollections.PersistentList;
import net.itsky.java.clojurecollections.TransientList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SortTest {

    private static final List<String> UNSORTED = List.of("FL8YPS", "6DSNQQ", "U419AB", "X82QVN", "CU25H4", "ZZCRWY");
    private static final List<String> SORTED = new ArrayList<>(UNSORTED);;

    private static final List<String> LONG_UNSORTED = List.of("0H9Y41BL8WUQ", "7WPFZU00FHJQ", "D0OBE9SD6DPE", "AGBYG6MMCC9B", "E9SPYKWAITU9", "1VT6J5VSDMX9", "WSW1YJI40W8H", "378PPNCN6OW8", "JL819RD3CFTS", "C2YGBBGXXCBV", "KAGQZ7DY82WS", "C6N3W0RU4N5M", "XSJ9V3MN73TA", "8OPVKYG8JHYA", "UN0P5TKDEA05", "XP0KKYPUSTSF", "JS308V9PAJ2Z", "09NYKUUCTM5U", "AFID27K03CXA", "EPS16MESPLM7", "4FSSP0UJB6Q6", "FJG8FS1F2QXB", "L06RQWNPRFLJ", "TW9GAH01TK02", "2BSNA7IBHTRH", "H7IQKOMZ388X", "Y4B7SEEE5QMP", "C22P8R9UBPS7", "KVJE98AOILZ8", "LJLDM4AEA0AD", "1EVSCZGQ55MV", "E1LN8FRI5VNQ", "32NYE2DD9MBO", "OCKJPM3NKQ7P", "0IFOJ8OAY4H9", "5ERNL1PBOKOC", "76I2GY4KQOXP", "8EKLG4FQFU8K", "VP0J12B839EU", "94D3IKF9BITJ" );
    private static final List<String> LONG_SORTED = new ArrayList<>(LONG_UNSORTED);

    static {
        Collections.sort(SORTED);
        Collections.sort(LONG_SORTED);
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
                    list = swapper.swap(list, k, k-1);
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
        for (int i = n-1; i > 0; i--) {
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

}
