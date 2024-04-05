package net.itsky.java.clojurecollections.app;

import net.itsky.java.sort.FlashSortArray;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Hello world!
 */
public class SortFileNumbers {

    private record LongWithIdx(long value, int idx) {
    }

    ;

    public void sortFileContent(List<String> lines) {
        int[] chars = lines.stream().flatMap(line -> line.chars().boxed()).mapToInt(x -> x.intValue()).toArray();
        int m = chars.length;
        int n = (m + 3) / 4;
        System.out.println("chars.length=" + m + " numbers.length=" + n);
        long[] numbers = IntStream.range(0, n).mapToLong(i -> {
            int idx = i * 4;
            long y = chars[idx++];
            for (int j = 1; j < 4; j++) {
                y <<= 8;
                if (idx < m) {
                    y += chars[idx++];
                }
            }
            return y;
        }).toArray();
        sortFileContent(numbers);
    }

    public void sortFileContent(long[] numbers) {

        long jl = 0;
        long fl = 0;
        long flA = 0;
        long flB = 0;
        long flC = 0;
        long flD = 0;
        long flE = 0;
        long flU = 0;

        long fl01 = 0;
        long fl02 = 0;
        long fl03 = 0;
        long fl08 = 0;

        for (int i = 0; i < 10; i++) {
            long[] jarray = Arrays.stream(numbers).toArray();
            long t0 = System.currentTimeMillis();
            Arrays.sort(jarray);
            jl += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " jl=" + jl / (i + 1));

            long[] farray = Arrays.stream(numbers).toArray();
            t0 = System.currentTimeMillis();
            FlashSortArray.fsort(farray);
            fl += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " fl=" + fl / (i + 1));
            LongArrayList jlist = new LongArrayList(jarray);
            LongArrayList fList = new LongArrayList(farray);
            if (!jlist.equals(fList)) {
                System.out.println("flashsort failed");
            }

            farray = Arrays.stream(numbers).toArray();
            t0 = System.currentTimeMillis();
            FlashSortArray.fsort(farray, 0.42, 1000);
            flA += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " flA=" + flA / (i + 1));
            fList = new LongArrayList(farray);
            if (!jlist.equals(fList)) {
                System.out.println("flashsort failed");
            }

            farray = Arrays.stream(numbers).toArray();
            t0 = System.currentTimeMillis();
            FlashSortArray.fsort(farray, 0.42, 3000);
            flB += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " flB=" + flB / (i + 1));
            fList = new LongArrayList(farray);
            if (!jlist.equals(fList)) {
                System.out.println("flashsort failed");
            }

            farray = Arrays.stream(numbers).toArray();
            t0 = System.currentTimeMillis();
            FlashSortArray.fsort(farray, 0.42, 30000);
            flC += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " flC=" + flC / (i + 1));
            fList = new LongArrayList(farray);
            if (!jlist.equals(fList)) {
                System.out.println("flashsort failed");
            }

            farray = Arrays.stream(numbers).toArray();
            t0 = System.currentTimeMillis();
            FlashSortArray.fsort(farray, 0.42, 100_000);
            flD += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " flD=" + flD / (i + 1));
            fList = new LongArrayList(farray);
            if (!jlist.equals(fList)) {
                System.out.println("flashsort failed");
            }

            farray = Arrays.stream(numbers).toArray();
            t0 = System.currentTimeMillis();
            FlashSortArray.fsort(farray, 0.42, 10_000_000);
            flE += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " flE=" + flE / (i + 1));
            fList = new LongArrayList(farray);
            if (!jlist.equals(fList)) {
                System.out.println("flashsort failed");
            }

            farray = Arrays.stream(numbers).toArray();
            t0 = System.currentTimeMillis();
            FlashSortArray.fsort(farray, 0.42, 1_000_000_000);
            flU += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " flU=" + flU / (i + 1));
            fList = new LongArrayList(farray);
            if (!jlist.equals(fList)) {
                System.out.println("flashsort failed");
            }

            farray = Arrays.stream(numbers).toArray();
            t0 = System.currentTimeMillis();
            FlashSortArray.fsort(farray, 0.1);
            fl01 += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " fl01=" + fl01 / (i + 1));
            fList = new LongArrayList(farray);
            if (!jlist.equals(fList)) {
                System.out.println("flashsort failed");
            }

            farray = Arrays.stream(numbers).toArray();
            t0 = System.currentTimeMillis();
            FlashSortArray.fsort(farray, 0.2);
            fl02 += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " fl02=" + fl02 / (i + 1));
            fList = new LongArrayList(farray);
            if (!jlist.equals(fList)) {
                System.out.println("flashsort failed");
            }

            farray = Arrays.stream(numbers).toArray();
            t0 = System.currentTimeMillis();
            FlashSortArray.fsort(farray, 0.3);
            fl03 += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " fl03=" + fl03 / (i + 1));
            fList = new LongArrayList(farray);
            if (!jlist.equals(fList)) {
                System.out.println("flashsort failed");
            }

            farray = Arrays.stream(numbers).toArray();
            t0 = System.currentTimeMillis();
            FlashSortArray.fsort(farray, 0.8);
            fl08 += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " fl08=" + fl08 / (i + 1));
            fList = new LongArrayList(farray);
            if (!jlist.equals(fList)) {
                System.out.println("flashsort failed");
            }
        }
    }
}