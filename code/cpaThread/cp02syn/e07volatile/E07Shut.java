package cpaThread.cp02syn.e07volatile;

public class E07Shut {
    public static void main(String[] args) {
        // 1. 新建一个 Interrupt 对象
        Interrupt i = new Interrupt();
        // 2. 一个线程执行 prt(), 另一个执行中断标记设置
        Thread t1 = new Thread(i::prt);
        Thread t2 = new Thread(()->{
            i.setRun(false);
        });
        // 3. 启动它们
        t1.setName("t1");
        t2.setName("t2");
        t1.start();
        t2.start();
    }
}
