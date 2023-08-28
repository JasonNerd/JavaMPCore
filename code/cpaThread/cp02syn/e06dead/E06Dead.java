package cpaThread.cp02syn.e06dead;

import cpaThread.cp02syn.e06dead.DataSource;

/**
 * 死锁: 资源共享, 互斥访问, 循环等待, 占有申请
 * 演示: 两个线程 a 和 b, 都需要访问变量 v, 其中访问操作 opa 需要 lockA 锁
 * 访问操作 opb 需要 lockB 锁, 其中 a 持有 lockA 锁并且申请 lockB 锁, 线程 b 持有
 * lockB 锁且申请 lockA 锁.
 */
public class E06Dead {
    public static void main(String[] args) throws InterruptedException {
        DataSource source = new DataSource();
        Thread a = new Thread(()->{
            try {
                source.opa();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        });
        Thread b = new Thread(()->{
            try {
                source.opb();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        });
        a.setName("a");
        b.setName("b");
        a.start();
        b.start();
        a.join();
        b.join();
        System.out.println();
    }
}
