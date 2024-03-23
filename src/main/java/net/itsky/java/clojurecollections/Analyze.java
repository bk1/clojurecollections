package net.itsky.java.clojurecollections;

import org.eclipse.collections.api.block.function.primitive.IntFunction;
import org.eclipse.collections.api.block.function.primitive.LongFunction;
import org.eclipse.collections.api.factory.map.primitive.MutableIntLongMapFactory;
import org.eclipse.collections.api.map.primitive.IntLongMap;
import org.eclipse.collections.api.map.primitive.MutableIntLongMap;
import org.eclipse.collections.impl.map.mutable.primitive.MutableIntLongMapFactoryImpl;

import java.util.List;

public class Analyze {

    public void analyze(List<String> lines) {
        MutableIntLongMapFactory factory = new MutableIntLongMapFactoryImpl();
        MutableIntLongMap map = factory.empty();
        for (String line : lines) {
            line += "\n";
            line.chars().forEach(c -> {
                map.put(c, 1+map.getIfAbsent(c, 0));
            });
        }
        for (int i : map.keySet().toArray()) {
            System.out.println("c=" + String.format("%6d", i) + " m=" + String.format("%9d", map.get(i)));
        }
    }
}
