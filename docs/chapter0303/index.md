---
title: "第三章 -- 线程通信(3)-其他通信方式"
date: 2023-09-12T14:37:38+08: 00
draft: false
tags: []
categories: []
twemoji: true
lightgallery: true
---

## pipe 管道
线程间可以使用**管道流(piped stream)**传输数据, 一个线程发送数据到输出管道, 另一个线程从输入管道接收数据. 在 java 中, 有四个类可提供使用:
`PipedInputStream`, `PipedOutputStream`, `PipedReader`, `PipedWriter`
前两者直接读写字节数据, 后两者则可直接读写字符.

如下的程序演示了线程间管道通信的基本情况:

ReadService:
```java
public class ReadService {
    private PipedInputStream reader;
    public ReadService(PipedInputStream in){
        reader = in;
    }
    public void read(){
        // 从 in 字节流中读取字节, 转为字符串并打印
        System.out.println("read begin");
        byte[] buffer = new byte[10];
        try {
            int l = reader.read(buffer);
            while (l > 0) {
                String data = new String(buffer, 0, l);
                System.out.println(l+": "+data);
                l = reader.read(buffer);
            }
            System.out.println("read end");
            reader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
```
WriteService
```java
public class WriteService {
    PipedOutputStream writer;
    public WriteService(PipedOutputStream out){
        writer = out;
    }
    public void write(String data){
        // 将字符串 data 写入到 out 管道, 此处演示时是单字符单字符的写入
        int n = data.length();
        System.out.println("Write begin");
        try {
            for (int i = 0; i < n; i++) {
                char c = data.charAt(i);
                writer.write(c);
            }
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Write end");
    }
}
```
测试类, 新建一个读线程和一个写线程, 启动它们.
```java
public class Main {
    public static void main(String[] args) {
        try {
            PipedInputStream in = new PipedInputStream();
            PipedOutputStream out = new PipedOutputStream();
            in.connect(out);    // 连接两个管道
            WriteService w = new WriteService(out);
            ReadService  r = new ReadService(in);
            // 新建写管道线程
            Thread wt = new Thread(() -> w.write("ALittleHotToday"));
            // 新建读管道线程
            Thread rd = new Thread(r::read);
            // 启动两个线程
            wt.start();
            rd.start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
```
输出如下:
```cmd
read begin
Write begin
Write end
10: ALittleHot
5: Today
read end
```
可见, 在写线程将数据全部写入到管道前, 读线程阻塞直到写线程工作完毕, 读线程读出数据.


## join()
join() 方法作用是等待子线程销毁, 它具有释放锁的特点, 常常用于主线程等待所有子线程执行结束后再进行更多处理的情景.

### join 与 InterruptedException
线程B 创建了 线程A, 并使用了 join() 方法等待线程A执行结束, 在线程B等待的过程中, 线程C中断了线程B, 此时会产生中断异常, 这是由于join方法内部使用的是wait()方法.
代码演示如下:
```java
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
```

输出如下:
```cmd
Thread B create&start  thread A
Thread B start waiting thread A
Exception in thread "B" java.lang.RuntimeException: java.lang.InterruptedException
	at cpaThread.cp03wn.e04other.join.InterruptE.lambda$main$1(InterruptE.java:20)
	at java.base/java.lang.Thread.run(Thread.java:833)
Caused by: java.lang.InterruptedException
	at java.base/java.lang.Object.wait(Native Method)
	at java.base/java.lang.Thread.join(Thread.java:1304)
	at java.base/java.lang.Thread.join(Thread.java:1372)
	at cpaThread.cp03wn.e04other.join.InterruptE.lambda$main$1(InterruptE.java:17)
	... 1 more
```

### join 细节
前面提到, join 基本功能是等待子线程执行结束, 而它内部使用 wait 方法实现, 因此主线程在等待子线程执行结束后继续向下执行需要重新获取锁. 据此, 设想这样的情景:
主线程创建了两个线程, 等待其中的一个线程, 这三个线程需要争抢同一个锁, 此时可能出现join()后面的语句提前执行.

线程类及执行类如下:
```java
public class PreThreadA extends Thread{
    private final PreThreadB lock;
    public PreThreadA(PreThreadB b){
        lock = b;
    }

    @Override
    public synchronized void run(){
        synchronized (lock) {
            System.out.println("A.run()@" + Thread.currentThread().getName() + " begin: " + System.currentTimeMillis());
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("A.run()@" + Thread.currentThread().getName() + " ended: " + System.currentTimeMillis());
        }
    }
}

public class PreThreadB extends Thread{
    @Override
    public synchronized void run(){
        System.out.println("B.run()@"+Thread.currentThread().getName()+" begin: "+System.currentTimeMillis());
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("B.run()@"+Thread.currentThread().getName()+" ended: "+System.currentTimeMillis());
    }
}

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
```
可能出现结果(部分举例):
```cmd
begin A
end A
main
begin B
end B
```
b.join() 抢到锁(b), 随后释放, A抢到锁(b), 输出begin A, 等待, 输出end A; b.join() 和 B 争抢锁b, b.join() 抢到, 随后输出main, B抢到锁(b), 输出begin B, 等待, 输出end B.

```cmd
begin B
end B
begin A
end A
main
```
b.join() 抢到锁(b), 随后释放, B抢到锁(b), 输出begin B, 等待, 输出end B; b.join() 和 A 争抢锁b, A 抢到, 输出begin A, 等待, 输出end A; b.join() 得到锁b, 随后输出main.

```cmd
begin A
end A
begin B
main
end B
```
b.join() 抢到锁(b), 随后释放, A抢到锁(b), 输出begin A, 等待, 输出end A; b.join() 和 B 争抢锁b, b.join() 抢到, join()执行结束; 随后进行输出时A和main争抢PrintStream锁, A抢到, 输出begin A, 等待, 此时main得到PrintStream锁, 输出main, A等待结束, 输出 end A.
