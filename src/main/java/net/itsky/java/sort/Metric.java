package net.itsky.java.sort;

import java.util.List;

@FunctionalInterface
public interface Metric<T> {
    long metric(T element);
}
