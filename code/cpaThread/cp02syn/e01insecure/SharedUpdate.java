package cpaThread.cp02syn.e01insecure;

public class SharedUpdate {
    private int n;
    public void updateLongTime(int n) throws InterruptedException {
        String t_n = Thread.currentThread().getName();
        System.out.println(t_n+" in updateLongTime() is going to update shared-var as "+n);
        this.n = n;
        // 写入变量后随机等待较长的时间(由于未使用 syn, 可能有其他线程对 this.n 进行了更改)
        Thread.sleep((int)(Math.random()*1200));
        System.out.println(t_n+" finished updating, now shared-var is "+this.n);
    }

    public void updateShortTime(int n) throws InterruptedException {
        String t_n = Thread.currentThread().getName();
        System.out.println(t_n+" in updateShortTime() is going to update shared-var as "+n);
        this.n = n;
        System.out.println(t_n+" finished updating, now shared-var is "+this.n);
    }
}
