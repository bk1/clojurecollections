package net.itsky.java.clojurecollections.util;

public class SubString implements CharSequence, Comparable<SubString> {

    private final CharSequence s;
    private final int n;
    private final int begin;
    private final int end;

    private int hashCached = 0;
    private boolean hashIsReallyZero = false;

    public SubString(CharSequence s, int begin, int end) {
        if (s == null) {
            throw new IllegalArgumentException("s must not be null");
        }
        if (begin < 0 || begin > end || end > s.length()) {
            throw new StringIndexOutOfBoundsException("required: 0 <= begin <= end <= s.length, but begin=" + begin + " end=" + end + " s.length=" + s.length());
        }
        this.s = s;
        this.begin = begin;
        this.end = end;
        this.n = end - begin;
    }

    @Override
    public int length() {
        return n;
    }

    @Override
    public char charAt(int index) {
        if (index >= n || index < 0) {
            throw new IndexOutOfBoundsException("n=" + n + " index=" + index);
        }
        return s.charAt(begin + index);
    }

    @Override
    public String toString() {
        if (s instanceof String str) {
            return str.substring(begin, end);
        } else {
            char[] chars = new char[n];
            s.chars().skip(begin).limit(n).reduce(0, (idx, c) -> {
                chars[idx] = (char) c;
                return idx + 1;
            });
            return new String(chars);
        }
    }

    @Override
    public int hashCode() {
        // The hash or hashIsZero fields are subject to a benign data race,
        // making it crucial to ensure that any observable result of the
        // calculation in this method stays correct under any possible read of
        // these fields. Necessary restrictions to allow this to be correct
        // without explicit memory fences or similar concurrency primitives is
        // that we can ever only write to one of these two fields for a given
        // String instance, and that the computation is idempotent and derived
        // from immutable state
        int result = hashCached;
        if (result == 0 && !hashIsReallyZero) {
            result = chars().reduce(0, (prev, c) -> prev * 31 + c);
            if (result == 0) {
                hashIsReallyZero = true;
            } else {
                hashCached = result;
            }
        }
        return result;
    }


    @Override
    public CharSequence subSequence(int start, int end) {
        return new SubString(s, this.begin + start, this.begin + end);
    }

    @Override
    public int compareTo(SubString other) {
        return CharSequence.compare(this, other);
    }
}
