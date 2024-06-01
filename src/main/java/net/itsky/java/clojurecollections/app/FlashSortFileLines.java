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
        MetricMapFactory metricMapFactory = new MetricMapFactory();
        Map<String, Metric<String>> metrics = metricMapFactory.createMetricMap();
        System.out.println();

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

}
