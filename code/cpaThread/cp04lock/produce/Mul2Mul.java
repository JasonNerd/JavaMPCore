package cpaThread.cp04lock.produce;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Mul2Mul {
    private boolean havingSet = false;  // 标志位, 初始时表示共享变量未设置值
    private final Lock lock = new ReentrantLock();
    private final Condition c = lock.newCondition();

    public void set() {
        lock.lock();
        while (havingSet){       // 已经有值了
            try {c.await(); Thread.sleep(100);}
            catch (InterruptedException e) {throw new RuntimeException(e);}
        }
        System.out.println("#### set ####");     // 表示已经生产
        havingSet = true;
        c.signalAll();
        lock.unlock();
    }

    public void get() {
        lock.lock();
        while (!havingSet){      // 还没设置值
            try { c.await(); Thread.sleep(100);}
            catch (InterruptedException e) { throw new RuntimeException(e);}
        }
        System.out.println("**** get ****");     // 表示已经消费
        havingSet = false;
        c.signalAll();
        lock.unlock();
    }

    public static void main(String[] args) {
        Mul2Mul service = new Mul2Mul();
        for(int i=0; i<5; i++) {
            new Thread(() -> {
                while (true)
                    service.set();
            }).start();
            new Thread(() -> {
                while (true)
                    service.get();
            }).start();
        }
    }
}
