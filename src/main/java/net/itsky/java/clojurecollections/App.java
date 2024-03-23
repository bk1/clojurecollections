package net.itsky.java.clojurecollections;

import net.itsky.java.sort.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {


        if (args.length == 0) {
            usage("needs file name");
        }
        if (args[0].equals("--help")) {
            usage(null);
        }
        File file = new File(args[0]);
        if (!file.exists()) {
            usage("file=" + file + " does not exist");
        }
        if (!file.canRead()) {
            usage("file=" + file + " is not readable");
        }
        if (!file.isFile()) {
            usage("file=" + file + " is not a regular file");
        }
        System.out.println("file=" + file);

        List<String> lines = new ArrayList<>(12_000_000);
        try (InputStream is = new FileInputStream(file)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
        } catch (IOException ex) {
            usage("exception ex=" + ex);
        }
        System.out.println("" + lines.size() + " lines");

        Analyze analyze = new Analyze();
        analyze.analyze(lines);
        System.out.println();

        Sort<String, List<String>> heapSortList = new HeapSort<>();
        Sort<String, PersistentList<String>> heapSortP = new HeapSort<>();
        Sort<String, TransientList<String>> heapSortT = new HeapSort<>();

        Sort<String, List<String>> ternaryHeapSortList = new TerneryHeapSort<>();
        Sort<String, PersistentList<String>> ternaryHeapSortP = new TerneryHeapSort<>();
        Sort<String, TransientList<String>> ternaryHeapSortT = new TerneryHeapSort<>();

        Sort<String, List<String>> quickSortList = new QuickSort<>();
        Sort<String, PersistentList<String>> quickSortP = new QuickSort<>();
        Sort<String, TransientList<String>> quickSortT = new QuickSort<>();

        long jl = 0;
        long hl = 0;
        long hp = 0;
        long ht = 0;
        long tl = 0;
        long tp = 0;
        long tt = 0;
        long ql = 0;
        long qp = 0;
        long qt = 0;

        for (int i = 0; i < 10; i++) {
            List<String> jlist = new ArrayList<>(lines);
            long t0 = System.currentTimeMillis();
            Collections.sort(jlist);
            jl += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " jl=" + jl / (i + 1));

            List<String> list = new ArrayList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedList = heapSortList.sort(list, Comparator.naturalOrder(), new ListSwapper<>());
            hl += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " hl=" + hl / (i + 1));

            PersistentList<String> plist = new PersistentList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedP = heapSortP.sort(plist, Comparator.naturalOrder(), new PersistentListSwapper<String>());
            hp += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " hp=" + hp / (i + 1));

            TransientList<String> tlist = new TransientList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedT = heapSortT.sort(tlist, Comparator.naturalOrder(), new TransientListSwapper<String>());
            ht += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " ht=" + ht / (i + 1));

            if (! jlist.equals(sortedList)) {
                System.out.println("heapsort failed");
            }
            if (! sortedList.equals(sortedP) || ! sortedList.equals(sortedT)) {
                System.out.println("sortedList, sortedP, sorted T differ for heapsort");
            }

            list = new ArrayList<>(lines);
            t0 = System.currentTimeMillis();
            sortedList = ternaryHeapSortList.sort(list, Comparator.naturalOrder(), new ListSwapper<>());
            tl += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " tl=" + tl / (i + 1));

            plist = new PersistentList<>(lines);
            t0 = System.currentTimeMillis();
            sortedP = ternaryHeapSortP.sort(plist, Comparator.naturalOrder(), new PersistentListSwapper<String>());
            tp += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " tp=" + tp / (i + 1));

            tlist = new TransientList<>(lines);
            t0 = System.currentTimeMillis();
            sortedT = ternaryHeapSortT.sort(tlist, Comparator.naturalOrder(), new TransientListSwapper<String>());
            tt += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " tt=" + tt / (i + 1));

            if (! jlist.equals(sortedList)) {
                System.out.println("heapsort failed");
            }
            if (! sortedList.equals(sortedP) || ! sortedList.equals(sortedT)) {
                System.out.println("sortedList, sortedP, sorted T differ for heapsort");
            }

            list = new ArrayList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedListQ = quickSortList.sort(list, Comparator.naturalOrder(), new ListSwapper<>());
            ql += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " ql=" + ql / (i + 1));

            plist = new PersistentList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedPQ = quickSortP.sort(plist, Comparator.naturalOrder(), new PersistentListSwapper<String>());
            qp += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " qp=" + qp / (i + 1));

            tlist = new TransientList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedTQ = quickSortT.sort(tlist, Comparator.naturalOrder(), new TransientListSwapper<String>());
            qt += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " qt=" + qt / (i + 1));

            if (! jlist.equals(sortedList)) {
                System.out.println("quicksort failed");
            }
            if (! sortedList.equals(sortedP) || ! sortedList.equals(sortedT)) {
                System.out.println("sortedList, sortedP, sorted T differ for quicksort");
            }

        }
    }

    private static void usage(String msg) {
        if (msg != null) {
            System.out.println(msg);
        }
        System.out.println("""
                USAGE
                                
                $0 filename
                                
                reads a file name and sorts it with differnt methods for performance measurement
                """);
        if (msg != null) {
            System.exit(1);
        } else {
            System.exit(0);
        }
    }
}
