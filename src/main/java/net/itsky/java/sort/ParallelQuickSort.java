package net.itsky.java.sort;

import java.security.SecureRandom;
import java.util.*;

public class ParallelQuickSort<T, L extends List<T>> implements Sort<T, L> {

    private final QuickSortPivotStyle pivotStyle;
    private final Random random;

    public ParallelQuickSort() {
        this(QuickSortPivotStyle.MEDIAN3);
    }

    public ParallelQuickSort(QuickSortPivotStyle pivotStyle) {
        this.pivotStyle = pivotStyle;
        if (pivotStyle == QuickSortPivotStyle.RANDOM) {
            random = new SecureRandom();
        } else {
            random = null;
        }
    }

    private record Pair(int lower, int upper) {
    }
    private final InsertionSort<Integer, List<Integer>> sortElementWithIdx2 = new InsertionSort<>();

    private final ListSwapper<Integer> swapInt = new ListSwapper<>();


    private Comparator<Integer> idxComparator(final L list, Comparator<T> comaparator) {
        return new Comparator<Integer>() {
            @Override
            public int compare(Integer i, Integer j) {
                return comaparator.compare(list.get(i), list.get(j));
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
                    List<Integer> indices = Arrays.asList(l, l + (len - 1) / 2, u - 1);
                    Collections.sort(indices, idxComparator(list, comparator));
                    result = indices.get(1);
                }
                break;
            case MEDIAN5:
                if (len < 20) {
                    result = u - 1;
                } else if (len < 100) {
                    List<Integer> indices = Arrays.asList(l, l + (len - 1) / 2, u - 1);
                    Collections.sort(indices, idxComparator(list, comparator));
                    result = indices.get(1);
                } else {
                    List<Integer> indices = Arrays.asList(l, l + (len - 1) / 4, l + (len - 1) / 2, l + 3 * (len - 1) / 4, u - 1);
                    Collections.sort(indices, idxComparator(list, comparator));
                    result = indices.get(2);
                }
                break;
            default:
                throw new IllegalStateException("unsupported value for pivotStyle=" + pivotStyle);
        }
        ;
        return result;
    }

    private static class ListHolder<T, L extends List<T>> {

        private L list;
        private final Swapper<T, L> swapper;

        public ListHolder(L list, Swapper<T, L> swapper) {
            this.list = list;
            this.swapper = swapper;
        }

        public synchronized L swap(int i, int j) {
            this.list = swapper.swap(list, i, j);
            return list;
        }

        public L list() {
            return list;
        }
    }

    @Override
    public L sort(L listParam, Comparator<T> comparator, Swapper<T, L> swapper) {
        L list = listParam;
        ListHolder<T, L> listHolder = new ListHolder<>(list, swapper);
        int n = list.size();
        Stack<Pair> tasks = new Stack<>();
        InsertionSort<T, L> insertionSort = new InsertionSort<>();
        tasks.push(new Pair(0, n));
        while (!tasks.isEmpty()) {
            Pair limits = tasks.pop();
            int l = limits.lower;
            int u = limits.upper;
            if (u - l <= 1) {
                // one element -> already sorted
                continue;
            }
            int idx = getPivot(list, l, u, comparator);
            T t = list.get(idx);
            if (idx != u - 1) {
                list = listHolder.swap(idx, u - 1);
            }
            int li = l;
            var ui = u - 2;
            while (true) {
                while (li < ui) {
                    int cmp = comparator.compare(list.get(li), t);
                    if (cmp > 0 || cmp == 0 && li > idx) {
                        // list[li] > t || list[li]==t && li>idx
                        break;
                    }
                    li++;
                }
                while (li < ui) {
                    int cmp =  comparator.compare(t, list.get(ui));
                    if (cmp > 0 || cmp == 0 && ui < idx) {
                        // list[ui] < t || list[ui]==t && ui<idx
                        break;
                    }
                    ui--;
                }
                if (li >= ui) {
                    break;
                }
                list = listHolder.swap(li, ui);
                li++;
                ui--;
            }
            int split;
            if (ui < li) {
                // ui < li: split = li
                split = li;
                list = listHolder.swap(split, u - 1);
            } else {
                // ui == li
                if (comparator.compare(list.get(ui), t) > 0) {
                    split = ui;
                    list = listHolder.swap(split, u - 1);
                } else {
                    split = ui + 1;
                    if (split != u - 1) {
                        list = listHolder.swap(split, u - 1);
                    }
                }
            }
            boolean lowerAtLeastTwo = l < split -1;
            boolean upperAtLastTwo = split+2 < u;
            Pair lowerPair = null;
            Pair upperPair = null;
            if (lowerAtLeastTwo) {
                lowerPair = new Pair(l, split);
            }
            if (upperAtLastTwo) {
                upperPair = new Pair(split + 1, u);
            }
            if (upperAtLastTwo || lowerAtLeastTwo) {
                if (! lowerAtLeastTwo) {
                    tasks.push(upperPair);
                } else if (! upperAtLastTwo) {
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
        }
        return listHolder.list();
    }
}
