package cpaThread.cp04lock.apis;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class APIService {
    ReentrantLock lock = new ReentrantLock();
    Condition c1 = lock.newCondition();
    Condition c2 = lock.newCondition();
    public void info(){
        if(!lock.isHeldByCurrentThread()) {
            lock.lock();
            System.out.println("----------------------------------------------------------------");
            System.out.println("\tThe lock is Fair? " + lock.isFair());
            System.out.println("\tThe lock is Locked? " + lock.isLocked());
            System.out.println("\tAny threads is waiting this lock? " + lock.hasQueuedThreads());
            System.out.println("\tHow many threads is waiting this lock? " + lock.getQueueLength());
            System.out.println("\tc1 has waiters? " + lock.hasWaiters(c1));
            System.out.println("\tc1 has waiters? " + lock.hasWaiters(c2));
            System.out.println("\tThere are " + lock.getWaitQueueLength(c1) + " wait in c1.");
            System.out.println("\tThere are " + lock.getWaitQueueLength(c2) + " wait in c2.");
            c1.signalAll();
            System.out.println("----------------------------------------------------------------");
            lock.unlock();
        }
    }
    public void ts_info(Thread[] threads){
        // 查询这些线程持有锁的情况
        if(!lock.isHeldByCurrentThread()) {
            lock.lock();
            for (Thread t : threads)
                if (lock.hasQueuedThread(t))
                    System.out.println("\t\t" + t.getName() + "now waiting this lock");
            c2.signalAll();
            lock.unlock();
        }
    }
    public void c1p(){
        lock.lock();
        System.out.println(Thread.currentThread().getName()+" 一共获取该锁"+lock.getHoldCount()+"次");
        try{
            Thread.sleep(10);
            c1.await();     // 进入 c1 等待队列
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        lock.unlock();
    }

    public void c2p(){
        lock.lock();
        System.out.println(Thread.currentThread().getName()+" 一共获取该锁"+lock.getHoldCount()+"次");
        try{
            c2.await();     // 进入 c1 等待队列
            Thread.sleep(10);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        lock.unlock();
    }
}
