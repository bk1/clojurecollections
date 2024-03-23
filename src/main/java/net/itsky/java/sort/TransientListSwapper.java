package net.itsky.java.sort;

import net.itsky.java.clojurecollections.TransientList;

public class TransientListSwapper<T> implements Swapper<T, TransientList<T>> {

    @Override
    public TransientList<T> swap(TransientList<T> list, int i, int j) {
        if (i != j) {
            T t = list.get(i);
            T u = list.get(j);
            return list.assocN(i, u).assocN(j, t);
        } else {
            return list;
        }
    }
}
