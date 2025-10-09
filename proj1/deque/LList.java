package deque;
import java.util.Iterator;
public interface LList<item> extends Iterable<item> {
    public void addFirst(item x);
    public void addLast(item x);
    public item getFirst();
    public item getLast();
    public item removeLast();
    public item removeFirst();
    public void insert(item x,int position);
    public int size();
    public boolean equals(Object o);
    public Iterator<item> iterator();
}
