package net.itsky.java.clojurecollections.app;

import net.itsky.java.sort.metric.*;
import net.itsky.java.sort.*;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectLongHashMap;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 *
 */
public class FlashSortFileLines {
    public void sortFileContent(List<String> lines) {

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

        Map<String, Metric<String>> metrics = new HashMap<>(Map.of("default", new Utf16StringMetric(), "cyrillic", new CyrillicStringMetric(), "2bcyrillic", new Cyrillic2BlockStringMetric(), "tree", metricDataTree, "1c", metricDataOneChar, "forest", metricDataForest, "cyrforest", metricDataCyrForest, "cyrforestcached", metricDataCyrForestCached, "cyrarrforest", metricDataCyrArrForest, "cyrforest4" , metricDataCyrForest4));
        for (int depth=1;depth<=10;depth++) {
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
            for (int ci=0; ci<= Character.MAX_VALUE; ci++) {
                String s = String.valueOf(ci);
                long m = metric.metric(s);
                if (m < prev) {
                    System.out.println("depth=" + depth+ " m=" + m + " prev=" +prev + " s=" + s);
                    System.exit(1);
                }
                long m1 = metricDataOneChar.metric(s);
                if (m != m1 && depth == 1) {
                    System.out.println("depth=" + depth+ " m=" + m + " m1=" +m1 + " prev=" + prev + " s=" + s);
                    System.exit(1);
                }
            }
        }


        MutableObjectLongMap<String> timing = new ObjectLongHashMap<>();
        SortMetricized<String, List<String>> flashSort = new FlashSort<>();

        ListSwapper<String> swapper = new ListSwapper<>();

        for (int i = 0; i < 10; i++) {
            List<String> jlist = new ArrayList<>(lines);
            long t0 = System.currentTimeMillis();
            Collections.sort(jlist);
            long t = timing.getIfAbsent("jlist", 0L)+System.currentTimeMillis()-t0;
            timing.put("jlist", t);
            System.out.println("\n---------------------------------------");
            System.out.println("i=" + i + " jlist: t=" + t / (i + 1));

            for (String name : metrics.keySet()) {
                Metric<String> metric = metrics.get(name);
                List<String> list = new ArrayList<>(lines);
                t0 = System.currentTimeMillis();
                List<String> sortedList = flashSort.sort(list, Comparator.naturalOrder(), swapper, metric);
                t = timing.getIfAbsent(name, 0L) + System.currentTimeMillis() - t0;
                timing.put(name, t);
                System.out.println("i=" + i + " " + name + ": t=" + t / (i + 1));
                if (!jlist.equals(sortedList)) {
                    System.out.println("flashSort_" + name + " failed");
                    for (int ii=0; ii < jlist.size(); ii++) {
                        if (! Objects.equals(jlist.get(ii), sortedList.get(ii))) {
                            System.out.println("name=" + name + " i=" + i + " ii=" + ii + " jlist[ii]=\"" + jlist.get(ii) + "\" slist[ii]=\"" + sortedList.get(ii) + "\"");
                            break;
                        }
                    }
                }
            }
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
