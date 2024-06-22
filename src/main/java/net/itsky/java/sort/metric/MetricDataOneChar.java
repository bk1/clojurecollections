package net.itsky.java.sort.metric;

import net.itsky.java.sort.Metric;
import org.eclipse.collections.api.list.primitive.IntList;
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

    private final int[] singleCharList;

    public MetricDataOneChar() {
        singleCharList = new int[ARR_SIZE];
    }

    public MetricDataOneChar(int[] singleCharList) {
        this.singleCharList = singleCharList;
    }

    public MetricDataOneChar(IntList singleCharList) {
        this.singleCharList = singleCharList.toArray();
    }

    public MetricDataOneChar(List<Integer> singleCharList) {
        this.singleCharList = singleCharList.stream().mapToInt(Integer::intValue).toArray();
    }

    public void read(InputStream stream) {
        try (BufferedInputStream bs = new BufferedInputStream(stream)) {
            try (DataInputStream ds = new DataInputStream(bs)) {
                int ci = 0;
                while (true) {
                    try {
                        int val = (int)(ds.readLong() >> 32);
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
        System.out.println("last=" + metric("\uffff") + "=" + singleCharList[ARR_SIZE-1]);
        System.out.println("read one");
    }

    public void write(OutputStream stream) {
        try (BufferedOutputStream bs = new BufferedOutputStream(stream)) {
            try (DataOutputStream ds = new DataOutputStream(bs)) {
                for (int ci = 0; ci < ARR_SIZE; ci++) {
                    ds.writeLong(((long)singleCharList[ci]) <<32);
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
        char c = s.charAt(0);
        return singleCharList[c];
    }
}
