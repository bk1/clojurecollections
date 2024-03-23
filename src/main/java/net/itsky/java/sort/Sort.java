package net.itsky.java.sort;

import java.util.Comparator;
import java.util.List;

@FunctionalInterface
public interface Sort<T, L extends List<T>> {
    L sort(L list, Comparator<T> comparator, Swapper<T, L> swapper);
}
