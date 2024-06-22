package net.itsky.java.sort.metric;

import net.itsky.java.sort.Metric;

public class Cyrillic2BlockStringMetric implements Metric<String> {

    public int metric(String s) {
        int n = s.length();
        int result =  0;
        boolean rareFound = false;
        for (int i = 0; i < 3; i++) {
            result *=0x0204;
            if (i < n && ! rareFound) {
                int c = s.charAt(i);
                if (c < 0x0100) {
                    c += 1;
                } else if (c < 0x0400) {
                    c = 0x0101;
                    rareFound = true;
                } else if (c < 0x0500) {
                    c -= 0x02fe;
                } else {
                    c = 0x0203;
                    rareFound = true;
                }
                result += c;
            }
        }
        return result;
    }
}
