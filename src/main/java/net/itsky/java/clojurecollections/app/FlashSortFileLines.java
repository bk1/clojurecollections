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
        try (InputStream stream = new FileInputStream("metric.dat")) {
            metricDataTree.read(stream);
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            ioex.printStackTrace();
        }
        System.out.println("metricDataTree ready");
        try (InputStream stream = new FileInputStream("1c-metric.dat")) {
            metricDataOneChar.read(stream);
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            ioex.printStackTrace();
        }
        System.out.println("metricDataOneChar ready");
        try (InputStream stream = new FileInputStream("metric.dat")) {
            metricDataForest.read(stream);
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            ioex.printStackTrace();
        }
        System.out.println("metricDataForest ready");
        try (InputStream stream = new FileInputStream("metric.dat")) {
            metricDataCyrForest.read(stream);
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            ioex.printStackTrace();
        }
        System.out.println("metricDataCyrForest ready");

        Map<String, Metric<String>> metrics = Map.of("default", new Utf16StringMetric(), "cyrillic", new CyrillicStringMetric(), "2bcyrillic", new Cyrillic2BlockStringMetric(), "tree", metricDataTree, "1c", metricDataOneChar, "forest", metricDataForest, "cyrforest", metricDataCyrForest);
        MutableObjectLongMap<String> timing = new ObjectLongHashMap<>();
        SortMetricized<String, List<String>> flashSort = new FlashSort<>();

        ListSwapper<String> swapper = new ListSwapper<>();

        for (int i = 0; i < 10; i++) {
            List<String> jlist = new ArrayList<>(lines);
            long t0 = System.currentTimeMillis();
            Collections.sort(jlist);
            long t = timing.getIfAbsent("jlist", 0L)+System.currentTimeMillis()-t0;
            timing.put("jlist", t);
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
                }
            }
        }
    }

}
