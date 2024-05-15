package net.itsky.java.sort;

import net.itsky.java.clojurecollections.PersistentList;
import net.itsky.java.clojurecollections.TransientList;
import net.itsky.java.sort.metric.*;
import org.eclipse.collections.api.factory.list.ImmutableListFactory;
import org.eclipse.collections.impl.list.immutable.ImmutableListFactoryImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static net.itsky.java.sort.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FlashSortTest {


    private final ListSwapper<String> listSwapper = new ListSwapper<>();
    private final PersistentListSwapper<String> persistentListSwapper = new PersistentListSwapper<>();

    private final TransientListSwapper<String> transientListSwapper = new TransientListSwapper<>();
    private final SortMetricized<String, List<String>> listSort = new FlashSort<>();
    private final SortMetricized<String, PersistentList<String>> plistSort = new FlashSort<>();

    private final SortMetricized<String, TransientList<String>> tlistSort = new FlashSort<>();

    private static final Metric<String> utf16StringMetric = new Utf16StringMetric();

    private static final Metric<String> cyrillic2BlockMetric = new Cyrillic2BlockStringMetric();

    private static final Metric<String> cyrillicMetric = new CyrillicStringMetric();

    private static final Metric<String> latin1Metric = new Latin1StringMetric();

    private static Metric<String> oneCharMetric;
    private static MetricDataIndependentChar[] independentCharMetrics;

    private static Metric<String> treeMetric;

    private static Metric<String> forestMetric;

    private static Metric<String> forestCyrMetric;

    private static Metric<String> forestCyrArrMetric;

    private static Metric<String> forestCyrMetricCached;

    private static Metric<String> forestCyrMetric4;


    private final ListSwapper<Integer> intListSwapper = new ListSwapper<>();
    private final PersistentListSwapper<Integer> persistentIntListSwapper = new PersistentListSwapper<>();

    private final TransientListSwapper<Integer> transientIntListSwapper = new TransientListSwapper<>();
    private final SortMetricized<Integer, List<Integer>> intListSort = new FlashSort<>();
    private final SortMetricized<Integer, PersistentList<Integer>> intPlistSort = new FlashSort<>();

    private final SortMetricized<Integer, TransientList<Integer>> intTlistSort = new FlashSort<>();

    private final Metric<Integer> defaultIntMetric = x -> x;

    private static List<Metric<String>> metrics;

    @BeforeAll
    static void initMetric() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        MetricDataOneChar oneCharMetric = new MetricDataOneChar();
        oneCharMetric.read(classLoader.getResourceAsStream("ukrainian-metric-1c.dat"));
        FlashSortTest.independentCharMetrics
                    = IntStream.rangeClosed(1, 10)
                .mapToObj(MetricDataIndependentChar::new)
                .map(metric->{
                    metric.read(classLoader.getResourceAsStream("ukrainian-metric-1c.dat"));
                    return metric;
                }).toArray(MetricDataIndependentChar[]::new);
        oneCharMetric.read(classLoader.getResourceAsStream("ukrainian-metric-1c.dat"));
        FlashSortTest.oneCharMetric = oneCharMetric;
        MetricDataTree treeMetric = new MetricDataTree();
        treeMetric.read(classLoader.getResourceAsStream("ukrainian-metric.dat"));
        FlashSortTest.treeMetric = treeMetric;
        MetricDataForest forestMetric = new MetricDataForest();
        forestMetric.read(classLoader.getResourceAsStream("ukrainian-metric.dat"));
        FlashSortTest.forestMetric = forestMetric;
        MetricDataCyrForest forestCyrMetric = new MetricDataCyrForest();
        forestCyrMetric.read(classLoader.getResourceAsStream("ukrainian-metric.dat"));
        FlashSortTest.forestCyrMetric = forestCyrMetric;
        MetricDataCyrArrForest forestCyrArrMetric = new MetricDataCyrArrForest();
        forestCyrArrMetric.read(classLoader.getResourceAsStream("ukrainian-metric.dat"));
        FlashSortTest.forestCyrArrMetric = forestCyrArrMetric;
        MetricDataCyrForestCached forestCyrMetricCached = new MetricDataCyrForestCached();
        forestCyrMetricCached.read(classLoader.getResourceAsStream("ukrainian-metric.dat"));
        FlashSortTest.forestCyrMetricCached = forestCyrMetricCached;
        MetricDataCyrForest4 forestCyrMetric4 = new MetricDataCyrForest4();
        forestCyrMetric4.read(classLoader.getResourceAsStream("ukrainian-metric.dat"), classLoader.getResourceAsStream("ukrainian-frequencies.dat"));
        FlashSortTest.forestCyrMetric4 = forestCyrMetric4;

        Stream<Metric<String>> streamA = Stream.of(utf16StringMetric, cyrillic2BlockMetric, cyrillicMetric, latin1Metric,
                        treeMetric, oneCharMetric, forestMetric, forestCyrMetric, forestCyrArrMetric, forestCyrMetricCached, forestCyrMetric4);
        Stream<Metric<String>> streamB = Arrays.stream(independentCharMetrics);
        FlashSortTest.metrics = Stream.concat(streamA, streamB).toList();
    }

    @Test
    void testFlashSortAsiatic() {
        metrics.stream().forEach(metric -> {
            List<String> unsortedList = new ArrayList<>(ASIATIC_UNSORTED);
            PersistentList<String> unsortedP = new PersistentList<>(ASIATIC_UNSORTED);
            TransientList<String> unsortedT = new TransientList<>(ASIATIC_UNSORTED);

            List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), listSwapper, metric);
            assertEquals(ASIATIC_SORTED, sortedList);

            PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), persistentListSwapper, metric);
            assertEquals(ASIATIC_SORTED, sortedP);

            TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), transientListSwapper, metric);
            assertEquals(ASIATIC_SORTED, sortedT);
        });
    }


    @Test
    void testFlashSort() {
        metrics.stream().forEach(metric -> {
            List<String> unsortedList = new ArrayList<>(UNSORTED);
            PersistentList<String> unsortedP = new PersistentList<>(UNSORTED);
            TransientList<String> unsortedT = new TransientList<>(UNSORTED);

            List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), listSwapper, metric);
            assertEquals(SORTED, sortedList);

            PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), persistentListSwapper, metric);
            assertEquals(SORTED, sortedP);

            TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), transientListSwapper, metric);
            assertEquals(SORTED, sortedT);
        });
    }

    @Test
    void testFlashSortLong() {
        metrics.stream().forEach(metric -> {
            List<String> unsortedList = new ArrayList<>(LONG_UNSORTED);
            PersistentList<String> unsortedP = new PersistentList<>(LONG_UNSORTED);
            TransientList<String> unsortedT = new TransientList<>(LONG_UNSORTED);


            List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), listSwapper, metric);
            assertEquals(LONG_SORTED, sortedList);

            PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), persistentListSwapper, metric);
            assertEquals(LONG_SORTED, sortedP);

            TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), transientListSwapper, metric);
            assertEquals(LONG_SORTED, sortedT);
        });
    }

    @Test
    void testFlashSort1000Lines() {
        metrics.stream().forEach(metric -> {
            List<String> unsortedList = new ArrayList<>(X_1000_LINES);
            PersistentList<String> unsortedP = new PersistentList<>(X_1000_LINES);
            TransientList<String> unsortedT = new TransientList<>(X_1000_LINES);


            List<String> sortedList = listSort.sort(unsortedList, Comparator.naturalOrder(), listSwapper, metric);
            assertEquals(X_1000_LINES_SORTED, sortedList);

            PersistentList<String> sortedP = plistSort.sort(unsortedP, Comparator.naturalOrder(), persistentListSwapper, metric);
            assertEquals(X_1000_LINES_SORTED, sortedP);

            TransientList<String> sortedT = tlistSort.sort(unsortedT, Comparator.naturalOrder(), transientListSwapper, metric);
            assertEquals(X_1000_LINES_SORTED, sortedT);
        });

    }

    @Test
    void testFlashSortShort() {
        List<Integer> empty = new ArrayList<>();
        List<Integer> emptyS = intListSort.sort(empty, Comparator.naturalOrder(), intListSwapper, defaultIntMetric);
        assertTrue(emptyS.isEmpty());
        List<Integer> oneU = Collections.singletonList(1);
        List<Integer> oneS = intListSort.sort(new ArrayList<>(oneU), Comparator.naturalOrder(), intListSwapper, defaultIntMetric);
        assertEquals(oneU, oneS);
        List<List<Integer>> lists = List.of(List.of(1, 2), List.of(2, 1), List.of(1, 2, 3), List.of(2, 1, 3), List.of(1, 3, 2), List.of(3, 1, 2), List.of(2, 3, 1), List.of(3, 2, 1),
                List.of(1, 2, 3, 4), List.of(1, 1, 1, 1), List.of(2, 1, 3, 4), List.of(3, 2, 1, 4), List.of(2, 3, 1, 4), List.of(3, 1, 2, 4), List.of(1, 3, 2, 4));
        for (List<Integer> unsorted : lists) {
            List<Integer> sorted = intListSort.sort(new ArrayList<>(unsorted), Comparator.naturalOrder(), intListSwapper, defaultIntMetric);
            List<Integer> expected = new ArrayList<>(unsorted);
            Collections.sort(expected);
            assertEquals(expected, sorted);
        }
    }

    private List<List<Integer>> createAllPermutations(int n) {
        if (n < 0 || n > 13) {
            throw new IllegalArgumentException("factorial of n fits into int for n=0,1,2,...13, but n=" + n);
        }
        long factorialL = LongStream.range(1L, n).reduce(1, (prod, i) -> prod * i);
        int factorial = (int) factorialL;
        if (factorialL != (long) factorial) {
            throw new IllegalArgumentException("factorial of n fits into int for n=0,1,2,...12, but n=" + n);
        }
        int[] indexes = new int[n];
        for (int i = 0; i < n; i++) {
            indexes[i] = 0;
        }
        List<List<Integer>> result = new ArrayList<>(factorial);
        ImmutableListFactory lf = new ImmutableListFactoryImpl();
        ListSwapper<Integer> swapper = new ListSwapper<>();
        List<Integer> elements = new ArrayList<>(IntStream.range(1, n + 1).boxed().toList());
        result.add(lf.<Integer>withAll(elements).castToList());
        int i = 0;
        while (i < n) {
            if (indexes[i] < i) {
                swapper.swap(elements, (i & 0x01) == 0 ? 0 : indexes[i], i);
                result.add(lf.withAll(elements).castToList());
                indexes[i]++;
                i = 0;
            } else {
                indexes[i] = 0;
                i++;
            }
        }
        return result;
    }


    @Test
    public void testAllPermutations() {
        for (int n = 0; n <= 8; n++) {
            List<List<Integer>> permutations = createAllPermutations(n);
            List<Integer> sorted = permutations.getFirst();
            for (List<Integer> unsorted : permutations) {
                assertEquals(sorted, intListSort.sort(new ArrayList<>(unsorted), Comparator.naturalOrder(), intListSwapper, defaultIntMetric), () -> "unsorted=" + unsorted);
                assertEquals(sorted, intPlistSort.sort(new PersistentList<>(unsorted), Comparator.naturalOrder(), persistentIntListSwapper, defaultIntMetric), () -> "unsorted=" + unsorted);
                assertEquals(sorted, intTlistSort.sort(new TransientList<>(unsorted), Comparator.naturalOrder(), transientIntListSwapper, defaultIntMetric), () -> "unsorted=" + unsorted);
            }
        }
    }


    @Test
    public void testAllPermutationsOfSignedNumbers() {
        Function<Integer, Integer> function = x -> x - 2;
        for (int n = 0; n <= 8; n++) {
            List<List<Integer>> permutations = createAllPermutations(n);
            List<Integer> sorted = permutations.getFirst().stream().map(function).toList();
            for (List<Integer> permutation : permutations) {
                List<Integer> unsorted = permutation.stream().map(function).toList();
                assertEquals(sorted, intListSort.sort(new ArrayList<>(unsorted), Comparator.naturalOrder(), intListSwapper, defaultIntMetric), () -> "unsorted=" + unsorted);
                assertEquals(sorted, intPlistSort.sort(new PersistentList<>(unsorted), Comparator.naturalOrder(), persistentIntListSwapper, defaultIntMetric), () -> "unsorted=" + unsorted);
                assertEquals(sorted, intTlistSort.sort(new TransientList<>(unsorted), Comparator.naturalOrder(), transientIntListSwapper, defaultIntMetric), () -> "unsorted=" + unsorted);
            }
        }
    }


    @Test
    public void testNumbers() {
        ListSwapper<String> swapper = new ListSwapper<>();
        metrics.stream().forEach(metric -> {
            assertEquals(NUMBERS_SORTED, listSort.sort(new ArrayList<>(NUMBERS_UNSORTED), Comparator.naturalOrder(), listSwapper, metric), () -> "unsorted=" + NUMBERS_UNSORTED);
            assertEquals(NUMBERS_SORTED, plistSort.sort(new PersistentList<>(NUMBERS_UNSORTED), Comparator.naturalOrder(), persistentListSwapper, metric), () -> "unsorted=" + NUMBERS_UNSORTED);
            assertEquals(NUMBERS_SORTED, tlistSort.sort(new TransientList<>(NUMBERS_UNSORTED), Comparator.naturalOrder(), transientListSwapper, metric), () -> "unsorted=" + NUMBERS_UNSORTED);
        });
    }

    @Test
    public void testLongNumbers() {
        ListSwapper<String> swapper = new ListSwapper<>();
        metrics.stream().forEach(metric -> {
            assertEquals(NUMBERS_LONG_SORTED, listSort.sort(new ArrayList<>(NUMBERS_LONG_UNSORTED), Comparator.naturalOrder(), listSwapper, metric));
            assertEquals(NUMBERS_LONG_SORTED, plistSort.sort(new PersistentList<>(NUMBERS_LONG_UNSORTED), Comparator.naturalOrder(), persistentListSwapper, metric));
            assertEquals(NUMBERS_LONG_SORTED, tlistSort.sort(new TransientList<>(NUMBERS_LONG_UNSORTED), Comparator.naturalOrder(), transientListSwapper, metric));
        });
    }


    @Test
    public void testPrefixedNumbers() {
        ListSwapper<String> swapper = new ListSwapper<>();
        metrics.stream().forEach(metric -> {
            assertEquals(NUMBERS_PREFIXED_SORTED, listSort.sort(new ArrayList<>(NUMBERS_PREFIXED_UNSORTED), Comparator.naturalOrder(), listSwapper, metric));
            assertEquals(NUMBERS_PREFIXED_SORTED, plistSort.sort(new PersistentList<>(NUMBERS_PREFIXED_UNSORTED), Comparator.naturalOrder(), persistentListSwapper, metric));
            assertEquals(NUMBERS_PREFIXED_SORTED, tlistSort.sort(new TransientList<>(NUMBERS_PREFIXED_UNSORTED), Comparator.naturalOrder(), transientListSwapper, metric));
        });
    }


    @Test
    public void testMixedPrefixedNumbers() {
        ListSwapper<String> swapper = new ListSwapper<>();
        metrics.stream().forEach(metric -> {
            assertEquals(NUMBERS_MIXED_PREFIXED_SORTED, listSort.sort(new ArrayList<>(NUMBERS_MIXED_PREFIXED_UNSORTED), Comparator.naturalOrder(), listSwapper, metric));
            assertEquals(NUMBERS_MIXED_PREFIXED_SORTED, plistSort.sort(new PersistentList<>(NUMBERS_MIXED_PREFIXED_UNSORTED), Comparator.naturalOrder(), persistentListSwapper, metric));
            assertEquals(NUMBERS_MIXED_PREFIXED_SORTED, tlistSort.sort(new TransientList<>(NUMBERS_MIXED_PREFIXED_UNSORTED), Comparator.naturalOrder(), transientListSwapper, metric));
        });
    }

}