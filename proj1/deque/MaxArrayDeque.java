package deque;

import java.util.Comparator;
import java.util.Iterator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        this.comparator = c;
    }

    public T max() {
        if (isEmpty()) { // 处理空队列情况
            return null;
        }
        T maxEle = null;
        Iterator<T> it = iterator(); // 显式获取迭代器
        while (it.hasNext()) {
            T x = it.next();
            if (maxEle == null || comparator.compare(maxEle, x) < 0) {
                maxEle = x;
            }
        }
        return maxEle;
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) { // 处理空队列情况
            return null;
        }
        T maxEle = null;
        Iterator<T> it = iterator(); // 显式获取迭代器
        while (it.hasNext()) {
            T x = it.next();
            if (maxEle == null || c.compare(maxEle, x) < 0) {
                maxEle = x;
            }
        }
        return maxEle;
    }
}
