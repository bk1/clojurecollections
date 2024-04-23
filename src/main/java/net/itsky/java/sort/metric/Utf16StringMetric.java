package net.itsky.java.sort.metric;

import net.itsky.java.sort.Metric;

public class Utf16StringMetric implements Metric<String> {

    public long metric(String s) {
        int n = s.length();
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result <<= 16;
            if (i < n) {
                result += s.charAt(i);
            }
        }
        result += Long.MIN_VALUE;
        return result;
    }
}
