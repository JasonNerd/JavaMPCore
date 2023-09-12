package cpaThread.cp03wn.e04other.join;

public class InterruptE {
    public static void main(String[] args) {
        // 线程B, 它新建并启动线程A, 随后等待线程A执行结束
        Thread t2 = new Thread(()->{
            // 1. 线程 A, 它可能需要执行很久
            Thread t1 = new Thread(()->{
                for(int i=0; i<Integer.MAX_VALUE; i++)
                    Math.random();
            });
            System.out.println("Thread B create&start  thread A");
            t1.setName("A");
            t1.start();
            try {
                System.out.println("Thread B start waiting thread A");
                t1.join();
                System.out.println("Thread B end waiting thread A");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        // 线程C, 它中断B
        Thread t3 = new Thread(t2::interrupt);
        t2.setName("B");
        t2.start();
        try {
            Thread.sleep(26);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        t3.setName("C");
        t3.start();
    }
}
