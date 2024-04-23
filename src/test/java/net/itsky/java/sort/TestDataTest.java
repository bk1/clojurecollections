package net.itsky.java.sort;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDataTest {

    @Test
    void testCreateList0() {
        List<String> list = TestData.createList(0, 9,8, 7);
        assertEquals(List.of(""), list);
    }


    void assertSetEquals(List<String> expected, List<String> found) {
        assertEquals(new TreeSet<String>(expected), new TreeSet<String>(found));
    }
    @Test
    void testCreateList1() {
        List<String> listA = TestData.createList(1, 'a', 'b', 'c');
        System.out.println(listA);
        assertSetEquals(List.of("", "a", "b", "c"), listA);
        List<String> list = TestData.createList(1, 9,8, 7);
        assertSetEquals(List.of("", "\u0007", "\u0008", "\u0009"), list);
    }

    @Test
    void testCreateList2() {
        List<String> listA = TestData.createList(2, 'a', 'b', 'c');
        System.out.println(listA);
        assertSetEquals(List.of("", "a", "b", "c", "aa", "ab", "ac", "ba", "bb", "bc", "ca", "cb", "cc"), listA);
    }

    @Test
    void testCreateList3() {
        List<String> listA = TestData.createList(3, 'a', 'b', 'c');
        System.out.println(listA);
        List<String> expectedA = new ArrayList<>(TestData.createList(2, 'a', 'b', 'c'));
        List<String> expectedB = Stream.of("a", "b", "c").flatMap(s-> Stream.of("a", "b", "c").map(t->s+t)).toList();
        List<String> expectedC = Stream.of("a", "b", "c").flatMap(s-> Stream.of("a", "b", "c").flatMap(t-> Stream.of("a", "b", "c").map(u->s+t+u))).toList();
        List<String> expected = Stream.concat(Stream.concat(expectedA.stream(), expectedB.stream()), expectedC.stream()).toList();
        assertSetEquals(expected, listA);

    }
}
