package net.itsky.java.clojurecollections;

import clojure.lang.IPersistentVector;
import clojure.lang.ITransientVector;
import clojure.lang.PersistentVector;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class TransientList<T> extends AbstractList<T> {

    static enum Marker {MARKER;};


    private class TransientIterator<T> implements Iterator<T> {

        private final int n = vector.count();
        private int i = 0;

        @Override
        public boolean hasNext() {
            return i < n;
        }

        @Override
        public T next() {
            return (T) vector.nth(i++);
        }

    }

    private final ITransientVector vector;

    TransientList(Marker ignored, ITransientVector vector) {
        this.vector = vector;
    }

    public TransientList(T... elements) {
        this.vector = PersistentVector.create((Object[]) elements).asTransient();
    }

    public TransientList(List<T> list) {
        this.vector = PersistentVector.create(list).asTransient();
    }

    public TransientList(Iterable<T> iterable) {
        this.vector = PersistentVector.create(iterable).asTransient();
    }


    @Override
    public T get(int index) {
        return (T) vector.nth(index);
    }

    public T nth(int index) {
        return get(index);
    }


    public T nth(int index, T notFound) {
        return (T) vector.nth(index, notFound);
    }

    @Override
    public int size() {
        return vector.count();
    }

    @Override
    public Iterator<T> iterator() {

        return new TransientIterator<T>();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof List<?> list) {
            var n = vector.count();
            if (list.size() != n) {
                return false;
            } else if (obj instanceof TransientList<?> transientList) {
                for (int i = 0; i < n; i++) {
                    if (!Objects.equals(vector.nth(i), transientList.vector.nth(i))) {
                        return false;
                    }
                }
                return true;
            } else if (obj instanceof PersistentList<?> persistentList) {
                for (int i = 0; i < n; i++) {
                    if (!Objects.equals(vector.nth(i), persistentList.nth(i))) {
                        return false;
                    }
                }
                return true;
            } else {
                Iterator<?> it = list.iterator();
                for (int i = 0; i < n && it.hasNext(); i++) {
                    if (!Objects.equals(vector.nth(i, null), it.next())) {
                        return false;
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return vector.hashCode();
    }

    @Override
    public String toString() {
        return vector.toString();
    }

    public TransientList<T> conj(T newLast) {
        ITransientVector vector = (ITransientVector) this.vector.conj(newLast);
        return new TransientList<T>(Marker.MARKER, vector);
    }

    public TransientList<T> assocN(int i, T newVal) {
        ITransientVector vector = this.vector.assocN(i, newVal);
        return new TransientList<>(Marker.MARKER, vector);
    }

    public TransientList<T> pop() {
        return new TransientList<>(Marker.MARKER, this.vector.pop());
    }

    public T peek() {
        return (T) ((IPersistentVector) vector.persistent()).peek();
    }

    public T[] toArray() {
        return (T[]) ((PersistentVector) vector.persistent()).toArray();
    }

    public PersistentList<T> persietent() {
        PersistentVector vector = (PersistentVector) this.vector.persistent();
        return new PersistentList<T>(PersistentList.Marker.MARKER, vector);
    }
}
