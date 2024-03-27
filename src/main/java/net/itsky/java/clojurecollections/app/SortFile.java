package net.itsky.java.clojurecollections.app;

import net.itsky.java.clojurecollections.PersistentList;
import net.itsky.java.clojurecollections.TransientList;
import net.itsky.java.sort.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Hello world!
 */
public class SortFile {
    public void sortFileContent(List<String> lines) {

        Sort<String, List<String>> heapSortList = new HeapSort<>();
        Sort<String, PersistentList<String>> heapSortP = new HeapSort<>();
        Sort<String, TransientList<String>> heapSortT = new HeapSort<>();

        Sort<String, List<String>> ternaryHeapSortList = new TerneryHeapSort<>();
        Sort<String, PersistentList<String>> ternaryHeapSortP = new TerneryHeapSort<>();
        Sort<String, TransientList<String>> ternaryHeapSortT = new TerneryHeapSort<>();

        Sort<String, List<String>> quickSort3List = new QuickSort<>(QuickSort.PivotStyle.MEDIAN3);
        Sort<String, PersistentList<String>> quickSort3P = new QuickSort<>(QuickSort.PivotStyle.MEDIAN3);
        Sort<String, TransientList<String>> quickSort3T = new QuickSort<>(QuickSort.PivotStyle.MEDIAN3);
        Sort<String, List<String>> quickSort5List = new QuickSort<>(QuickSort.PivotStyle.MEDIAN5);
        Sort<String, PersistentList<String>> quickSort5P = new QuickSort<>(QuickSort.PivotStyle.MEDIAN5);
        Sort<String, TransientList<String>> quickSort5T = new QuickSort<>(QuickSort.PivotStyle.MEDIAN5);
        Sort<String, List<String>> randomQuickSortList = new QuickSort<>(QuickSort.PivotStyle.RANDOM);
        Sort<String, PersistentList<String>> randomQuickSortP = new QuickSort<>(QuickSort.PivotStyle.RANDOM);
        Sort<String, TransientList<String>> randomQuickSortT = new QuickSort<>(QuickSort.PivotStyle.RANDOM);

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
        long q5l = 0;
        long q5p = 0;
        long q5t = 0;
        long qrl = 0;
        long qrp = 0;
        long qrt = 0;

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
                System.out.println("sortedP or sortedT differ for heapsort");
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
                System.out.println("ternary heapsort failed");
            }
            if (! sortedList.equals(sortedP) || ! sortedList.equals(sortedT)) {
                System.out.println("sortedP or sortedT differ for ternary heapsort");
            }

            list = new ArrayList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sorted3ListQ = quickSort3List.sort(list, Comparator.naturalOrder(), new ListSwapper<>());
            ql += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " ql=" + ql / (i + 1));

            plist = new PersistentList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sorted3PQ = quickSort3P.sort(plist, Comparator.naturalOrder(), new PersistentListSwapper<String>());
            qp += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " qp=" + qp / (i + 1));

            tlist = new TransientList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sorted3TQ = quickSort3T.sort(tlist, Comparator.naturalOrder(), new TransientListSwapper<String>());
            qt += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " qt=" + qt / (i + 1));

            if (! jlist.equals(sorted3ListQ)) {
                System.out.println("quicksort failed (3)");
            }
            if (! sortedList.equals(sorted3PQ) || ! sortedList.equals(sorted3TQ)) {
                System.out.println("sorted3PQ or sorted3TQ differ for quicksort (3)");
            }


            list = new ArrayList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedListQ = quickSort5List.sort(list, Comparator.naturalOrder(), new ListSwapper<>());
            q5l += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " q5l=" + q5l / (i + 1));

            plist = new PersistentList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedPQ = quickSort5P.sort(plist, Comparator.naturalOrder(), new PersistentListSwapper<String>());
            q5p += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " q5p=" + q5p / (i + 1));

            tlist = new TransientList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedTQ = quickSort5T.sort(tlist, Comparator.naturalOrder(), new TransientListSwapper<String>());
            q5t += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " q5t=" + q5t / (i + 1));

            if (! jlist.equals(sortedListQ)) {
                System.out.println("quicksort failed (5)");
            }
            if (! sortedList.equals(sortedPQ) || ! sortedList.equals(sortedTQ)) {
                System.out.println("sortedPQ or sortedTQ differ for quicksort (5)");
            }


            list = new ArrayList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedRListQ = randomQuickSortList.sort(list, Comparator.naturalOrder(), new ListSwapper<>());
            qrl += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " qrl=" + qrl / (i + 1));

            plist = new PersistentList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedRPQ = randomQuickSortP.sort(plist, Comparator.naturalOrder(), new PersistentListSwapper<String>());
            qrp += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " qrp=" + qrp / (i + 1));

            tlist = new TransientList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedRTQ = randomQuickSortT.sort(tlist, Comparator.naturalOrder(), new TransientListSwapper<String>());
            qrt += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " qrt=" + qrt / (i + 1));

            if (! jlist.equals(sortedRListQ)) {
                System.out.println("quicksort failed (R)");
            }
            if (! sortedList.equals(sortedRPQ) || ! sortedList.equals(sortedRTQ)) {
                System.out.println("sortedRPQ or sortedRTQ differ for quicksort (R)");
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
