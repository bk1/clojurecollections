// (C) Karl Brodowsky IT Sky Consulting GmbH 2019
// GNU-LGPL (see LICENSE in the root directory of the project)

package net.itsky.java.sort;

import java.util.Comparator;
import java.util.List;


public class FlashSort<T, L extends List<T>> implements SortMetricized<T, L> {

    private static final double DEFAULT_FACTOR = 0.42;
    private static final int DEFAULT_LIMIT = 10000;

    private double factor = DEFAULT_FACTOR;
    private int limit = DEFAULT_LIMIT;

    private QuickSort<T, L> quickSort = new QuickSort<>(QuickSortPivotStyle.MEDIAN5);

    private TerneryHeapSortSubList<T, L> terneryHeapSort = new TerneryHeapSortSubList<>();

    @Override
    public L sort(L list, Comparator<T> comparator, Swapper<T, L> swapper, Metric<T> metric) {
        return fsort(list, comparator, swapper, metric, 0, list.size());
    }

    public L fsort(L list, Comparator<T> comparator, Swapper<T, L> swapper, Metric<T> metric, int begin, int end) {

        int aSizeTotal = list.size();
        if (begin < 0 || end > aSizeTotal || begin > end) {
            throw new IllegalArgumentException("illegal limits. Required: 0 <= begin <= end <= aSize found: begin=" + begin + " end=" + end + " aSize=" + aSizeTotal);
        }
        int aSize = end - begin;
        if (aSize <= 1) {
            // nothing to sort
            return list;
        }

        // preparation: form classes
        // use fsortCalculateK for a purpose it has not been made for, but since it is identical with what is needed here it is correct
        int lSize = fsortCalculateK(aSize, factor, aSize) + 1;
        if (lSize < 2) {
            lSize = 2;
        }
        int[] l = new int[lSize];
        int maxIdx = begin;
        long mMin = metric.metric(list.get(begin));
        long mMax = mMin;
        for (int i = begin; i < end; i++) {
            long mi = metric.metric(list.get(i));
            if (mi < mMin) {
                mMin = mi;
            }
            if (mi > mMax) {
                maxIdx = i;
                mMax = mi;
            }
        }
        if (mMin == mMax) {
            // min and max are the same --> already sorted
            return terneryHeapSort.sort(list, comparator, swapper, begin, end);
        }
        double step = (lSize - 1) / (double) (mMax - mMin);

        // count the elements in each of the lSize classes
        for (int i = begin; i < end; i++) {
            int k = fsortCalculateK(metric.metric(list.get(i)) - mMin, step, lSize);
            l[k]++;
        }

        /* find the start positions for each of the classes */
        int[] ll = new int[lSize + 1];
        ll[0] = begin;
        l[0] += begin;
        ll[1] = l[0];

        for (int k = 1; k < lSize; k++) {
            l[k] += l[k - 1];
            ll[k + 1] = l[k];
        }

        list = swapper.swap(list, maxIdx, begin);

        /* do the permutation */
        int nMove = 0;
        int j = begin;
        int k = lSize - 1;
        while (nMove < aSize - 1) {
            while (j >= l[k]) {
                j++;
                k = fsortCalculateK(metric.metric(list.get(j)) - mMin, step, lSize);
            }
            /* now: j < l[k] */
            while (j != l[k]) {
                k = fsortCalculateK(metric.metric(list.get(j)) - mMin, step, lSize);
                int idx = l[k] - 1;
                list = swapper.swap(list, j, idx);
                l[k]--;
                nMove++;
            }
        }

        /* use qsort or hsort for each class */
        for (k = 0; k < lSize; k++) {
            int lower = ll[k];
            int upper = ll[k + 1];
            int classSize = upper - lower;
            if (classSize > limit) {
                list = fsort(list, comparator, swapper, metric, lower, upper);
            } else if (classSize > 1) {
                list = terneryHeapSort.sort(list, comparator, swapper, lower, upper);
            }
        }
        return list;
    }

    private int fsortCalculateK(long metricValue, double factor, int n) {
        // calculate prod as factor*value, add a small delta for rounding, force it into the closed interval [0,n-1]
        int result = (int) (factor * metricValue + 1e-9);
        if (result < 0) {
            return 0;
        } else if (result >= n) {
            return n - 1;
        } else {
            return result;
        }
    }

}
