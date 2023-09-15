package cpaThread.cp04lock.rw;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ReadWriteLock: 读写锁
 * 该锁实现读线程间异步执行
 */
public class RWLock {
    private int opn = 1;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void readMul(){
        try{
            lock.readLock().lock();
            System.out.println(Thread.currentThread().getName()+" read the value of opn*2 is "+opn*2);
            Thread.sleep(200);
            System.out.println(Thread.currentThread().getName()+" read over.");
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            lock.readLock().unlock();
        }
    }

    public void readAdd(){
        try{
            lock.readLock().lock();
            System.out.println(Thread.currentThread().getName()+" read the value of (opn+2) is "+(opn+2));
            Thread.sleep(200);
            System.out.println(Thread.currentThread().getName()+" read over.");
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            lock.readLock().unlock();
        }
    }

    public void addMul(){
        try{
            lock.writeLock().lock();
            opn = (opn+2)*opn;
            System.out.println(Thread.currentThread().getName()+" update the value of operator: "+opn);
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            System.out.println(Thread.currentThread().getName()+" write over.");
            lock.writeLock().unlock();
        }
    }
}
