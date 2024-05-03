package net.itsky.java.sort.metric;

import net.itsky.java.sort.Metric;
import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.api.map.primitive.MutableIntLongMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntLongHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.MutableIntLongMapFactoryImpl;
import org.eclipse.collections.impl.map.mutable.primitive.MutableObjectLongMapFactoryImpl;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;

import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;

public class MetricDataOneChar implements Metric<String> {
    public static final int ARR_SIZE = Character.MAX_VALUE + 1;

    private final long[] singleCharList;

    public MetricDataOneChar() {
        singleCharList = new long[ARR_SIZE];
    }

    public MetricDataOneChar(long[] singleCharList) {
        this.singleCharList = singleCharList;
    }

    public MetricDataOneChar(LongList singleCharList) {
        this.singleCharList = singleCharList.toArray();
    }

    public MetricDataOneChar(List<Long> singleCharList) {
        this.singleCharList = singleCharList.stream().mapToLong(Long::longValue).toArray();
    }

    public void read(InputStream stream) {
        try (BufferedInputStream bs = new BufferedInputStream(stream)) {
            try (DataInputStream ds = new DataInputStream(bs)) {
                int ci = 0;
                while (true) {
                    try {
                        long val = ds.readLong();
                        singleCharList[ci++] = val;
                        if (ci >= ARR_SIZE) {
                            break;
                        }
                    } catch (EOFException eof) {
                        break;
                    }
                }
            }
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            throw new UncheckedIOException(ioex);
        }
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
        char c = s.charAt(0);
        return singleCharList[c];
    }
}
