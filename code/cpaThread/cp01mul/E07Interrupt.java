package cpaThread.cp01mul;

public class E07Interrupt extends Thread{
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+" start!");
        int i = 0;
        try {
            for (i = 0; i < Integer.MAX_VALUE; i++)
                if (Thread.interrupted())
                    throw new InterruptedException();
        }catch (InterruptedException e){
            System.out.println("i = "+i);
            System.out.println(Thread.currentThread().getName()+" is interrupted!");
            System.out.println(Thread.currentThread().getName()+" end!");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t = new E07Interrupt();
        t.start();
        Thread.sleep(166);
        t.interrupt();  // 中断线程
    }
}
