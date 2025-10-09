package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements ArrayList<T> {
    public int size;
    public int capacity;
    public T[] data;
    public int head;
    public int tail;
    public ArrayDeque(){
        size=0;
        capacity=8;
        head=0;tail=1;
        data= (T[]) new Object[capacity];
    }

    @Override
    public void addFirst(T x){
        if(isEmpty()){
            data[head]=x;
            size+=1;
            return;
        }
        if (size >= capacity-1) {
            resize((int) (capacity * 1.5));

        }
        size+=1;

        if(head>0){
            head-=1;
        }
        else{
            head=capacity-1;
        }
        data[head]=x;
    }

    @Override
    public void addLast(T x) {
        if(isEmpty()){
            data[head]=x;
            size+=1;
            return;
        }
        if (size >= capacity-1) {
            resize((int) (capacity * 1.5));
        }
        size+=1;
        data[tail]=x;
        tail=(tail+1)%capacity;

    }

    @Override
    public T getFirst() {

        return data[head];
    }

    @Override
    public T getLast() {
        if(tail!=0)
        {
            return data[tail - 1];
        }
        else {
            return data[capacity-1];
        }
    }

    @Override
    public T removeLast() {
        if(isEmpty()){
            return null;
        }
        if(size==1){
            T res = data[head];
            data[head]=null;
            size=0;
            return res;
        }
        T res;
        if(tail!=0){
            tail-=1;
            res=data[tail];
            data[tail]=null;
        }
        else{
            tail=capacity-1;
            res=data[tail];
            data[tail]=null;
        }

        size-=1;
        if(size*4<capacity&&size>=8){
            resize(capacity/2);
        }
        return res;
    }

    @Override
    public T removeFirst() {
        if(isEmpty()){
            return null;
        }
        T res=data[head];
        data[head]=null;
        if(size!=1)
        {
            head = (head + 1) % capacity;
        }
        size-=1;
        if(size*4<capacity&&size>=8){
            resize(capacity/2);
        }
        return res;
    }

    @Override
    public void insert(T x, int position) {
        if(isEmpty()){
            data[(head+position)%capacity]=x;
            size+=1;
            return;
        }
        if(size==capacity-1){
            resize((int)(capacity*1.5));
        }
        System.arraycopy(data,position,data,position+1,size-position);
        data[(head+position)%capacity]=x;
        tail+=1;
        size+=1;

    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void resize(int capacity) {
        T[] newData=(T[]) new Object[capacity];
        int length=tail-head;
        if(length<0){
            length=this.capacity-head;
            System.arraycopy(data,head,newData,0,length);
            System.arraycopy(data,0,newData,length,tail);
        }
        else
        {
            System.arraycopy(data, head, newData, 0, length);
        }
        head=0;tail=size;
        data=newData;
        this.capacity=capacity;
    }

    @Override
    public boolean isEmpty() {
        return size==0;
    }

    @Override
    public T get(int index) {
        return data[(head+index)%capacity];
    }

    @Override
    public void printDeque() {
        int nowPos=head;
        while(head!=tail-1){
            System.out.println(data[nowPos]);
            head=(head+1)%capacity;
        }
        return;
    }

    private class ArrayListIterator implements Iterator<T>{
        private int nowPos;
        public ArrayListIterator(){
            nowPos=head;
        }
        @Override
        public boolean hasNext() {
            return ((nowPos+1)%capacity!=tail+1);
        }

        @Override
        public T next() {
            T returnItem=data[nowPos];
            nowPos=(nowPos+1)%capacity;
            return returnItem;
        }
    }
    @Override
    public Iterator<T> iterator() {
        return new ArrayListIterator();
    }
    @Override
    public boolean equals(Object o){
        if(!(o instanceof ArrayDeque)){
            return false;
        }
        else{
            if(((ArrayDeque<?>) o).size!=this.size){
                return false;
            }
            int head_o=((ArrayDeque<?>) o).head;
            int head_this=this.head;
            for(int i=0;i<this.size;i++){
                if(((ArrayDeque<?>) o).data[(head_o+i)%((ArrayDeque<?>) o).capacity]!=data[(head_this+i)%capacity]){
                    return false;
                }
            }
            return true;

        }
    }

}
