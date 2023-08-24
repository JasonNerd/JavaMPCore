package cpaThread.cp02syn.e02reentrant;


/** 将练习1的方法稍作修改, 注意到此时 ReentrantBlk 中的方法均为 synchronized 方法
 * 故而线程顺序执行代码, 不会有非线程安全问题
 * 结论: 线程获取了一个对象锁, 则该线程可以执行该锁锁住的任何其他代码(方法 or 代码块)
 */
public class E02Reentrant {
    public static void main(String[] args){
        ReentrantBlk shareVar = new ReentrantBlk();
        // 线程 a 使用方法 A 更新变量为 200
        Thread a = new Thread(() -> {
            try {
                shareVar.setA(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        a.setName("ThreadA200");
        // 线程 b 使用方法 B 更新变量为 100
        Thread b = new Thread(() -> {
            try {
                shareVar.setB(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        b.setName("ThreadB100");
        // 启动线程
        a.start();
        b.start();
    }
}
