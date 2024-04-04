// (C) Karl Brodowsky IT Sky Consulting GmbH 2019
// GNU-LGPL (see LICENSE in the root directory of the project)

package net.itsky.java.sort;

import java.util.Arrays;


public class FlashSortArray {
    private static int fsortCalculateK(long metricValue, double factor, int lsize) {
        // calculate prod as factor*value, add a small delta for rounding, force it into the closed interval [0,lsize-1] using min and max
        int result = (int) (factor * metricValue + 1e-9);
        if (result < 0) {
            return 0;
        } else if (result > lsize -1) {
            return lsize - 1;
        } else {
            return result;
        }
    }

    public static void fsort(long[] array) {
        fsort(array, 0.42);
    }

    public static void fsort(long[] array,
                             double factor) {
        int aSize = array.length;
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
        int maxIdx = 0;
        long aMin = array[0];
        long aMax = array[0];
        for (int i = 0; i < aSize; i++) {
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
        for (int i = 0; i < aSize; i++) {
            int k = fsortCalculateK(array[i] - aMin, step, lSize);
            l[k]++;
        }

        /* find the start positions for each of the classes */
        int[] ll = new int[lSize + 1];
        ll[0] = 0;
        ll[1] = l[0];

        for (int k = 1; k < lSize; k++) {
            l[k] += l[k - 1];
            ll[k + 1] = l[k];
        }

        array[maxIdx] = array[0];
        array[0] = aMax;

        /* do the permutation */
        int nMove = 0;
        int j = 0;
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
            int classSize = ll[k + 1] - ll[k];
            if (classSize > 1) {
                Arrays.sort(array, ll[k], ll[k+1]);
            }
        }
        return;
    }

}
