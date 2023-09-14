package cpaThread.cp04lock.produce;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class One2One {
    private boolean havingSet = false;  // 标志位, 初始时表示共享变量未设置值
    private final Lock lock = new ReentrantLock();
    private final Condition c = lock.newCondition();

    public void set() {
        lock.lock();
        if(havingSet){       // 已经有值了
            try {c.await(); Thread.sleep(100);}
            catch (InterruptedException e) {throw new RuntimeException(e);}
        }
        System.out.println(Thread.currentThread().getName()+" print ####");     // 表示已经生产
        havingSet = true;
        c.signal();
        lock.unlock();
    }

    public void get() {
        lock.lock();
        if(!havingSet){      // 还没设置值
            try { c.await(); Thread.sleep(100);}
            catch (InterruptedException e) { throw new RuntimeException(e);}
        }
        System.out.println(Thread.currentThread().getName()+" print ****");     // 表示已经消费
        havingSet = false;
        c.signal();
        lock.unlock();
    }

    public static void main(String[] args) {
        One2One service = new One2One();
        Thread producer = new Thread(()->{
            while (true)
                service.set();
        });
        Thread consumer = new Thread(()->{
            while (true)
                service.get();
        });
        producer.start();
        consumer.start();
    }
}
