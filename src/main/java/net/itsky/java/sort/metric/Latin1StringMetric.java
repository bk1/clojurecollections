package net.itsky.java.sort.metric;

import net.itsky.java.sort.Metric;

public class Latin1StringMetric implements Metric<String> {

    public long metric(String s) {
        int n = s.length();
        long result =  0;
        boolean upperFound = false;
        for (int i = 0; i < 7; i++) {
            result *=258;
            if (i < n && ! upperFound) {
                int c = s.charAt(i);
                if (c >= 256) {
                    c = 256;
                    upperFound = true;
                }
                result += c+1;
            }
        }
        return result;
    }
}
