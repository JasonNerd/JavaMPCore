package cpaThread.cp03wn.e02while;

import java.util.ArrayList;
import java.util.List;

/**
 * 需要模拟这样一种情况, 有一个共享的列表, 初始大小为0, 一些线程向其中添加元素,
 * 另一些线程删除其中的元素. 注意删除前需要判断列表是否为空
 */
public class MList {
    private List<String> ml = new ArrayList<>();
    private final Object lock = new Object();
    public void add(String e){
        synchronized (lock){
            ml.add(e);
            lock.notifyAll();       // 唤醒正在等待的删除线程
            System.out.println("Thread " + Thread.currentThread().getName()+ " add an element.");
        }
    }

    public void removeA() throws InterruptedException {
        synchronized (lock){
            if(ml.size() == 0){     // 如果为空则等待添加
                System.out.println("Thread " + Thread.currentThread().getName()+ " now waiting.");
                lock.wait();
                System.out.println("Thread " + Thread.currentThread().getName()+ " now end waiting.");
            }
            ml.remove(0);   // 删除第一个元素
            System.out.println("List size is " + ml.size());
        }
    }

    public void removeB() throws InterruptedException {
        synchronized (lock){
            if(ml.size() > 0){     // 如果为空则等待添加
                System.out.println("Thread " + Thread.currentThread().getName()+ " now waiting.");
                ml.remove(0);   // 删除第一个元素
                System.out.println("Thread " + Thread.currentThread().getName()+ " now end waiting.");
            } else lock.wait();
            System.out.println("List size is " + ml.size());
        }
    }
}
