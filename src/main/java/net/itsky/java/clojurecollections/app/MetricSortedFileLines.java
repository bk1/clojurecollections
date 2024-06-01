package net.itsky.java.clojurecollections.app;

import net.itsky.java.sort.FlashSort;
import net.itsky.java.sort.ListSwapper;
import net.itsky.java.sort.Metric;
import net.itsky.java.sort.SortMetricized;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectLongHashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class MetricSortedFileLines {

    private static final int ARR_SIZE = 0x100000;

    public void processFileContent(List<String> lines) {

        MetricMapFactory metricMapFactory = new MetricMapFactory();
        Map<String, Metric<String>> metrics = metricMapFactory.createMetricMap();
        System.out.println();

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
            System.out.println("name=" + name + " classSizeCount=" + classSizeCount + " totalCount=" + totalCount + " avg=" + totalCount / (double) classSizeCount + " minClassSize=" + minClassSize + " maxClassSize=" + maxClassSize);
            for (int k = 0; k < 8; k++) {
                System.out.print(" cc[" + k + "]=" + classCategories[k]);
            }
            System.out.println();
            System.out.println();
        }
    }

}
