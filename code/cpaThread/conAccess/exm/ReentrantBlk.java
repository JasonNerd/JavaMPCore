package cpaThread.conAccess.exm;

public class ReentrantBlk {
    private int n;
    public synchronized void setA(int n) throws InterruptedException {
        System.out.println(Thread.currentThread().getName()+" in updateLongTime() is going to update shared-var as "+n);
        this.n = n;
        Thread.sleep((int)(Math.random()*1200));    // 写入变量后随机等待较长的时间(在这段时间内, 可能有其他线程进行了更改)
        get();      // get() 也由 synchronized 修饰, 执行其中的代码需要获取对象锁
    }

    public synchronized void setB(int n) throws InterruptedException {
        System.out.println(Thread.currentThread().getName()+
                " in updateShortTime() is going to update shared-var as "+n);
        this.n = n;
        get();
    }

    public synchronized void get(){
        System.out.println(Thread.currentThread().getName()+" finished updating, now shared-var is "+this.n);
    }
}
