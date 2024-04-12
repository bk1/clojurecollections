package net.itsky.java.clojurecollections.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubStringTest {

    @Test
    void testIndexOutOfRange() {
        String s = "ABC";
        assertThrows(StringIndexOutOfBoundsException.class, () -> new SubString(s, -1, 2));
        assertThrows(StringIndexOutOfBoundsException.class, () -> new SubString(s, 0, -1));
        assertThrows(StringIndexOutOfBoundsException.class, () -> new SubString(s, 1, 0));
        assertThrows(StringIndexOutOfBoundsException.class, () -> new SubString(s, 1, 5));
    }


    @Test
    void testNull() {
        assertThrows(IllegalArgumentException.class, () -> new SubString(null, 0, 0));
    }

    @Test
    void testEmpty() {
        check("", 0, 0);
    }

    @Test
    void testOne() {
        check("A", 0, 0);
        check("A", 0, 1);
        check("A", 1, 1);
    }

    @Test
    void testMany() {
        String base = "ABCDEFGHIJKL";
        for (int i = 0; i < 12; i++) {
            String s = new String(base.substring(0, i));
            for (int j = 0; j < i; j++) {
                for (int k = j; k <= i; k++) {
                    check(s, j, k);
                }
            }
        }
    }

    @Test
    void testManyFromStringBuffer() {
        String base = "ABCDEFGHIJKL";
        for (int i = 0; i < 12; i++) {
            CharSequence s = new StringBuffer(base.substring(0, i));
            for (int j = 0; j < i; j++) {
                for (int k = j; k <= i; k++) {
                    CharSequence expected = s.subSequence(j, k);
                    CharSequence found = new SubString(s, j, k);
                    check(expected, found, j, k);
                }
            }
        }
    }

    @Test
    void testIterated() {
        String base = "ABCDEFGHIJKL";
        for (int i = 0; i < 12; i++) {
            String s = new String(base.substring(0, i));
            for (int j = 0; j < i; j++) {
                for (int k = j; k <= i; k++) {
                    String string = s.substring(j, k);
                    SubString subString = new SubString(s, j, k);
                    int n = string.length();
                    for (int l = 0; l < n; l++) {
                        for (int m = l; m < n; m++) {
                            String expected = string.substring(l, m);
                            SubString found1 = new SubString(subString, l, m);
                            check(expected, found1, l, m);
                            SubString found2 = (SubString) subString.subSequence(l, m);
                            check(expected, found2, l, m);
                        }
                    }
                }
            }
        }
    }

    private void check(String input, int start, int end) {
        CharSequence expected = input.substring(start, end);
        CharSequence found = new SubString(input, start, end);
        check(expected, found, start, end);
    }

    private void check(CharSequence expected, CharSequence found, int start, int end) {
        assertEquals(expected.length(), found.length());
        for (int i = 0; i < found.length(); i++) {
            assertEquals(expected.charAt(i), found.charAt(i));
        }
        assertEquals(expected.toString(), found.toString());
        int[] arrExpected = expected.chars().toArray();
        int[] arrFound = found.chars().toArray();
        assertArrayEquals(arrExpected, arrFound);
        assertEquals(expected.hashCode(), found.hashCode(), () -> "expected="+expected + " found=" + found);
    }

}
