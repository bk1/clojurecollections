package net.itsky.java.sort;

import java.util.Comparator;
import java.util.List;

public class InsertionSort<T, L extends List<T>> implements Sort<T, L> {

    @Override
    public L sort(L list, Comparator<T> comparator, Swapper<T, L> swapper) {
        int n = list.size();
        for (int i = 0; i < n; i++) {
            T t = list.get(i);
            int j = 0;
            for (j = 0; j < i; j++) {
                if (comparator.compare(list.get(j), list.get(i)) >= 0) {
                    break;
                }
            }
            for (int k = i; k > j; k--) {
                list = swapper.swap(list, k, k - 1);
            }
        }
        return list;
    }


}
