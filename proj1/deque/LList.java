package deque;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public interface LList<item> extends Iterable<item> {
     void addFirst(item x);

     void addLast(item x);

     item getFirst();

     item getLast();

     item removeLast();

     item removeFirst();

     void insert(item x, int position);

     int size();

     boolean equals(Object o);

     @NotNull Iterator<item> iterator();
}
