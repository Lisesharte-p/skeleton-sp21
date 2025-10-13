package deque;

import java.util.Iterator;

public interface ArrayList<item> extends Iterable<item> {
    void addFirst(item x);

    void addLast(item x);

    item getFirst();

    item getLast();

    item removeLast();

    item removeFirst();

    void insert(item x, int position);

    int size();

    boolean equals(Object o);

    void resize(int capacity);

    boolean isEmpty();

    item get(int index);

    void printDeque();

    Iterator<item> iterator();
}
