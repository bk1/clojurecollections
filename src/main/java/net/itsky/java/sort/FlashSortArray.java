// (C) Karl Brodowsky IT Sky Consulting GmbH 2019
// GNU-LGPL (see LICENSE in the root directory of the project)

package net.itsky.java.sort;

import java.util.Arrays;


public class FlashSortArray {

    private static final double DEFAULT_FACTOR = 0.42;
    private static final int DEFAULT_LIMIT = 10000;


    private static int fsortCalculateK(long metricValue, double factor, int n) {
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

    public static void fsort(long[] array) {
        fsort(array, DEFAULT_FACTOR, DEFAULT_LIMIT, 0, array.length);
    }

    public static void fsort(long[] array,
                             double factor) {
        fsort(array, factor, DEFAULT_LIMIT, 0, array.length);
    }


    public static void fsort(long[] array,
                             double factor,
                             int limit) {
        fsort(array, factor, limit, 0, array.length);
    }
    public static void fsort(long[] array,
                             int begin,
                             int end) {
        fsort(array, DEFAULT_FACTOR, DEFAULT_LIMIT, begin, end);
    }

    public static void fsort(long[] array,
                             double factor,
                             int limit,
                             int begin,
                             int end) {
        int aSizeTotal = array.length;
        if (begin < 0 || end > aSizeTotal || begin > end) {
            throw new IllegalArgumentException("illegal limits. Required: 0 <= begin <= end <= aSize found: begin=" + begin + " end=" + end + " aSize=" + aSizeTotal);
        }
        int aSize = end - begin;
        if (aSize <= 1) {
            // nothing to sort
            return;
        }

        // preparation: form classes
        // use fsortCalculateK for a purpose it has not been made for, but since it is identical with what is needed here it is correct
        int lSize = fsortCalculateK(aSize, factor, aSize) + 1;
        if (lSize < 2) {
            lSize = 2;
        }
        int[] l = new int[lSize];
        int maxIdx = begin;
        long aMin = array[begin];
        long aMax = array[begin];
        for (int i = begin; i < end; i++) {
            long ai = array[i];
            if (ai < aMin) {
                aMin = ai;
            }
            if (ai > aMax) {
                maxIdx = i;
                aMax = ai;
            }
        }
        if (aMin == aMax) {
            // min and max are the same --> already sorted
            return;
        }
        double step = (lSize - 1) / (double) (aMax - aMin);

        // count the elements in each of the lSize classes
        for (int i = begin; i < end; i++) {
            int k = fsortCalculateK(array[i] - aMin, step, lSize);
            l[k]++;
        }

        //statistics(aSize, lSize, l, step);

        /* find the start positions for each of the classes */
        int[] ll = new int[lSize + 1];
        ll[0] = begin;
        l[0] += begin;
        ll[1] = l[0];

        for (int k = 1; k < lSize; k++) {
            l[k] += l[k - 1];
            ll[k + 1] = l[k];
        }

        array[maxIdx] = array[begin];
        array[begin] = aMax;

        /* do the permutation */
        int nMove = 0;
        int j = begin;
        int k = lSize - 1;
        while (nMove < aSize - 1) {
            while (j >= l[k]) {
                j++;
                k = fsortCalculateK(array[j] - aMin, step, lSize);
            }
            /* now: j < l[k] */
            long x = array[j];
            while (j != l[k]) {
                k = fsortCalculateK(x - aMin, step, lSize);
                int idx = l[k] - 1;
                long y = array[idx];
                array[idx] = x;
                x = y;
                l[k]--;
                nMove++;
            }
        }

        /* use qsort or hsort for each class */
        for (k = 0; k < lSize; k++) {
            int lower = ll[k];
            int upper = ll[k+1];
            int classSize = upper - lower;
            if (classSize > limit) {
                fsort(array, lower, upper);
            } else if (classSize > 2) {
                Arrays.sort(array, lower, upper);
            } else if (classSize == 2 && array[lower] > array[upper -1]) {
                long x = array[lower];
                array[lower] = array[upper-1];
                array[upper-1] = x;
            }
        }
    }

    private static void statistics(int aSize, int lSize, int[] l, double step) {
        int maxL = 0;
        int minL = Integer.MAX_VALUE;
        long sumL = aSize;
        long sumLL = 0;
        for (int k = 0; k < lSize; k++) {
            int lk = l[k];
            if (lk > maxL) {
                maxL = lk;
            }
            if (lk < minL) {
                minL = lk;
            }
            sumLL += lk*lk;
        }
        double avg = sumL / (double) lSize;
        double sdev = Math.sqrt((sumLL - aSize * avg) / lSize);
        System.out.println("aSize=" + aSize + " lSize=" + lSize + " minL=" + minL + " maxL=" + maxL + " step=" + step + " avg=" +avg + " sdev=" + sdev);
    }

}
