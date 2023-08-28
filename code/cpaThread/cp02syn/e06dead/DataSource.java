package cpaThread.cp02syn.e06dead;

public class DataSource {
    private int var = 0;
    final Object lockA = new Object();
    final Object lockB = new Object();

    public void opa() throws InterruptedException {
        Thread.sleep((int)(Math.random()*100));
        synchronized (lockA){
            System.out.println(Thread.currentThread().getName()+" enter lockA.");
            Thread.sleep((int)(Math.random()*200));
            var = 1;
            System.out.println(Thread.currentThread().getName()+" try lockB.");
            opb();
        }
    }

    public void opb() throws InterruptedException {
        Thread.sleep((int)(Math.random()*120));
        synchronized (lockB){
            System.out.println(Thread.currentThread().getName()+" enter lockB.");
            Thread.sleep((int)(Math.random()*180));
            var = -1;
            System.out.println(Thread.currentThread().getName()+" try lockA.");
            opa();
        }
    }
}
