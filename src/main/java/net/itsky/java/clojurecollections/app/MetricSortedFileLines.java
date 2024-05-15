package net.itsky.java.clojurecollections.app;

import net.itsky.java.sort.FlashSort;
import net.itsky.java.sort.ListSwapper;
import net.itsky.java.sort.Metric;
import net.itsky.java.sort.SortMetricized;
import net.itsky.java.sort.metric.*;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectLongHashMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 *
 */
public class MetricSortedFileLines {

    private static final int ARR_SIZE = 0x100000;
    public void processFileContent(List<String> lines) {

        MetricDataTree metricDataTree = new MetricDataTree();
        MetricDataOneChar metricDataOneChar = new MetricDataOneChar();
        MetricDataForest metricDataForest = new MetricDataForest();
        MetricDataCyrForest metricDataCyrForest = new MetricDataCyrForest();
        MetricDataCyrForestCached metricDataCyrForestCached = new MetricDataCyrForestCached();
        MetricDataCyrArrForest metricDataCyrArrForest = new MetricDataCyrArrForest();
        MetricDataCyrForest4 metricDataCyrForest4 = new MetricDataCyrForest4();

        try (InputStream stream = getMetricStream()) {
            metricDataTree.read(stream);
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            ioex.printStackTrace();
        }
        System.out.println("metricDataTree ready");
        try (InputStream stream = get1cStream()) {
            metricDataOneChar.read(stream);
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            ioex.printStackTrace();
        }
        System.out.println("metricDataOneChar ready");
        try (InputStream stream = getMetricStream()) {
            metricDataForest.read(stream);
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            ioex.printStackTrace();
        }
        System.out.println("metricDataForest ready");
        try (InputStream stream = getMetricStream()) {
            metricDataCyrForest.read(stream);
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            ioex.printStackTrace();
        }
        System.out.println("metricDataCyrForest ready");
        try (InputStream stream = getMetricStream()) {
            metricDataCyrForestCached.read(stream);
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            ioex.printStackTrace();
        }
        System.out.println("metricDataCyrForestCached ready");
        try (InputStream stream = getMetricStream()) {
            metricDataCyrArrForest.read(stream);
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            ioex.printStackTrace();
        }
        System.out.println("metricDataCyrArrForest ready");
        try (InputStream streamM = getMetricStream(); InputStream streamF = getFrequencyStream()) {
            metricDataCyrForest4.read(streamM, streamF);
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            ioex.printStackTrace();
        }
        System.out.println("metricDataCyrArrForest ready");

        Map<String, Metric<String>> metrics = new HashMap<>(Map.of("default", new Utf16StringMetric(), "cyrillic", new CyrillicStringMetric(), "2bcyrillic", new Cyrillic2BlockStringMetric(), "tree", metricDataTree, "1c", metricDataOneChar, "forest", metricDataForest, "cyrforest", metricDataCyrForest, "cyrforestcached", metricDataCyrForestCached, "cyrarrforest", metricDataCyrArrForest, "cyrforest4", metricDataCyrForest4));
        for (int depth = 1; depth <= 10; depth++) {
            MetricDataIndependentChar metric = new MetricDataIndependentChar(depth);
            String key = "ind[" + depth + "]";
            try (InputStream stream = get1cStream()) {
                metric.read(stream);
            } catch (IOException ioex) {
                System.out.println("ioex=" + ioex + " key=" + key);
                ioex.printStackTrace();
            }
            System.out.println("key ready");
            metrics.put(key, metric);
            long prev = 0L;
            for (int ci = 0; ci <= Character.MAX_VALUE; ci++) {
                String s = String.valueOf(ci);
                long m = metric.metric(s);
                if (m < prev) {
                    System.out.println("depth=" + depth + " m=" + m + " prev=" + prev + " s=" + s);
                    System.exit(1);
                }
                long m1 = metricDataOneChar.metric(s);
                if (m != m1 && depth == 1) {
                    System.out.println("depth=" + depth + " m=" + m + " m1=" + m1 + " prev=" + prev + " s=" + s);
                    System.exit(1);
                }
            }
        }


        MutableObjectLongMap<String> timing = new ObjectLongHashMap<>();
        SortMetricized<String, List<String>> flashSort = new FlashSort<>();

        ListSwapper<String> swapper = new ListSwapper<>();

        List<String> list = new ArrayList<>(lines);
        Collections.sort(list);
        for (String name : metrics.keySet()) {
            System.out.println("name=" + name);
            Metric<String> metric = metrics.get(name);
            int[] count = new int[ARR_SIZE];
            long prev = 0L;
            String prevLine = "";
            for (String line : list) {
                long m = metric.metric(line);
                if (m < prev) {
                    System.out.println("ERR not monotonous prevLine=" + prevLine + " line=" + line + " prev=" + prev + " m=" + m);
                }
                if (m < 0) {
                    System.out.println("ERR negative m=" + m + " line=" + line);
                }
                prev = m;
                int idx = (int) (m >> 43);
                if (idx < 0 || idx >= ARR_SIZE) {
                    System.out.println("ERR out of bounds m=" + m + " idx=" + idx + " line=" + line);
                }
                count[idx]++;
            }
            int[] classCategories = new int[8];
            int minClassSize = Integer.MAX_VALUE;
            int maxClassSize = 0;
            int classSizeCount = 0;
            int totalCount = 0;
            for (int i = 0; i < ARR_SIZE; i++) {
                int cnt = count[i];
                if (cnt > 0) {
                    totalCount += cnt;
                    minClassSize = Math.min(cnt, minClassSize);
                    maxClassSize = Math.max(cnt, maxClassSize);
                    classSizeCount++;
                    int log = (int) Math.log10(cnt);
                    classCategories[log]++;
                }
            }
            System.out.println("name=" + name + " classSizeCount=" + classSizeCount + " totalCount=" + totalCount + " avg=" + totalCount/(double)classSizeCount + " minClassSize=" + minClassSize + " maxClassSize=" + maxClassSize);
            for (int k = 0; k < 8; k++) {
                System.out.print(" cc[" + k + "]=" + classCategories[k]);
            }
            System.out.println();
        }
    }

    private InputStream getMetricStream() {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream("ukrainian-metric.dat");
    }

    private InputStream getFrequencyStream() {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream("ukrainian-frequencies.dat");
    }

    private InputStream get1cStream() {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream("ukrainian-metric-1c.dat");
    }

}
