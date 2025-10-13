package deque;


import java.util.Iterator;

public class ArrayDeque<T> implements deque.Deque<T> {
    private int size;
    private int capacity;
    private T[] data;
    private int head;
    private int tail;

    public ArrayDeque() {
        size = 0;
        capacity = 8;
        head = 0;
        tail = 1;
        data = (T[]) new Object[capacity];
    }

    @Override
    public void addFirst(T x) {
        if (isEmpty()) {
            data[head] = x;
            size += 1;
            return;
        }
        if (size >= capacity - 1) {
            resize((int) (capacity * 1.5));

        }
        size += 1;

        if (head > 0) {
            head -= 1;
        } else {
            head = capacity - 1;
        }
        data[head] = x;
    }

    @Override
    public void addLast(T x) {
        if (isEmpty()) {
            data[head] = x;
            size += 1;
            return;
        }
        if (size >= capacity - 1) {
            resize((int) (capacity * 1.5));
        }
        size += 1;
        data[tail] = x;
        tail = (tail + 1) % capacity;

    }


//    public T getFirst() {
//
//        return data[head];
//    }


//    public T getLast() {
//        if (tail != 0) {
//            return data[tail - 1];
//        } else {
//            return data[capacity - 1];
//        }
//    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        if (size == 1) {
            T res = data[head];
            data[head] = null;
            size = 0;
            return res;
        }
        T res;
        if (tail != 0) {
            tail -= 1;
            res = data[tail];
            data[tail] = null;
        } else {
            tail = capacity - 1;
            res = data[tail];
            data[tail] = null;
        }

        size -= 1;
        if (size * 4 < capacity && size >= 8) {
            resize(capacity / 2);
        }
        return res;
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T res = data[head];
        data[head] = null;
        if (size != 1) {
            head = (head + 1) % capacity;
        }
        size -= 1;
        if (size * 4 < capacity && size >= 8) {
            resize(capacity / 2);
        }
        return res;
    }


//    public void insert(T x, int position) {
//        if (isEmpty()) {
//            data[(head + position) % capacity] = x;
//            size += 1;
//            return;
//        }
//        if (size == capacity - 1) {
//            resize((int) (capacity * 1.5));
//        }
//        System.arraycopy(data, position, data, position + 1, size - position);
//        data[(head + position) % capacity] = x;
//        tail += 1;
//        size += 1;
//
//    }

    @Override
    public int size() {
        return size;
    }


    private void resize(int capacityI) {
        T[] newData = (T[]) new Object[capacityI];
        int length = tail - head;
        if (length < 0) {
            length = this.capacity - head;
            System.arraycopy(data, head, newData, 0, length);
            System.arraycopy(data, 0, newData, length, tail);
        } else {
            System.arraycopy(data, head, newData, 0, length);
        }
        head = 0;
        tail = size;
        data = newData;
        this.capacity = capacityI;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public T get(int index) {
        return data[(head + index) % capacity];
    }

    @Override
    public void printDeque() {
        int nowPos = head;
        int elementsPrinted = 0;

        while (elementsPrinted < size) {
            System.out.print(data[nowPos] + " ");
            nowPos = (nowPos + 1) % capacity;
            elementsPrinted++;
        }
        System.out.println();
    }

    public Iterator<T> iterator() {
        return new ArrayListIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Deque)) return false;
        Deque<?> other = (Deque<?>) o;
        if (this.size() != other.size()) return false;


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

    private class ArrayListIterator implements Iterator<T> {
        private int currentIndex;
        private int count;

        ArrayListIterator() {
            currentIndex = head;
            count = 0;
        }

        @Override
        public boolean hasNext() {
            return count < size;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            T returnItem = data[currentIndex];
            currentIndex = (currentIndex + 1) % capacity;
            count++;
            return returnItem;
        }
    }

}
