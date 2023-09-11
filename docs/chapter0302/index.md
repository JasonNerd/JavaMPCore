---
title: "第三章 -- 线程通信(2)-生产者消费者"
date: 2023-09-11T14:12:59+08: 00
draft: false
tags: ["wait", "notify"]
categories: ["concurrent", "Java"]
twemoji: true
lightgallery: true
---

## 生产者消费者
编写了如下共享变量/消费服务类/生产服务类

共享变量实体类:
```java
public class V {
    private final List<Object> sharedBins = new ArrayList<>();
    private final int maxSize;

    public V(int sz){
        maxSize = sz;
    }

    public void add(Object e){
        sharedBins.add(e);
    }

    public Object pop(){
        return sharedBins.remove(0);
    }

    public boolean empty(){
        return sharedBins.size() == 0;
    }

    public boolean full(){
        return sharedBins.size() == maxSize;
    }
}
```
消费服务方法:
```java
public class C {
    private final V sharedBins;
    public C(V sharedBins){
        this.sharedBins = sharedBins;
    }
    /* 如果 sharedBins 空则等待, 否则从 sharedBins 消费一个值 */
    public void consume() throws InterruptedException {
        synchronized (sharedBins){
            while (sharedBins.empty()) {
                System.out.println("\t[consume]: "+Thread.currentThread().getName()+" waiting ...");
                sharedBins.wait();  // 立即放弃锁并等待
            }
            Object o = sharedBins.pop();
            sharedBins.notify();
            System.out.println("[consume]: "+Thread.currentThread().getName()+" consumes "+o);
        }
    }
}
```
生产服务方法:
```java
public class P {
    private final V sharedBins;
    private int cnt=0;
    public P(V sharedBins){
        this.sharedBins = sharedBins;
    }
    /* 如果 sharedBins 满则等待, 否则向 sharedBins 添加一个值 */
    public void produce() throws InterruptedException {
        synchronized (sharedBins){
            while (sharedBins.full()) {
                System.out.println("\t[produce]: "+Thread.currentThread().getName()+" waiting ...");
                sharedBins.wait();
            }
            sharedBins.notify();
            cnt++;
            sharedBins.add("val(" + cnt + ")");
            System.out.println("[produce]: " + Thread.currentThread().getName() + " produces " + cnt);
        }
    }
}
```

## 1. 单生产/单消费
```java
public class Test {
    /**
     * 使用 wait/notify 机制实现生产者-消费者模式
     *      P: 生产变量, 单个
     *      C: 消费变量, 单个
     *      V: 仅允许存放一个值
     */
    public static void testOnePOneC(){
        V v = new V(1);     // 仅允许放1个值
        P p = new P(v);         // 生产者
        C c = new C(v);         // 消费者
        // 创建一个生产者线程
        Thread producer = new Thread(){
            @Override
            public void run() {
                while (true){
                    int t = (int) (Math.random()*100);
                    try {
                        Thread.sleep(t);
                        p.produce();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        // 创建一个消费者线程
        Thread consumer = new Thread(){
            @Override
            public void run() {
                while (true){
                    int t = (int) (Math.random()*10);
                    try {
                        Thread.sleep(t);
                        c.consume();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        producer.setName("Producer");
        consumer.setName("Consumer");
        producer.start();
        consumer.start();
    }
}
```
有序执行, 没有异常

## 2. 多生产/多消费
相较于1, 仅仅把P, V的线程数量均增加为2:
```java
public class Test {
    public static void test() {
        V v = new V(1);     // 仅允许放1个值
        P p = new P(v);
        C c = new C(v);
        int thread_num = 2;
        // 多个生产者
        Thread[] producers = new Thread[thread_num];
        for(int i=0; i<thread_num; i++) {
            producers[i] = new Thread(() -> {
                while (true) {
                    try {
                        p.produce();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        // 多个消费者
        Thread[] consumers = new Thread[thread_num];
        for(int i=0; i<thread_num; i++) {
            consumers[i] = new Thread(() -> {
                while (true) {
                    try {
                        c.consume();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        // 设置名称, 启动它们
        for(int i=0; i<thread_num; i++){
            producers[i].setName("Producer@"+i);
            consumers[i].setName("Consumer@"+i);
            producers[i].start();
            consumers[i].start();
        }
    }
}
```

很快, 异常出现了
```cmd
[produce]: Producer@0 produces 1
	[produce]: Producer@0 waiting ...
	[produce]: Producer@1 waiting ...
[consume]: Consumer@1 consumes val(1)
	[consume]: Consumer@1 waiting ...
	[consume]: Consumer@0 waiting ...
[produce]: Producer@0 produces 2
	[produce]: Producer@0 waiting ...
	[produce]: Producer@1 waiting ...

```
过程如下:
P0生产1, 唤醒(Null), P0等待, P1等待
C1消费1, 唤醒(P0), C1等待, C0等待
P0生产2, 唤醒(P1), P0等待, P1等待

此时所有的线程均进入等待状态, 这是由于生产者P0唤醒的是同类P1, 在填入一个值后, 它们均为因等待条件成立而进入等待状态.

如果把线程数增大为3, 仍可能出现这样的情况, 但概率会降低

## 3. 多生产/多消费(2)
此时若将 notify() 方法改为 notifyAll(), 那么就不会进入全都等待的情况, 它们有序执行.

## 4. 将共享变量修改为多值
此时就是正常的生产者消费者模式, 有多个生产者、多个消费者, 产品槽有最大容量.
细节包括:
* 对同一个变量进行操作需要加锁, 依据对象锁的概念, 恰好可以使用各线程共享的变量(也即产品槽)
* 无论生产者还是消费者, 在完成自己的业务后都将通知在等待的所有线程
* 每一个线程在进入等待后恢复执行时需要再次判断条件(while 的必要性)
