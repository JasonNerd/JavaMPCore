---
title: "Java多线程 - 第一章"
date: 2023-08-21T11:51:34+08: 00
draft: false
tags: ["MultiThread"]
categories: ["concurrent", "Java"]
twemoji: true
lightgallery: true
---
线程的启动、暂停、恢复、停止、优先级、安全问题。

## 进程与线程
**进程**: 进程时一次程序的执行, 是一个程序及其数据在处理机上顺序执行时所发生的活动, 是程序在一个数据集合上运行的过程, 它是进行资源分配和调度的一个独立单位.
**线程**, 一个进程可以包含多个线程, 各个线程并发的执行各个子任务.

## 线程基本特性
### 1. main线程
`Thread.currentThread().getName()` 获取当前线程的名称: main线程执行了main方法

### 2. Thread.start()
通过继承 `Thread` 类来定义自己的线程类, 重写 `run()` 方法以实现运行自己的业务逻辑. 注意, start()方法会新建线程并执行run()方法, 但如果直接调用run()方法则仍交给调用者线程处理(此时就相当于调用普通方法)

### 3. thread 随机性
1. 线程启动顺序与`start()`执行顺序无关, 启动次序随机.
2. 各个线程可能穿插执行, 穿插次序随机.

### 4. `Runnable` 接口
`Thread()`构造函数可以接受实现了 Runnable 接口的类对象.
通过继承Thread 或是通过实现 Runnable 接口 都可以实现自己的 Thread 类, 在功能上两者没有差异.

### 5. 共享变量
多个线程在对一个共享变量操作时可能发生数据不一致的情况. 此即为线程不安全. 可以使用 `synchronized` 关键字修饰方法, 使得方法中的代码能完整的被一个线程执行完毕而不会被打断. 

### 6. 部分 api 说明
1. `System.out.println()` 方法是同步的, 也即不会出现交替输出的情况

2. `currentThread()`返回代码片段正在被哪个线程调用, 注意与 `this` 当前对象进行区分.

3. `isAlive()`判断当前线程是否处于活动状态.

4. `sleep(m)` 方法可以使当前正在执行的线程暂停执行 m 毫秒.

5. `getId()` 方法返回线程唯一标识.

6. `yield()`方法的作用是放弃当前的 CPU资源，将它让给其他的任务去占用 CPU执行时间。但再次获得的时间不确定，有可能刚刚放弃，马上又获得 CPU 时间片。

### 7. interrupt
中断线程, `interrupt` 方法设置线程中断标志, 此时线程不会立即停止, 而是允许线程在进行一定处理后再自行停止.
典型过程是:
1. main 线程设置了线程 t 的中断标志
2. 线程 t 反复测试中断标志
3. 若标志为 true, 则抛出 `InterruptedException`, 此时线程转为执行异常处理逻辑, 随后退出执行.
4. 否则继续运行非异常逻辑.

### 8. priority
在操作系统中，线程可以划分优先级，优先级较高的线程得到的 CPU 资源较多，也就是CPU优先执行优先级较高的线程对象中的任务.

在Java 中，线程的优先级分为1~ 10 这10个等级，如果小于1或大于10，则JDK抛出异常 `IllegalArgumentException`. 高优先级的线程总是大部分先执行完，但不代表高优先级的线程全部先执行完.

在 Java 中，线程的优先级具有**继承性**，比如A 线启动 B 线程，则B 线的优先级与A 是一样的.


#### 注意
1. `Thread.interrupted()` 和 `this.isInterrupted()` 都可用于测试中断状态, 不同的是, 前者测试的是执行当前代码的线程的中断状态, 且会清除中断标志. 后者则测试this对应的线程的中断状态, 但不会清除标志.**一般而言使用前者就可以了**

2. 处于 active 的线程可被停止, 如果此时线程处于 sleep 状态, 则直接抛出 InterruptedException.

3. 使用 stop() 释放锁将会给数据造成不一致性的结果。如果出现这样的情况，程序处理的数据就有可能遭到破坏，最终导致程序执行的流程错误，一定要特别注意

4. 可以使用 suspend() 方法暂停线程，使用 resume() 方法恢复线的执行.在使用 suspend 与 resume 方法时，如果使用不当，极易造成公共的同步对象的独占，使得其他线程无法访问公共同步对象。在使用suspend与resume方法时也容易出现因为线程的暂停而导致数据不同步的情况

### 9. Daemon
在 Java 线程中有两种线程，一种是用户线程，另一种是守护线程。守护线程是一种特殊的线程，它的特性有“陪伴”的含义，当进程中不存在非守护线程了，则守护线程自动销毁。典型的守护线程就是垃圾回收线程，当进程中没有非守护线程了，则垃圾回收线程也就没有存在的必要了，自动销毁。用个比较通俗的比喻来解释一下“守护线程”:任何一个守护线程都是整个JVM 中所有非守护线程的“保姆”，只要当前JVM实例中存在任何一个非守护线程没有结束，守护线程就在工作，只有当最后一个非守护线程结束时，守护线程才随着JVM一同结束工作。Daemon 的作用是为其他线程的运行提供便利服务，守护线程最典型的应用就是 GC (垃圾回收器)它就是一个很称职的守护者。


## 附录
### 1. main线程
```java
public static void main(String[] args) {
    System.out.println(Thread.currentThread().getName());
}
```

### 2. Thread.start()
```java
public class MyThread extends Thread implements Runnable{
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
        Thread t = new MyThread();
        t.start();
        System.out.println("in thread "+Thread.currentThread().getName()+" end");
    }
}
/*
in thread main start
in thread main end
	 in thread Thread-0
	 Hello multi thread!
*/
```

### 3. thread 随机性
```java
public class IdThread extends Thread{
    // 具有 id, 可以将自己的id输出
    private int id;
    public IdThread(int id){
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("✦✦✦✦IdThread "+id+" start.✦✦✦✦");
        for(int i=0; i<3; i++){
            System.out.println("IdThread "+id+" is running.");
            try {
                Thread.sleep((int) (Math.random()*500));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("✧✧✧✧IdThread "+id+" end.✧✧✧✧");
    }

    public static void main(String[] args) {
        // 1. 创建很多个线程
        int thread_num = 10;
        Thread[] threads = new Thread[thread_num];
        for(int i=0; i<thread_num; i++)
            threads[i] = new IdThread(i);
        // 2. 依次启动他们
        for(int i=0; i<thread_num; i++)
            threads[i].start();
    }
}
/*
✦✦✦✦IdThread 2 start.✦✦✦✦
✦✦✦✦IdThread 5 start.✦✦✦✦
✦✦✦✦IdThread 4 start.✦✦✦✦
✦✦✦✦IdThread 3 start.✦✦✦✦
✦✦✦✦IdThread 9 start.✦✦✦✦
✦✦✦✦IdThread 7 start.✦✦✦✦
✦✦✦✦IdThread 1 start.✦✦✦✦
✦✦✦✦IdThread 0 start.✦✦✦✦
IdThread 0 is running.
IdThread 4 is running.
IdThread 9 is running.
✦✦✦✦IdThread 6 start.✦✦✦✦
IdThread 6 is running.
✦✦✦✦IdThread 8 start.✦✦✦✦
IdThread 7 is running.
IdThread 3 is running.
IdThread 2 is running.
IdThread 1 is running.
IdThread 5 is running.
IdThread 8 is running.
IdThread 5 is running.
IdThread 1 is running.
IdThread 9 is running.
IdThread 7 is running.
IdThread 7 is running.
IdThread 8 is running.
IdThread 9 is running.
IdThread 5 is running.
IdThread 1 is running.
IdThread 4 is running.
IdThread 2 is running.
✧✧✧✧IdThread 7 end.✧✧✧✧
IdThread 2 is running.
IdThread 0 is running.
✧✧✧✧IdThread 9 end.✧✧✧✧
✧✧✧✧IdThread 1 end.✧✧✧✧
IdThread 3 is running.
IdThread 8 is running.
IdThread 4 is running.
IdThread 6 is running.
IdThread 0 is running.
✧✧✧✧IdThread 8 end.✧✧✧✧
IdThread 6 is running.
IdThread 3 is running.
✧✧✧✧IdThread 5 end.✧✧✧✧
✧✧✧✧IdThread 3 end.✧✧✧✧
✧✧✧✧IdThread 2 end.✧✧✧✧
✧✧✧✧IdThread 6 end.✧✧✧✧
✧✧✧✧IdThread 0 end.✧✧✧✧
✧✧✧✧IdThread 4 end.✧✧✧✧
*/
```

### 4. Runnable
使用 Runnable 复现了 3 中的随机性
```java
public class IdRunnable implements Runnable{
    private int id;
    public IdRunnable(int id){
        this.id = id;
    }
    @Override
    public void run() {
        System.out.println("✦✦✦✦IdRunnable "+id+" start.✦✦✦✦");
        for(int i=0; i<3; i++){
            System.out.println("IdRunnable "+id+" is running.");
            try {
                Thread.sleep((int) (Math.random()*500));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("✧✧✧✧IdThread "+id+" end.✧✧✧✧");
    }

    public static void main(String[] args) {
        // 1. 创建很多个线程(通过 Runnable 接口)
        int thread_num = 10;
        Thread[] threads = new Thread[thread_num];
        for(int i=0; i<thread_num; i++)
            threads[i] = new Thread(new IdRunnable(i));
        // 2. 依次调用start()(但启动次序未知)
        for(int i=0; i<thread_num; i++)
            threads[i].start();
        // 3. 从结果看出各线程的启动、执行都具有随机性.
    }
}

```

### 5. 共享变量
```java
public class SharedT implements Runnable{
    private int cnt;
    public SharedT(int cnt){
        this.cnt = cnt;
    }
    @Override
    public void run() {
        cnt--;
        System.out.println(Thread.currentThread().getName()+": cnt="+cnt);
    }
    public static void main(String[] args) {
        // 多个线程对同一个变量进行操作
        int cnt = 5;
        Runnable r = new SharedT(cnt);
        // 创建多个线程并启动它们, 每个线程都对共享变量进行减1.
        for(int i=0; i<cnt; i++)
           new Thread(r).start();
    }
}

```

### 6. currentThread
```java
public class E06CurrentT extends Thread{
    public E06CurrentT(){
        System.out.println("Constructor---begin");
        System.out.println("Thread.currentThread().getName()="+ Thread.currentThread().getName ());
        System.out.println("Thread.currentThread().isAlive()="+ Thread.currentThread().isAlive());
        System.out.println("this.getName()=" + this.getName());
        System.out.println("this.isAlive()=" + this.isAlive());
        System.out.println("Constructor---end");
    }

    @Override
    public void run() {
        System.out.println("run---begin");
        System.out.println("Thread.currentThread().getName()="+ Thread.currentThread().getName ());
        System.out.println("Thread.currentThread().isAlive()="+ Thread.currentThread().isAlive());
        System.out.println("this.getName()=" + this.getName());
        System.out.println("this.isAlive()=" + this.isAlive());
        System.out.println("run---end");
    }

    public static void main(String[] args) {
        Thread t = new E06CurrentT();
        t.setName("red");
        t.start();
    }
}

/*
Constructor---begin
Thread.currentThread().getName()=main
Thread.currentThread().isAlive()=true
this.getName()=Thread-0
this.isAlive()=false
Constructor---end
run---begin
Thread.currentThread().getName()=red
Thread.currentThread().isAlive()=true
this.getName()=red
this.isAlive()=true
run---end
*/
```

### 7. interrupt
```java
public class E07Interrupt extends Thread{
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+" start!");
        int i = 0;
        try {
            for (i = 0; i < Integer.MAX_VALUE; i++)
                if (Thread.interrupted())
                    throw new InterruptedException();
        }catch (InterruptedException e){
            System.out.println("i = "+i);
            System.out.println(Thread.currentThread().getName()+" is interrupted!");
            System.out.println(Thread.currentThread().getName()+" end!");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t = new E07Interrupt();
        t.start();
        Thread.sleep(166);
        t.interrupt();  // 中断线程
    }
}

```
### priority
```java
public class PriorT extends Thread{
    private final int id;
    private final int p;
    private final String c;
    public PriorT(int id){
        this.id = id;
        this.c = id % 2 == 0 ? "✦✦✦✦" : "✧✧✧✧";
        this.p = id % 2 == 0 ? 2 : 8;
        this.setPriority(this.p);
    }
    @Override
    public void run() {
//        System.out.println(c+"PriorT "+id+" start!["+p+"]"+c);
        for(int i=0; i<2e5; i++)
            new Random().nextDouble();
        System.out.println(c+"PriorT "+id+" finished!["+p+"]"+c);
    }

    public static void main(String[] args) {
        // 创建多个 PriorT 线程 并启动它们
        int n = 20;
        for(int i=0; i<n; i++)
            new PriorT(i).start();
    }
}
/*
✧✧✧✧PriorT 1 finished![8]✧✧✧✧
✧✧✧✧PriorT 17 finished![8]✧✧✧✧
✧✧✧✧PriorT 5 finished![8]✧✧✧✧
✧✧✧✧PriorT 7 finished![8]✧✧✧✧
✧✧✧✧PriorT 9 finished![8]✧✧✧✧
✧✧✧✧PriorT 15 finished![8]✧✧✧✧
✧✧✧✧PriorT 13 finished![8]✧✧✧✧
✦✦✦✦PriorT 6 finished![2]✦✦✦✦
✧✧✧✧PriorT 11 finished![8]✧✧✧✧
✦✦✦✦PriorT 16 finished![2]✦✦✦✦
✦✦✦✦PriorT 10 finished![2]✦✦✦✦
✧✧✧✧PriorT 3 finished![8]✧✧✧✧
✦✦✦✦PriorT 4 finished![2]✦✦✦✦
✦✦✦✦PriorT 18 finished![2]✦✦✦✦
✧✧✧✧PriorT 19 finished![8]✧✧✧✧
✦✦✦✦PriorT 8 finished![2]✦✦✦✦
✦✦✦✦PriorT 12 finished![2]✦✦✦✦
✦✦✦✦PriorT 2 finished![2]✦✦✦✦
✦✦✦✦PriorT 0 finished![2]✦✦✦✦
✦✦✦✦PriorT 14 finished![2]✦✦✦✦
*/

```



## 链接参考
星形符号
https://cn.piliapp.com/emoji/list/star/
