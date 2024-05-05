package net.itsky.java.clojurecollections.analyze;

import net.itsky.java.sort.metric.MetricDataTree;
import org.eclipse.collections.api.factory.map.primitive.MutableIntLongMapFactory;
import org.eclipse.collections.api.factory.map.primitive.MutableObjectLongMapFactory;
import org.eclipse.collections.api.map.primitive.MutableIntLongMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.impl.map.mutable.primitive.MutableIntLongMapFactoryImpl;
import org.eclipse.collections.impl.map.mutable.primitive.MutableObjectLongMapFactoryImpl;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;

import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AnalyzeStr {

    private static final long MIN_VAL = 1;
    private static final long NORMAL_VAL = 100;
    private static final long START_VAL = 1000;

    private static final int MAX_SHORT_SUB_WORD_LENGTH = 5;
    private static final int MAX_LONG_SUB_WORD_LENGTH = 10;

    private static final long MIN_SUBWORD5_FREQ = 100 * NORMAL_VAL + MIN_VAL;


    private static final int MAX_FREQ_LENGTH = 6;

    private static final double LONG_MAX_VALUE = (double) Long.MAX_VALUE;

    private long step(int l) {
        if (l <= 1) {
            return 0;
        } else {
            return 1L << (20 + 2 * (l - 2));
        }
    }

    private long lineCounter = 0;
    private long wordCounterS = 0;
    private long wordCounterL = 0;
    private long wordCounterI = 0;

    record KeyValue(String key, long value) {
    }

    ;

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
            long val = lower == 0 ? START_VAL : NORMAL_VAL;
            int nn = Math.min(n, lower + MAX_SHORT_SUB_WORD_LENGTH);
            IntStream.range(lower + 1, nn).forEach(upper -> {
                String subString = s.substring(lower, upper); // Nnew SubString(s, lower, upper);
                subWordMap.put(subString, val + subWordMap.getIfAbsent(subString, MIN_VAL));
            });
        });
    }

    private void analyzeLongSubWords(String word) {
        countWordL();
        wordMap.put(word, NORMAL_VAL + wordMap.getIfAbsent(word, MIN_VAL));
        int wordLength = word.length();
        if (wordLength < MAX_SHORT_SUB_WORD_LENGTH + 1) {
            return;
        }
        IntStream.range(0, wordLength - MAX_SHORT_SUB_WORD_LENGTH - 1)
                .filter(lower -> subWordMap.getIfAbsent(word.substring(lower, lower + MAX_SHORT_SUB_WORD_LENGTH), 0) > MIN_SUBWORD5_FREQ)
                .forEach(lower -> {
                    System.out.println("lower=" + lower + " word=" + word);
                    long val = lower == 0 ? START_VAL : NORMAL_VAL;
                    int nn = Math.min(wordLength, lower + MAX_LONG_SUB_WORD_LENGTH);
                    IntStream.range(lower + MAX_SHORT_SUB_WORD_LENGTH + 1, nn)
                            .forEach(upper -> {
                                String subString = word.substring(lower, upper); // Nnew SubString(s, lower, upper);
                                System.out.println("lower=" + lower + " upper=" + upper + " substring=" + subString);
                                subWordMap.put(subString, val + subWordMap.getIfAbsent(subString, MIN_VAL));
                            });
                });
    }

    private String analyzeIntervals(String s, int maxLeftOverSize) {
        countWordI();
        int n = s.length();
        IntStream.range(0, n - maxLeftOverSize)
                .forEach(lower -> {
                            long val = lower == 0 ? START_VAL : NORMAL_VAL;
                            int m = Math.min(n, lower + MAX_LONG_SUB_WORD_LENGTH);
                            IntStream.rangeClosed(lower + 1, m).forEach(upper -> {
                                String subString = s.substring(lower, upper);
                                StringInterval subInt = new StringInterval(subString, subString);
                                SortedMap<StringInterval, Long> tail = intervalMap.tailMap(subInt);
                                if (!tail.isEmpty()) {
                                    StringInterval key = tail.firstKey();
                                    tail.put(key, tail.get(key) + val);
                                }
                            });
                        }
                );
        if (n < maxLeftOverSize) {
            return s;
        } else {
            return s.substring(n - maxLeftOverSize, n);
        }
    }

    public void analyzeFileContent(List<String> lines) {
        // count frequencies of single characters
        lines.stream().map(line -> line + "\n").flatMapToInt(String::chars).forEach(c -> {
            charMap.put(c, NORMAL_VAL + charMap.getIfAbsent(c, MIN_VAL));
        });

        // find short substrings
        String r1 = lines.stream().map(line -> line + "\n").reduce("", (leftoverWhiteSpace, line) -> {
            countLine();
            String[] parts = (leftoverWhiteSpace + line).splitWithDelimiters("\\w+", 0);
            int last = parts.length - 1;
            Stream.of(parts).limit(last).filter(s -> !s.isEmpty()).map(String::intern).forEach(this::analyzeShortSubWords);
            return parts[last];
        });

        int subWordMapSize1 = subWordMap.size();
        analyzeShortSubWords(r1);

        // find long substrings that start with a short substring with a minimum frequency
        String r2 = lines.stream().map(line -> line + "\n").reduce("", (leftoverWhiteSpace, line) -> {
            countLine();
            String[] parts = (leftoverWhiteSpace + line).splitWithDelimiters("\\w+", 0);
            int last = parts.length - 1;
            Stream.of(parts).limit(last).filter(s -> !s.isEmpty()).map(String::intern).forEach(this::analyzeLongSubWords);
            return parts[last];
        });
        analyzeLongSubWords(r2);

        System.out.println("singleMap.size=" + charMap.size());
        for (int c : charMap.keySet().toArray()) {
            System.out.println("c=" + String.format("0x%04x", c) + " " + formatChars(c) + " m=" + String.format("%9d", charMap.get(c)));
        }
        long totalCount = subWordMap.values().sum();
        System.out.println("wordMap.size=" + wordMap.size());
        System.out.println("subWordMap1.size=" + subWordMapSize1);
        System.out.println("subWordMap2.size=" + subWordMap.size());
        System.out.println("totalCount=" + totalCount);
        System.exit(0);

        CharSequence lastCharSeq = subWordMap
                .keySet()
                .stream()
                .sorted()
                .filter(s -> {
                    int l = s.length();
                    return l <= 1 || subWordMap.get(s) >= l * START_VAL;
                })
                .reduce("", (previous, current) -> {
                    String next = current + "\u0000";
                    StringInterval intervalA = new StringInterval(previous.toString(), current.toString());
                    intervalMap.put(intervalA, 1L);
                    StringInterval intervalB = new StringInterval(current.toString(), next.toString());
                    intervalMap.put(intervalB, 1L);
                    return next;
                });
        intervalMap.put(new StringInterval(lastCharSeq.toString(), "\uffff\uffff\uffff\uffff\uffff\uffff\uffff\uffff\uffff\uffff\uffff\uffff"), 1L);
        String r3 = lines.stream().map(line -> line + "\n").reduce("", (leftOver, line) -> {
            countLine();
            return analyzeIntervals(leftOver + line, MAX_LONG_SUB_WORD_LENGTH - 1);
        });
        analyzeIntervals(r3, 0);

        SortedMap<String, Long> dataMap = new TreeSortedMap<>(Comparator.reverseOrder());
        SortedMap<String, Long> filteredDataMap = new TreeSortedMap<>(Comparator.reverseOrder());

        long total = intervalMap.values().stream().mapToLong(x -> x.longValue()).sum();
        double factor = Long.MAX_VALUE / (double) total;
        System.out.println("total=" + total + " factor=" + factor);
        long subTotal = 0;
        long i = 0;
        for (SortedMap.Entry<StringInterval, Long> entry : intervalMap.reversed().entrySet()) {
            StringInterval key = entry.getKey();
            long value = entry.getValue();
            subTotal += value;
            double metricDouble = Math.min(subTotal * factor, LONG_MAX_VALUE);
            long metric = (long) metricDouble;
            if (Math.abs(metric - metricDouble) > 1000) {
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
        try (OutputStream stream = new FileOutputStream("frequencies.dat")) {
            try (BufferedOutputStream bs = new BufferedOutputStream(stream)) {
                try (DataOutputStream ds = new DataOutputStream(bs)) {
                    subWordMap.keySet()
                            .stream()
                            .filter(k -> subWordMap.getIfAbsent(k, 0) > MIN_SUBWORD5_FREQ)
                            .filter(k -> k.length() <= MAX_FREQ_LENGTH)
                            .map(k -> new KeyValue(k.toString(), subWordMap.getIfAbsent(k, 0)))
                            .forEach(kv -> {
                                try {
                                    ds.writeUTF(kv.key());
                                    ds.writeLong(kv.value());
                                } catch (IOException ioex) {
                                    throw new UncheckedIOException(ioex);
                                }
                            });


                }
            }
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
