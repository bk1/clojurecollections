package net.itsky.java.sort.metric;

import net.itsky.java.sort.Metric;
import org.eclipse.collections.api.list.primitive.LongList;

import java.io.*;
import java.util.List;

public class MetricDataIndependentChar implements Metric<String> {
    public static final int ARR_SIZE = Character.MAX_VALUE + 1;

    private final long[] singleCharList;

    private long factor;

    private final int depth;

    public MetricDataIndependentChar(int depth) {
        this(depth, new long[ARR_SIZE+1]);
    }

    public MetricDataIndependentChar(int depth, long[] singleCharList) {
        this.depth = depth;
        this.singleCharList = singleCharList;
        if (depth <= 0 || depth > 10) {
            throw new IllegalArgumentException("depth=" + depth +" must be > 0 and <= 10");
        }

    }

    public MetricDataIndependentChar(int depth, LongList singleCharList) {
        this(depth, singleCharList.toArray());
    }

    public MetricDataIndependentChar(int depth, List<Long> singleCharList) {
        this(depth, singleCharList.stream().mapToLong(Long::longValue).toArray());
    }

    public void read(InputStream stream) {
        double root = Math.pow(Long.MAX_VALUE, 1.0/depth);
        long divisor = depth == 1 ? 1L : (long) (Long.MAX_VALUE / (root-2));
        try (BufferedInputStream bs = new BufferedInputStream(stream)) {
            try (DataInputStream ds = new DataInputStream(bs)) {
                int ci = 0;
                while (true) {
                    try {
                        long val = ds.readLong();
                        singleCharList[ci++] = val / divisor;
                        if (ci >= ARR_SIZE) {
                            break;
                        }
                    } catch (EOFException eof) {
                        break;
                    }
                }
            }
            singleCharList[ARR_SIZE] = Long.MAX_VALUE;
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            throw new UncheckedIOException(ioex);
        }
        factor = singleCharList[ARR_SIZE-1]+2;
        System.out.println("read ind depth=" + depth);
    }

    public void write(OutputStream stream) {
        try (BufferedOutputStream bs = new BufferedOutputStream(stream)) {
            try (DataOutputStream ds = new DataOutputStream(bs)) {
                for (int ci = 0; ci < ARR_SIZE; ci++) {
                    ds.writeLong(singleCharList[ci]);
                }
            }
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            throw new UncheckedIOException(ioex);
        }
    }

    public long metric(String s) {
        if (s == null || s.isEmpty()) {
            return 0L;
        }
        boolean charsLeft = true;
        int l = s.length();
        long result = 0L;
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
