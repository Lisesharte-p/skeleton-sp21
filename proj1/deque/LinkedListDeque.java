package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>{
    public Node sentinel_left;
    public Node sentinel_right;
    public int size;

    public int size(){
        return this.size;
    }
    public LinkedListDeque(LinkedListDeque<T> x){
        sentinel_left=new Node(null,null,null);
        sentinel_right=new Node(null,null,sentinel_left);
        sentinel_left.next=sentinel_right;
        size=0;
        for(T data:x){
            addLast(data);
        }
    }
    public LinkedListDeque(){
        sentinel_left=new Node(null,null,null);
        sentinel_right=new Node(null,null,sentinel_left);
        sentinel_left.next=sentinel_right;
        size=0;
    }
    private T getRecHelper(Node current,int index){
        if(index==0){
            return current.next.data;

        }
        return getRecHelper(current.next,index-1);
    }
    public T getRecursive(int index){

        return getRecHelper(sentinel_left,index);
    }
    public void addFirst(T item){
        Node newNode=new Node();
        newNode.data=item;
        newNode.next=sentinel_left.next;
        newNode.perv=sentinel_left;

        sentinel_left.next.perv = newNode;

        sentinel_left.next=newNode;
        this.size+=1;
    }
    public void addLast(T item){
        Node newNode=new Node();
        newNode.data=item;

        newNode.perv = sentinel_right.perv;

        newNode.next=sentinel_right;
        newNode.perv.next=newNode;
        sentinel_right.perv=newNode;
        this.size+=1;
    }

    @Override
    public T getFirst() {
        if(!isEmpty())
        {
            return sentinel_left.next.data;
        }
        return null;
    }

    @Override
    public T getLast() {
        if(!isEmpty())
        {
            return sentinel_right.perv.data;
        }
        return null;
    }

    public boolean isEmpty(){
        return size==0;
    }
    public void printDeque(){
        Node p=sentinel_left.next;
        while(p.next!=null){
            System.out.println(p.data);
            p=p.next;
        }
    }
    public T removeFirst(){
        if(isEmpty()){
            return null;
        }
        T res=sentinel_left.next.data;
        sentinel_left.next.next.perv=sentinel_left;
        sentinel_left.next=sentinel_left.next.next;
        this.size-=1;
        return res;
    }

    @Override
    public void insert(T x, int position) {
        if(position>size||position<0){
            return;
        }
        Node p=sentinel_left;
        for(int i=0;i<position;i++){
            p=p.next;
        }
        Node newNode=new Node(x,p.next,p.perv);
        p.next=newNode;
        newNode.next.perv=newNode;

    }

    public T removeLast(){
        if(isEmpty()){
            return null;
        }
        T res=sentinel_right.perv.data;
        sentinel_right.perv.perv.next=sentinel_right;
        sentinel_right.perv=sentinel_right.perv.perv;
        this.size-=1;
        return res;
    }
    public T get(int index){
        if(index>size){
            return null;
        }
        if(index==0){
            return getFirst();
        }

        Node p=sentinel_left.next;
        for(int i=0;i<index;i++){
            p=p.next;
        }
        return p.data;
    }
    @Override
    public boolean equals(Object o){
        if(!(o instanceof LinkedListDeque))
        {
            return false;
        }
        else {
            if(((LinkedListDeque<?>) o).size!=this.size){
                return false;
            }
            Node p=sentinel_left;
            Node p_o= (Node) ((LinkedListDeque<?>) o).sentinel_left;
            while (p.next!=null){
                if(p.next.data!=p_o.next.data){
                    return false;
                }
                p=p.next;
                p_o=p_o.next;
            }
            return true;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new LLDequeIterator();
    }
    private class LLDequeIterator implements Iterator<T>{
        private int nowPos;
        public LLDequeIterator(){
            nowPos=0;
        }
        @Override
        public boolean hasNext() {
            return nowPos<size;
        }

        @Override
        public T next() {
            T returnItem=get(nowPos);
            nowPos+=1;
            return returnItem;
        }
    }
    public class Node{
        public T data;
        public Node next;
        public Node perv;
        public Node(T data,Node next,Node perv){
            this.data=data;
            this.next=next;
            this.perv=perv;
        }
        public Node(){

        }
    }
}
