package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;
    public MaxArrayDeque(Comparator<T> c){
        super();
        this.comparator=c;

    }
    public T max(){
        T max_ele=null;
        for(T x:this){
            if(this.comparator.compare(max_ele,x)<0){
                max_ele=x;
            }
        }
        return max_ele;
    }



    public T max(Comparator<T> c){
        T max_ele=null;
        for(T x:this){
            if(c.compare(max_ele,x)<0){
                max_ele=x;
            }
        }
        return max_ele;
    }

}
