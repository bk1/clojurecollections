package net.itsky.java.sort;

import java.util.Comparator;
import java.util.List;

public class TerneryHeapSortSubList<T, L extends List<T>> implements SortSubList<T, L> {

    int parent(int idx, int begin) {
        return begin + (idx - begin - 1) / 3;
    }

    int leftChild(int idx, int begin) {
        return begin + (idx - begin) *3 + 1;
    }
    int midChild(int idx, int begin) {
        return begin + (idx - begin) *3 + 2;
    }

    int rightChild(int idx, int begin) {
        return begin + (idx-begin) *3 + 3;
    }


    @Override
    public L sort(L list, Comparator<T> comparator, Swapper<T, L> swapper, int begin, int end) {
        checkBoundaries(list, begin, end);
        int aSize = end - begin;
        if (aSize <= 1) {
            // nothing to sort
            return list;
        }

        list = heapify(list, begin, end, comparator, swapper);
        for (int i = end - 1; i > begin; i--) {
            list = swapper.swap(list, begin, i);
            list = siftDown(list, begin, begin,i -1, comparator, swapper);
        }
        return list;
    }


    L heapify(L list, int begin, int end, Comparator<T> comparator, Swapper<T, L> swapper) {
        int last = end - 1;
        int start = parent(last, begin);
        for (int parent = start; parent >= begin; parent--) {
            list = siftDown(list, parent, begin, last, comparator, swapper);
        }
        return list;
    }

    L siftDown(L list, int start, int begin, int last, Comparator<T> comparator, Swapper<T, L> swapper) {
        int parent = start;
        while (true) {
            int left = leftChild(parent, begin);
            int mid = midChild(parent, begin);
            int right = rightChild(parent, begin);
            if (left > last) {
                break;
            }
            int swap = parent;
            if (comparator.compare(list.get(parent), list.get(left)) < 0) {
                swap = left;
            }
            if (mid <= last && comparator.compare(list.get(swap), list.get(mid)) < 0) {
                swap = mid;
            }
            if (right <= last && comparator.compare(list.get(swap), list.get(right)) < 0) {
                swap = right;
            }
            if (swap != parent) {
                list = swapper.swap(list, parent, swap);
                parent = swap;
            } else {
                break;
            }
        }
        return list;
    }

}
