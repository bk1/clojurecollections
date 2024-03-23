package net.itsky.java.sort;

import java.util.List;

public class ListSwapper<T> implements Swapper<T, List<T>> {

    @Override
    public List<T> swap(List<T> list, int i, int j) {
        if (i != j) {
            T t = list.get(i);
            list.set(i, list.get(j));
            list.set(j, t);
        }
        return list;
    }

}
