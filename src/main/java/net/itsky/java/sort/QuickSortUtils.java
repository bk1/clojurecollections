package net.itsky.java.sort;

import java.security.SecureRandom;
import java.util.*;

public class QuickSortUtils<T, L extends List<T>> {

    private final QuickSortPivotStyle pivotStyle;

    private final InsertionSort<Integer, List<Integer>> sortElementWithIdx2 = new InsertionSort<>();

    private final ListSwapper<Integer> swapInt = new ListSwapper<>();
    private final Random random;

    private static final Random DUMMY_RANDOM = new Random();

    public QuickSortUtils(QuickSortPivotStyle pivotStyle) {
        this.pivotStyle = pivotStyle;
        if (pivotStyle == QuickSortPivotStyle.RANDOM) {
            random  = new SecureRandom();
        } else {
            random = DUMMY_RANDOM;
        }
    }

    private Comparator<Integer> idxComparator(final L list, Comparator<T> comparator) {
        return new Comparator<Integer>() {
            @Override
            public int compare(Integer i, Integer j) {
                return comparator.compare(list.get(i), list.get(j));
            }
        };
    }

      int getPivot(L list, int l, int u, Comparator<T> comparator) {
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
        if (result < l || result >= u) {
            throw new IllegalStateException("pivot element idx=" + result + " not in [" + l + ", " + (u-1) + "]");
        }
        return result;
    }
}
