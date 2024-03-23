package net.itsky.java.sort;

import java.security.SecureRandom;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class RandomPivotQuickSort<T, L extends List<T>> implements Sort<T, L> {

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
            if (l < split - 1) {
                tasks.push(new Pair(l, split));
            }
            if (split + 2 < u) {
                tasks.push(new Pair(split+1, u));
            }
        }
        return list;
    }
}
