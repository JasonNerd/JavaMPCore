package cpaThread.cp02syn.e03blk;

public class SynBlock {
    // 编写两个方法, 它们做的事情类似, 一个是同步方法
    private long t_start=Long.MAX_VALUE, t_end=Long.MIN_VALUE;  // 统计所有的线程完成的起始时间和终止时间

    public synchronized void operationA() throws InterruptedException {
        System.out.println("task begin @Thread="+Thread.currentThread().getName());
        long t1 = System.currentTimeMillis();   // 某一线程获得该锁后任务起始时间
        if (t1 < t_start)   // 不会有其他线程修改 t_start 值
            t_start = t1;
        Thread.sleep(1000);
        System.out.println("task end @Thread="+Thread.currentThread().getName());
        long t2 = System.currentTimeMillis();
        if (t2 > t_end)
            t_end = t2;
        System.out.println("Task @Thread="+Thread.currentThread().getName()+" use time "+(t2-t1)+" ms");
    }

    // 另一个基于同步方法改为使用同步代码块, 使得耗时操作异步执行, 而对于共享变量的操作则使用 syn 块
    public void operationB() throws InterruptedException {
        System.out.println("task begin @Thread="+Thread.currentThread().getName());
        long t1 = System.currentTimeMillis();   // 某一线程获得该锁后任务起始时间(本地方法变量, 不共享)
        Thread.sleep(1000);
        System.out.println("task end @Thread="+Thread.currentThread().getName());
        long t2 = System.currentTimeMillis();
        synchronized (this) {
            if (t1 < t_start)
                t_start = t1;
            if (t2 > t_end)
                t_end = t2;
        }
        System.out.println("Task @Thread="+Thread.currentThread().getName()+" use time "+(t2-t1)+" ms");
    }

    // 一个同步方法, 打印所有线程执行的总耗时, 涉及共享变量, 选择使用同步方法
    public synchronized void printTotalTime(){
        System.out.println("Totally task finished with "+(t_end-t_start)+" ms.");
    }

}
