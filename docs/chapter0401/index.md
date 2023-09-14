---
title: "第四章 - Lock对象的使用(1)"
date: 2023-09-13T15:33:02+08: 00
draft: false
tags: ["ReentrantLock"]
categories: ["concurrent", "Java"]
twemoji: true
lightgallery: true
---

## ReentrantLock
在Java多线程中，可以使用synchronized关键字来实现线程之间同步互斥，但在JDK1.5中新增加了ReentrantLock类也能达到同样的效果，并且在扩展功能上也更加强大，比如具有嗅探锁定、多路分支通知等功能，而且在使用上也比synchronized 更加的灵活。

基本使用方法
```java
public class CP04Lock {
    private final Lock lock = new ReentrantLock();
    public void method(){
        lock.lock();        // 开启监视
        System.out.println(Thread.currentThread().getName()+" entered.");
        int slp = (int)(Math.random()*200);
        try {
            Thread.sleep(slp);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName()+" going to exit.");
        lock.unlock();      // 解除监视
    }

    public static void main(String[] args) {
        // 测试
        final int thread_num = 5;
        CP04Lock service = new CP04Lock();
        Thread[] threads = new Thread[thread_num];
        for(int i=0; i<thread_num; i++) {
            threads[i] = new Thread(service::method);
            threads[i].setName("thread@"+i);
        }
        for(int i=0; i<thread_num; i++)
            threads[i].start();
    }
}
/*
thread@0 entered.
thread@0 going to exit.
thread@2 entered.
thread@2 going to exit.
thread@3 entered.
thread@3 going to exit.
thread@4 entered.
thread@4 going to exit.
thread@1 entered.
thread@1 going to exit.
*/
```
在需要锁定的代码前后使用lock/unlock即可, 此时各个线程会依次进入执行.

## await()/ signal()
关键字`synchronized`与`wait()`和`notify()/notifyAll()`方法相结合可以实现等待/通知模式. ReentrantLock 也可以实现同样的功能, 这需要借助于 **`Condition`** 对象。Condition类是在JDK5中出现的技术，使用它有更好的灵活性，比如可以实现多路通知功能，也就是**在一个Lock对象里面可以创建多个Condition(即对象监视器)实例**，线程对象可以注册在指定的Condition中，从而可以有选择性地进行线程通知，在调度线程上更加灵活. 使用ReentrantLock结合Condition类是可以实现选择性通知, 这个功能在Condition类中是默认提供的。而synchronized就相当于整个Lock 对象中只有一个单一的Condition对象，所有的线程都注册在它一个对象的身上, notifyA11()需要通知所有的WAITING线程, 没有选择权, 这会出现相当大的效率问题.

使用多个 Condition, 结合 await()/ signal(), 程序演示了基本的使用方法:
```java
/**
 * apple: ca
 * juice: cb
 * ca: walk
 * cb: drink
 */
public class Service {
    private final Lock lock = new ReentrantLock();
    private final Condition ca = lock.newCondition();
    private final Condition cb = lock.newCondition();

    private void occ(int t){
        int v = (int)(Math.random()*t);
        try {
            Thread.sleep(v);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void apple(){
        System.out.println(Thread.currentThread().getName()+" enter apple.");
        occ(100);
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName()+" now waiting@ca.");
            ca.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName()+" quit apple.");
        lock.unlock();
    }

    public void juice(){
        System.out.println(Thread.currentThread().getName()+" enter juice.");
        occ(260);
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName()+" now waiting@cb.");
            cb.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName()+" quit juice.");
        lock.unlock();
    }

    public void walk(){
        lock.lock();
        System.out.println(Thread.currentThread().getName()+" signal ca.");
        ca.signalAll();
        lock.unlock();
    }

    public void drink(){
        lock.lock();
        System.out.println(Thread.currentThread().getName()+" signal cb.");
        cb.signalAll();
        lock.unlock();
    }
}

```

测试方法
```java
public class ConditionalSignal {
    public static void main(String[] args) throws InterruptedException {
        Service service = new Service();
        // 创建4个线程, 2个将在ca处等待, 2个在cb处等待
        final int thread_num = 2;
        Thread[] threads = new Thread[thread_num*2];
        for(int i=0; i<thread_num; i++) {
            threads[2*i] = new Thread(service::apple);      // ca
            threads[2*i].setName("CA@"+(2*i));
            threads[2*i+1] = new Thread(service::juice);    // cb
            threads[2*i+1].setName("CB@"+(2*i+1));
        }
        // 启动这些线程
        for(int i=0; i<thread_num*2; i++)
            threads[i].start();
        // main线程先通知 ca
        Thread.sleep(500);
        service.walk();
        Thread.sleep(500);
        service.drink();
    }
}
```
输出
```cmd
CB@1 enter juice.
CB@3 enter juice.
CA@2 enter apple.
CA@0 enter apple.
CA@2 now waiting@ca.
CA@0 now waiting@ca.
CB@1 now waiting@cb.
CB@3 now waiting@cb.
main signal ca.
CA@2 quit apple.
CA@0 quit apple.
main signal cb.
CB@1 quit juice.
CB@3 quit juice.

```

## 生产者消费者模式
### 一对一交替打印
通过使用一个完成标志, 实现两个线程的交替执行.
```java
public class One2One {
    private boolean havingSet = false;  // 标志位, 初始时表示共享变量未设置值
    private final Lock lock = new ReentrantLock();
    private final Condition c = lock.newCondition();

    public void set() {
        lock.lock();
        if(havingSet){       // 已经有值了
            try {c.await(); Thread.sleep(100);}
            catch (InterruptedException e) {throw new RuntimeException(e);}
        }
        System.out.println(Thread.currentThread().getName()+" print ####");     // 表示已经生产
        havingSet = true;
        c.signal();
        lock.unlock();
    }

    public void get() {
        lock.lock();
        if(!havingSet){      // 还没设置值
            try { c.await(); Thread.sleep(100);}
            catch (InterruptedException e) { throw new RuntimeException(e);}
        }
        System.out.println(Thread.currentThread().getName()+" print ****");     // 表示已经消费
        havingSet = false;
        c.signal();
        lock.unlock();
    }

    public static void main(String[] args) {
        One2One service = new One2One();
        Thread producer = new Thread(()->{
            while (true)
                service.set();
        });
        Thread consumer = new Thread(()->{
            while (true)
                service.get();
        });
        producer.start();
        consumer.start();
    }
}
```
它们交替执行, 输出如下:
```
Thread-0 print ####
Thread-1 print ****
Thread-0 print ####
Thread-1 print ****
Thread-0 print ####
...
...
```
如果是多个生产者消费者, 只需要把if改while, sinal 改sinalAll

## 公平锁与非公平锁
如下, 测试了公平锁与非公平锁, 默认 ReentrantLock 是非公平锁.
```java
public class IfFairService {
    private final Lock lock;
    public IfFairService(boolean ifFair){
        lock = new ReentrantLock(ifFair);
    }

    public void service(){
        lock.lock();
        try {
            System.out.println("----"+Thread.currentThread().getName()+"----");
            Thread.sleep(20);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        lock.unlock();
    }
}
```
测试代码
```java
public class FairTest {
    public static void main(String[] args) throws InterruptedException {
        IfFairService service = new IfFairService(false);    // 调整锁是否公平锁
        final int thread_num = 1000;
        Thread[] fts = new Thread[thread_num];
        for (int i=0; i<thread_num; i++){
            fts[i] = new Thread(service::service);
            fts[i].setName("First@"+i);
        }
        Thread[] sts = new Thread[thread_num];
        for (int i=0; i<thread_num; i++) {
            sts[i] = new Thread(service::service);
            sts[i].setName("Second@" + i);
        }
        // 先启动 fts
        for (int i=0; i<thread_num; i++)
            fts[i].start();
        // 再启动 sts
        for (int i=0; i<thread_num; i++)
            sts[i].start();
    }
}
```
