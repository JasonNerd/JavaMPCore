package cpaThread.cp01mul.exm;

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
