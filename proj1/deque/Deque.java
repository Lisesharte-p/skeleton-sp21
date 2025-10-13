package deque;

import java.util.Iterator;

public interface Deque<T> extends Iterable<T> {
    public void addFirst(T item);

    public void addLast(T item);

    T getFirst();

    T getLast();

    public boolean isEmpty();

    public int size();

    public void printDeque();

    public T removeFirst();

    void insert(T x, int position);

    public T removeLast();

    public T get(int index);


    public Iterator<T> iterator();
}
