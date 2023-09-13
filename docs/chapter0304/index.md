---
title: "第三章 -- 线程通信(4)-线程本地对象"
date: 2023-09-13T14:28:35+08: 00
draft: false
tags: ["ThreadLocal"]
categories: ["concurrent", "Java"]
twemoji: true
lightgallery: true
---


## 线程内部的变量共享
JDK中提供的工具类 `ThreadLocal` 解决了线程内部的变量共享的问题, 它使得每个线程都可以绑定自己的值, 不同线程之间变量不共享.
该类的主要方法是get()和set(), 其中set是存储变量值, get()则获取变量, 用法演示如下:
```java
// LocalUsage.java
public class LocalUsage {
    public static void main(String[] args) throws InterruptedException {
        ThreadLocal<Object> tlk1 = new ThreadLocal<>();
        Thread t1 = new Thread(()->{
            System.out.println("t1(1) get tlk1="+tlk1.get());
            tlk1.set("12345");
            System.out.println("t1(2) get tlk1="+tlk1.get());
        });
        Thread t2 = new Thread(()->{
            System.out.println("t2(1) get tlk1="+tlk1.get());
            tlk1.set("67890");
            System.out.println("t2(2) get tlk1="+tlk1.get());
        });
        t1.start();
        Thread.sleep(5);
        t2.start();
    }
}
```
输出如下:
```java
t1(1) get tlk1=null
t1(2) get tlk1=12345
t2(1) get tlk1=null
t2(2) get tlk1=67890
```
该程序初始化了一个 ThreadLocal 对象 tlk1, 2个线程 t1 t2. 这两个 Thread 都会先试图获取 tlk1 存储的值, 随后设置自己的值, 然后再取出它并打印. 从输出可以看出, 尽管 ThreadLocal 对象只有一个, 但两个线程的变量存取是相互独立的.

### 存取数据流分析
注意前面的描述 `ThreadLocal` 是一个工具类, 它提供的是线程内部共享变量的存取控制接口而不是实际存取的对象, 也就是变量并非是存在了 ThreadLocal 对象中, ThreadLocal 对象只是提供存取的接口(get/set).

实际上, 线程内部共享变量 var 是存放在当前线程的 `ThreadLocalMap` 中, `ThreadLocalMap` 可以存储多个键值对, 键即为 `ThreadLocal` 对象, 值就是 var.

在线程执行 `tlk1.get()` 方法时, 实际上 get 是首先获取 currentThread() 的 ThreadLocalMap, 接着以 `tlk1` 为键索引到了对应的存储值.


## InheritableThreadLocal
这一类可以实现父子类间的变量共享, 也即父类设置的值, 子类可以获取到. 需要注意的是, 对于基本类型以及常量字符串, 子类或者父类对其进行修改后, 另一方读取时将仍为旧值, 但如果是自定义类型, 其中的字段改变时, 则可以感知到.

1. ThreadLocal 本身不支持父子类共享
    修改 `LocalUsage.java`, 添加一行:
    ```java
    // LocalUsage.java --v2
    ThreadLocal<Object> tlk1 = new ThreadLocal<>();
    tlk1.set("main thread set val");
    ...
    ```
    输出仍为:
    ```
    t1(1) get tlk1=null
    t1(2) get tlk1=12345
    t2(1) get tlk1=null
    t2(2) get tlk1=67890
    ```
2. InheritableThreadLocal 支持父子类共享
    修改 `LocalUsage.java`:
    ```java
    // LocalUsage.java --v3
    InheritableThreadLocal<Object> tlk1 = new InheritableThreadLocal<>();
    tlk1.set("main thread set val");
    ...
    ```
    输出将发生改变:
    ```java
    t1(1) get tlk1=main thread set val
    t1(2) get tlk1=12345
    t2(1) get tlk1=main thread set val
    t2(2) get tlk1=67890
    ```
3. 对于基本类型及字符串类型, 子类若修改了值, 父类无法感知
    在`// LocalUsage.java --v3` 的基础上继续添加:
    ```java
    // LocalUsage.java --v4
    ...
    t2.start();
    // 等待两个子线程执行完毕, 再来查看存储的值
    t1.join();
    t2.join();
    System.out.println("main(2) get tlk1="+tlk1.get());
    ```
    输出发现还是原先main的值
    ```java
    t1(1) get tlk1=main thread set val
    t1(2) get tlk1=12345
    t2(1) get tlk1=main thread set val
    t2(2) get tlk1=67890
    main(2) get tlk1=main thread set val
    ```



