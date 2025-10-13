package deque;

import org.junit.Test;


import java.util.Iterator;

import static org.junit.Assert.*;


/**
 * Performs some basic linked list tests.
 */
public class LinkedListDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();

        assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
        lld1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

        lld1.addLast("middle");
        assertEquals(2, lld1.size());

        lld1.addLast("back");
        assertEquals(3, lld1.size());

        System.out.println("Printing out deque: ");
        lld1.printDeque();

    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        // should be empty
        assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

        lld1.addFirst(10);
        // should not be empty

        assertFalse("lld1 should contain 1 item", lld1.isEmpty());

        lld1.removeFirst();
        // should be empty
        assertTrue("lld1 should be empty after removal", lld1.isEmpty());

    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);

    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {


        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();
        LinkedListDeque<Float> lld2 = new LinkedListDeque<Float>();
        LinkedListDeque<Boolean> lld3 = new LinkedListDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159F);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        float d = lld2.removeFirst();
        boolean b = lld3.removeFirst();
        assertEquals("string", s);
        assertEquals(3.14159F, d, 0.1);
        assertTrue(b);
    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());


    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {

        System.out.println("Make sure to uncomment the lines below (and delete this print statement).");

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }


    }

    @Test
    public void getTest() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();

        // 空队列get应返回null
        assertNull("空队列get(0)应返回null", deque.get(0));
        assertNull("空队列getRecursive(0)应返回null", deque.getRecursive(0));

        // 单元素队列
        deque.addFirst(10);

        assertEquals("get(0)应返回10", (Integer) 10, deque.get(0));
        assertEquals("getRecursive(0)应返回10", (Integer) 10, deque.getRecursive(0));

        // 多元素队列
        deque.addLast(20);
        deque.addFirst(5);
        // 队列应为：5,10,20
        assertEquals("get(0)应返回5", (Integer) 5, deque.get(0));
        assertEquals("get(1)应返回10", (Integer) 10, deque.get(1));
        assertEquals("get(2)应返回20", (Integer) 20, deque.get(2));
        assertEquals("getRecursive(2)应返回20", (Integer) 20, deque.getRecursive(2));

        // 越界索引
        assertNull("get(3)应返回null", deque.get(3));

        assertNull("getRecursive(3)应返回null", deque.getRecursive(3));
    }

    /**
     * 测试混合添加和删除操作的顺序正确性
     */
    @Test
    public void mixedAddRemoveTest() {
        LinkedListDeque<String> deque = new LinkedListDeque<>();

        // 混合前后添加
        deque.addFirst("a");
        deque.addLast("b");
        deque.addFirst("c");
        deque.addLast("d");
        // 预期顺序：c, a, b, d
        assertEquals(4, deque.size());
        assertEquals("c", deque.removeFirst());
        assertEquals("d", deque.removeLast());
        // 剩余：a, b
        assertEquals("a", deque.removeFirst());
        assertEquals("b", deque.removeLast());
        assertTrue(deque.isEmpty());

        // 交替添加删除
        deque.addFirst("x");
        assertEquals("x", deque.removeLast());
        deque.addLast("y");
        assertEquals("y", deque.removeFirst());
        assertTrue(deque.isEmpty());
    }

    /**
     * 测试迭代器功能（如果实现了Iterable）
     */
    @Test
    public void iteratorTest() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        for (int i = 0; i < 5; i++) {
            deque.addLast(i);
        }

        Iterator<Integer> it = deque.iterator();
        int index = 0;
        while (it.hasNext()) {
            assertEquals((Integer) index, it.next());
            index++;
        }
        assertEquals(5, index); // 确保遍历了所有元素

        // 测试增强for循环
        index = 0;
        for (int num : deque) {
            assertEquals(index, num);
            index++;
        }
    }

    /**
     * 测试equals方法（如果实现了）
     */
    @Test
    public void equalsTest() {
        LinkedListDeque<Integer> d1 = new LinkedListDeque<>();
        LinkedListDeque<Integer> d2 = new LinkedListDeque<>();
        ArrayDeque<Integer> a1 = new ArrayDeque<>();
        // 空队列相等
        System.out.println("d1 size: " + d1.size());
        System.out.println("a1 size: " + a1.size());
        System.out.println("d1 isEmpty: " + d1.isEmpty());
        System.out.println("a1 isEmpty: " + a1.isEmpty());
        System.out.println("d1 size: " + d1.size());
        System.out.println("a1 size: " + a1.size());
        System.out.println("d1 isEmpty: " + d1.isEmpty());
        System.out.println("a1 isEmpty: " + a1.isEmpty());

        // 检查 instanceof
        System.out.println("a1 instanceof Deque: " + (a1 instanceof Deque));
        System.out.println("d1 instanceof Deque: " + (d1 instanceof Deque));

        // 检查迭代器
        Iterator<Integer> d1It = d1.iterator();
        Iterator<Integer> a1It = a1.iterator();
        System.out.println("d1 iterator hasNext: " + d1It.hasNext());
        System.out.println("a1 iterator hasNext: " + a1It.hasNext());

        // 空队列相等
        boolean result = d1.equals(a1);
        System.out.println("d1.equals(a1) result: " + result);
        assertTrue("Empty LinkedListDeque should equal empty ArrayDeque", result);
        // 空队列相等
        assertTrue("Empty LinkedListDeque should equal empty ArrayDeque", d1.equals(a1));
        assertTrue(d1.equals(d2));
        assertTrue(d1.equals(a1));
        // 单元素相等
        d1.addFirst(1);
        d2.addFirst(1);
        a1.addFirst(1);
        assertTrue(d1.equals(d2));
        assertTrue(d1.equals(a1));

        // 元素不同
        d2.removeFirst();
        d2.addFirst(2);
        assertFalse(d1.equals(d2));

        // 长度不同
        d1.addLast(3);
        d2.addFirst(1);
        assertFalse(d1.equals(d2));

        // 元素顺序不同
        d1 = new LinkedListDeque<>();
        d2 = new LinkedListDeque<>();
        d1.addFirst(1);
        d1.addLast(2); // [1,2]
        d2.addFirst(2);
        d2.addLast(1); // [2,1]
        assertFalse(d1.equals(d2));

        // 与非Deque对象不相等
        assertFalse(d1.equals("not a deque"));
    }

    /** 测试复制构造函数（如果实现了） */
//    @Test
//    public void copyConstructorTest() {
//        LinkedListDeque<String> original = new LinkedListDeque<>();
//        original.addFirst("hello");
//        original.addLast("world");
//
//        // 复制构造
//        LinkedListDeque<String> copy = new LinkedListDeque<>(original);
//
//        // 验证内容相同
//        assertTrue(original.equals(copy));
//
//        // 验证修改原队列不影响复制队列
//        original.addFirst("test");
//        assertFalse(original.equals(copy));
//        assertEquals(3, original.size());
//        assertEquals(2, copy.size());
//        assertEquals("hello", copy.get(0));
//    }

    /**
     * 测试size为1时的各种操作
     */
    @Test
    public void singleElementTest() {
        LinkedListDeque<Double> deque = new LinkedListDeque<>();
        deque.addFirst(3.14);

        assertEquals(1, deque.size());
        assertFalse(deque.isEmpty());
        assertEquals((Double) 3.14, deque.get(0));
        assertEquals((Double) 3.14, deque.removeFirst());
        assertTrue(deque.isEmpty());

        deque.addLast(2.71);
        assertEquals(1, deque.size());
        assertEquals((Double) 2.71, deque.removeLast());
        assertTrue(deque.isEmpty());
    }

    /**
     * 测试连续多次添加删除后的稳定性
     */
    @Test
    public void stressTest() {
        LinkedListDeque<Integer> deque = new LinkedListDeque<>();
        int n = 10000;

        // 交替添加前后
        for (int i = 0; i < n; i++) {
            if (i % 2 == 0) {
                deque.addFirst(i);
            } else {
                deque.addLast(i);
            }
        }
        assertEquals(n, deque.size());

        // 交替删除前后
        for (int i = 0; i < n / 2; i++) {
            int expectedFirst = n - 2 - 2 * i; // 推导规律：添加顺序决定删除顺序
            assertEquals((Integer) expectedFirst, deque.removeFirst());
            int expectedLast = n - 2 * i - 1;
            assertEquals((Integer) expectedLast, deque.removeLast());
        }
        assertTrue(deque.isEmpty());
    }
}
