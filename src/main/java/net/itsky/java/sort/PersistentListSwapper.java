package net.itsky.java.sort;

import net.itsky.java.clojurecollections.PersistentList;

import java.util.List;

public class PersistentListSwapper<T> implements Swapper<T, PersistentList<T>> {

    @Override
    public PersistentList<T> swap(PersistentList<T> list, int i, int j) {
        if (i != j) {
            T t = list.get(i);
            T u = list.get(j);
            return list.assocN(i, u).assocN(j, t);
        } else {
            return list;
        }
    }
}
