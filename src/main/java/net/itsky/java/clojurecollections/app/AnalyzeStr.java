package net.itsky.java.clojurecollections.app;

import org.eclipse.collections.api.factory.map.primitive.MutableIntLongMapFactory;
import org.eclipse.collections.api.factory.map.primitive.MutableLongLongMapFactory;
import org.eclipse.collections.api.factory.map.primitive.MutableObjectLongMapFactory;
import org.eclipse.collections.api.map.primitive.MutableIntLongMap;
import org.eclipse.collections.api.map.primitive.MutableLongLongMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.impl.map.mutable.primitive.MutableIntLongMapFactoryImpl;
import org.eclipse.collections.impl.map.mutable.primitive.MutableLongLongMapFactoryImpl;
import org.eclipse.collections.impl.map.mutable.primitive.MutableObjectLongMapFactoryImpl;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AnalyzeStr {

    private static final int C = 1;
    private static final int M = 100;

    private static final int MAX_SUB_WORD_LENGTH = 10;

    public void analyze(List<String> lines) {
        MutableIntLongMapFactory factoryI = new MutableIntLongMapFactoryImpl();
        MutableObjectLongMapFactory factoryS = new MutableObjectLongMapFactoryImpl();
        MutableObjectLongMap<String> wordMap = factoryS.empty();
        MutableObjectLongMap<String> subWordMap = factoryS.empty();

        MutableIntLongMap singleMap = factoryI.empty();
        lines.stream().map(line -> line+"\n").flatMapToInt(String::chars).forEach(c -> {
            singleMap.put(c, M + singleMap.getIfAbsent(c, C));
        });
        String r = lines.stream().map(line->line+"\n").reduce("", (leftoverWhiteSpace, line) -> {
            String[] parts = (leftoverWhiteSpace + line).splitWithDelimiters("\\w+", 0);
            int last = parts.length - 1;
            Stream.of(parts).limit(last).filter(s-> !s.isEmpty()).map(String::intern).forEach(s -> {
                wordMap.put(s, M+wordMap.getIfAbsent(s, C));
                int n = s.length();
                IntStream.range(0, n).forEach(lower -> {
                    int nn = Math.min(n, lower+MAX_SUB_WORD_LENGTH);
                    IntStream.range(lower+1, nn).forEach(upper-> {
                        String subString = s.substring(lower, upper).intern();
                        subWordMap.put(subString, M+subWordMap.getIfAbsent(subString, C));
                    });
                        }

                );
            });
            return parts[last];
        });
        wordMap.put(r, M+wordMap.getIfAbsent(r, C));



        System.out.println("singleMap.size=" + singleMap.size());
        for (int c : singleMap.keySet().toArray()) {
            System.out.println("c=" + String.format("0x%04x", c) + " " + formatChars(c) + " m=" + String.format("%9d", singleMap.get(c)));
        }
        System.out.println("wordMap.size=" + wordMap.size());
        System.out.println("subWordMap.size=" + subWordMap.size());
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
