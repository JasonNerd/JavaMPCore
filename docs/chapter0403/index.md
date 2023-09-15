---
title: ""第四章 - Lock对象的使用(3) - 读写锁""
date: 2023-09-15T14:25:11+08: 00
draft: false
tags: ["ReadWriteLock"]
categories: ["concurrent", "Java"]
twemoji: true
lightgallery: true
---


类ReentrantLock具有完全互斥排他的效果，即同一时间只有一个线程在执行 `ReentrantLock.lock()` 方法后面的任务。在JDK中提供了一种读写锁 `ReentrantReadWriteLock` 类，使用它可以加快运行效率，在某些不需要操作实例变量的方法中，完全可以使用读写锁 ReentrantReadWriteLock 来提升该方法的代码运行速度。读写锁表示也有两个锁，一个是读操作相关的锁，也称为共享锁;另一个是写操作相关的锁，也叫排他锁。也就是多个读锁之间不互斥，读锁与写锁互斥，写锁与写锁互斥。在没有线程Thread进行写入操作时，进行读取操作的多个Thread都可以获取读锁，而进行写人操作的 Thread 只有在获取写锁后才能进行写人操作。即多个Thread 可以同时进行读取操作但是同一时刻只允许一个Thread 进行写入操作。


服务程序如下:
```java
/**
 * ReadWriteLock: 读写锁
 * 该锁实现读线程间异步执行
 */
public class RWLock {
    private int opn = 1;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void readMul(){
        try{
            lock.readLock().lock();
            System.out.println(Thread.currentThread().getName()+" read the value of opn*2 is "+opn*2);
            Thread.sleep(200);
            System.out.println(Thread.currentThread().getName()+" read over.");
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            lock.readLock().unlock();
        }
    }

    public void readAdd(){
        try{
            lock.readLock().lock();
            System.out.println(Thread.currentThread().getName()+" read the value of (opn+2) is "+(opn+2));
            Thread.sleep(200);
            System.out.println(Thread.currentThread().getName()+" read over.");
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            lock.readLock().unlock();
        }
    }

    public void addMul(){
        try{
            lock.writeLock().lock();
            opn = (opn+2)*opn;
            System.out.println(Thread.currentThread().getName()+" update the value of operator: "+opn);
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            System.out.println(Thread.currentThread().getName()+" write over.");
            lock.writeLock().unlock();
        }
    }
}
```

使用10个线程测试, 此时有7个读线程, 3个写线程
```java
public class RWMain {
    public static void main(String[] args) {
        // 测试读写锁
        final int thread_num = 10;
        RWLock service = new RWLock();
        int service_num = 3;
        Thread[] threads = new Thread[thread_num];
        for(int i=0; i<thread_num; i++){
            if(i%service_num==0)threads[i]=new Thread(service::readAdd);     // 读服务1
            if(i%service_num==1)threads[i]=new Thread(service::readMul);     // 读服务2
            if(i%service_num==2)threads[i]=new Thread(service::addMul);      // 写服务
            threads[i].setName("Thread"+i);
        }
        for(int i=0; i<thread_num; i++){
            threads[i].start();
        }
    }
}
```

输出如下:
```cmd
Thread1 read the value of opn*2 is 2
Thread0 read the value of (opn+2) is 3
Thread4 read the value of opn*2 is 2
Thread3 read the value of (opn+2) is 3
Thread6 read the value of (opn+2) is 3
Thread4 read over.
Thread1 read over.
Thread3 read over.
Thread0 read over.
Thread6 read over.
Thread2 update the value of operator: 3
Thread2 write over.
Thread5 update the value of operator: 15
Thread5 write over.
Thread7 read the value of opn*2 is 30
Thread7 read over.
Thread8 update the value of operator: 255
Thread8 write over.
Thread9 read the value of (opn+2) is 257
Thread9 read over.
```
