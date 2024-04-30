package net.itsky.java.sort.metric;

import net.itsky.java.sort.Metric;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MetricDataCyrArrForest implements Metric<String> {

    private static final int LIST_SIZE = Character.MAX_VALUE+1;

    private static final int LAT_BLOCK_LOWER = 0x000;
    private static final int LAT_BLOCK_UPPER = 0x100;

    private static final int BETWEEN = 0x280;
    private static final int CYR_BLOCK_LOWER = 0x400;
    private static final int CYR_BLOCK_UPPER = 0x500;

    private static final int ABOVE = 0x500;


    public record LocalTree(long metricOneChar, long metricBetween, long metricAbove, LocalTree[] latBlock, LocalTree[] cyrBlock) {
        public long metric(String s) {
            if (s.isEmpty() || latBlock == null && cyrBlock == null) {
                return metricOneChar;
            }
            char c = s.charAt(0);
            String t = s.substring(1);
            if (c < LAT_BLOCK_UPPER && latBlock != null) {
                return latBlock[c].metric(t);
            }
            if (c < CYR_BLOCK_LOWER) {
                return metricBetween;
            }
            if (CYR_BLOCK_LOWER <= c && c < CYR_BLOCK_UPPER && cyrBlock != null) {
                return cyrBlock[c-CYR_BLOCK_LOWER].metric(t);
            }
            return metricAbove;
        }
    }

    private final LocalTree[] list = new LocalTree[LIST_SIZE];

    private static final Comparator<String> reverseOrder = Comparator.reverseOrder();

    public void read(InputStream stream) {
        System.out.println("reading");
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
            if (ci <= LAT_BLOCK_UPPER || CYR_BLOCK_LOWER <= ci  && ci < CYR_BLOCK_UPPER) {
                LocalTree[] latBlock = IntStream.range(LAT_BLOCK_LOWER, LAT_BLOCK_UPPER)
                        .mapToObj(di -> key + String.valueOf((char) di))
                        .map(key2 -> getMetric(map, key2))
                        .map(m -> new LocalTree(m, m, m, null, null))
                        .toArray(LocalTree[]::new);
                LocalTree[] cyrBlock = IntStream.range(CYR_BLOCK_LOWER, CYR_BLOCK_UPPER)
                        .mapToObj(di -> key + String.valueOf((char) di))
                        .map(key2 -> getMetric(map, key2))
                        .map(m -> new LocalTree(m, m, m, null, null))
                        .toArray(LocalTree[]::new);
                localTree = new LocalTree(metric, metricBetween, metricAbove, latBlock, cyrBlock);
            } else {
                localTree = new LocalTree(metric, metric, metric,null, null);
            }
            list[ci] = localTree;
        }
        System.out.println("read");
    }

    public void write(OutputStream stream) {
        System.out.println("writing");
        try (BufferedOutputStream bs = new BufferedOutputStream(stream)) {
            try (DataOutputStream ds = new DataOutputStream(bs)) {
                for (int ci = 0; ci < LIST_SIZE; ci++) {
                    String s1 = String.valueOf((char) ci);
                    LocalTree localTree = list[ci];
                    ds.writeUTF(s1);
                    ds.writeLong(localTree.metricOneChar);
                    IntStream.concat(IntStream.range(LAT_BLOCK_LOWER, LAT_BLOCK_UPPER+1), IntStream.range(CYR_BLOCK_LOWER-1, CYR_BLOCK_UPPER+1))
                            .forEach(cj -> {
                                String s2 = String.valueOf((char) cj);
                                String s = s1+s2;
                                long metric = localTree.metric(s2);
                                try {
                                    ds.writeUTF(s);
                                    ds.writeLong(metric);
                                } catch (IOException ioex) {
                                    throw new UncheckedIOException(ioex);
                                }
                            });
                }
            }
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            throw new UncheckedIOException(ioex);
        }
        System.out.println("written");
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
        return list[ci].metric(t);
    }

    public List<String> keys() {
        return Stream.concat(IntStream.range(0, LIST_SIZE).mapToObj(ci -> String.valueOf((char) ci)),
            IntStream.range(0, LIST_SIZE).boxed()
                    .flatMap(ci -> IntStream.concat(IntStream.range(LAT_BLOCK_LOWER, LAT_BLOCK_UPPER+1),
                            IntStream.range(CYR_BLOCK_LOWER-1, CYR_BLOCK_UPPER+1))
                            .mapToObj(cj -> new String(new char[] { (char) ci.intValue(), (char) cj }))))
                .toList();
    }

}
