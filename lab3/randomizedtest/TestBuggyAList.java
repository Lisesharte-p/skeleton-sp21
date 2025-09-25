package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import timingtest.AList;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        AList<Integer>ns=new AList<>();
        BuggyAList<Integer>bns=new BuggyAList<>();
        ns.addLast(1);
        ns.addLast(2);
        ns.addLast(3);
        bns.addLast(1);
        bns.addLast(2);
        bns.addLast(3);
        assertEquals(ns.removeLast(),bns.removeLast());
        assertEquals(ns.removeLast(),bns.removeLast());
        assertEquals(ns.removeLast(),bns.removeLast());
    }
    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer>Buggy=new BuggyAList<>();
        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                Buggy.addLast((randVal));
                assertEquals(L.getLast(),Buggy.getLast());
//                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int size_buggy=Buggy.size();
                assertEquals(size,size_buggy);
//                System.out.println("size: " + size);
            } else if (operationNumber==2) {
                if(L.size()>0){

                    assertEquals(L.removeLast(),
                    Buggy.removeLast());
                }

            } else if (operationNumber==3) {
                if(L.size()>0){
                    assertEquals(L.getLast(),
                    Buggy.getLast());
                }

            }
        }
    }
}
