package deque;

import java.util.Iterator;

public interface ArrayList<item> extends Iterable<item>{
    public void addFirst(item x);
    public void addLast(item x);
    public item getFirst();
    public item getLast();
    public item removeLast();
    public item removeFirst();
    public void insert(item x,int position);
    public int size();
    public boolean equals(Object o);
    public void resize(int capacity);
    public boolean isEmpty();
    public item get(int index);
    public void printDeque();
    public Iterator<item> iterator();
}
