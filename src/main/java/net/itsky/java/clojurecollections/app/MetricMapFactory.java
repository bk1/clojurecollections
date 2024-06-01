package net.itsky.java.clojurecollections.app;

import net.itsky.java.sort.Metric;
import net.itsky.java.sort.metric.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MetricMapFactory {
    public Map<String, Metric<String>> createMetricMap() {

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

        Map<String, Metric<String>> metrics = new LinkedHashMap<>(Map.of("default", new Utf16StringMetric(),
                "cyrillic", new CyrillicStringMetric(),
                "2bcyrillic", new Cyrillic2BlockStringMetric(),
                "tree",                metricDataTree,
                "1c", metricDataOneChar,
                "forest", metricDataForest,
                "cyrforest", metricDataCyrForest,
                "cyrforestcached", metricDataCyrForestCached,
                "cyrarrforest", metricDataCyrArrForest,
                "cyrforest4", metricDataCyrForest4));
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
        return metrics;
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
