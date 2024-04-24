package net.itsky.java.sort.metric;

import clojure.lang.IFn;
import net.itsky.java.sort.Metric;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.IntStream;

public class MetricDataCyrForest implements Metric<String> {

    private static final int LIST_SIZE = Character.MAX_VALUE+1;

    private static final int LAT_BLOCK_LOWER = 0x000;
    private static final int LAT_BLOCK_UPPER = 0x100;

    private static final int BETWEEN = 0x280;
    private static final int CYR_BLOCK_LOWER = 0x400;
    private static final int CYR_BLOCK_UPPER = 0x500;

    private static final int ABOVE = 0x500;

    public void write(PipedOutputStream outputStream) {
    }

    public record LocalTree(long metricOneChar, long metricBetween, long metricAbove, List<LocalTree> latBlock, List<LocalTree> cyrBlock) {
        public long metric(String s) {
            if (s.isEmpty() || latBlock == null && cyrBlock == null) {
                return metricOneChar;
            }
            char c = s.charAt(0);
            String t = s.substring(1);
            if (c < LAT_BLOCK_UPPER && latBlock != null) {
                return latBlock.get(c).metric(t);
            }
            if (c < CYR_BLOCK_LOWER) {
                return metricBetween;
            }
            if (CYR_BLOCK_LOWER <= c && c < CYR_BLOCK_UPPER && cyrBlock != null) {
                return cyrBlock.get(c-CYR_BLOCK_LOWER).metric(t);
            }
            return metricAbove;
        }
    }

    private final List<LocalTree> arr = new ArrayList<>(LIST_SIZE);

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
            long metric = getMetric(map, key);
            long metricBetween = getMetric(map, key+String.valueOf((char) BETWEEN));
            long metricAbove = getMetric(map, key+String.valueOf((char)ABOVE));
            final LocalTree localTree;
            if (ci <= LAT_BLOCK_UPPER || CYR_BLOCK_LOWER <= ci  && ci < LAT_BLOCK_UPPER) {
                List<LocalTree> latBlock = IntStream.range(LAT_BLOCK_LOWER, LAT_BLOCK_UPPER)
                        .mapToObj(di -> key + String.valueOf((char) di))
                        .map(key2 -> getMetric(map, key2))
                        .map(m -> new LocalTree(m, m, m, null, null))
                        .toList();
                List<LocalTree> cyrBlock = IntStream.range(CYR_BLOCK_LOWER, CYR_BLOCK_UPPER)
                        .mapToObj(di -> key + String.valueOf((char) di))
                        .map(key2 -> getMetric(map, key2))
                        .map(m -> new LocalTree(m, m, m, null, null))
                        .toList();
                localTree = new LocalTree(metric, metricBetween, metricAbove, latBlock, cyrBlock);
            } else {
                localTree = new LocalTree(metric, metric, metric,null, null);
            }
            if (arr.size() <= ci) {
                arr.add(localTree);
            } else {
                arr.set(ci, localTree);
            }
        }

    }

    private static long getMetric(SortedMap<String, Long> map, String key) {
        SortedMap<String, Long> tailMap = map.tailMap(key);
        if (tailMap.isEmpty()) {
            return Long.MIN_VALUE;
        } else {
            return tailMap.firstEntry().getValue();
        }
    }

    public long metric(String s) {
        if (s == null|| s.isEmpty()) {
            return Long.MIN_VALUE;
        }
        int ci = s.charAt(0);
        String t = s.substring(1);
        return arr.get(ci).metric(t);
    }

}
