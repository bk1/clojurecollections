package net.itsky.java.sort;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QuickSortUtilsTest {

    private final QuickSortUtils<Integer, List<Integer>> first = new QuickSortUtils<>(QuickSortPivotStyle.FIRST);
    private final QuickSortUtils<Integer, List<Integer>> middle = new QuickSortUtils<>(QuickSortPivotStyle.MIDDLE);
    private final QuickSortUtils<Integer, List<Integer>> last = new QuickSortUtils<>(QuickSortPivotStyle.LAST);
    private final QuickSortUtils<Integer, List<Integer>> median3 = new QuickSortUtils<>(QuickSortPivotStyle.MEDIAN3);
    private final QuickSortUtils<Integer, List<Integer>> median5 = new QuickSortUtils<>(QuickSortPivotStyle.MEDIAN5);
    private final QuickSortUtils<Integer, List<Integer>> random = new QuickSortUtils<>(QuickSortPivotStyle.RANDOM);

    private final List<QuickSortUtils<Integer, List<Integer>>> quickSortUtilsList = List.of(first, middle, last, median3, median5, random);

    private final List<Integer> LONG_LIST = IntStream.range(0, 1000).map(x -> (x * 23 + 17) % 1000).boxed().toList();

    private final List<Integer> NON_UNIQUE_LIST = IntStream.range(0, 999).map(x -> (x * 99 + 17) % 999).boxed().toList();

    Comparator<Integer> comparator = Comparator.naturalOrder();

    @Test
    void testPivotInside() {
        for (int n = 1; n < 10; n++) {
            List<List<Integer>> permutations = TestUtils.createAllPermutations(n);
            for (List<Integer> list : permutations) {
                for (QuickSortUtils<Integer, List<Integer>> quickSortUtils : quickSortUtilsList) {
                    int pivotIdx = quickSortUtils.getPivot(list, 0, n, comparator);
                    assertTrue(0 <= pivotIdx);
                    assertTrue(pivotIdx < n);
                }
            }
        }
    }

    @Test
    void testPivotInsideLongList() {
        int n = LONG_LIST.size();
        for (int u = 1; u <= n; u++) {
            for (QuickSortUtils<Integer, List<Integer>> quickSortUtils : quickSortUtilsList) {
                int pivotIdx = quickSortUtils.getPivot(LONG_LIST, 0, u, comparator);
                assertTrue(0 <= pivotIdx);
                assertTrue(pivotIdx < n);
            }
        }
    }


    @Test
    void testFirst() {
        for (int n = 1; n < 10; n++) {
            List<List<Integer>> permutations = TestUtils.createAllPermutations(n);
            for (List<Integer> list : permutations) {
                for (int i = 0; i < n; i++) {
                    for (int j = i + 1; j < n; j++) {
                        int pivotIdx = first.getPivot(list, i, j, comparator);
                        assertEquals(i, pivotIdx);
                    }
                }
            }
        }
    }

    @Test
    void testFirstLongList() {
        int n = LONG_LIST.size();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int pivotIdx = first.getPivot(LONG_LIST, i, j, comparator);
                assertEquals(i, pivotIdx);
            }
        }
    }

    @Test
    void testLast() {
        for (int n = 1; n < 10; n++) {
            List<List<Integer>> permutations = TestUtils.createAllPermutations(n);
            for (List<Integer> list : permutations) {
                for (int i = 0; i < n; i++) {
                    for (int j = i + 1; j < n; j++) {
                        int pivotIdx = last.getPivot(list, i, j, comparator);
                        assertEquals(j - 1, pivotIdx, "i=" + i + " j=" + j);
                    }
                }
            }
        }
    }

    @Test
    void testLastLongList() {
        int n = LONG_LIST.size();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int pivotIdx = last.getPivot(LONG_LIST, i, j, comparator);
                assertEquals(j - 1, pivotIdx, "i=" + i + " j=" + j);
            }
        }
    }


    @Test
    void testMiddle() {
        for (int n = 1; n < 10; n++) {
            List<List<Integer>> permutations = TestUtils.createAllPermutations(n);
            for (List<Integer> list : permutations) {
                for (int i = 0; i < n; i++) {
                    for (int j = i + 1; j < n; j++) {
                        int pivotIdx = middle.getPivot(list, i, j, comparator);
                        assertEquals((i + j - 1) >> 1, pivotIdx, "i=" + i + " j=" + j);
                    }
                }
            }
        }
    }

    @Test
    void testMiddleLongList() {
        int n = LONG_LIST.size();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int pivotIdx = middle.getPivot(LONG_LIST, i, j, comparator);
                assertEquals((i + j - 1) >> 1, pivotIdx, "i=" + i + " j=" + j);
            }
        }
    }

    @Test
    void testMedian3LongList() {
        checkMedian3OnList(LONG_LIST);
    }


    @Test
    void testMedian3NonUniqueList() {
        checkMedian3OnList(NON_UNIQUE_LIST);
    }

    private void checkMedian3OnList(List<Integer> list) {
        int totalSize = list.size();
        for (int lower = 0; lower < totalSize; lower++) {
            for (int upper = lower + 20; upper < totalSize; upper++) {
                int pivotIdx = median3.getPivot(list, lower, upper, comparator);
                int pivotElement = list.get(pivotIdx);
                int firstIdx = first.getPivot(list, lower, upper, comparator);
                int firstElement = list.get(firstIdx);
                int middleIdx = middle.getPivot(list, lower, upper, comparator);
                int middleElement = list.get(middleIdx);
                int lastIdx = last.getPivot(list, lower, upper, comparator);
                int lastElement = list.get(lastIdx);
                TreeSet<Integer> idxSet = new TreeSet<>(List.of(firstIdx, middleIdx, lastIdx));
                assertTrue(idxSet.contains(pivotIdx));
                TreeSet<Integer> elementSet = new TreeSet<>(List.of(firstElement, middleElement, lastElement));
                assertTrue(elementSet.contains(pivotElement));
                assertTrue(elementSet.getFirst() <= pivotElement);
                assertTrue(pivotElement <= elementSet.getLast());
                if (elementSet.size() == 3) {
                    assertTrue(elementSet.getFirst() < pivotElement);
                    assertTrue(pivotElement < elementSet.getLast());
                }
            }
        }
    }

    @Test
    void testMedian5LongList() {
        checkMedian5OnList(LONG_LIST);
    }


    @Test
    void testMedian5NonUniqueList() {
        checkMedian5OnList(NON_UNIQUE_LIST);
    }


    private void checkMedian5OnList(List<Integer> list) {
        int totalSize = list.size();
        for (int lower = 0; lower < totalSize; lower++) {
            for (int upper = lower + 20; upper < totalSize; upper++) {
                int size = upper - lower;
                int pivotIdx = median5.getPivot(list, lower, upper, comparator);
                if (size < 100) {
                    assertEquals(median3.getPivot(list, lower, upper, comparator), pivotIdx);
                } else {
                    int pivotElement = list.get(pivotIdx);
                    int firstIdx = first.getPivot(list, lower, upper, comparator);
                    int firstElement = list.get(firstIdx);
                    int secondIdx = (3*lower+(upper-1)) / 4;
                    int secondElement = list.get(secondIdx);
                    int middleIdx = middle.getPivot(list, lower, upper, comparator);
                    int middleElement = list.get(middleIdx);
                    int fourthIdx = (lower+3*(upper-1))/4;
                    int fourthElement = list.get(fourthIdx);
                    int lastIdx = last.getPivot(list, lower, upper, comparator);
                    int lastElement = list.get(lastIdx);
                    TreeSet<Integer> idxSet = new TreeSet<>(List.of(firstIdx, secondIdx, middleIdx, fourthIdx, lastIdx));
                    assertTrue(idxSet.contains(pivotIdx));
                    TreeSet<Integer> elementSet = new TreeSet<>(List.of(firstElement, secondElement, middleElement, fourthElement, lastElement));
                    assertTrue(elementSet.contains(pivotElement));
                    assertTrue(elementSet.getFirst() <= pivotElement);
                    assertTrue(pivotElement <= elementSet.getLast());
                    if (elementSet.size() >= 3) {
                        assertTrue(elementSet.getFirst() < pivotElement);
                        assertTrue(pivotElement < elementSet.getLast());
                    }
                    if (elementSet.size() == 5) {
                        List<Integer> elementList = new ArrayList(elementSet);
                        for (int i = 0; i < 5; i++) {
                            int element = elementList.get(i);
                            if (i < 2) {
                                assertTrue(element <= pivotElement);
                            } else if (i == 2) {
                                assertEquals(element, pivotElement);
                            } else {
                                assertTrue(element >= pivotElement);
                            }
                        }
                    }
                }
            }
        }
    }
}





