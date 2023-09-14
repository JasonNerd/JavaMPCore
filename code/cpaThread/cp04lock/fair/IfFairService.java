package cpaThread.cp04lock.fair;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IfFairService {
    private final Lock lock;
    public IfFairService(boolean ifFair){
        lock = new ReentrantLock(ifFair);
    }

    public void service(){
        lock.lock();
        try {
            System.out.println("----"+Thread.currentThread().getName()+"----");
            Thread.sleep(20);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        lock.unlock();
    }
}
