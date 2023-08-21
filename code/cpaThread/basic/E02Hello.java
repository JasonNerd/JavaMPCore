package cpaThread.basic;

public class E02Hello extends Thread implements Runnable{
    // 通过继承 Thread 类定义自己的线程类, 重写 run() 方法以实现运行自己的业务逻辑
    @Override
    public void run() {
        super.run();
        System.out.println("\t in thread "+Thread.currentThread().getName());
        System.out.println("\t Hello multi thread!");
    }
    // 通过 start() 方法启动一个线程
    public static void main(String[] args) {
        System.out.println("in thread "+Thread.currentThread().getName()+" start");
        Thread t = new E02Hello();
        t.start();
        System.out.println("in thread "+Thread.currentThread().getName()+" end");
    }
}
