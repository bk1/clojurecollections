package net.itsky.java.clojurecollections.app;

import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.factory.map.primitive.MutableIntLongMapFactory;
import org.eclipse.collections.api.map.primitive.IntLongMap;
import org.eclipse.collections.api.map.primitive.MutableIntLongMap;
import org.eclipse.collections.impl.map.mutable.primitive.MutableIntLongMapFactoryImpl;

import java.util.List;

public class Analyze {

    private static final int C = 1;
    private static final int M = 100;

    private static final int BEGIN = 65536;
    private static final int END = 65537;

    private static final int FACTOR = 65538;

    public void analyze(List<String> lines) {
        MutableIntLongMapFactory factory = new MutableIntLongMapFactoryImpl();
        MutableIntLongMap singleMap = factory.empty();
        for (String line : lines) {
            line += "\n";
            line.chars().forEach(c -> {
                singleMap.put(c, M + singleMap.getIfAbsent(c, C));
            });
        }
        MutableIntLongMap doubleMap = factory.empty();
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
        for (int i : singleMap.keySet().toArray()) {
            System.out.println("c=" + String.format("%6d", i) + " m=" + String.format("%9d", singleMap.get(i)));
        }
        for (int i : doubleMap.keySet().toArray()) {
            int c = i / FACTOR;
            int d = i % FACTOR;
            System.out.println("(c,d)=" + String.format("%6d", c) + ", " + String.format("%6d", d) + ") m=" + String.format("%9d", doubleMap.get(i)));
        }
    }
}
