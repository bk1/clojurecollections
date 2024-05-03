package net.itsky.java.sort.metric;

import net.itsky.java.sort.Metric;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.map.primitive.ObjectLongMap;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectLongHashMap;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MetricDataCyrForest4 implements Metric<String> {

    private static final int DEPTH = 4;
    private static final int LIST_SIZE = Character.MAX_VALUE+1;

    private static final long MIN_FREQUENCY = 100_1000;

    private static final int LAT_BLOCK_LOWER = 0x000;
    private static final int LAT_BLOCK_UPPER = 0x100;

    private static final int BETWEEN = 0x280;
    private static final int CYR_BLOCK_LOWER = 0x400;
    private static final int CYR_BLOCK_UPPER = 0x500;

    private static final int ABOVE = 0x500;


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
            if (c < CYR_BLOCK_UPPER && cyrBlock != null) {
                return cyrBlock.get(c-CYR_BLOCK_LOWER).metric(t);
            }
            return metricAbove;
        }
    }

    private final List<LocalTree> list = new ArrayList<>(LIST_SIZE);

    private static final Comparator<String> reverseOrder = Comparator.reverseOrder();

    public void read(InputStream metricStream, InputStream frequenciesStream) {
        System.out.println("reading");
        SortedMap<String, Long> map = new TreeSortedMap<>(reverseOrder);
        try (BufferedInputStream bs = new BufferedInputStream(metricStream)) {
            try (DataInputStream ds = new DataInputStream(bs)) {
                while (true) {
                    try {
                        String key = ds.readUTF().intern();
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
        MutableObjectLongMap<String> frequencies = new ObjectLongHashMap<>(map.size());
        long sum[] = new long[DEPTH+2];
        int count[] = new int[DEPTH+2];
        try (BufferedInputStream bs = new BufferedInputStream(frequenciesStream)) {
            try (DataInputStream ds = new DataInputStream(bs)) {
                while (true) {
                    try {
                        String key0 = ds.readUTF();
                        long val = ds.readLong();
                        if (key0.length() <= DEPTH && val >= MIN_FREQUENCY) {
                            String key = key0.intern();
                            frequencies.put(key, val);
                            count[key.length()]++;
                            sum[key.length()]+=val;
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
        for (int i = 0; i < DEPTH+2; i++) {
            System.out.println("key.length=" + i + " count=" + count[i] + " sum=" + sum[i] + (count[i] == 0 ? "" : (" avg=" + sum[i]/count[i])));
        }
        for (int ci = 0; ci < LIST_SIZE; ci++) {
            String key = String.valueOf((char) ci);
            long metric = getMetric(map, key);
            long metricBetween = getMetric(map, key+String.valueOf((char) BETWEEN));
            long metricAbove = getMetric(map, key+String.valueOf((char)ABOVE));
            final LocalTree localTree;
            /*
MIN_FREQ=1:
key.length=0 count=0 sum=0
key.length=1 count=264 sum=12920747364 avg=48942224
key.length=2 count=5009 sum=11887353709 avg=2373198
key.length=3 count=69309 sum=11153963409 avg=160930
key.length=4 count=77263 sum=9239434663 avg=119584
key.length=5 count=0 sum=0

MIN_FREQ=100_000:
key.length=0 count=0 sum=0
key.length=1 count=135 sum=12902727335 avg=95575758
key.length=2 count=1055 sum=10713014855 avg=10154516
key.length=3 count=2098 sum=7377040098 avg=3516225
key.length=4 count=1314 sum=3247249914 avg=2471270
key.length=5 count=0 sum=0
             */
            if (ci <= LAT_BLOCK_UPPER || CYR_BLOCK_LOWER <= ci  && ci < CYR_BLOCK_UPPER) {
                List<LocalTree> latBlock = createLatBlock(key, map, frequencies, DEPTH-1);
                List<LocalTree> cyrBlock = createCyrBlock(key, map, frequencies, DEPTH-1);
                localTree = new LocalTree(metric, metricBetween, metricAbove, latBlock, cyrBlock);
            } else {
                localTree = new LocalTree(metric, metric, metric,null, null);
            }
            if (list.size() <= ci) {
                list.add(localTree);
            } else {
                list.set(ci, localTree);
            }
        }
        System.out.println("read");
    }

    private List<LocalTree> createLatBlock(String parentKey, SortedMap<String, Long> map, ObjectLongMap<String> frequencyMap, int remainingDepth) {
        return createBlock(parentKey, map, frequencyMap, remainingDepth, LAT_BLOCK_LOWER, LAT_BLOCK_UPPER);
    }

    private List<LocalTree> createCyrBlock(String parentKey, SortedMap<String, Long> map, ObjectLongMap<String> frequencyMap, int remainingDepth) {
        return createBlock(parentKey, map, frequencyMap, remainingDepth, CYR_BLOCK_LOWER, CYR_BLOCK_UPPER);
    }

    private List<LocalTree> createBlock(String parentKey, SortedMap<String, Long> map, ObjectLongMap<String> frequencyMap, int remainingDepth, int blockLower, int blockUpper) {
        List<LocalTree> result = new ArrayList<>(blockUpper - blockLower);
        for (int i = blockLower; i < blockUpper; i++) {
            String key = parentKey + (char) i;
            long metric = getMetric(map, key);
            if (remainingDepth <= 0 || frequencyMap.getIfAbsent(key, 0) < MIN_FREQUENCY) {
                result.add(new LocalTree(metric, metric, metric, null, null));
            } else {
                long metricBetween = getMetric(map, key + (char) BETWEEN);
                long metricAbove = getMetric(map, key + (char) ABOVE);
                result.add(new LocalTree(metric, metricBetween, metricAbove,
                        createLatBlock(key, map, frequencyMap, remainingDepth -1),
                        createCyrBlock(key, map, frequencyMap, remainingDepth -1)));
            }
        }
        return result;
    }


    public void write(OutputStream stream) {
        System.out.println("writing");
        try (BufferedOutputStream bs = new BufferedOutputStream(stream)) {
            try (DataOutputStream ds = new DataOutputStream(bs)) {
                for (int ci = 0; ci < LIST_SIZE; ci++) {
                    String s1 = String.valueOf((char) ci);
                    LocalTree localTree = list.get(ci);
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
            return 0L;
        }
        int ci = s.charAt(0);
        String t = s.substring(1);
        return list.get(ci).metric(t);
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
