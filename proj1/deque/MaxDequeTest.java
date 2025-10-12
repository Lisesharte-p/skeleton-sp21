package deque;


import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Comparator;
import java.util.Optional;

public class MaxDequeTest {

    public static class intComparator implements Comparator<Integer>{

        @Override
        public int compare(Integer o1, Integer o2) {

            if(o1==null){
                return -1;
            }
            if(o2==null){
                return 1;
            }
            if(o1>o2){
                return 1;
            }
            else if(o1<o2){
                return -1;
            }

            return 0;
        }
    }
    @Test
    public void IntTest(){
        MaxArrayDeque<Integer> md=new MaxArrayDeque<Integer>(new intComparator());
        for(int i=0;i<10000;i++){
            if(i%2==0){
                md.addFirst(i);
            }
            else {
                md.addLast(i);
            }
        }
        assertEquals(9999,(int)(md.max()));


    }
}
