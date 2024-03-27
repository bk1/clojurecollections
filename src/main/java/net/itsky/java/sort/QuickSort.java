package net.itsky.java.sort;

import java.security.SecureRandom;
import java.util.*;

public class QuickSort<T, L extends List<T>> implements Sort<T, L> {

    public static enum PivotStyle {
        FIRST,
        MIDDLE,
        LAST,
        MEDIAN3,
        MEDIAN5,
        RANDOM
    }

    private final PivotStyle pivotStyle;
    private final Random random;

    public QuickSort() {
        this(PivotStyle.MEDIAN5);
    }

    public QuickSort(PivotStyle pivotStyle) {
        this.pivotStyle = pivotStyle;
        if (pivotStyle == PivotStyle.RANDOM) {
            random = new SecureRandom();
        } else {
            random = null;
        }
    }

    private static record Pair(int lower, int upper) {
    }

    private static record ElementWitIdx<U>(int idx, U element) {
    }

    private final InsertionSort<ElementWitIdx<T>, List<ElementWitIdx<T>>> sortElementWithIdx = new InsertionSort<>();
    private final InsertionSort<Integer, List<Integer>> sortElementWithIdx2 = new InsertionSort<>();


    private final ListSwapper<ElementWitIdx<T>> swapElementWithIdx = new ListSwapper<>();
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
                if (len < 12) {
                    result = u - 1;
                } else {
                    List<Integer> indices = sortElementWithIdx2.sort(Arrays.asList(l, l + (len - 1) / 2, u - 1), idxComparator(list, comparator), swapInt);
                    result = indices.get(1);
                }
                break;
            case MEDIAN5:
                if (len < 12) {
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
    public L sort(L listParam, Comparator<T> comparator, Swapper<T, L> swapper) {
        L list = listParam;
        int n = list.size();
        long countSwaps = 0;
        long countCmp = 0;
        long countCmpA = 0;
        long countCmpB = 0;
        long countCmpC = 0;
        long countCmpD = 0;
        long countCmpE = 0;
        long countCmpF = 0;
        long countCmpG = 0;
        long countCmpH = 0;
        long countSplitCaseA =0;
        long countSplitCaseB =0;
        long countSplitCaseC =0;
        double splitPosL = 0;
        double splitPosU = 0;
        long splitCount = 0;
        Stack<Pair> tasks = new Stack<>();
        tasks.push(new Pair(0, n));
        while (!tasks.isEmpty()) {
            Pair limits = tasks.pop();
            int l = limits.lower;
            int u = limits.upper;
            if (u - l <= 1) {
                // one element -> already sorted
                break;
            }
            int idx = getPivot(list, l, u, comparator);
            T t = list.get(idx);
            /*
            if (u - l < 5) {
                t = list.get(u - 1);
            } else {
                int idx1 = l;
                int idx2 = (l + u - 1) / 2;
                int idx3 = u - 1;
                T t1 = list.get(idx1);
                T t2 = list.get(idx2);
                T t3 = list.get(idx3);
                List<ElementWitIdx<T>> elementWitIdxList = sortElementWithIdx.sort(Arrays.asList(new ElementWitIdx(idx1, t1), new ElementWitIdx(idx2, t2), new ElementWitIdx(idx3, t3)), new Comparator<ElementWitIdx<T>>() {
                    @Override
                    public int compare(ElementWitIdx<T> o1, ElementWitIdx<T> o2) {
                        return comparator.compare(o1.element(), o2.element());
                    }
                }, swapElementWithIdx);
                ElementWitIdx<T> elementWitIdx = elementWitIdxList.get(1);
                t = elementWitIdx.element();
                int idx = elementWitIdx.idx();
                if (idx != u - 1) {
                    list = swapper.swap(list, idx, u - 1);
                }
            }
             */
            if (idx != u - 1) {
                list = swapper.swap(list, idx, u - 1);
            }
            int li = l;
            var ui = u - 2;
            while (true) {
                while (li < ui) {
                    countCmp++;
                    countCmpA++;
                    countCmpE++;
                    int cmp = comparator.compare(list.get(li), t);
                    if (cmp > 0 || cmp == 0 && li > idx) {
                        // list[li] > t || list[li]==t && li>idx
                        break;
                    }
                    li++;
                    countCmp++;
                    countCmpA++;
                    countCmpF++;
                }
                while (li < ui) {
                    countCmp++;
                    countCmpB++;
                    countCmpG++;
                    int cmp =  comparator.compare(t, list.get(ui));
                    if (cmp > 0 || cmp == 0 && ui < idx) {
                        // list[ui] < t || list[ui]==t && ui<idx
                        break;
                    }
                    ui--;
                    countCmp++;
                    countCmpB++;
                    countCmpH++;
                }
                if (li >= ui) {
                    break;
                }
                list = swapper.swap(list, li, ui);
                countSwaps++;
                li++;
                ui--;
            }
            int split;
            if (ui < li) {
                // ui < li: split = li
                countSplitCaseA++;
                split = li;
                list = swapper.swap(list, split, u - 1);
                countSwaps++;
            } else {
                // ui == li
                countCmp++;
                countCmpD++;
                if (comparator.compare(list.get(ui), t) > 0) {
                    countSplitCaseB++;
                    split = ui;
                    list = swapper.swap(list, split, u - 1);
                    countSwaps++;
                } else {
                    countSplitCaseC++;
                    split = ui + 1;
                    if (split != u - 1) {
                        list = swapper.swap(list, split, u - 1);
                        countSwaps++;
                    }
                }
            }
            if (l < split - 1) {
                tasks.push(new Pair(l, split));
            }
            if (split + 2 < u) {
                tasks.push(new Pair(split + 1, u));
            }
            if (u > l + 2) {
                splitCount += (u - l);
                splitPosL += (double) (split - l);
                splitPosU += (double) (u - split);
            }
        }
        if (n > 0 && splitCount > 0 && countCmpE > 0 && countCmpG > 0) {
            System.out.println("quicksort n=" + list.size() + " swaps=" + countSwaps + " cmps=" + countCmp + " cmpsA=" + countCmpA + " cmpsB=" + countCmpB + " cmpsC=" + countCmpC + " cmpsD=" + countCmpD
                    + " groupA:cmpsE=" + countCmpE + " cmpsF=" + countCmpF + " groupB:cmpsG=" + countCmpG + " cmpsH=" + countCmpH + " cmp/A=" + (countCmpA / countCmpE) + " cmp/B=" + countCmpB / countCmpG
                    + " splitCount=" + splitCount + " splitPosAvg=(" + (splitPosL / splitCount) + ", " + (splitPosU / splitCount) + ")"
                    + " splitCaseA=" + countSplitCaseA + " splitCaseB=" + countSplitCaseB + " splitCaseC=" + countSplitCaseC);
        }
        return list;
    }
}
