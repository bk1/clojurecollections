package net.itsky.java.sort.metric;

import net.itsky.java.sort.Metric;

public class Utf16StringMetric implements Metric<String> {

    public int metric(String s) {
        int n = s.length();
        long result = 0;
        for (int i = 0; i < 2; i++) {
            result <<= 16;
            if (i < n) {
                result += s.charAt(i);
            }
        }
        result >>= 1;
        result &= 0x7fff_ffffL;
        return (int) result;
    }
}
