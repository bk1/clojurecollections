package net.itsky.java.sort.metric;


import com.google.common.collect.MapMaker;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MetricDataCyrForestCached extends MetricDataCyrForest {

    private ConcurrentMap<String, Integer> cache = new ConcurrentHashMap<>();

    @Override
    public int metric(String s) {
        if (s == null || s.isEmpty()) {
            return super.metric(s);
        }
        String key = s.length() <= 2 ? s : s.substring(0, 2);
        Integer val = cache.get(key);
        if (val == null) {
            val = super.metric(key);
            cache.put(key, val);
        }
        return val.intValue();
    }
}
