package cpaThread.conAccess.exm;

public class SharedUpdate {
    private int n;
    public synchronized void updateLongTime(int n) throws InterruptedException {
        String t_n = Thread.currentThread().getName();
        System.out.println(t_n+" in updateLongTime() is going to update shared-var as "+n);
        this.n = n;
        Thread.sleep((int)(Math.random()*1200));    // 写入变量后随机等待较长的时间(在这段时间内, 可能有其他线程进行了更改)
        System.out.println(t_n+" finished updating, now shared-var is "+this.n);
    }

    public synchronized void updateShortTime(int n) throws InterruptedException {
        String t_n = Thread.currentThread().getName();
        System.out.println(t_n+" in updateShortTime() is going to update shared-var as "+n);
        this.n = n;
        System.out.println(t_n+" finished updating, now shared-var is "+this.n);
    }
}
