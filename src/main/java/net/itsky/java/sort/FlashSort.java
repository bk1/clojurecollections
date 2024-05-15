// (C) Karl Brodowsky IT Sky Consulting GmbH 2019
// GNU-LGPL (see LICENSE in the root directory of the project)

package net.itsky.java.sort;

import java.util.Comparator;
import java.util.List;

import static java.lang.System.currentTimeMillis;


public class FlashSort<T, L extends List<T>> implements SortMetricized<T, L> {

    private static final double DEFAULT_FACTOR = 0.42;
    private static final int DEFAULT_LIMIT = 10000;

    private static final long HALF_MIN_VALUE = Long.MIN_VALUE>>1;
    private static final long HALF_MAX_VALUE = Long.MAX_VALUE>>1;

    private double factor = DEFAULT_FACTOR;
    private int limit = DEFAULT_LIMIT;

    private long tm;
    private long th;

    private QuickSort<T, L> quickSort = new QuickSort<>(QuickSortPivotStyle.MEDIAN5);

    private TerneryHeapSortSubList<T, L> terneryHeapSort = new TerneryHeapSortSubList<>();

    @Override
    public L sort(L list, Comparator<T> comparator, Swapper<T, L> swapper, Metric<T> metric) {
        return fsort(list, comparator, swapper, metric, 0, list.size());
    }

    public L fsort(L list, Comparator<T> comparator, Swapper<T, L> swapper, Metric<T> metric, int begin, int end) {
        boolean sortWholeList = begin == 0 && end == list.size();
        if (sortWholeList) {
            tm = 0;
            th = 0;
            System.out.println("\n---------------------------------------");
        }
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
        long t0 = currentTimeMillis();
        long mMin = metric.metric(list.get(begin));
        tm += currentTimeMillis()-t0;
        long mMax = mMin;
        for (int i = begin; i < end; i++) {
            t0 = currentTimeMillis();
            long mi = metric.metric(list.get(i));
            tm += currentTimeMillis()-t0;
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
            t0 = currentTimeMillis();
            list = terneryHeapSort.sort(list, comparator, swapper, begin, end);
            th += currentTimeMillis()-t0;
            return list;
        }
        final double step = (lSize - 1) / (double) (mMax - mMin);

        // count the elements in each of the lSize classes
        for (int i = begin; i < end; i++) {
            t0 = currentTimeMillis();
            long metricAtI = metric.metric(list.get(i));
            tm += currentTimeMillis() - t0;
            int k = fsortCalculateK(metricAtI - mMin, step, lSize);
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
                t0= currentTimeMillis();
                long metricAtJ = metric.metric(list.get(j));
                tm += currentTimeMillis()-t0;
                k = fsortCalculateK(metricAtJ - mMin, step, lSize);
            }
            /* now: j < l[k] */
            while (j != l[k]) {
                t0= currentTimeMillis();
                long metricAtJ = metric.metric(list.get(j));
                tm += currentTimeMillis()-t0;
                k = fsortCalculateK(metricAtJ - mMin, step, lSize);
                int idx = l[k] - 1;
                list = swapper.swap(list, j, idx);
                l[k]--;
                nMove++;
            }
        }
        int minClassSize = Integer.MAX_VALUE;
        int maxClassSize = 0;
        int classSizeCount = 0;
        int[] classCategories = new int[8];
        T maxClassMin = null;
        T maxClassMax = null;
        int classesBelowLimit = 0;
        int classesAboveLimit = 0;
        /* use fsort or qsort or hsort for each class */
        for (k = 0; k < lSize; k++) {
            int lower = ll[k];
            int upper = ll[k + 1];
            int classSize = upper - lower;
            if (classSize > 0 && sortWholeList) {
                classSizeCount++;
                int log = (int)Math.log10(classSize);
                classCategories[log]++;
            }
            if (classSize < minClassSize) {
                minClassSize = classSize;
            }
            if (classSize > limit) {
                list = fsort(list, comparator, swapper, metric, lower, upper);
                classesAboveLimit++;
            } else if (classSize > 1) {
                t0 = currentTimeMillis();
                list = terneryHeapSort.sort(list, comparator, swapper, lower, upper);
                th += currentTimeMillis()-t0;
                classesBelowLimit++;
            }
            if (classSize > maxClassSize) {
                maxClassSize = classSize;
                maxClassMin = list.get(lower);
                maxClassMax = list.get(upper-1);
            }
            if (classSize > 100000 && sortWholeList) {
                T classMin = list.get(lower);
                T classMax = list.get(upper-1);
                long mmin = metric.metric(classMin);
                long mmax = metric.metric(classMax);
                int kmin = fsortCalculateK(mmin - mMin, step, lSize);
                int kmax = fsortCalculateK(mmax - mMin, step, lSize);
                double delta = (mmax - mmin) * step;
                if (! classMin.equals(classMax)) {
                    System.out.println("classSize=" + classSize + " delta=" + delta + " kmin=" + kmin + " kmax=" + kmax + " mmin=" + mmin + " mmax=" +mmax + " minT=" + classMin + " maxT=" + classMax);
                }
            }
        }
        if (sortWholeList) {
            double avgClassSize = 0;
            if (sortWholeList && lSize > 0 && classSizeCount > 0) {
                avgClassSize = (end - begin) / (double) classSizeCount;
            }
            System.out.println("tm=" + tm + " th=" + th + " minCS=" + minClassSize + " maxCS=" + maxClassSize + " avgCS=" + avgClassSize + " nCS=" + classSizeCount + " nCSbl=" + classesBelowLimit + " nCSal=" + classesAboveLimit + " mMin="+mMin + " mMax=" + mMax + " step=" + step);
            for (int i = 0; i <classCategories.length; i++) {
                System.out.print(" cc[" + i + "]=" + classCategories[i]);
            }
            System.out.println();
            System.out.println("mMin=" + mMin + " mMax=" + mMax + " mMin=" + mMin + " mMax=" + mMax + " step=" + step + " lSize=" + lSize + " aSize=" + aSize);
            System.out.println("m=" + metric.metric(maxClassMin) + " maxClassMin=" + maxClassMin);
            System.out.println("m=" + metric.metric(maxClassMax) + " maxClassMax=" + maxClassMax);
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
