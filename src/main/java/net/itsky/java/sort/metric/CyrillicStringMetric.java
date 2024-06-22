package net.itsky.java.sort.metric;

import net.itsky.java.sort.Metric;

public class CyrillicStringMetric implements Metric<String> {

    public int metric(String s) {
        int n = s.length();
        int result =  0;
        boolean upperFound = false;
        for (int i = 0; i < 3; i++) {
            result *=0x0502;
            if (i < n && ! upperFound) {
                int c = s.charAt(i);
                if (c >= 0x0500) {
                    c = 0x0500;
                    upperFound = true;
                }
                result += c+0x0001;
            } else {
                result += 0x0000;
            }
        }
        return result;
    }
}
