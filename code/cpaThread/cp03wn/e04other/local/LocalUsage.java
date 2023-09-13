package cpaThread.cp03wn.e04other.local;

/**
 * ThreadLocal 工具类提供了一种使得线程存储私有值的方式:
 *      它向 currentThread() 里的 ThreadLocalMap 对象存储键值对 (t, value)
 *      这里的 t 是指 ThreadLocal 对象
 */
public class LocalUsage {
    public static void main(String[] args) throws InterruptedException {
        InheritableThreadLocal<Object> tlk1 = new InheritableThreadLocal<>();
        tlk1.set("main thread set val");
        Thread t1 = new Thread(()->{
            System.out.println("t1(1) get tlk1="+tlk1.get());
            tlk1.set("12345");
            System.out.println("t1(2) get tlk1="+tlk1.get());
        });
        Thread t2 = new Thread(()->{
            System.out.println("t2(1) get tlk1="+tlk1.get());
            tlk1.set("67890");
            System.out.println("t2(2) get tlk1="+tlk1.get());
        });
        t1.start();
        Thread.sleep(5);
        t2.start();
        // 等待两个子线程执行完毕, 再来查看存储的值
        t1.join();
        t2.join();
        System.out.println("main(2) get tlk1="+tlk1.get());
    }
}
