package net.itsky.java.sort.metric;

import net.itsky.java.sort.Metric;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;

import java.io.*;
import java.util.Comparator;
import java.util.SortedMap;

public class MetricDataTree implements Metric<String> {
    private final SortedMap<String, Integer> map;

    private static final Comparator<String> reverseOrder = Comparator.reverseOrder();

    public MetricDataTree() {
        map = new TreeSortedMap<>(reverseOrder);
    }

    public MetricDataTree(SortedMap<String, Integer> map) {
        this.map = map;
    }

    public void read(InputStream stream) {
        try (BufferedInputStream bs = new BufferedInputStream(stream)) {
            try (DataInputStream ds = new DataInputStream(bs)) {
                while (true) {
                    try {
                        String key = ds.readUTF();
                        int val = (int) (ds.readLong() >> 32);
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
                for (SortedMap.Entry<String, Integer> entry : map.entrySet()) {
                    ds.writeUTF(entry.getKey());
                    ds.writeLong(((long)entry.getValue()) << 32);
                }
            }
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            throw new UncheckedIOException(ioex);
        }
    }

    public int metric(String s) {
        if (s == null) {
            return 0;
        }
        SortedMap<String, Integer> tailMap = map.tailMap(s);
        if (tailMap.isEmpty()) {
            return 0;
        }
        return tailMap.firstEntry().getValue();
    }
}
