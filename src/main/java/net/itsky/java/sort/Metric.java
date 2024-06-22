package net.itsky.java.sort;

import java.util.List;

@FunctionalInterface
public interface Metric<T> {
    int metric(T element);
}
