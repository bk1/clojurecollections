package net.itsky.java.clojurecollections;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransientListTest {

    @Test
    void testEquality() {
        TransientList<String> tlist1 = new TransientList<>("A", "B", "C");
        List<String> list = List.of("A", "B", "C");
        TransientList<String> tlist2 = new TransientList<>(list);
        PersistentList<String> plist = new PersistentList<>(list);
        assertEquals(tlist1, tlist2);
        assertEquals(tlist1, list);
        assertEquals(list, tlist1);
        assertEquals(tlist1, plist);
        assertEquals(plist, tlist1);
    }

    @Test
    void testNonEqualityObj() {
        TransientList<String> tlist1 = new TransientList<>("A", "B", "C");
        assertNotEquals("not a List", tlist1);
        assertNotEquals(tlist1, "not a List");
    }

    @Test
    void testChangeList2() {
        TransientList<String> list = new TransientList<>("A", "B", "C");
        List<String> abc = List.of("A", "B", "C");
        assertEquals(abc, list);
        assertEquals(list, abc);
        TransientList<String> list2 = list.assocN(1, "D");
        List<String> adc = List.of("A", "D", "C");
        assertEquals(adc, list2);
        assertEquals(list2, adc);
    }

    @Test
    void testModifyList() {
        TransientList<String> list = new TransientList<>("A", "B", "C");
        assertThrows(UnsupportedOperationException.class, () -> {
            list.set(1, "D");
        });
    }


    @Test
    void testExpandList() {
        TransientList<String> list = new TransientList<>("A", "B", "C");
        List<String> abc = List.of("A", "B", "C");
        assertEquals(abc, list);
        assertEquals(list, abc);
        TransientList<String> list2 = list.assocN(3, "D");
        List<String> abcd = List.of("A", "B", "C", "D");
        assertEquals(abcd, list2);
        assertEquals(list2, abcd);
    }


    @Test
    void testExpandListWithGap() {
        TransientList<String> list = new TransientList<>("A", "B", "C");
        assertThrows(IndexOutOfBoundsException.class, () -> {
            list.assocN(4, "E");
        });
    }


    @Test
    void testExpandListCons() {
        TransientList<String> list = new TransientList<>("A", "B", "C");
        List<String> abc = new TransientList<>(List.of("A", "B", "C"));
        assertEquals(abc, list);
        assertEquals(list, abc);
        TransientList<String> list2 = list.conj("D");
        List<String> abcd = new TransientList<>(List.of("A", "B", "C", "D"));
        assertEquals(abcd, list2);
        assertEquals(list2, abcd);
    }
}
