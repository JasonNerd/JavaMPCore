package cpaThread.cp06sig;

public class SigTest {
    public static void main(String[] args) {
        int tn = 3;
        for(int i=0; i<tn; i++){
            Thread t = new Thread(()->{
                System.out.println(Thread.currentThread().getName()+": "+LazyMan.getInstance().hashCode());
            });
            t.setName("Thread@"+i);
            t.start();
        }
    }
}
