package cpaThread.cp03wn.e04other.join;

public class PreThreadB extends Thread{
    @Override
    public synchronized void run(){
        System.out.println("B.run()@"+Thread.currentThread().getName()+" begin: "+System.currentTimeMillis());
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("B.run()@"+Thread.currentThread().getName()+" ended: "+System.currentTimeMillis());
    }
}
