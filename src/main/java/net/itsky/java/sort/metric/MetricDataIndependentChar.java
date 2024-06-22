package net.itsky.java.sort.metric;

import net.itsky.java.sort.Metric;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.LongList;

import java.io.*;
import java.util.List;

public class MetricDataIndependentChar implements Metric<String> {
    public static final int ARR_SIZE = Character.MAX_VALUE + 1;

    private final int[] singleCharList;

    private int factor;

    private final int depth;

    private final double root;

    public MetricDataIndependentChar(int depth) {
        this(depth, new int[ARR_SIZE+1]);
    }

    public MetricDataIndependentChar(int depth, int[] singleCharList) {
        this.depth = depth;
        this.root = Math.pow(Integer.MAX_VALUE, 1.0/depth);
        this.singleCharList = singleCharList;
        if (depth <= 0 || depth > 10) {
            throw new IllegalArgumentException("depth=" + depth +" must be > 0 and <= 10");
        }

    }

    public MetricDataIndependentChar(int depth, IntList singleCharList) {
        this(depth, singleCharList.toArray());
    }

    public MetricDataIndependentChar(int depth, List<Integer> singleCharList) {
        this(depth, singleCharList.stream().mapToInt(Integer::intValue).toArray());
    }

    private long calcDivisor() {
        return (depth == 1 ? 1L : (long) (Integer.MAX_VALUE / (root-2))) << 32;
    }
    public void read(InputStream stream) {
        long divisor = calcDivisor();
        try (BufferedInputStream bs = new BufferedInputStream(stream)) {
            try (DataInputStream ds = new DataInputStream(bs)) {
                int ci = 0;
                while (true) {
                    try {
                        long val = ds.readLong();
                        singleCharList[ci++] = (int) (val / divisor);
                        if (ci >= ARR_SIZE) {
                            break;
                        }
                    } catch (EOFException eof) {
                        break;
                    }
                }
            }
            singleCharList[ARR_SIZE] = Integer.MAX_VALUE;
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            throw new UncheckedIOException(ioex);
        }
        factor = singleCharList[ARR_SIZE-1]+2;
        System.out.println("read ind depth=" + depth);
    }

    public void write(OutputStream stream) {
        long divisor = calcDivisor();
        try (BufferedOutputStream bs = new BufferedOutputStream(stream)) {
            try (DataOutputStream ds = new DataOutputStream(bs)) {
                for (int ci = 0; ci < ARR_SIZE; ci++) {
                    ds.writeLong(singleCharList[ci]*divisor);
                }
            }
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            throw new UncheckedIOException(ioex);
        }
    }

    public int metric(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        boolean charsLeft = true;
        int l = s.length();
        int result = 0;
        boolean done = false;
        for (int i = 0; i < depth; i++) {
            result *= factor;
            done |= (i>=l);
            if (! done) {
                int c = s.charAt(i);
                long m = singleCharList[c];
                done |= singleCharList[c+1]==m;
                result += m;
            }
        }
        return result;
    }
}
