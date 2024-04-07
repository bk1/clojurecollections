package net.itsky.java.sort;

import java.util.Comparator;
import java.util.List;

@FunctionalInterface
public interface SortSubList<T, L extends List<T>> {
    L sort(L list, Comparator<T> comparator, Swapper<T, L> swapper, int begin, int end);


    default void checkBoundaries(L list, int begin, int end) {
        int aSizeTotal = list.size();
        if (begin < 0 || end > aSizeTotal || begin > end) {
            throw new IllegalArgumentException("illegal limits. Required: 0 <= begin <= end <= aSize found: begin=" + begin + " end=" + end + " aSize=" + aSizeTotal);
        }
    }

}
