package net.itsky.java.sort.metric;

import net.itsky.java.sort.Metric;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.IntStream;

public class MetricDataForest implements Metric<String> {

    private static final int LIST_SIZE = Character.MAX_VALUE+1;

    public record LocalTree(int metricOneChar, SortedMap<String, Integer> map) {
        public int metric(String s) {
            if (s.length() == 1) {
                return metricOneChar;
            }
            SortedMap<String, Integer> tailMap = map.tailMap(s);
            if (tailMap.isEmpty()) {
                return metricOneChar;
            } else {
                return tailMap.firstEntry().getValue();
            }
        }
    }

    private final List<LocalTree> list = new ArrayList<>(LIST_SIZE);

    private static final Comparator<String> reverseOrder = Comparator.reverseOrder();

    public void read(InputStream stream) {
        SortedMap<String, Integer> map = new TreeSortedMap<>(reverseOrder);
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
        for (int ci = 0; ci < LIST_SIZE; ci++) {
            String key = String.valueOf((char) ci);
            SortedMap<String, Integer> tailMap = map.tailMap(key);
            final int metric;
            if (tailMap.isEmpty()) {
                metric = 0;
            } else {
                metric = tailMap.firstEntry().getValue();
            }
            LocalTree localTree = new LocalTree(metric, new TreeSortedMap<>(reverseOrder));
            if (list.size() <= ci) {
              list.add(localTree);
            } else {
                list.set(ci, localTree);
            }
        }
        for (SortedMap.Entry<String, Integer> entry : map.entrySet()){
            String key = entry.getKey();
            if (! key.isEmpty()) {
                int metric = entry.getValue();
                int ci = key.charAt(0);
                list.get(ci).map().put(key, metric);
            }
        }

    }

    public void write(OutputStream stream) {
        try (BufferedOutputStream bs = new BufferedOutputStream(stream)) {
            try (DataOutputStream ds = new DataOutputStream(bs)) {
                for (LocalTree localTree : list) {
                    for (SortedMap.Entry<String, Integer> entry :localTree.map().entrySet()) {
                        ds.writeUTF(entry.getKey());
                        ds.writeLong(((long)entry.getValue()) << 32);
                    }
                }
            }
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            throw new UncheckedIOException(ioex);
        }
    }

    public int metric(String s) {
        if (s == null|| s.isEmpty()) {
            return 0;
        }
        int ci = s.charAt(0);
        return list.get(ci).metric(s);
    }

    public List<String> keys() {
        return IntStream.range(0, LIST_SIZE).mapToObj(list::get).flatMap(localTree -> localTree.map().keySet().stream()).toList();
    }
}
