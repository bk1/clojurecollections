package net.itsky.java.clojurecollections.analyze;

import org.eclipse.collections.api.factory.map.primitive.MutableIntLongMapFactory;
import org.eclipse.collections.api.factory.map.primitive.MutableLongLongMapFactory;
import org.eclipse.collections.api.map.primitive.MutableIntLongMap;
import org.eclipse.collections.api.map.primitive.MutableLongLongMap;
import org.eclipse.collections.impl.map.mutable.primitive.MutableIntLongMapFactoryImpl;
import org.eclipse.collections.impl.map.mutable.primitive.MutableLongLongMapFactoryImpl;

import java.io.*;
import java.util.List;

public class AnalyzeNum {

    private static final int C = 1;
    private static final int M = 100;

    private static final int ARR_SIZE = Character.MAX_VALUE + 1;

    private static final int BEGIN = 65536;
    private static final int END = 65537;

    private static final int FACTOR = 65538;

    private static final long FACTOR_SQUARED = (long) FACTOR*FACTOR;

    public void analyze(List<String> lines) {
        MutableIntLongMapFactory factoryI = new MutableIntLongMapFactoryImpl();
        MutableLongLongMapFactory factoryL = new MutableLongLongMapFactoryImpl();

        MutableIntLongMap singleMap = factoryI.empty();
        for (String line : lines) {
            line += "\n";
            line.chars().forEach(c -> {
                singleMap.put(c, M + singleMap.getIfAbsent(c, C));
            });
        }
        long total = singleMap.sum() + ARR_SIZE - singleMap.size();
        long keySum = singleMap.keySet().sum();
        System.out.println("total=" + total + " keySum=" + keySum);
        double factor = Long.MAX_VALUE / total;
        long[] singleList = new long[ARR_SIZE];
        long subTotal = 0;
        for (int ci = 0; ci < ARR_SIZE; ci++) {
            long val = singleMap.getIfAbsent(ci, 1);
            subTotal += val;
            double metricDouble = subTotal * factor;
            long metric = (long) metricDouble;
            singleList[ci] = metric;
        }

        try (OutputStream stream = new FileOutputStream("1c-metric.dat")) {
            try (BufferedOutputStream bs = new BufferedOutputStream(stream)) {
                try (DataOutputStream ds = new DataOutputStream(bs)) {
                    for (int ci = 0; ci < ARR_SIZE; ci++) {
                        ds.writeLong(singleList[ci]);
                    }
                }
            }
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            ioex.printStackTrace();
        }
        MutableIntLongMap doubleMap = factoryI.empty();
        for (String line : lines) {
            line += "\n";
            line.chars().reduce(BEGIN, (prevChar, c) -> {
                if (singleMap.containsKey(prevChar)) {
                    int key = prevChar * FACTOR + c;
                    doubleMap.put(key, M+doubleMap.getIfAbsent(key, C));
                }
                return c;
            });
        }
        MutableLongLongMap tripleMap = factoryL.empty();
        for (String line : lines) {
            line += "\n";
           line.chars().mapToLong(c->(long) c).reduce(BEGIN + FACTOR * BEGIN, (prevKey, c) -> {
               long dkey = prevKey % FACTOR_SQUARED;
               long key = dkey * FACTOR + c;
               if (doubleMap.containsKey((int) dkey)) {
                    tripleMap.put(key, M+tripleMap.getIfAbsent(key, C));
                }
                return key;
            });
        }

        System.out.println("singleMap.size=" + singleMap.size());
        for (int c : singleMap.keySet().toArray()) {
            System.out.println("c=" + String.format("0x%04x", c) + " " + formatChars(c) + " m=" + String.format("%9d", singleMap.get(c)));
        }
        System.out.println("doubleMap.size=" + doubleMap.size());
        for (int key : doubleMap.keySet().toArray()) {
            int c = key / FACTOR;
            int d = key % FACTOR;
            System.out.println("(c,d)=(" + String.format("0x%04x", c) + ", " + String.format("0x%04x", d) + ") " + formatChars(c, d) + " m=" + String.format("%9d", doubleMap.get(key)));
        }
        System.out.println("tripleMap.size=" + tripleMap.size());
        for (long key : tripleMap.keySet().toArray()) {
            long cc = key / FACTOR;
            long e = key % FACTOR;
            long d = cc % FACTOR;
            long c = cc / FACTOR;
            System.out.println("key=" + key + " (c,d, e)=" + String.format("0x%04x", c) + ", " + String.format("0x%04x", d) + ", " + String.format("0x%04x", e) + ") " + formatChars(c, d, e) + " m=" + String.format("%9d", tripleMap.get(key)));
        }
    }

    private String formatChars(long... cs) {
        StringBuilder result = new StringBuilder();
        for (long c : cs) {
            if (c >= 32 && c <= 0xff00) {
                result.append(String.format("%c", (char) c));
            }
        }
        return result.toString();
    }
}
