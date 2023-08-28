package cpaThread.cp02syn.e05static;

public class E05Static {
    public static void main(String[] args) {
        Thread[] threads = new Thread[3];
        MethodStatic a = new MethodStatic();
        MethodStatic b = new MethodStatic();
        threads[0] = new Thread(){
            @Override
            public void run() {
                a.ssvA();       // 等价于 MethodStatic.ssvA
            }
        };
        threads[1] = new Thread(){
            @Override
            public void run() {
                b.ssvB();       // 等价于 MethodStatic.ssvB
            }
        };
        threads[2] = new Thread(){
            @Override
            public void run() {
                b.svC();
            }
        };
        // a. 静态Class锁与对象锁不一样: 假如 1 和 2 是异步的
        // b. 静态Class锁对所有的实例对象都起作用: 假如 0 和 1同步
        for(Thread t: threads)
            t.start();
        // 由于线程较少, 因此可能运行多次才可观察到现象(主要是a)
    }
    /**
     * Thread-1 enter B
     * Thread-2 enter C
     * Thread-1 quit B
     * Thread-2 quit C
     * Thread-0 enter A
     * Thread-0 quit A
     */
}
