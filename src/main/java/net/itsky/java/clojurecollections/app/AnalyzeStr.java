package net.itsky.java.clojurecollections.app;

import net.itsky.java.clojurecollections.util.MetricDataTree;
import org.eclipse.collections.api.factory.map.primitive.MutableIntLongMapFactory;
import org.eclipse.collections.api.factory.map.primitive.MutableObjectLongMapFactory;
import org.eclipse.collections.api.map.primitive.MutableIntLongMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.impl.map.mutable.primitive.MutableIntLongMapFactoryImpl;
import org.eclipse.collections.impl.map.mutable.primitive.MutableObjectLongMapFactoryImpl;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AnalyzeStr {

    private static final int C = 1;
    private static final int M = 100;

    private static final int MAX_SHORT_SUB_WORD_LENGTH = 5;
    private static final int MAX_LONG_SUB_WORD_LENGTH = 10;

    private static final int MIN_SUBWORD5_FREQ = 100*M+C;


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
                    int nn = Math.min(n, lower+ MAX_SHORT_SUB_WORD_LENGTH);
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
        if (n < MAX_SHORT_SUB_WORD_LENGTH +1) {
            return;
        }
        IntStream.range(0, n- MAX_SHORT_SUB_WORD_LENGTH -1)
                .filter(lower->subWordMap.getIfAbsent(s.substring(lower, lower+ MAX_SHORT_SUB_WORD_LENGTH), 0) > MIN_SUBWORD5_FREQ)
                .forEach(lower -> {
                    int nn = Math.min(n, lower+ MAX_LONG_SUB_WORD_LENGTH);
                    IntStream.range(lower+ MAX_SHORT_SUB_WORD_LENGTH +1, nn).forEach(upper-> {
                        String subString = s.substring(lower, upper); // Nnew SubString(s, lower, upper);
                        subWordMap.put(subString, M+subWordMap.getIfAbsent(subString, C));
                    });
                }
        );

    }

    private String analyzeIntervals(String s, int maxLeftOverSize) {
        countWordI();
        int n = s.length();
        IntStream.range(0, n-maxLeftOverSize)
                .forEach(lower -> {
                            int upper = Math.min(n, lower+ MAX_LONG_SUB_WORD_LENGTH);
                            String subString = s.substring(lower, upper);
                            StringInterval subInt = new StringInterval(subString, subString);
                            SortedMap<StringInterval, Long> tail = intervalMap.tailMap(subInt);
                            if (! tail.isEmpty()) {
                                StringInterval key = tail.firstKey();
                                tail.put(key, tail.get(key)+1);
                            }
                        }
                );
        if (n < maxLeftOverSize) {
            return s;
        } else {
            return s.substring(n-maxLeftOverSize, n);
        }
    }

    public void analyzeFileContent(List<String> lines) {
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
        String r3 = lines.stream().map(line->line+"\n").reduce("", (leftOver, line) -> {
            countLine();
            return analyzeIntervals(leftOver+line, MAX_LONG_SUB_WORD_LENGTH-1);
        });
        analyzeIntervals(r3, 0);

        SortedMap<String, Long> dataMap = new TreeSortedMap<>(Comparator.reverseOrder());
        long total = intervalMap.values().stream().mapToLong(x->x.longValue()).sum();
        double factor = 2.0*Long.MAX_VALUE / total;
        System.out.println("total=" + total + " factor=" + factor);
        long subTotal = 0;
        long i = 0;
        for (SortedMap.Entry<StringInterval, Long> entry : intervalMap.reversed().entrySet()) {
            StringInterval key = entry.getKey();
            long value = entry.getValue();
            subTotal+= value;
            double metricDouble = subTotal * factor - Long.MAX_VALUE;
            long metric = (long) metricDouble;
            if (Math.abs(metric-metricDouble) >1000) {
                System.out.println("metric=" + metric + " metricDouble=" + metricDouble);
            }
            dataMap.put(key.start(), metric);
            System.out.println(String.format("%8d v=%8d m=%8d s=%8d %s", i++, value, metric, subTotal, key.toString()));
        }
        System.out.println("intervalMap.size=" + intervalMap.size());
        try (OutputStream stream = new FileOutputStream("metric.dat")) {
            MetricDataTree metricData = new MetricDataTree(dataMap);
            metricData.write(stream);
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            ioex.printStackTrace();
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
