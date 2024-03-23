package net.itsky.java.sort;

import java.security.SecureRandom;
import java.util.*;

public class QuickSort<T, L extends List<T>> implements Sort<T, L> {

    private static record Pair(int lower, int upper) {
    }

    private static final Random rand = new SecureRandom();
    @Override
    public L sort(L list, Comparator<T> comparator, Swapper<T, L> swapper) {
        int n = list.size();
        Stack<Pair> tasks = new Stack<>();
        tasks.push(new Pair(0, n));
        while (! tasks.isEmpty()) {
            Pair limits = tasks.pop();
            int l = limits.lower;
            int u = limits.upper;
            if (u - l <= 1) {
                break;
            }

            int idx = rand.nextInt(u-l) + l;
            T t = list.get(idx);
            list = swapper.swap(list, u-1, idx);
            int i = l;
            var j = u-2;
            while (i < j) {
                while (i < j && comparator.compare(list.get(i), t) <= 0){
                    i++;
                }
                while (i < j && comparator.compare(t, list.get(j)) < 0){
                    j--;
                }
                if (i < j && comparator.compare(list.get(i), list.get(j)) > 0) {
                    list = swapper.swap(list, i, j);
                }
            }
            if (comparator.compare(list.get(i), t) > 0) {
                list = swapper.swap(list, i, u-1);
            } else {
                i = u-1;
            }
            int split = i;
            System.out.println("t=" + t + " idx= " + idx + " l=" + l + " u=" + u + " list=" + list);
            if (l < split - 1) {
                System.out.println("push l=" + l + " split=" + split);
                tasks.push(new Pair(l, split));
            }
            if (split + 2 < u) {
                System.out.println("push split+1=" + (split+1) + " u=" + u);
                tasks.push(new Pair(split+1, u));
            }
        }
        System.out.println();
        return list;
    }
}
