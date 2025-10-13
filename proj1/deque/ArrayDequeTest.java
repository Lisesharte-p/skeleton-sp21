package deque;

import org.junit.Test;
import java.util.Iterator;
import static org.junit.Assert.*;

public class ArrayDequeTest {

    /** 测试添加元素、isEmpty（通过size判断）和size的正确性 */
    @Test
    public void addIsEmptySizeTest() {
        ArrayDeque<String> ad = new ArrayDeque<>();
        assertTrue("新队列size应为0", ad.isEmpty());

        ad.addFirst("a");
        assertEquals(1, ad.size());
        assertFalse("添加元素后size不应为0", ad.isEmpty());

        ad.addLast("b");
        assertEquals(2, ad.size());

        ad.addFirst("c");
        assertEquals(3, ad.size());
    }

    /** 测试添加后删除元素，验证队列最终为空 */
    @Test
    public void addRemoveTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        ad.addFirst(10);
        assertFalse(ad.isEmpty());

        ad.removeFirst();
        assertTrue(ad.isEmpty());

        ad.addLast(20);
        assertFalse(ad.isEmpty());

        ad.removeLast();
        assertTrue(ad.isEmpty());
    }

    /** 测试从空队列删除元素的行为 */
    @Test
    public void removeEmptyTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        // 空队列删除返回null
        assertNull(ad.removeFirst());
        assertNull(ad.removeLast());

        // 非空队列删除后再删
        ad.addFirst(5);
        ad.removeFirst();
        assertNull(ad.removeFirst());
        assertEquals(0, ad.size());
    }

    /** 测试泛型参数的兼容性 */
    @Test
    public void multipleParamTest() {
        ArrayDeque<String> strDeque = new ArrayDeque<>();
        ArrayDeque<Double> doubleDeque = new ArrayDeque<>();
        ArrayDeque<Boolean> boolDeque = new ArrayDeque<>();

        strDeque.addLast("test");
        doubleDeque.addFirst(3.14);
        boolDeque.addFirst(true);

        assertEquals("test", strDeque.removeLast());
        assertEquals(3.14, doubleDeque.removeFirst(), 0.001);
        assertTrue(boolDeque.removeFirst());
    }

    /** 测试空队列获取首尾元素的返回值 */
    @Test
    public void emptyGetTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        assertNull(ad.get(0));
        assertNull(ad.get(ad.size()-1));
    }

    /** 测试大量元素添加后的顺序和扩容机制 */
    @Test
    public void bigArrayDequeTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        int count = 1000000;

        // 从尾部添加大量元素
        for (int i = 0; i < count; i++) {
            ad.addLast(i);
        }
        assertEquals(count, ad.size());

        // 验证前半部分顺序
        for (int i = 0; i < count / 2; i++) {
            assertEquals((Integer) i, ad.removeFirst());
        }

        // 验证后半部分顺序
        for (int i = count - 1; i >= count / 2; i--) {
            assertEquals((Integer) i, ad.removeLast());
        }
        assertEquals(0, ad.size());
    }

    /** 测试getFirst和get(ad.size())的正确性 */
    @Test
    public void getFirstLastTest() {
        ArrayDeque<String> ad = new ArrayDeque<>();

        ad.addFirst("first");
        assertEquals("first", ad.get(0));
        assertEquals("first", ad.get(ad.size()));

        ad.addLast("last");
        assertEquals("first", ad.get(0));
        assertEquals("last", ad.get(ad.size()));

        ad.removeFirst();
        assertEquals("last", ad.get(0));
        ad.removeLast();
        assertNull(ad.get(ad.size()));
    }

    /** 测试混合添加/删除操作的顺序正确性 */
    @Test
    public void mixedAddRemoveTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();

        // 混合前后添加
        ad.addFirst(1);
        ad.addLast(2);
        ad.addFirst(0);
        ad.addLast(3);
        // 预期顺序：0,1,2,3

        assertEquals(0, ad.removeFirst().intValue());
        assertEquals(3, ad.removeLast().intValue());
        assertEquals(1, ad.removeFirst().intValue());
        assertEquals(2, ad.removeLast().intValue());
        assertTrue(ad.isEmpty());

        // 交替添加删除
        ad.addLast(10);
        assertEquals(10, ad.removeFirst().intValue());
        ad.addFirst(20);
        assertEquals(20, ad.removeLast().intValue());
    }

    /** 测试迭代器功能 */
    @Test
    public void iteratorTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        for (int i = 0; i < 5; i++) {
            ad.addLast(i);
        }

        // 测试迭代器遍历顺序
        Iterator<Integer> it = ad.iterator();
        int index = 0;
        while (it.hasNext()) {
            assertEquals((Integer) index, it.next());
            index++;
        }
        assertEquals(5, index);

        // 测试增强for循环
        index = 0;
        for (int num : ad) {
            assertEquals(index, num);
            index++;
        }
    }

    /** 测试equals方法 */
    @Test
    public void equalsTest() {
        ArrayDeque<Integer> d1 = new ArrayDeque<>();
        ArrayDeque<Integer> d2 = new ArrayDeque<>();

        // 空队列相等
        assertEquals(d1, d2);

        // 单元素相等
        d1.addFirst(5);
        d2.addFirst(5);
        assertEquals(d1, d2);

        // 元素值不同
        d2.removeFirst();
        d2.addFirst(6);
        assertNotEquals(d1, d2);

        // 长度不同
        d1.addLast(7);
        d2.addFirst(5);
        assertNotEquals(d1, d2);

        // 元素顺序不同
        d1 = new ArrayDeque<>();
        d2 = new ArrayDeque<>();
        d1.addFirst(1);
        d1.addLast(2); // [1,2]
        d2.addFirst(2);
        d2.addLast(1); // [2,1]
        assertNotEquals(d1, d2);

        // 与非Deque对象不相等
        assertNotEquals("not a deque", d1);
    }

    /** 测试单个元素的各种操作 */
    @Test
    public void singleElementTest() {
        ArrayDeque<Character> ad = new ArrayDeque<>();
        ad.addFirst('a');

        assertEquals(1, ad.size());
        assertEquals('a', ad.get(0).charValue());
        assertEquals('a', ad.get(ad.size()).charValue());

        assertEquals('a', ad.removeFirst().charValue());
        assertTrue(ad.isEmpty());

        ad.addLast('b');
        assertEquals(1, ad.size());
        assertEquals('b', ad.removeLast().charValue());
        assertTrue(ad.isEmpty());
    }

    /* 测试插入操作（ArrayList接口特有） */
//    @Test
//    public void insertTest() {
//        ArrayDeque<String> ad = new ArrayDeque<>();
//
//        // 插入到空队列
//        ad.insert("a", 0);
//        assertEquals(1, ad.size());
//        assertEquals("a", ad.get(0));
//
//        // 插入到头部
//        ad.insert("b", 0);
//        assertEquals(2, ad.size());
//        assertEquals("b", ad.get(0));
//        assertEquals("a", ad.get(ad.size())());
//
//        // 插入到中间
//        ad.insert("c", 1);
//        assertEquals(3, ad.size());
//        // 顺序：b -> c -> a
//        ad.removeFirst(); // 移除b
//        assertEquals("c", ad.get(0));
//        ad.removeFirst(); // 移除c
//        assertEquals("a", ad.get(0));
//
//        // 插入到尾部
//        ad.insert("d", 1); // 当前队列只有a，插入到位置1（尾部）
//        assertEquals(2, ad.size());
//        assertEquals("d", ad.get(ad.size())());
//    }

    /** 测试扩容和缩容机制 */
    @Test
    public void resizeTest() {
//        ArrayDeque<Integer> ad = new ArrayDeque<>();
//        ad.capacity=8;
//        int initialCapacity = ad.capacity; // 初始容量8
//
//        // 测试扩容（超过容量时）
//        for (int i = 0; i < initialCapacity; i++) {
//            ad.addLast(i);
//        }
//        // 触发扩容（1.5倍）
//        ad.addLast(initialCapacity);
//        assertTrue(ad.capacity > initialCapacity);
//
//        // 测试缩容（元素数量过少时）
//        int largeCapacity = ad.capacity;
//        while (ad.size() > largeCapacity / 4) {
//            ad.removeFirst();
//        }
        // 触发缩容（1/2）
//        ad.removeFirst();
//        assertEquals(largeCapacity / 2, ad.capacity);
    }

    /** 压力测试：大量交替添加删除操作 */
    @Test
    public void stressTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        int n = 10;

        // 交替从首尾添加
        for (int i = 0; i < n; i++) {
            if (i % 2 == 0) {
                ad.addFirst(i);
            } else {
                ad.addLast(i);
            }
        }
        assertEquals(n, ad.size());

        // 交替从首尾删除并验证
        for (int i = 0; i < n / 2; i++) {
            int expectedFirst = (n - 2) - 2 * i; // 推导添加顺序对应的预期值
            assertEquals((Integer) expectedFirst, ad.removeFirst());

            int expectedLast = n - 1 - 2 * i;
            assertEquals((Integer) expectedLast, ad.removeLast());
        }
        assertTrue(ad.isEmpty());
    }
}
