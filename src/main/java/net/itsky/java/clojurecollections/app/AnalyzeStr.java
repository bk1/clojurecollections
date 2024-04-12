package net.itsky.java.clojurecollections.app;

import net.itsky.java.clojurecollections.util.SubString;
import org.eclipse.collections.api.factory.map.primitive.MutableIntLongMapFactory;
import org.eclipse.collections.api.factory.map.primitive.MutableObjectLongMapFactory;
import org.eclipse.collections.api.map.primitive.MutableIntLongMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.impl.map.mutable.primitive.MutableIntLongMapFactoryImpl;
import org.eclipse.collections.impl.map.mutable.primitive.MutableObjectLongMapFactoryImpl;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AnalyzeStr {

    private static final int C = 1;
    private static final int M = 100;

    private static final int MAX_SUB_WORD_LENGTH = 10;

    private long lineCounter = 0;
    private long wordCounter = 0;


    private final MutableIntLongMapFactory factoryI = new MutableIntLongMapFactoryImpl();
    private final MutableObjectLongMapFactory factoryS = new MutableObjectLongMapFactoryImpl();
    private final MutableObjectLongMap<String> wordMap = factoryS.empty();
    private final MutableObjectLongMap<SubString> subWordMap = factoryS.empty();

    MutableIntLongMap charMap = factoryI.empty();

    public AnalyzeStr() {

    }
    private void countLine() {
        lineCounter++;
        if ((lineCounter & 0x00ff) == 0) {
            System.out.println("lineCounter=" + lineCounter + " wordCounter=" + wordCounter + "  charMap.size=" + charMap.size() + " wordMap.size=" + wordMap.size() + " subWordMap.size=" + subWordMap.size());
        }
    }

    private void countWord() {
        wordCounter++;
    }


    private void analyzeWord(String s) {
        countWord();
        wordMap.put(s, M+wordMap.getIfAbsent(s, C));
        int n = s.length();
        IntStream.range(0, n).forEach(lower -> {
                    int nn = Math.min(n, lower+MAX_SUB_WORD_LENGTH);
                    IntStream.range(lower+1, nn).forEach(upper-> {
                        SubString subString = new SubString(s, lower, upper);
                        subWordMap.put(subString, M+subWordMap.getIfAbsent(subString, C));
                    });
                }

        );

    }

    public void analyze(List<String> lines) {
        lines.stream().map(line -> line+"\n").flatMapToInt(String::chars).forEach(c -> {
            charMap.put(c, M + charMap.getIfAbsent(c, C));
        });
        String r = lines.stream().map(line->line+"\n").reduce("", (leftoverWhiteSpace, line) -> {
            countLine();
            String[] parts = (leftoverWhiteSpace + line).splitWithDelimiters("\\w+", 0);
            int last = parts.length - 1;
            Stream.of(parts).limit(last).filter(s-> !s.isEmpty()).map(String::intern).forEach(this::analyzeWord);
            return parts[last];
        });
        analyzeWord(r);

        System.out.println("singleMap.size=" + charMap.size());
        for (int c : charMap.keySet().toArray()) {
            System.out.println("c=" + String.format("0x%04x", c) + " " + formatChars(c) + " m=" + String.format("%9d", charMap.get(c)));
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
