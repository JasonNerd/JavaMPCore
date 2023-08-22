---
title: "Java多线程 - 第二章"
date: 2023-08-21T16:26:01+08: 00
draft: false
tags: ["synchronized", "volatile"]
categories: ["concurrent", "Java"]
twemoji: true
lightgallery: true
---
本章主要介绍 Java 多线程中的同步，也就是如何在 Java 语言中写出线程安全的程序如何在Java 语言中解决非线程安全的相关问题。
本章应该着重掌握如下技术点:
* 非线程安全是如何出现的
* synchronized 对象监视器为 Object 时的使用
* synchronized 对象监视器为 Class 时的使用
* 关键字 volatile 的主要作用
* 关键字volatile与synchronized 的区别及使用情况

## synchronized
### 1. synchronized 修饰方法
非线程安全问题存在于实例变量中, 如果是方法内部的私有变量, 则不存在非线程安全问题, 所得结果也就是线程安全的了.

两个线程同时操作业务对象中的同一个实例变量, 则有可能会出现非线程安全问题. 例如丢失修改, 线程a将变量修改后, 线程b又修改了该变量, 导致后续线程a再次读取时获取的是线程b操作后的值.

解决方法是为修改实例变量的方法或代码块添加 synchronized 关键字.

### 2. synchronized 的锁是对象锁
当多个线程欲访问 synchronized 修饰的方法时, 它们需要首先争抢锁, 先抢到的先执行, 没抢到的暂时等待. 这里的锁就是**对象锁**, 一个对象就是一个锁, 如果是不同的对象, 那么锁也不一样, 就不存在争抢的问题, 或者说各线程操作的对象都不一样, 不是共享的, 自然不会有线程不安全问题.

而对于对象中没有 synchronized 修饰的方法, 因为不需要抢对象锁, 所以所有的线程都可以进入执行.

### 3. synchronized 的锁是可重入的
关键字 synchronized 拥有锁重入的功能，也就是在使用synchronized 时，当一个线程得到一个对象锁后，再次请求此对象锁时是可以再次得到该对象的锁的。也即在一个synchronized方法/块的内部调用本类的其他 synchronized 方法/块时，是永远可以得到锁的。




## 代码附录
### 1. synchronized 修饰方法
#### lost write
对于线程A, 它丢失了修改
```java
public class SharedUpdate {
    private int n;
    public void updateLongTime(int n) throws InterruptedException {
        String t_n = Thread.currentThread().getName();
        System.out.println(t_n+" in updateLongTime() is going to update shared-var as "+n);
        this.n = n;
        Thread.sleep((int)(Math.random()*1200));    // 写入变量后随机等待较长的时间(在这段时间内, 可能有其他线程进行了更改)
        System.out.println(t_n+" finished updating, now shared-var is "+this.n);
    }

    public void updateShortTime(int n) throws InterruptedException {
        String t_n = Thread.currentThread().getName();
        System.out.println(t_n+" in updateShortTime() is going to update shared-var as "+n);
        this.n = n;
        System.out.println(t_n+" finished updating, now shared-var is "+this.n);
    }
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
/*
ThreadA200 in updateLongTime() is going to update shared-var as 200
ThreadB100 in updateShortTime() is going to update shared-var as 100
ThreadB100 finished updating, now shared-var is 100
ThreadA200 finished updating, now shared-var is 100
*/

```

