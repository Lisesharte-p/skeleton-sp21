package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B {
    BSTNode root;
    BSTMap<K, V> left;
    BSTMap<K, V> right;

    BSTMap() {
        root = new BSTNode();
    }

    @Override
    public void clear() {
        root = new BSTNode();
        left=null;
        right=null;
    }

    @Override
    public boolean containsKey(Object key) {
        if(root.key==null){
            return false;
        }
        if (root.key.compareTo((K)key)==0) {
            return true;
        } else if (root.key.compareTo((K) key) < 0) {
            if (left == null) {
                return false;
            }
            return left.containsKey(key);
        } else {
            if (right == null) {
                return false;
            }
            return right.containsKey(key);
        }
    }

    @Override
    public Object get(Object key) {
        if(!this.containsKey(key)){
            return null;
        }
        if (root.key.compareTo((K)key)==0) {
            return root.value;
        } else if (root.key.compareTo((K) key) < 0) {
            if (left == null) {
                return null;
            }
            return left.get(key);
        } else {
            if (right == null) {
                return null;
            }
            return right.get(key);
        }
    }

    @Override
    public int size() {
        if(left==null&&right==null&&root.key==null){
            return 0;
        }
        if (left == null && right == null) {
            return 1;
        }
        else if(left==null){
            return right.size()+1;
        }
        else if(right==null){
            return left.size()+1;
        }else{
            return left.size()+ right.size()+1;
        }
    }

    @Override
    public void put(Object key, Object value) {
        if (root.key == null) {
            root.key = (K) key;
            root.value = (V) value;
        }else{
            if(root.key.compareTo((K)key)<0){
                if(left==null){
                    left=new BSTMap<>();
                    left.root.key= (K) key;
                    left.root.value= (V) value;
                }else{
                    left.put(key,value);
                }
            } else if (root.key.compareTo((K)key)>0) {
                if(right==null){
                    right=new BSTMap<>();
                    right.root.key= (K) key;
                    right.root.value= (V) value;
                }else{
                    right.put(key,value);
                }
            }
        }
    }

    public void printInOrder() {

    }

    @Override
    public Set keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(Object key) {
        BSTNode nodeToRemove;

        if(root.key.compareTo((K)key)==0){
            if(this.left==null&&this.right==null){

            } else if (this.left==null) {

            } else if (this.right == null){

            }else{

            }
        } else if (root.key.compareTo((K)key)==-1) {
            this.right.remove(key);
        }else{
            this.left.remove(key);
        }

    }

    @Override
    public Object remove(Object key, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator iterator() {
        throw new UnsupportedOperationException();
    }

    public class BSTNode {
        K key;
        V value;

        BSTNode() {
            key = null;
            value = null;
        }
    }
}
