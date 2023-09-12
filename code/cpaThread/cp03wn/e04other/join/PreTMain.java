package cpaThread.cp03wn.e04other.join;

public class PreTMain {
    public static void main(String[] args) throws InterruptedException {
        PreThreadB b = new PreThreadB();
        PreThreadA a = new PreThreadA(b);
        b.setName("b");
        a.setName("a");
        a.start();
        b.start();
        b.join(20);    // 执行 join 方法需要获得 对象b 的锁(然后快速释放, main 线程进入计时等待)
        System.out.println("Main now end");
    }
}
