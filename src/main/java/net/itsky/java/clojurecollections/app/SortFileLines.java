package net.itsky.java.clojurecollections.app;

import net.itsky.java.clojurecollections.PersistentList;
import net.itsky.java.clojurecollections.TransientList;
import net.itsky.java.sort.metric.Utf16StringMetric;
import net.itsky.java.sort.metric.MetricDataForest;
import net.itsky.java.sort.metric.MetricDataOneChar;
import net.itsky.java.sort.metric.MetricDataTree;
import net.itsky.java.sort.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Hello world!
 */
public class SortFileLines {
    public void sortFileContent(List<String> lines) {

        Sort<String, List<String>> heapSortList = new HeapSort<>();
        Sort<String, PersistentList<String>> heapSortP = new HeapSort<>();
        Sort<String, TransientList<String>> heapSortT = new HeapSort<>();

        Sort<String, List<String>> ternaryHeapSortList = new TerneryHeapSort<>();
        Sort<String, PersistentList<String>> ternaryHeapSortP = new TerneryHeapSort<>();
        Sort<String, TransientList<String>> ternaryHeapSortT = new TerneryHeapSort<>();

        Sort<String, List<String>> quickSort3List = new QuickSort<>(QuickSortPivotStyle.MEDIAN3);
        Sort<String, PersistentList<String>> quickSort3P = new QuickSort<>(QuickSortPivotStyle.MEDIAN3);
        Sort<String, TransientList<String>> quickSort3T = new QuickSort<>(QuickSortPivotStyle.MEDIAN3);
        Sort<String, List<String>> quickSortMList = new QuickSort<>(QuickSortPivotStyle.MIDDLE);
        Sort<String, PersistentList<String>> quickSortMP = new QuickSort<>(QuickSortPivotStyle.MIDDLE);
        Sort<String, TransientList<String>> quickSortMT = new QuickSort<>(QuickSortPivotStyle.MIDDLE);
        Sort<String, List<String>> quickSort5List = new QuickSort<>(QuickSortPivotStyle.MEDIAN5);
        Sort<String, PersistentList<String>> quickSort5P = new QuickSort<>(QuickSortPivotStyle.MEDIAN5);
        Sort<String, TransientList<String>> quickSort5T = new QuickSort<>(QuickSortPivotStyle.MEDIAN5);
        Sort<String, List<String>> randomQuickSortList = new QuickSort<>(QuickSortPivotStyle.RANDOM);
        Sort<String, PersistentList<String>> randomQuickSortP = new QuickSort<>(QuickSortPivotStyle.RANDOM);
        Sort<String, TransientList<String>> randomQuickSortT = new QuickSort<>(QuickSortPivotStyle.RANDOM);

        Sort<String, List<String>> parallelQuickSortList = new ParallelQuickSort<>();
        Sort<String, PersistentList<String>> parallelQuickSortP = new ParallelQuickSort<>();
        Sort<String, TransientList<String>> parallelQuickSortT = new ParallelQuickSort<>();

        SortMetricized<String, List<String>> defaultFlashSortList = new FlashSort<>();
        SortMetricized<String, PersistentList<String>> defaultFlashSortP = new FlashSort<>();
        SortMetricized<String, TransientList<String>> defaultFlashSortT = new FlashSort<>();


        MetricDataTree metricDataTree = new MetricDataTree();
        MetricDataOneChar metricDataOneChar = new MetricDataOneChar();
        MetricDataForest metricDataForest = new MetricDataForest();
        try (InputStream stream = new FileInputStream("metric.dat")) {
            metricDataTree.read(stream);
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            ioex.printStackTrace();
        }
        System.out.println("metricDataTree ready");
        try (InputStream stream = new FileInputStream("1c-metric.dat")) {
            metricDataOneChar.read(stream);
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            ioex.printStackTrace();
        }
        System.out.println("metricDataOneChar ready");
        try (InputStream stream = new FileInputStream("metric.dat")) {
            metricDataForest.read(stream);
        } catch (IOException ioex) {
            System.out.println("ioex=" + ioex);
            ioex.printStackTrace();
        }
        System.out.println("metricDataForest ready");

        long jl = 0;
        long hl = 0;
        long hp = 0;
        long ht = 0;
        long tl = 0;
        long tp = 0;
        long tt = 0;

        long q3l = 0;
        long q3p = 0;
        long q3t = 0;
        long q5l = 0;
        long q5p = 0;
        long q5t = 0;
        long qrl = 0;
        long qrp = 0;
        long qrt = 0;
        long qml = 0;
        long qmp = 0;
        long qmt = 0;

        long pl = 0;
        long pp = 0;
        long pt = 0;

        long fl = 0;
        long fp = 0;
        long ft = 0;
        long fdl = 0;
        long fdp = 0;
        long fdt = 0;
        long fel = 0;
        long fep = 0;
        long fet = 0;
        long ffl = 0;
        long ffp = 0;
        long fft = 0;

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

            if (!jlist.equals(sortedList)) {
                System.out.println("heapsort failed");
            }
            if (!sortedList.equals(sortedP) || !sortedList.equals(sortedT)) {
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

            if (!jlist.equals(sortedList)) {
                System.out.println("ternary heapsort failed");
            }
            if (!sortedList.equals(sortedP) || !sortedList.equals(sortedT)) {
                System.out.println("sortedP or sortedT differ for ternary heapsort");
            }

            list = new ArrayList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sorted3ListQ = quickSort3List.sort(list, Comparator.naturalOrder(), new ListSwapper<>());
            q3l += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " q3l=" + q3l / (i + 1));

            plist = new PersistentList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sorted3PQ = quickSort3P.sort(plist, Comparator.naturalOrder(), new PersistentListSwapper<String>());
            q3p += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " q3p=" + q3p / (i + 1));

            tlist = new TransientList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sorted3TQ = quickSort3T.sort(tlist, Comparator.naturalOrder(), new TransientListSwapper<String>());
            q3t += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " q3t=" + q3t / (i + 1));

            if (!jlist.equals(sorted3ListQ)) {
                System.out.println("quicksort failed (3)");
            }
            if (!sortedList.equals(sorted3PQ) || !sortedList.equals(sorted3TQ)) {
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

            if (!jlist.equals(sortedListQ)) {
                System.out.println("quicksort failed (5)");
            }
            if (!sortedList.equals(sortedPQ) || !sortedList.equals(sortedTQ)) {
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

            if (!jlist.equals(sortedRListQ)) {
                System.out.println("quicksort failed (R)");
            }
            if (!sortedList.equals(sortedRPQ) || !sortedList.equals(sortedRTQ)) {
                System.out.println("sortedRPQ or sortedRTQ differ for quicksort (R)");
            }


            list = new ArrayList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedMListQ = quickSortMList.sort(list, Comparator.naturalOrder(), new ListSwapper<>());
            qml += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " qml=" + qml / (i + 1));

            plist = new PersistentList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedMPQ = quickSortMP.sort(plist, Comparator.naturalOrder(), new PersistentListSwapper<String>());
            qmp += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " qmp=" + qmp / (i + 1));

            tlist = new TransientList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedMTQ = quickSortMT.sort(tlist, Comparator.naturalOrder(), new TransientListSwapper<String>());
            qmt += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " qmt=" + qmt / (i + 1));

            if (!jlist.equals(sortedMListQ)) {
                System.out.println("quicksort failed (M)");
            }
            if (!sortedList.equals(sortedMPQ) || !sortedList.equals(sortedMTQ)) {
                System.out.println("sortedMPQ or sortedMTQ differ for quicksort (M)");
            }

            list = new ArrayList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedListPQ = parallelQuickSortList.sort(list, Comparator.naturalOrder(), new ListSwapper<>());
            pl += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " pl =" + pl / (i + 1));

            plist = new PersistentList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedPPQ = parallelQuickSortP.sort(plist, Comparator.naturalOrder(), new PersistentListSwapper<String>());
            pp += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " pp=" + pp / (i + 1));

            tlist = new TransientList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedTPQ = parallelQuickSortT.sort(tlist, Comparator.naturalOrder(), new TransientListSwapper<String>());
            pt += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " pt=" + pt
                    / (i + 1));

            if (!jlist.equals(sortedListPQ)) {
                System.out.println("parallel quicksort failed");
            }
            if (!sortedList.equals(sortedPPQ) || !sortedList.equals(sortedTPQ)) {
                System.out.println("sortedRPQ or sortedRTQ differ for parallel quicksort");
            }


            Metric<String> defaultMetric = new Utf16StringMetric();

            list = new ArrayList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedListF = defaultFlashSortList.sort(list, Comparator.naturalOrder(), new ListSwapper<>(), defaultMetric);
            fl += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " fl =" + fl / (i + 1));

            plist = new PersistentList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedPF = defaultFlashSortP.sort(plist, Comparator.naturalOrder(), new PersistentListSwapper<String>(), defaultMetric);
            fp += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " fp=" + fp / (i + 1));

            tlist = new TransientList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedTF = defaultFlashSortT.sort(tlist, Comparator.naturalOrder(), new TransientListSwapper<String>(), defaultMetric);
            ft += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " ft=" + ft
                    / (i + 1));

            if (!jlist.equals(sortedListF)) {
                System.out.println("flashSort failed");
                //assertEquals(jlist, sortedListF);

            }
            if (!sortedList.equals(sortedPF) || !sortedList.equals(sortedTF)) {
                System.out.println("sortedPF or sortedTF differ for flashsort");
            }


            list = new ArrayList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedListFd = defaultFlashSortList.sort(list, Comparator.naturalOrder(), new ListSwapper<>(), metricDataTree);
            fdl += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " fdl =" + fdl / (i + 1));

            plist = new PersistentList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedPFd = defaultFlashSortP.sort(plist, Comparator.naturalOrder(), new PersistentListSwapper<String>(), metricDataTree);
            fdp += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " fdp=" + fdp / (i + 1));

            tlist = new TransientList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedTFd = defaultFlashSortT.sort(tlist, Comparator.naturalOrder(), new TransientListSwapper<String>(), metricDataTree);
            fdt += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " fdt=" + fdt
                    / (i + 1));
            if (!jlist.equals(sortedListFd)) {
                System.out.println("flashSortd failed");
            }
            if (!sortedList.equals(sortedPFd) || !sortedList.equals(sortedTFd)) {
                System.out.println("sortedPFd or sortedTFd differ for flashsortd");
            }


            list = new ArrayList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedListFe = defaultFlashSortList.sort(list, Comparator.naturalOrder(), new ListSwapper<>(), metricDataOneChar);
            fel += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " fel =" + fel / (i + 1));

            plist = new PersistentList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedPFe = defaultFlashSortP.sort(plist, Comparator.naturalOrder(), new PersistentListSwapper<String>(), metricDataOneChar);
            fep += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " fep=" + fep / (i + 1));

            tlist = new TransientList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedTFe = defaultFlashSortT.sort(tlist, Comparator.naturalOrder(), new TransientListSwapper<String>(), metricDataOneChar);
            fet += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " fet=" + fet / (i + 1));
            if (!jlist.equals(sortedListFd)) {
                System.out.println("flashSorte failed");
            }
            if (!sortedList.equals(sortedPFe) || !sortedList.equals(sortedTFe)) {
                System.out.println("sortedPFe or sortedTFe differ for flashsorte");
            }





            list = new ArrayList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedListFf = defaultFlashSortList.sort(list, Comparator.naturalOrder(), new ListSwapper<>(), metricDataForest);
            ffl += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " ffl =" + ffl / (i + 1));

            plist = new PersistentList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedPFf = defaultFlashSortP.sort(plist, Comparator.naturalOrder(), new PersistentListSwapper<String>(), metricDataForest);
            ffp += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " ffp=" + ffp / (i + 1));

            tlist = new TransientList<>(lines);
            t0 = System.currentTimeMillis();
            List<String> sortedTFf = defaultFlashSortT.sort(tlist, Comparator.naturalOrder(), new TransientListSwapper<String>(), metricDataForest);
            fft += System.currentTimeMillis() - t0;
            System.out.println("i=" + i + " fft=" + fft / (i + 1));
            if (!jlist.equals(sortedListFf)) {
                System.out.println("flashSortf failed");
            }
            if (!sortedList.equals(sortedPFf) || !sortedList.equals(sortedTFf)) {
                System.out.println("sortedPFf or sortedTFf differ for flashsortf");
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
