package net.itsky.java.sort;

import java.security.SecureRandom;
import java.util.*;

public class QuickSort<T, L extends List<T>> implements Sort<T, L>, SortSubList<T, L> {

    private final QuickSortPivotStyle pivotStyle;
    private final Random random;

    public QuickSort() {
        this(QuickSortPivotStyle.MEDIAN3);
    }

    public QuickSort(QuickSortPivotStyle pivotStyle) {
        this.pivotStyle = pivotStyle;
        if (pivotStyle == QuickSortPivotStyle.RANDOM) {
            random = new SecureRandom();
        } else {
            random = null;
        }
    }

    private final InsertionSort<Integer, List<Integer>> sortElementWithIdx2 = new InsertionSort<>();

    private final ListSwapper<Integer> swapInt = new ListSwapper<>();


    private Comparator<Integer> idxComparator(final L list, Comparator<T> comparator) {
        return new Comparator<Integer>() {
            @Override
            public int compare(Integer i, Integer j) {
                return comparator.compare(list.get(i), list.get(j));
            }
        };
    }

    private int getPivot(L list, int l, int u, Comparator<T> comparator) {
        int len = u - l;
        final int result;
        switch (pivotStyle) {
            case FIRST:
                result = l;
                break;
            case LAST:
                result = u - 1;
                break;
            case MIDDLE:
                result = l + (u - 1 - l) / 2;
                break;
            case RANDOM:
                result = random.nextInt(u - l) + l;
                break;
            case MEDIAN3:
                if (len < 20) {
                    result = u - 1;
                } else {
                    List<Integer> indices = sortElementWithIdx2.sort(Arrays.asList(l, l + (len - 1) / 2, u - 1), idxComparator(list, comparator), swapInt);
                    result = indices.get(1);
                }
                break;
            case MEDIAN5:
                if (len < 20) {
                    result = u - 1;
                } else if (len < 100) {
                    List<Integer> indices = sortElementWithIdx2.sort(Arrays.asList(l, l + (len - 1) / 2, u - 1), idxComparator(list, comparator), swapInt);
                    result = indices.get(1);
                } else {
                    List<Integer> indices = sortElementWithIdx2.sort(Arrays.asList(l, l + (len - 1) / 4, l + (len - 1) / 2, l + 3 * (len - 1) / 4, u - 1), idxComparator(list, comparator), swapInt);
                    result = indices.get(2);
                }
                break;
            default:
                throw new IllegalStateException("unsupported value for pivotStyle=" + pivotStyle);
        }
        ;
        return result;
    }


    @Override
    public L sort(L list, Comparator<T> comparator, Swapper<T, L> swapper) {
        return sort(list, comparator, swapper, 0, list.size());
    }

    @Override
    public L sort(L list, Comparator<T> comparator, Swapper<T, L> swapper, int begin, int end) {
        checkBoundaries(list, begin, end);
        int aSize = end - begin;
        if (aSize <= 1) {
            // nothing to sort
            return list;
        }
        long tTotal = 0;
        long tInsertionSort = 0;
        long tGetPivot = 0;
        long tLowerSkip = 0;
        long tUpperSkip = 0;
        long tSwap = 0;
        long tSplit = 0;
        long tLowerSkipCompare = 0;
        long tUpperSkipCompare = 0;
        long t0, tTotal0, t00;
        tTotal0 = System.currentTimeMillis();
        Stack<Pair> tasks = new Stack<>();
        InsertionSort<T, L> insertionSort = new InsertionSort<>();
        tasks.push(new Pair(begin, end));
        while (!tasks.isEmpty()) {
            Pair limits = tasks.pop();
            int l = limits.lower();
            int u = limits.upper();
            if (u - l <= 1) {
                // one element -> sublist already sorted
                continue;
            }
            if (u - l <= 8) {
                t0 = System.currentTimeMillis();
                list = insertionSort.sort(list, comparator, swapper, l, u);
                tInsertionSort += System.currentTimeMillis() - t0;
                continue;
            }
            t0 = System.currentTimeMillis();
            int idx = getPivot(list, l, u, comparator);
            tGetPivot += System.currentTimeMillis() - t0;
            T pivotElement = list.get(idx);
            if (idx != u - 1) {
                list = swapper.swap(list, idx, u - 1);
            }
            int li = l;
            var ui = u - 2;
            while (true) {
                t0 = System.currentTimeMillis();
                while (li < ui) {
                    int cmp = comparator.compare(list.get(li), pivotElement);
                    tLowerSkipCompare++;
                    if (cmp > 0 || cmp == 0 && li > idx) {
                        // list[li] > pivotElement || list[li] == pivotElement && li > idx
                        break;
                    }
                    li++;
                }
                tLowerSkip += System.currentTimeMillis() - t0;
                t0 = System.currentTimeMillis();
                while (li < ui) {
                    int cmp = comparator.compare(pivotElement, list.get(ui));
                    tUpperSkipCompare ++;
                    if (cmp > 0 || cmp == 0 && ui < idx) {
                        // list[ui] < pivotElement || list[ui] == pivotElement && ui < idx
                        break;
                    }
                    ui--;
                }
                tUpperSkip += System.currentTimeMillis() - t0;
                if (li >= ui) {
                    break;
                }
                t0 = System.currentTimeMillis();
                list = swapper.swap(list, li, ui);
                tSwap += System.currentTimeMillis() - t0;
                li++;
                ui--;
            }
            t0 = System.currentTimeMillis();
            int split;
            if (ui < li) {
                // ui < li: split = li
                split = li;
                list = swapper.swap(list, split, u - 1);
            } else {
                // ui == li
                if (comparator.compare(list.get(ui), pivotElement) > 0) {
                    split = ui;
                    list = swapper.swap(list, split, u - 1);
                } else {
                    split = ui + 1;
                    if (split != u - 1) {
                        list = swapper.swap(list, split, u - 1);
                    }
                }
            }
            boolean lowerAtLeastTwo = l < split - 1;
            boolean upperAtLastTwo = split + 2 < u;
            Pair lowerPair = null;
            Pair upperPair = null;
            if (lowerAtLeastTwo) {
                lowerPair = new Pair(l, split);
            }
            if (upperAtLastTwo) {
                upperPair = new Pair(split + 1, u);
            }
            if (upperAtLastTwo || lowerAtLeastTwo) {
                if (!lowerAtLeastTwo) {
                    tasks.push(upperPair);
                } else if (!upperAtLastTwo) {
                    tasks.push(lowerPair);
                } else if (split - l > u - split - 1) {
                    // lowerPair is larger than upperPair
                    tasks.push(lowerPair);
                    tasks.push(upperPair);
                } else {
                    // upperPair is larger than lowerPair
                    tasks.push(upperPair);
                    tasks.push(lowerPair);
                }
            }
            tSplit += System.currentTimeMillis() - t0;
        }
        tTotal = System.currentTimeMillis() - tTotal0;
        if (tTotal >= 1000) {
            System.out.println("tTotal=" + tTotal + " tInsertionSort=" + tInsertionSort + " tGetPivot=" + tGetPivot + " tLowerSkip=" + tLowerSkip + " tLowerSkipCompare=" + tLowerSkipCompare + " tUpperSkip=" + tUpperSkip + " tUppserSkipCompare=" + tUpperSkipCompare + " tSwap=" + tSwap + " tSplit=" + tSplit
                    + " sum=" + (tInsertionSort + tGetPivot + tLowerSkip + tUpperSkip + tSwap + tSplit));
        }
        return list;
    }
}
