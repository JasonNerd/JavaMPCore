package cpaThread.cp04lock.cond;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * apple: ca
 * juice: cb
 * ca: walk
 * cb: drink
 */
public class Service {
    private final Lock lock = new ReentrantLock();
    private final Condition ca = lock.newCondition();
    private final Condition cb = lock.newCondition();

    private void occ(int t){
        int v = (int)(Math.random()*t);
        try {
            Thread.sleep(v);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void apple(){
        System.out.println(Thread.currentThread().getName()+" enter apple.");
        occ(100);
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName()+" now waiting@ca.");
            ca.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName()+" quit apple.");
        lock.unlock();
    }

    public void juice(){
        System.out.println(Thread.currentThread().getName()+" enter juice.");
        occ(260);
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName()+" now waiting@cb.");
            cb.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName()+" quit juice.");
        lock.unlock();
    }

    public void walk(){
        lock.lock();
        System.out.println(Thread.currentThread().getName()+" signal ca.");
        ca.signalAll();
        lock.unlock();
    }

    public void drink(){
        lock.lock();
        System.out.println(Thread.currentThread().getName()+" signal cb.");
        cb.signalAll();
        lock.unlock();
    }
}
