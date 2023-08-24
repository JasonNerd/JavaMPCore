package cpaThread.cp02syn.e01insecure;

public class E01WriteLost {
    // 如果两个线程同时操作业务对象中的实例变量, 则有可能会出现非线程安全问题
    public static void main(String[] args){
        SharedUpdate shareVar = new SharedUpdate();
        // 线程 a 使用方法 A 更新变量为 200
        Thread a = new Thread(() -> {
            try {
                shareVar.updateLongTime(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        a.setName("ThreadA200");
        // 线程 b 使用方法 B 更新变量为 100
        Thread b = new Thread(() -> {
            try {
                shareVar.updateShortTime(100);
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
