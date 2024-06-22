package net.itsky.java.sort.metric;

import net.itsky.java.sort.Metric;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.impl.map.mutable.primitive.MutableObjectIntMapFactoryImpl;
import org.eclipse.collections.impl.map.mutable.primitive.MutableObjectLongMapFactoryImpl;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;

import java.io.*;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;

public class MetricDataHash implements Metric<String> {
    private static final int MAX_SUB_WORD_LENGTH = 10;
    private final MutableObjectIntMap<String> map;

    private static final Comparator<String> reverseOrder = Comparator.reverseOrder();

    public MetricDataHash() {
        map = new MutableObjectIntMapFactoryImpl().empty();
    }

    public MetricDataHash(MutableObjectIntMap<String> map) {
        this.map = map;
    }

    public void read(InputStream stream) {
        MutableSortedMap<String, Integer> sortedMap = new TreeSortedMap<>(reverseOrder);
        try (BufferedInputStream bs = new BufferedInputStream(stream)) {
            try (DataInputStream ds = new DataInputStream(bs)) {
                while (true) {
                    try {
                        String key = ds.readUTF();
                        int val = (int) (ds.readLong() >> 32);
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
                final int val;
                SortedMap<String, Integer> tailMap = sortedMap.tailMap(cs);
                if (tailMap.isEmpty()) {
                    val = 0;
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

    public int metric(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        int n = Math.min(s.length(), MAX_SUB_WORD_LENGTH);
        for (int i = n; i > 0; i--) {
            String key = s.substring(0, i);
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }
        return 0;
    }
}
