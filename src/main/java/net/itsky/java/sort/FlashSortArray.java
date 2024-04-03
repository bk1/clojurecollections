// (C) Karl Brodowsky IT Sky Consulting GmbH 2019
// GNU-LGPL (see LICENSE in the root directory of the project)

package net.itsky.java.sort;

import java.util.Arrays;


public class FlashSortArray {
    private static int fsortCalculateK(long metricValue, double factor, int lsize) {
        // calculate prod as factor*value, add a small delta for rounding, force it into the closed interval [0,lsize-1] using min and max
        double prodUnlimited = factor * metricValue + 1e-9;
        double prodNonNegative = Math.max(0, prodUnlimited);
        double prodFloat = Math.min(prodNonNegative, lsize - 1);
        int result = (int) prodFloat;
        if (prodFloat < 0 || Math.abs(prodFloat - result) > 1.5) {
            // this should not happen, because prodFloat was prepared in a way to avoid it...
            // TODO prove & test with extreme values
            throw new IllegalStateException("Overflow/Underflow of Int when casting from " + prodFloat);
        }
        return result;
    }

    public static void fsort(long[] array) {
        fsort(array, 0.42);
    }

    public static void fsort(long[] array,
                             double factor) {
        int nmemb = array.length;
        if (nmemb <= 1) {
            // nothing to sort
            return;
        }

        /* preparation: form classes */
        /* use calculate_k for a purpose it has not been made for, but since it is identical with what is needed here it is correct */
        int lsize = fsortCalculateK(nmemb, factor, nmemb) + 1;
        if (lsize < 2) {
            lsize = 2;
        }
        int[] l = new int[lsize];
        //size_t idx_min = 0;
        int amin = 0;
        int amax = 0;
        for (int i = 0; i < nmemb; i++) {
            if (array[i] < array[amin]) {
                // idx_min = i;
                amin = i;
            }
            if (array[i] > array[amax]) {
                amax = i;
            }
        }
        if (array[amin] == array[amax]) {
            /* min and max are the same --> already sorted */
            return;
        }
        long amin_metric = array[amin];
        long amax_metric = array[amax];
        double step = (lsize - 1) / (double) (amax_metric - amin_metric);

        /* count the elements in each of the lsize classes */
        for (int i = 0; i < nmemb; i++) {
            int k = fsortCalculateK(array[i] - amin_metric, step, lsize);
            l[k]++;
        }

        /* find the start positions for each of the classes */
        int[] ll = new int[lsize + 1];
        ll[0] = 0;
        ll[1] = l[0];

        for (int k = 1; k < lsize; k++) {
            l[k] += l[k - 1];
            ll[k + 1] = l[k];
        }

        {
            long x = array[0];
            array[0] = array[amax];
            array[amax] = x;
        }

        /* do the permutation */
        int nmove = 0;
        int j = 0;
        int k = lsize - 1;
        while (nmove < nmemb - 1) {
            while (j >= l[k]) {
                j++;
                k = fsortCalculateK(array[j] - amin_metric, step, lsize);
            }
            /* now: j < l[k] */
            long x = array[j];
            /* flash_ptr takes element a[j] such that j > l[k] */
            while (j != l[k]) {
                k = fsortCalculateK(x - amin_metric, step, lsize);
                long y = array[l[k] - 1];
                array[l[k] - 1] = x;
                x = y;
                l[k]--;
                nmove++;
            }
        }

        /* use qsort or hsort for each class */
        for (k = 0; k < lsize; k++) {
            int n = ll[k + 1] - ll[k];
            if (n > 1 && ll[k + 1] < ll[k] || n > nmemb) {
                throw new IllegalStateException(String.format("wrong order: k=%ld lsize=%ld nmemb=%ld n=%ld ll[k]=%ld ll[k+1]=%ld\n", k, lsize, nmemb, n, ll[k], ll[k + 1]));
            }
            if (n > 1) {
                Arrays.sort(array, ll[k], ll[k] + n);
            }
        }
        return;
    }

}
