---
title: "第四章 - Lock对象的使用(2) - 常用API"
date: 2023-09-14T15:21:09+08: 00
draft: false
tags: ["ReentrantLock"]
categories: ["concurrent", "Java"]
twemoji: true
lightgallery: true
---
本小节主要学习 ReentrantLock 常用的对象方法(因此隐含的参数是 lock 对象)
## 1. $getHoldCount() \rightarrow int$
查询线程保持该锁的次数, 也即调用 `lock.lock()` 方法的次数
## 2. $getQueueLength() \rightarrow int$
返回正在等待该锁释放的线程个数
## 3. $getWaitQueueLength(Condition) \rightarrow int$
返回正在等待该锁的某一条件的线程个数
## 4. $hasQueuedThread(Thread) \rightarrow boolean$
查询指定线程是否处于该锁的等待队列
另外`hasQueuedThreads()`方法则查询是否有线程位于该锁的等待队列
## 5. $hasWaiters(Condition) \rightarrow boolean$ 
查询是否有线程正在等待(该锁的)指定Condition.(也即执行了condition.await()方法)
## 6. $isFair() \rightarrow boolean$
返回该锁是否为公平锁
## 7. $isHeldByCurrentThread() \rightarrow boolean$
返回该锁当前是否被当前线程持有
## 8. $isLocked() \rightarrow boolean$
返回该锁当前是否被某一线程持有(是否没被释放)
## 9. $tryLock() \rightarrow boolean$
Acquires the lock if it is not held by another thread and returns immediately with the value true, setting the lock hold count to one. Even when this lock has been set to use a fair ordering policy, a call to tryLock() will immediately acquire the lock if it is available, whether or not other threads are currently waiting for the lock. 

## 代码示例
编写了一个类, 它包含一个lock, 两个与该lock关联的condition, 两个方法与condition对应, 两个打印线程信息的方法
```java
public class APIService {
    ReentrantLock lock = new ReentrantLock();
    Condition c1 = lock.newCondition();
    Condition c2 = lock.newCondition();
    public void info(){
        if(!lock.isHeldByCurrentThread()) {
            lock.lock();
            System.out.println("\tThe lock is Fair? " + lock.isFair());
            System.out.println("\tThe lock is Locked? " + lock.isLocked());
            System.out.println("\tAny threads is waiting this lock? " + lock.hasQueuedThreads());
            System.out.println("\tHow many threads is waiting this lock? " + lock.getQueueLength());
            System.out.println("\tc1 has waiters? " + lock.hasWaiters(c1));
            System.out.println("\tc1 has waiters? " + lock.hasWaiters(c2));
            System.out.println("\tThere are " + lock.getWaitQueueLength(c1) + " wait in c1.");
            System.out.println("\tThere are " + lock.getWaitQueueLength(c2) + " wait in c2.");
            c1.signalAll();
            lock.unlock();
        }
    }
    public void ts_info(Thread[] threads){
        // 查询这些线程持有锁的情况
        if(!lock.isHeldByCurrentThread()) {
            lock.lock();
            for (Thread t : threads)
                if (lock.hasQueuedThread(t))
                    System.out.println("\t\t" + t.getName() + "now waiting this lock");
            c2.signalAll();
            lock.unlock();
        }
    }
    public void c1p(){
        lock.lock();
        System.out.println(Thread.currentThread().getName()+" 一共获取该锁"+lock.getHoldCount()+"次");
        try{
            Thread.sleep(10);
            c1.await();     // 进入 c1 等待队列
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        lock.unlock();
    }

    public void c2p(){
        lock.lock();
        System.out.println(Thread.currentThread().getName()+" 一共获取该锁"+lock.getHoldCount()+"次");
        try{
            c2.await();     // 进入 c1 等待队列
            Thread.sleep(10);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        lock.unlock();
    }
}
```
编写了测试的主方法, 它创建多个线程, 为每个线程指定一个要执行的任务, 随后再创建一个信息打印线程.
```java
public class APITest {
    public static void main(String[] args) {
        final int thread_num = 3;
        APIService service = new APIService();
        // 初始化了几个线程, 各个线程执行哪一个方法不确定, 执行次数也不确定
        Thread[] threads = new Thread[thread_num];
        for(int i=0; i<thread_num; i++){
            int rd = (int)(Math.random()*10);   // 10以内的随机数
            boolean even = rd % 2 == 0;      // 是不是偶数
            if(even){
                threads[i] = new Thread(()->{
                    for(int j=0; j<rd; j++)service.c1p();
                });
                threads[i].setName("C1@"+i);
            }else {
                threads[i] = new Thread(()->{
                    for(int j=0; j<rd; j++)service.c2p();
                });
                threads[i].setName("C2@"+i);
            }
            threads[i].start();
        }
        // 再来个打印线程信息的线程
        Thread info = new Thread(()->{
            int i = 0;
            while (true) {
                service.info();
                service.ts_info(threads);
                ++i;
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if(i>=10)break;
            }
        });
        info.setName("InfoPrintThread");
        info.start();
    }
}
```

如下为某次运行的打印信息, 
```cmd
C2@0 一共获取该锁1次
C1@2 一共获取该锁1次
----------------------------------------------------------------
	The lock is Fair? false
	The lock is Locked? true
	Any threads is waiting this lock? false
	How many threads is waiting this lock? 0
	c1 has waiters? true
	c1 has waiters? true
	There are 1 wait in c1.
	There are 1 wait in c2.
----------------------------------------------------------------
		C1@2now waiting this lock
C1@2 一共获取该锁1次
----------------------------------------------------------------
	The lock is Fair? false
	The lock is Locked? true
	Any threads is waiting this lock? false
	How many threads is waiting this lock? 0
	c1 has waiters? true
	c1 has waiters? false
	There are 1 wait in c1.
	There are 0 wait in c2.
----------------------------------------------------------------
		C1@2now waiting this lock
C1@2 一共获取该锁1次
----------------------------------------------------------------
	The lock is Fair? false
	The lock is Locked? true
	Any threads is waiting this lock? false
	How many threads is waiting this lock? 0
	c1 has waiters? true
	c1 has waiters? false
	There are 1 wait in c1.
	There are 0 wait in c2.
----------------------------------------------------------------
		C1@2now waiting this lock
C1@2 一共获取该锁1次
----------------------------------------------------------------
	The lock is Fair? false
	The lock is Locked? true
	Any threads is waiting this lock? false
	How many threads is waiting this lock? 0
	c1 has waiters? true
	c1 has waiters? false
	There are 1 wait in c1.
	There are 0 wait in c2.
----------------------------------------------------------------
		C1@2now waiting this lock
C1@2 一共获取该锁1次
----------------------------------------------------------------
	The lock is Fair? false
	The lock is Locked? true
	Any threads is waiting this lock? false
	How many threads is waiting this lock? 0
	c1 has waiters? true
	c1 has waiters? false
	There are 1 wait in c1.
	There are 0 wait in c2.
----------------------------------------------------------------
		C1@2now waiting this lock
C1@2 一共获取该锁1次
----------------------------------------------------------------
	The lock is Fair? false
	The lock is Locked? true
	Any threads is waiting this lock? false
	How many threads is waiting this lock? 0
	c1 has waiters? true
	c1 has waiters? false
	There are 1 wait in c1.
	There are 0 wait in c2.
----------------------------------------------------------------
		C1@2now waiting this lock
----------------------------------------------------------------
	The lock is Fair? false
	The lock is Locked? true
	Any threads is waiting this lock? false
	How many threads is waiting this lock? 0
	c1 has waiters? false
	c1 has waiters? false
	There are 0 wait in c1.
	There are 0 wait in c2.
----------------------------------------------------------------
```