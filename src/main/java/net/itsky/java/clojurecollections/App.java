package net.itsky.java.clojurecollections;

import clojure.lang.ITransientVector;
import clojure.lang.PersistentVector;

import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        App app = new App();
        System.out.println(app.getClojureList(100));
    }

    public List<String> getClojureList(int n) {
        PersistentVector list = PersistentVector.create();
        for (int i = 0; i < n; i++) {
            list = list.cons((Object) ("" + i*i));
        }
        return list;
    }
}
