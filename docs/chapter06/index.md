---
title: ""第六章 - 多线程与单例模式""
date: 2023-09-15T15:29:15+08: 00
draft: false
tags: ["singleton"]
categories: ["concurrent", "Java"]
twemoji: true
lightgallery: true
---

常见的单例模式包括立即加载(饿汉模式)和延迟加载(懒汉模式), 在饿汉模式中, 单例的对象在类进行加载时就已经初始化, 这包括在声明该变量时就进行初始化, 或是在静态代码块或使用静态类中初始化(获取), 类应当还需要一个私有的构造方法和一个获取该示例的方法 `getInstance()`, 懒汉模式则在 getInstance 内初始化该对象, 流程是首先判断该对象是否为null, 如果为null则先初始化它.

显而易见, 在饿汉模式中, getInstance 方法只有一个返回操作, 因此在多线程的情景下不会出现线程安全问题, 但在懒汉模式中就必须使用到一定的同步策略了.

### 立即加载
直接初始化.
```java
class HungryMan{
    private HungryMan instance = new HungryMan();
    private HungryMan(){}
    public static SomeObj getInstance(){return instance;}
}
```
静态块初始化
```java
class HungryMan{
    private HungryMan instance;
    static{
        instance = new HungryMan();
    }
    private HungryMan(){}
    public static HungryMan getInstance(){return instance;}
}
```
静态内部类
```java
class HungryMan{
    private static InstanceHandler{
        private static HungryMan instance = new HungryMan();
    }
    private HungryMan(){}
    public static HungryMan getInstance(){return HungryMan.instance;}
}
```
此外由于enum枚举类会自动在类加载时进行构造方法的调用, 因此也是一种饿汉模式的实现方式.

### 懒汉模式
通常情况下的懒汉模式代码如下:
```java
class LazyMan{
    private LazyMan instance;
    private LazyMan(){}
    public static LazyMan getInstance(){
        try{
            if(instance == null){
                Thread.sleep(100);
                instance = new SomeObj();
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        return instance;
    }
}
```
但这在多线程模式下会出现问题, 一种方法是直接在 getInstance 方法前加上 syndronized 关键字修饰. 然而这这这种直接全部锁住的方式会带来很大的效率问题.

## DCL法则
它是指 `双重锁检查(double check locking)` 机制. 先看为什么会出现线程安全问题, 当多个线程都需要执行 getInstance 中的代码时, 由于目前没有哪个线程将单例进行初始化, 因此都会通过if的条件判断, 此时就出现了不同线程中获取的对象并非同一个的情况.

先不考虑多线程的情景, 此处设置if判断是为了判断实例是否为空, 为空的时候才进行创建, 同时创建对象时会产生一些时耗. 在多线程的情况下, 可能出现在对象实际初始化完成前就有多个线程进入了if内部的代码, 因此, 还需要在实际的初始化动作开始前再次检查实例是否为空.
```java
public class LazyMan{
    private volatile static LazyMan instance;
    private LazyMan(){}
    public static LazyMan getInstance(){
        try{
            if(instance == null){
                Thread.sleep(100);
                synchronized (String.class){
                    if(instance == null)
                        instance = new LazyMan();
                }
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        return instance;
    }
}
```

如下为测试代码和输出情况
```java
public class SigTest {
    public static void main(String[] args) {
        int tn = 3;
        for(int i=0; i<tn; i++){
            Thread t = new Thread(()->{
                System.out.println(Thread.currentThread().getName()+": "+LazyMan.getInstance().hashCode());
            });
            t.setName("Thread@"+i);
            t.start();
        }
    }
}
/*
Thread@1: 823599515
Thread@2: 823599515
Thread@0: 823599515
*/
```
### volatile 的必要性
`instance = new LazyMan();`
在内部是分为3个步骤的:
```cmd
(1) memory = allocate();    // 分配内存空间
(2) ctorInstance(memory);   // 初始化对象
(3) myObject = memory;      // 设置 instance 为刚分配的内存地址
```
可能重排为:
```cmd
(1) memory = allocate();    // 分配内存空间
(2) myObject = memory;      // 设置 instance 为刚分配的内存地址
(3) ctorInstance(memory);   // 初始化对象
```
此时可能出现: 虽然构造方法还没有执行, 但 myObject 对象已经有了地址, 不是null, 访问对象中的字段仍然是默认值(未初始化的)