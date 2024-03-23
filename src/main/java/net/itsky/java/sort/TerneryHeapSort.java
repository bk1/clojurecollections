package net.itsky.java.sort;

import java.util.Comparator;
import java.util.List;

public class TerneryHeapSort<T, L extends List<T>> implements Sort<T, L> {

    int parent(int idx) {
        return (idx - 1) / 3;
    }

    int leftChild(int idx) {
        return (idx *3) + 1;
    }
    int midChild(int idx) {
        return (idx *3) + 2;
    }

    int rightChild(int idx) {
        return (idx *3) + 3;
    }


    @Override
    public L sort(L list, Comparator<T> comparator, Swapper<T, L> swapper) {
        int n = list.size();
        if (n <= 1) {
            return list;
        }
        list = heapify(list, n, comparator, swapper);
        for (int end = n - 1; end > 0; end--) {
            list = swapper.swap(list, 0, end);
            list = siftDown(list, 0, end -1, comparator, swapper);
        }
        return list;
    }


    L heapify(L list, int n, Comparator<T> comparator, Swapper<T, L> swapper) {
        int end = n - 1;
        int start = parent(end);
        for (int parent = start; parent >= 0; parent--) {
            list = siftDown(list, parent, n-1, comparator, swapper);
        }
        return list;
    }

    L siftDown(L list, int start, int end, Comparator<T> comparator, Swapper<T, L> swapper) {
        int parent = start;
        while (true) {
            int left = leftChild(parent);
            int mid = midChild(parent);
            int right = rightChild(parent);
            if (left > end) {
                break;
            }
            int swap = parent;
            if (comparator.compare(list.get(parent), list.get(left)) < 0) {
                swap = left;
            }
            if (mid <= end && comparator.compare(list.get(swap), list.get(mid)) < 0) {
                swap = mid;
            }
            if (right <= end && comparator.compare(list.get(swap), list.get(right)) < 0) {
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
