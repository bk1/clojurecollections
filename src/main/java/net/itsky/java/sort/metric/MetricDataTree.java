package net.itsky.java.sort.metric;

import net.itsky.java.sort.Metric;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;

import java.io.*;
import java.util.Comparator;
import java.util.SortedMap;

public class MetricDataTree implements Metric<String> {
    private final SortedMap<String, Long> map;

    private static final Comparator<String> reverseOrder = Comparator.reverseOrder();

    public MetricDataTree() {
        map = new TreeSortedMap<>(reverseOrder);
    }

    public MetricDataTree(SortedMap<String, Long> map) {
        this.map = map;
    }

    public void read(InputStream stream) {
        try (BufferedInputStream bs = new BufferedInputStream(stream)) {
            try (DataInputStream ds = new DataInputStream(bs)) {
                while (true) {
                    try {
                        String key = ds.readUTF();
                        long val = ds.readLong();
                        map.put(key, val);
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
                for (SortedMap.Entry<String, Long> entry : map.entrySet()) {
                    ds.writeUTF(entry.getKey());
                    ds.writeLong(entry.getValue());
                }
            }
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            throw new UncheckedIOException(ioex);
        }
    }

    public long metric(String s) {
        if (s == null) {
            return Long.MIN_VALUE;
        }
        SortedMap<String, Long> tailMap = map.tailMap(s);
        if (tailMap.isEmpty()) {
            return Long.MIN_VALUE;
        }
        return tailMap.firstEntry().getValue();
    }
}
