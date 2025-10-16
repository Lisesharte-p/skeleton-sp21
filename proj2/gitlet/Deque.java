package gitlet;


import java.util.Iterator;

public interface Deque<T> extends Iterable<T> {
    void addFirst(T item);

    void addLast(T item);

    T getFirst();

    T getLast();

    boolean isEmpty();

    int size();

    void printDeque();

    T removeFirst();

    void insert(T x, int position);

    T removeLast();

    T get(int index);


    Iterator<T> iterator();
}
