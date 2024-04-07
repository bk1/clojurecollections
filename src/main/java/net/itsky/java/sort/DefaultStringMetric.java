package net.itsky.java.sort;

public class DefaultStringMetric implements Metric<String> {

    public long metric(String s) {
        int n = s.length();
        long result =  0;
        for (int i = 0; i < 4; i++) {
            result <<= 16;
            if (i < n) {
                result += s.charAt(i) + 0x8000;
            } else {
                result += 0x8000;
            }
        }
        return result;
    }
}
