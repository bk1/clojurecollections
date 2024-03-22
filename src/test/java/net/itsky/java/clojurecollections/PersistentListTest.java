package net.itsky.java.clojurecollections;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PersistentListTest {

    @Test
    void testEquality() {
        PersistentList<String> plist1 = new PersistentList<>("A", "B", "C");
        List<String> list = List.of("A", "B", "C");
        PersistentList<String> plist2 = new PersistentList<>(list);
        TransientList<String> tlist = new TransientList<>(list);
        assertEquals(plist1, plist2);
        assertEquals(plist1, list);
        assertEquals(list, plist1);
        assertEquals(plist1, tlist);
        assertEquals(tlist, plist1);
        assertEquals(plist1.hashCode(), plist2.hashCode());
        assertEquals(plist1.hashCode(), list.hashCode());
        assertEquals(list.hashCode(), plist1.hashCode());
        //assertEquals(plist1.hashCode(), tlist.hashCode());
        //assertEquals(tlist.hashCode(), plist1.hashCode());
    }

    @Test
    void testNonEqualityObj() {
        PersistentList<String> plist1 = new PersistentList<>("A", "B", "C");
        assertNotEquals("not a List", plist1);
        assertNotEquals(plist1, "not a List");
    }

    @Test
    void testNonEqualityDifferentSize() {
        PersistentList<String> plist1 = new PersistentList<>("A", "B", "C");
        List<String> list = List.of("A", "B", "C", "D");
        PersistentList<String> plist2 = new PersistentList<>(list);
        TransientList<String> tlist = new TransientList<>(list);
        assertNotEquals(plist1, plist2);
        assertNotEquals(plist1, list);
        assertNotEquals(list, plist1);
        assertNotEquals(plist1, tlist);
        assertNotEquals(tlist, plist1);
    }

    @Test
    void testNonEqualitySameSize() {
        PersistentList<String> plist1 = new PersistentList<>("A", "B", "C");
        List<String> list = List.of("A", "D", "C");
        PersistentList<String> plist2 = new PersistentList<>(list);
        TransientList<String> tlist = new TransientList<>(list);
        assertNotEquals(plist1, plist2);
        assertNotEquals(plist1, list);
        assertNotEquals(list, plist1);
        assertNotEquals(plist1, tlist);
        assertNotEquals(tlist, plist1);
    }

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
