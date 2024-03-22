package net.itsky.java.clojurecollections;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersistentListTest {

    @Test
    void testChangeList() {
        PersistentList<String> list = new PersistentList<>("A", "B", "C");
        List<String> abc = List.of("A", "B", "C");
        assertEquals(abc, list);
        assertEquals(list, abc);
        PersistentList<String> list2 = list.assocN(1, "D");
        List<String> adc = List.of("A", "D", "C");
        assertEquals(adc, list2);
        assertEquals(list2, adc);
        assertEquals(abc, list);
        assertEquals(list, abc);
    }

    @Test
    void testModifyList() {
        PersistentList<String> list = new PersistentList<>("A", "B", "C");
        assertThrows(UnsupportedOperationException.class, () -> {
            list.set(1, "D");
        });
    }


    @Test
    void testExpandList() {
        PersistentList<String> list = new PersistentList<>("A", "B", "C");
        List<String> abc = List.of("A", "B", "C");
        assertEquals(abc, list);
        assertEquals(list, abc);
        PersistentList<String> list2 = list.assocN(3, "D");
        List<String> abcd = List.of("A", "B", "C", "D");
        assertEquals(abcd, list2);
        assertEquals(list2, abcd);
        assertEquals(abc, list);
        assertEquals(list, abc);
    }


    @Test
    void testExpandListWithGap() {
        PersistentList<String> list = new PersistentList<>("A", "B", "C");
        List<String> abc = List.of("A", "B", "C");
        assertThrows(IndexOutOfBoundsException.class, () -> {
                    list.assocN(4, "E");
                });
    }



    @Test
    void testExpandListCons() {
        PersistentList<String> list = new PersistentList<>("A", "B", "C");
        List<String> abc = List.of("A", "B", "C");
        assertEquals(abc, list);
        assertEquals(list, abc);
        PersistentList<String> list2 = list.cons("D");
        List<String> abcd = List.of("A", "B", "C", "D");
        assertEquals(abcd, list2);
        assertEquals(list2, abcd);
        assertEquals(abc, list);
        assertEquals(list, abc);
    }

}
