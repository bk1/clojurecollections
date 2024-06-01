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

    public record LocalTree(long metricOneChar, SortedMap<String, Long> map) {
        public long metric(String s) {
            if (s.length() == 1) {
                return metricOneChar;
            }
            SortedMap<String, Long> tailMap = map.tailMap(s);
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
        SortedMap<String, Long> map = new TreeSortedMap<>(reverseOrder);
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
        for (int ci = 0; ci < LIST_SIZE; ci++) {
            String key = String.valueOf((char) ci);
            SortedMap<String, Long> tailMap = map.tailMap(key);
            final long metric;
            if (tailMap.isEmpty()) {
                metric = 0L;
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
        for (SortedMap.Entry<String, Long> entry : map.entrySet()){
            String key = entry.getKey();
            if (! key.isEmpty()) {
                long metric = entry.getValue();
                int ci = key.charAt(0);
                list.get(ci).map().put(key, metric);
            }
        }

    }

    public void write(OutputStream stream) {
        try (BufferedOutputStream bs = new BufferedOutputStream(stream)) {
            try (DataOutputStream ds = new DataOutputStream(bs)) {
                for (LocalTree localTree : list) {
                    for (SortedMap.Entry<String, Long> entry :localTree.map().entrySet()) {
                        ds.writeUTF(entry.getKey());
                        ds.writeLong(entry.getValue());
                    }
                }
            }
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            throw new UncheckedIOException(ioex);
        }
    }

    public long metric(String s) {
        if (s == null|| s.isEmpty()) {
            return 0L;
        }
        int ci = s.charAt(0);
        return list.get(ci).metric(s);
    }

    public List<String> keys() {
        return IntStream.range(0, LIST_SIZE).mapToObj(list::get).flatMap(localTree -> localTree.map().keySet().stream()).toList();
    }
}
