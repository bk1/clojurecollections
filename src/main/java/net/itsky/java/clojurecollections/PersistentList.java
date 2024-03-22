package net.itsky.java.clojurecollections;

import clojure.lang.ITransientVector;
import clojure.lang.PersistentVector;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class PersistentList<T> extends AbstractList<T> {

    static enum Marker { MARKER; };

    private final PersistentVector vector;

    PersistentList(Marker ignored, PersistentVector vector) {
        this.vector = vector;
    }

    public PersistentList(T... elements) {
        this.vector = PersistentVector.create((Object[]) elements);
    }

    public PersistentList(List<T> list) {
        this.vector = PersistentVector.create(list);
    }

    public PersistentList(Iterable<T> iterable) {
        this.vector = PersistentVector.create(iterable);
    }


    @Override
    public T get(int index) {
        return (T) vector.get(index);
    }

    public T nth(int index) {
        return get(index);
    }


    public T nth(int index, T notFound) {
        return (T) vector.nth(index, notFound);
    }

    @Override
    public int size() {
        return vector.size();
    }

    @Override
    public Iterator<T> iterator() {
        return (Iterator<T>) vector.iterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof List<?> list) {
            var n = vector.count();
            if (list.size() != n) {
                return false;
            } else if (obj instanceof PersistentList<?> persistentList) {
                for (int i = 0; i < n; i++) {
                    if (!Objects.equals(vector.nth(i), persistentList.vector.nth(i))) {
                        return false;
                    }
                }
                return true;
            } else if (obj instanceof TransientList<?> transientList) {
                for (int i = 0; i < n; i++) {
                    if (!Objects.equals(vector.nth(i), transientList.nth(i))) {
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

    public PersistentList<T> cons(T newLast) {
        PersistentVector vector = this.vector.cons(newLast);
        return new PersistentList<T>(Marker.MARKER, vector);
    }

    public PersistentList<T>  assocN(int i, T newVal) {
        PersistentVector vector = this.vector.assocN(i, newVal);
        return new PersistentList<>(Marker.MARKER, vector);
    }

    public PersistentList<T> pop() {
        return new PersistentList<>(Marker.MARKER, this.vector.pop());
    }

    public T peek() {
        return (T) vector.peek();
    }

    public T[] toArray() {
        return (T[]) vector.toArray();
    }

    public TransientList<T> asTransient() {
        ITransientVector vector = (ITransientVector) this.vector.asTransient();
        return new TransientList<>(TransientList.Marker.MARKER, vector);
    }

}
