package cpaThread.cp03wn.e01basic;

public class WNUsage {
    private final Object lock = new Object();
    // 1. wait method
    public void wtMessage() throws InterruptedException {
        synchronized (lock){
            System.out.println("Thread "+Thread.currentThread().getName()+" start waiting ... ...");
            lock.wait();
            System.out.println("Thread "+Thread.currentThread().getName()+" end waiting.");
        }
    }

    // 2. notify method
    public void nfMessage(){
        synchronized (lock){
            System.out.println("Thread "+Thread.currentThread().getName()+" begin to notify ... ...");
            lock.notify();
            System.out.println("Thread "+Thread.currentThread().getName()+" notify finished.");
        }
    }

    public void nfMessageAll(){
        synchronized (lock){
            System.out.println("Thread "+Thread.currentThread().getName()+" begin to notify ... ...");
            lock.notifyAll();
            System.out.println("Thread "+Thread.currentThread().getName()+" notify finished.");
        }
    }
}
