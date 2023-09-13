package cpaThread.cp04lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock
 * lock 对象的基本使用方法
 */
public class CP04Lock {
    private final Lock lock = new ReentrantLock();
    public void method(){
        lock.lock();        // 开启监视
        System.out.println(Thread.currentThread().getName()+" entered.");
        int slp = (int)(Math.random()*200);
        try {
            Thread.sleep(slp);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName()+" going to exit.");
        lock.unlock();      // 解除监视
    }

    public static void main(String[] args) {
        // 测试
        final int thread_num = 5;
        CP04Lock service = new CP04Lock();
        Thread[] threads = new Thread[thread_num];
        for(int i=0; i<thread_num; i++) {
            threads[i] = new Thread(service::method);
            threads[i].setName("thread@"+i);
        }
        for(int i=0; i<thread_num; i++)
            threads[i].start();
    }

}
