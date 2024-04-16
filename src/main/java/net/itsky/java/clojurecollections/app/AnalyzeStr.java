package net.itsky.java.clojurecollections.app;

import org.eclipse.collections.api.factory.map.primitive.MutableIntLongMapFactory;
import org.eclipse.collections.api.factory.map.primitive.MutableObjectLongMapFactory;
import org.eclipse.collections.api.map.primitive.MutableIntLongMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.impl.map.mutable.primitive.MutableIntLongMapFactoryImpl;
import org.eclipse.collections.impl.map.mutable.primitive.MutableObjectLongMapFactoryImpl;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AnalyzeStr {

    private static final int C = 1;
    private static final int M = 100;

    private static final int MAX_SUB_WORD_LENGTH = 5;
    private static final int MAX_SUB_WORD_LENGTH2 = 5;

    private static final int MIN_SUBWORD5_FREQ = 100;


    private long lineCounter = 0;
    private long wordCounterS = 0;
    private long wordCounterL = 0;
    private long wordCounterI = 0;

    record StringInterval(String start, String end) implements Comparable<StringInterval> {
        @Override
        public int compareTo(StringInterval other) {
            return other.start.compareTo(this.start);
        }

        @Override
        public String toString() {
            return "[\"" + start + "\", \"" + end + "\")";
        }
    }


    private final MutableIntLongMapFactory factoryI = new MutableIntLongMapFactoryImpl();
    private final MutableObjectLongMapFactory factoryS = new MutableObjectLongMapFactoryImpl();

    private final SortedMap<StringInterval, Long> intervalMap = new TreeSortedMap<>();
    private final MutableObjectLongMap<String> wordMap = factoryS.empty();
    private final MutableObjectLongMap<CharSequence> subWordMap = factoryS.empty();



    MutableIntLongMap charMap = factoryI.empty();

    public AnalyzeStr() {

    }
    private void countLine() {
        lineCounter++;
        if ((lineCounter & 0x00ff) == 0) {
            System.out.println("lineCounter=" + lineCounter + " wordCounterS=" + wordCounterS + " wordCounterL=" + wordCounterL + " wordCounterI=" + wordCounterI + " charMap.size=" + charMap.size() + " wordMap.size=" + wordMap.size() + " subWordMap.size=" + subWordMap.size());
        }
    }

    private void countWordS() {
        wordCounterS++;
    }
    private void countWordL() {
        wordCounterL++;
    }
    private void countWordI() {
        wordCounterI++;
    }



    private void analyzeShortSubWords(String s) {
        countWordS();
        int n = s.length();
        IntStream.range(0, n).forEach(lower -> {
                    int nn = Math.min(n, lower+MAX_SUB_WORD_LENGTH);
                    IntStream.range(lower+1, nn).forEach(upper-> {
                        String subString = s.substring(lower, upper); // Nnew SubString(s, lower, upper);
                        subWordMap.put(subString, M+subWordMap.getIfAbsent(subString, C));
                    });
                }

        );

    }

    private void analyzeLongSubWords(String s) {
        countWordL();
        wordMap.put(s, M+wordMap.getIfAbsent(s, C));
        int n = s.length();
        if (n < MAX_SUB_WORD_LENGTH+1) {
            return;
        }
        IntStream.range(0, n-MAX_SUB_WORD_LENGTH-1)
                .filter(lower->subWordMap.getIfAbsent(s.substring(lower, lower+MAX_SUB_WORD_LENGTH), 0) > MIN_SUBWORD5_FREQ)
                .forEach(lower -> {
                    int nn = Math.min(n, lower+MAX_SUB_WORD_LENGTH2);
                    IntStream.range(lower+MAX_SUB_WORD_LENGTH+1, nn).forEach(upper-> {
                        String subString = s.substring(lower, upper); // Nnew SubString(s, lower, upper);
                        subWordMap.put(subString, M+subWordMap.getIfAbsent(subString, C));
                    });
                }
        );

    }

    private void analyzeIntervals(String s) {
        countWordI();
        int n = s.length();
        IntStream.range(0, n-1)
                .forEach(lower -> {
                            int nn = Math.min(n, lower+MAX_SUB_WORD_LENGTH2);
                            String subString = s.substring(lower, nn);
                            StringInterval subInt = new StringInterval(subString, subString);
                            SortedMap<StringInterval, Long> tail = intervalMap.tailMap(subInt);
                            if (! tail.isEmpty()) {
                                StringInterval key = tail.firstKey();
                                tail.put(key, tail.get(key)+1);
                            }
                        }
                );

    }

    public void analyze(List<String> lines) {
        lines.stream().map(line -> line+"\n").flatMapToInt(String::chars).forEach(c -> {
            charMap.put(c, M + charMap.getIfAbsent(c, C));
        });
        String r1 = lines.stream().map(line->line+"\n").reduce("", (leftoverWhiteSpace, line) -> {
            countLine();
            String[] parts = (leftoverWhiteSpace + line).splitWithDelimiters("\\w+", 0);
            int last = parts.length - 1;
            Stream.of(parts).limit(last).filter(s-> !s.isEmpty()).map(String::intern).forEach(this::analyzeShortSubWords);
            return parts[last];
        });

        int subWordMapSize1 = subWordMap.size();
        analyzeShortSubWords(r1);

        String r2 = lines.stream().map(line->line+"\n").reduce("", (leftoverWhiteSpace, line) -> {
            countLine();
            String[] parts = (leftoverWhiteSpace + line).splitWithDelimiters("\\w+", 0);
            int last = parts.length - 1;
            Stream.of(parts).limit(last).filter(s-> !s.isEmpty()).map(String::intern).forEach(this::analyzeLongSubWords);
            return parts[last];
        });
        analyzeLongSubWords(r2);

        System.out.println("singleMap.size=" + charMap.size());
        for (int c : charMap.keySet().toArray()) {
            System.out.println("c=" + String.format("0x%04x", c) + " " + formatChars(c) + " m=" + String.format("%9d", charMap.get(c)));
        }
        System.out.println("wordMap.size=" + wordMap.size());
        System.out.println("subWordMap1.size=" + subWordMapSize1);
        System.out.println("subWordMap2.size=" + subWordMap.size());"x".toString();

        CharSequence lastCharSeq = subWordMap.keySet().stream().sorted().filter(s -> s.length() <= 1 || subWordMap.get(s) >= MIN_SUBWORD5_FREQ).reduce("", (previous, current) -> { StringInterval interval = new StringInterval(previous.toString(), current.toString()); intervalMap.put(interval, 1L);return current; });
        intervalMap.put(new StringInterval(lastCharSeq.toString(), "\uffff\uffff\uffff\uffff\uffff\uffff\uffff\uffff\uffff\uffff\uffff\uffff"), 1L);
        String r3 = lines.stream().map(line->line+"\n").reduce("", (leftoverWhiteSpace, line) -> {
            countLine();
            String[] parts = (leftoverWhiteSpace + line).splitWithDelimiters("\\w+", 0);
            int last = parts.length - 1;
            Stream.of(parts).limit(last).filter(s-> !s.isEmpty()).map(String::intern).forEach(this::analyzeIntervals);
            return parts[last];
        });
        analyzeIntervals(r3);

        long subTotal = 0;
        long i = 0;
        for (SortedMap.Entry<StringInterval, Long> entry : intervalMap.reversed().entrySet()) {
            StringInterval key = entry.getKey();
            long value = entry.getValue();
            subTotal+= value;
            System.out.println(String.format("%8d v=%8d s=%8d %s", i++, value, subTotal, key.toString()));
        }
        System.out.println("intevalMap.size=" + intervalMap.size());
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
