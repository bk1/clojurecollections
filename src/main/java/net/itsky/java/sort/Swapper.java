package net.itsky.java.sort;

import java.util.List;

@FunctionalInterface
public interface Swapper<T, L extends List<T>> {
    L swap(L list, int i, int j);
}
