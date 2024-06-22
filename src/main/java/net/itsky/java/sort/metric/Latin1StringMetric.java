package net.itsky.java.sort.metric;

import net.itsky.java.sort.Metric;

public class Latin1StringMetric implements Metric<String> {

    public int metric(String s) {
        int n = s.length();
        int result =  0;
        boolean upperFound = false;
        for (int i = 0; i < 3; i++) {
            result *= 0x102;
            if (i < n && ! upperFound) {
                int c = s.charAt(i);
                if (c >= 256) {
                    c = 256;
                    upperFound = true;
                }
                result += c+1;
            }
        }
        result *= 0x12;
        if (4 < n && ! upperFound) {
            int c = s.charAt(4);
            if (c >= 256) {
                c= 256;
            }
            c >>= 4;
            result += c+1;
        }
        return result;
    }
}
