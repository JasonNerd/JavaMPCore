package cpaThread.cp03wn.e04other.join;

public class PreThreadA extends Thread{
    private final PreThreadB lock;
    public PreThreadA(PreThreadB b){
        lock = b;
    }

    @Override
    public synchronized void run(){
        synchronized (lock) {
            System.out.println("A.run()@" + Thread.currentThread().getName() + " begin: " + System.currentTimeMillis());
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("A.run()@" + Thread.currentThread().getName() + " ended: " + System.currentTimeMillis());
        }
    }
}
