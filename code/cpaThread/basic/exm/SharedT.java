package cpaThread.basic.exm;

public class SharedT implements Runnable{
    private int cnt;
    public SharedT(int cnt){
        this.cnt = cnt;
    }
    @Override
    public void run() {
        cnt--;
        System.out.println(Thread.currentThread().getName()+": cnt="+cnt);
    }
}
