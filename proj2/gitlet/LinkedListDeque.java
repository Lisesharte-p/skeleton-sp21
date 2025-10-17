package gitlet;


import java.util.Iterator;
import java.util.TreeMap;

public class LinkedListDeque<T> implements gitlet.Deque<T> {
    private final Node sentinelLeft;
    private final Node sentinelRight;
    private int size;

    public LinkedListDeque(LinkedListDeque<T> x) {
        sentinelLeft = new Node(null, null, null);
        sentinelRight = new Node(null, null, sentinelLeft);
        sentinelLeft.next = sentinelRight;
        size = 0;

        for (T t : x) {
            addLast(t);
        }
    }

    public LinkedListDeque(T[] list) {
        sentinelLeft = new Node(null, null, null);
        sentinelRight = new Node(null, null, sentinelLeft);
        sentinelLeft.next = sentinelRight;
        size = 0;

        for (T t : list) {
            addLast(t);
        }
    }

    public LinkedListDeque() {
        sentinelLeft = new Node(null, null, null);
        sentinelRight = new Node(null, null, sentinelLeft);
        sentinelLeft.next = sentinelRight;
        size = 0;
    }

    public int size() {
        return this.size;
    }

    private T getRecHelper(Node current, int index) {
        if (index == 0) {
            return current.next.data;

        }
        return getRecHelper(current.next, index - 1);
    }

    public T getRecursive(int index) {

        return getRecHelper(sentinelLeft, index);
    }

    @Override
    public void addFirst(T item) {
        Node newNode = new Node();
        newNode.data = item;
        newNode.next = sentinelLeft.next;
        newNode.perv = sentinelLeft;

        sentinelLeft.next.perv = newNode;

        sentinelLeft.next = newNode;
        this.size += 1;
    }

    @Override
    public void addLast(T item) {
        Node newNode = new Node();
        newNode.data = item;

        newNode.perv = sentinelRight.perv;

        newNode.next = sentinelRight;
        newNode.perv.next = newNode;
        sentinelRight.perv = newNode;
        this.size += 1;
    }


    public T getFirst() {
        if (!isEmpty()) {
            return sentinelLeft.next.data;
        }
        return null;
    }


    public T getLast() {
        if (!isEmpty()) {
            return sentinelRight.perv.data;
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void printDeque() {
        Node p = sentinelLeft.next;
        while (p.next != null) {
            System.out.println(p.data);
            p = p.next;
        }
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T res = sentinelLeft.next.data;
        sentinelLeft.next.next.perv = sentinelLeft;
        sentinelLeft.next = sentinelLeft.next.next;
        this.size -= 1;
        return res;
    }


    public void insert(T x, int position) {
        if (position > size || position < 0) {
            return;
        }
        Node p = sentinelLeft;
        for (int i = 0; i < position; i++) {
            p = p.next;
        }
        Node newNode = new Node(x, p.next, p.perv);
        p.next = newNode;
        newNode.next.perv = newNode;

    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T res = sentinelRight.perv.data;
        sentinelRight.perv.perv.next = sentinelRight;
        sentinelRight.perv = sentinelRight.perv.perv;
        this.size -= 1;
        return res;
    }

    @Override
    public T get(int index) {
        if (index > size) {
            return null;
        }
        if (index == 0) {
            return sentinelLeft.next.data;
        }

        Node p = sentinelLeft.next;
        for (int i = 0; i < index; i++) {
            p = p.next;
        }
        return p.data;
    }

    public void remove(int index){
        Node p=sentinelLeft;
        p=p.next;
        while(index!=0){
            index--;
            p=p.next;
        }
        p.next.perv=p.perv;
        p.perv.next=p.next;

    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Deque)) return false;
        Iterable<?> other = (Iterable<?>) o;
        if (this.size() != ((Deque<?>) o).size()) return false;


        Iterator<T> thisIt = this.iterator();
        Iterator<?> otherIt = other.iterator();
        while (thisIt.hasNext()) {
            T thisVal = thisIt.next();
            Object otherVal = otherIt.next();

            if (!objectsEqual(thisVal, otherVal)) {
                return false;
            }
        }
        return true;
    }


    private boolean objectsEqual(Object a, Object b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }


    public Iterator<T> iterator() {
        return new LLDequeIterator();
    }

    private class LLDequeIterator implements Iterator<T> {
        private int nowPos;

        LLDequeIterator() {
            nowPos = 0;
        }

        @Override
        public boolean hasNext() {
            return nowPos < size;
        }

        @Override
        public T next() {
            T returnItem = get(nowPos);
            nowPos += 1;
            return returnItem;
        }
    }

    private class Node {
        private T data;
        private Node next;
        private Node perv;

        Node(T data, Node next, Node perv) {
            this.data = data;
            this.next = next;
            this.perv = perv;
        }

        Node() {

        }

        public T getData() {
            return data;
        }

        public Node getNext() {
            return this.next;
        }
    }
    public boolean found(T inData){
        for(T x:this){
            if(x.equals(inData)){
                return true;
            }
        }
        return false;
    }
    public int getIndex(T inData){
        int index=0;
        Node current=sentinelLeft;
        while(current.next!=null){
            if(current.next.data.equals(inData)){
                return index;
            }
            index++;
        }
        return -1;
    }
}
