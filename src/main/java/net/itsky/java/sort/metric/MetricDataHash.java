package net.itsky.java.sort.metric;

import net.itsky.java.sort.Metric;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.impl.map.mutable.primitive.MutableObjectLongMapFactoryImpl;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;

import java.io.*;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;

public class MetricDataHash implements Metric<String> {
    private static final int MAX_SUB_WORD_LENGTH = 10;
    private final MutableObjectLongMap<String> map;

    private static final Comparator<String> reverseOrder = Comparator.reverseOrder();

    public MetricDataHash() {
        map = new MutableObjectLongMapFactoryImpl().empty();
    }

    public MetricDataHash(MutableObjectLongMap<String> map) {
        this.map = map;
    }

    public void read(InputStream stream) {
        MutableSortedMap<String, Long> sortedMap = new TreeSortedMap<>(reverseOrder);
        try (BufferedInputStream bs = new BufferedInputStream(stream)) {
            try (DataInputStream ds = new DataInputStream(bs)) {
                while (true) {
                    try {
                        String key = ds.readUTF();
                        long val = ds.readLong();
                        map.put(key, val);
                        sortedMap.put(key, val);
                    } catch (EOFException eof) {
                        break;
                    }
                }
            }
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            throw new UncheckedIOException(ioex);
        }
        System.out.println("read");
        for (int ci = 0; ci <= Character.MAX_VALUE; ci++) {
            String cs = String.valueOf((char) ci);
            if (! map.containsKey(cs)) {
                final long val;
                SortedMap<String, Long> tailMap = sortedMap.tailMap(cs);
                if (tailMap.isEmpty()) {
                    val = Long.MIN_VALUE;
                } else {
                    val = tailMap.firstEntry().getValue();
                }
                map.put(cs, val);
            }
        }
    }

    public void write(OutputStream stream) {
        try (BufferedOutputStream bs = new BufferedOutputStream(stream)) {
            try (DataOutputStream ds = new DataOutputStream(bs)) {
                for (String key : map.keySet()) {
                    ds.writeUTF(key);
                    ds.writeLong(map.get(key));
                }
            }
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            throw new UncheckedIOException(ioex);
        }
    }

    public long metric(String s) {
        if (s == null || s.isEmpty()) {
            return Long.MIN_VALUE;
        }
        int n = Math.min(s.length(), MAX_SUB_WORD_LENGTH);
        for (int i = n; i > 0; i--) {
            String key = s.substring(0, i);
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }
        return Long.MIN_VALUE;
    }
}
