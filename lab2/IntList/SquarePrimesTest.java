package IntList;

import static org.junit.Assert.*;
import org.junit.Test;

public class SquarePrimesTest {

    /**
     * Here is a test for isPrime method. Try running it.
     * It passes, but the starter code implementation of isPrime
     * is broken. Write your own JUnit Test to try to uncover the bug!
     */
    @Test
    public void testSquarePrimesSimple() {
        IntList lst = IntList.of(14, 15, 16, 17, 18);
        boolean changed = IntListExercises.squarePrimes(lst);
        assertEquals("14 -> 15 -> 16 -> 289 -> 18", lst.toString());
        assertTrue(changed);
    }
    @Test
    public void testSquarePrimes1(){
        IntList lst = IntList.of(10,11,17,7,13,21,36);
        boolean changed=IntListExercises.squarePrimes(lst);
        assertEquals("10 -> 121 -> 289 -> 49 -> 169 -> 21 -> 36",lst.toString());
        assertTrue(changed);
    }
}
