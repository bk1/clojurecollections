package net.itsky.java.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class TestData {
    static final List<String> UNSORTED = List.of("FL8YPS", "6DSNQQ", "U419AB", "X82QVN", "CU25H4", "ZZCRWY");
    static final List<String> SORTED = new ArrayList<>(UNSORTED);
    static final List<String> LONG_UNSORTED = List.of("0H9Y41BL8WUQ", "7WPFZU00FHJQ", "D0OBE9SD6DPE", "AGBYG6MMCC9B", "E9SPYKWAITU9", "1VT6J5VSDMX9", "WSW1YJI40W8H", "378PPNCN6OW8", "JL819RD3CFTS", "C2YGBBGXXCBV", "KAGQZ7DY82WS", "C6N3W0RU4N5M", "XSJ9V3MN73TA", "8OPVKYG8JHYA", "UN0P5TKDEA05", "XP0KKYPUSTSF", "JS308V9PAJ2Z", "09NYKUUCTM5U", "AFID27K03CXA", "EPS16MESPLM7", "4FSSP0UJB6Q6", "FJG8FS1F2QXB", "L06RQWNPRFLJ", "TW9GAH01TK02", "2BSNA7IBHTRH", "H7IQKOMZ388X", "Y4B7SEEE5QMP", "C22P8R9UBPS7", "KVJE98AOILZ8", "LJLDM4AEA0AD", "1EVSCZGQ55MV", "E1LN8FRI5VNQ", "32NYE2DD9MBO", "OCKJPM3NKQ7P", "0IFOJ8OAY4H9", "5ERNL1PBOKOC", "76I2GY4KQOXP", "8EKLG4FQFU8K", "VP0J12B839EU", "94D3IKF9BITJ");
    static final List<String> LONG_SORTED = new ArrayList<>(LONG_UNSORTED);
    static final List<String> NUMBERS_UNSORTED = IntStream.range(0, 1001).map(x -> (1 + x + x * x) % 1001).boxed().map(x -> String.format("%04d", x)).toList();
    static final List<String> NUMBERS_SORTED = new ArrayList<>(NUMBERS_UNSORTED);
    static final List<String> NUMBERS_LONG_UNSORTED = LongStream.range(0, 1002003).map(x -> (1 + x + x * x + x * x * x) % 1002003004L).mapToObj(x -> String.format("%010d", x)).toList();
    static final List<String> NUMBERS_LONG_SORTED = new ArrayList<>(NUMBERS_LONG_UNSORTED);
    static final List<String> NUMBERS_PREFIXED_UNSORTED = LongStream.range(0, 1002003).map(x -> (1 + x + x * x + x * x * x) % 1002003004L).mapToObj(x -> String.format("XYZ%010d", x)).toList();
    static final List<String> NUMBERS_MIXED_PREFIXED_UNSORTED = NUMBERS_PREFIXED_UNSORTED.stream().flatMap(s -> Stream.of("XYZT" + s, "\ufedc\ufedb\ufeda\ufed9"+ s)).toList();
    static final List<String> NUMBERS_MIXED_PREFIXED_SORTED = new ArrayList<>(NUMBERS_MIXED_PREFIXED_UNSORTED);
    static final List<String> NUMBERS_PREFIXED_SORTED = new ArrayList<>(NUMBERS_PREFIXED_UNSORTED);
    static final List<String> UNSORTED_ASIATIC = List.of("A", "農林大臣", "", "B", "和田博雄", "", "C", "罗放", "", "D");
    static final List<String> SORTED_ASIATIC = new ArrayList<>(UNSORTED_ASIATIC);

    static {
        Collections.sort(SORTED);
        Collections.sort(LONG_SORTED);
        Collections.sort(NUMBERS_SORTED);
        Collections.sort(NUMBERS_LONG_SORTED);
        Collections.sort(NUMBERS_PREFIXED_SORTED);
        Collections.sort(NUMBERS_MIXED_PREFIXED_SORTED);
        Collections.sort(SORTED_ASIATIC);

    }
}
