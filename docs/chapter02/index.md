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

 注意这在继承时仍然适用, 例如一个子类继承了父类方法, 此时子类的一个 synchronized 方法调用父类的一个 synchronized 方法时也可以获得锁.

 另外, 假如子类重写了父类的一个 synchronized 方法, 但重写后的方法未使用 synchronized 关键字修饰, 则这个方法仍然是非同步的.

### 4. 抛出异常则锁自动释放

### 5. synchronized 同步语句块 
有时候不需要对整个方法体的代码都使用 synchronized, 而是只对一个语句块加 synchronized 关键字修饰, 然而, 值得注意的是, 方法前的 synchronized 关键字默认是使用当前对象锁, 这不需要显示说明, 而对于方法内的某一个语句块, 则需要显示说明, 格式如下:
```java
public void fun(){
    // some code
    synchronized(this){
        // some code need synchronize
    }
    // some other code
}
```

### 6. 锁互斥
假设一个对象有两个 synchronized 方法, 一个线程已持有该对象锁并正在运行其中一个 synchronized 方法, 那么此时所有 synchronized 方法都被锁住了, 其他线程无法访问. 同理, `synchronized(this)` 代码块也具备这样的特点, 当一个线程持有了该对象锁后, 该对象中所有的`synchronized(this)`代码块就仅允许该线程访问, 其他线程无法访问.

### 7. 任意对象作为锁
可以将当前对象作为锁, 则合理的, 可以将任意对象作为锁, 也即一个对象一把锁.
结论:
1. 当多个线程同时执行 synchronized(x){} 同步代码块时呈现同步效果.
2. 当其他线程执行x对象中 synchronized 方法时呈现同步效果
3. 当其他线程执行 x 对象中 synchronized(this) 代码块时呈现同步效果

### 8. 静态 synchronized 方法/代码块
每一个 .java 文件对应的 Class 类的实例都是一个, 也就是在内存中是单例的. Class 类用于描述类的基本信息, 包括有多少个字段, 有多少个构造方法, 有多少个普通方法等等, 它在内存中仅保存一份.

关键字 synchronized 可用于修饰 static 方法, 此时线程进入方法就需要持有该类的 Class 对象锁.

值得注意的是, 静态Class锁与对象锁不一样, 他们不是同一个锁, 另一方面, 静态Class锁对所有的实例对象都起作用.

### 9. String 常量池带来的同步问题
任何对象都可以作为锁, 同理 String 常量也是, 然而假如一个方法使用String 常量作为锁, 可能导致一些误会, 例如:
```java
a = "ABC"
b = "ABC"
synchronized("ABC"){
    // some code
}
synchronized(a){
    // some code
}
synchronized(b){
    // some code
}
```
它们实际持有同一把锁, 因此一般不使用String 常量作为锁.

### 10. 无限等待与线程死锁
```java
/**
 * 死锁: 资源共享, 互斥访问, 循环等待, 占有申请
 * 演示: 两个线程 a 和 b, 都需要访问变量 v, 其中访问操作 opa 需要 lockA 锁
 * 访问操作 opb 需要 lockB 锁, 其中 a 持有 lockA 锁并且申请 lockB 锁, 线程 b 持有
 * lockB 锁且申请 lockA 锁.
 */
```
可以使用 `jps` 查看java进程:
```cmd
20928 
35380 Launcher
36516 Jps
24092 E06Dead
```
再使用 `jstack -l 24092` 命令查看线程运行状况:
```cmd
......
......
Found one Java-level deadlock:
=============================
"a":
  waiting to lock monitor 0x000001d6d3863540 (object 0x000000071b054bd8, a java.lang.Object),
        at cpaThread.cp02syn.e06dead.DataSource.opa(DataSource.java:11)
        - waiting to lock <0x000000071b054bc8> (a java.lang.Object)
        at cpaThread.cp02syn.e06dead.DataSource.opb(DataSource.java:26)
        - locked <0x000000071b054bd8> (a java.lang.Object)
        at cpaThread.cp02syn.e06dead.E06Dead.lambda$main$1(E06Dead.java:23)
        at cpaThread.cp02syn.e06dead.E06Dead$$Lambda$16/0x0000000800c02000.run(Unknown Source)
        at java.lang.Thread.run(java.base@17.0.1/Thread.java:833)

Found 1 deadlock.
```
### 11. 锁对象不可变
`synchronized(object)` 中的 object 应当是不可变的, 锁对象的改变将导致线程异步执行, 注意这里的对象改变是指锁对象指向了一个新的引用, 其内部字段的修改并不能称之为对象变化.

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

#### Reentrant
synchronized 的锁是可重入的, 将上一例稍作修改, 使出现 synchronized 方法调用 synchronized 方法的情况.

```java
public class ReentrantBlk {
    private int n;
    public synchronized void setA(int n) throws InterruptedException {
        System.out.println(Thread.currentThread().getName()+" in updateLongTime() is going to update shared-var as "+n);
        this.n = n;
        Thread.sleep((int)(Math.random()*1200));    // 写入变量后随机等待较长的时间(在这段时间内, 可能有其他线程进行了更改)
        get();      // get() 也由 synchronized 修饰, 执行其中的代码需要获取对象锁
    }

    public synchronized void setB(int n) throws InterruptedException {
        System.out.println(Thread.currentThread().getName()+
                " in updateShortTime() is going to update shared-var as "+n);
        this.n = n;
        get();
    }

    public synchronized void get(){
        System.out.println(Thread.currentThread().getName()+" finished updating, now shared-var is "+this.n);
    }

    // 如果两个线程同时操作业务对象中的实例变量, 则有可能会出现非线程安全问题
    public static void main(String[] args){
        ReentrantBlk shareVar = new ReentrantBlk();
        // 线程 a 使用方法 A 更新变量为 200
        Thread a = new Thread(() -> {
            try {
                shareVar.setA(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        a.setName("ThreadA200");
        // 线程 b 使用方法 B 更新变量为 100
        Thread b = new Thread(() -> {
            try {
                shareVar.setB(100);
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
```

### 6. synchronized 同步语句块
相较于原同步方法, 同步语句块仅仅在操作共享变量时才会进行同步操作, `Thread.join()`方法是等待子线程执行结束再继续执行. 注意方法内部变量是不共享的, 各个线程互不干扰, 因此可以更改代码结构(同时维持原业务逻辑)使得两个实例变量在一个 synchronized 语句块中进行更新. 总体来说, 该程序比较了两种情况下的执行耗时.

```java
public class SynBlock {
    // 编写两个方法, 它们做的事情类似, 一个是同步方法
    private long t_start=Long.MAX_VALUE, t_end=Long.MIN_VALUE;  // 统计所有的线程完成的起始时间和终止时间

    public synchronized void operationA() throws InterruptedException {
        System.out.println("task begin @Thread="+Thread.currentThread().getName());
        long t1 = System.currentTimeMillis();   // 某一线程获得该锁后任务起始时间
        if (t1 < t_start)   // 不会有其他线程修改 t_start 值
            t_start = t1;
        Thread.sleep(1000);
        System.out.println("task end @Thread="+Thread.currentThread().getName());
        long t2 = System.currentTimeMillis();
        if (t2 > t_end)
            t_end = t2;
        System.out.println("Task @Thread="+Thread.currentThread().getName()+" use time "+(t2-t1)+" ms");
    }

    // 另一个基于同步方法改为使用同步代码块, 使得耗时操作异步执行, 而对于共享变量的操作则使用 syn 块
    public void operationB() throws InterruptedException {
        System.out.println("task begin @Thread="+Thread.currentThread().getName());
        long t1 = System.currentTimeMillis();   // 某一线程获得该锁后任务起始时间(本地方法变量, 不共享)
        Thread.sleep(1000);
        System.out.println("task end @Thread="+Thread.currentThread().getName());
        long t2 = System.currentTimeMillis();
        synchronized (this) {
            if (t1 < t_start)
                t_start = t1;
            if (t2 > t_end)
                t_end = t2;
        }
        System.out.println("Task @Thread="+Thread.currentThread().getName()+" use time "+(t2-t1)+" ms");
    }

    // 一个同步方法, 打印所有线程执行的总耗时, 涉及共享变量, 选择使用同步方法
    public synchronized void printTotalTime(){
        System.out.println("Totally task finished with "+(t_end-t_start)+" ms.");
    }

}

// 测试类
public class E02SynBLK {
    private final static int t_num = 3;       // 3 个线程
    // 测试多个线程执行一个耗时任务后的总耗时
    public static void main(String[] args) throws InterruptedException {
        SynBlock synBlock = new SynBlock();
        Thread[] threads = new Thread[t_num];
        for(int i=0; i<t_num; i++){
            threads[i] = new Thread(() -> {
                try {
                    synBlock.operationB();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            threads[i].start();
        }
        // 等待所有子线程执行完毕
        for(int i=0; i<t_num; i++) {
            threads[i].join();
        }
        synBlock.printTotalTime();  // 打印执行时间
    }
}

/*
// operationB 
task begin @Thread=Thread-1
task begin @Thread=Thread-0
task begin @Thread=Thread-2
task end @Thread=Thread-1
task end @Thread=Thread-0
Task @Thread=Thread-0 use time 1001 ms
Task @Thread=Thread-1 use time 1001 ms
task end @Thread=Thread-2
Task @Thread=Thread-2 use time 1003 ms
Totally task finished with 1003 ms.

// operationA
task begin @Thread=Thread-0
task end @Thread=Thread-0
Task @Thread=Thread-0 use time 1000 ms
task begin @Thread=Thread-2
task end @Thread=Thread-2
Task @Thread=Thread-2 use time 1012 ms
task begin @Thread=Thread-1
task end @Thread=Thread-1
Task @Thread=Thread-1 use time 1010 ms
Totally task finished with 3022 ms.
*/
```


### 7. 任意对象作为锁
如下为书中的一个例子(有改动), 他首先定义一个单元素列表list, 包含 add() 和 size() 方法, 这两个方法都是同步的, 然后定义一个服务类, 其中包含访问数据的方法 addService, 线程只可以通过服务方法来访问list. 值得注意的是, 单元素特性是在 addService 方法中体现的, 他首先调用 size() 方法获取 size, 若小于1则允许添加(执行add()) 否则拒绝添加.

如下为上述行为的代码实现

```java
/** 仅包含一个元素的数组 */
public class SigEleList <T> {
    private List<T> list = new ArrayList<T>();
    public synchronized void add(T t){
        list.add(t);
    }
    public synchronized int size(){
        return list.size();
    }
}
/**
 * 注意 add() 和 size() 已经是 synchronized
 * 那么 addWithoutSyn 会是安全的吗 ?
 * addWithSyn 做出的改动有什么含义 ?
 */
public class SigAddService {
    public void addServiceA(SigEleList<Integer> list, int t) throws InterruptedException {
        if(list.size() < 1){
            System.out.println(Thread.currentThread().getName()+" begin add.");
            Thread.sleep(1);      // 模拟取回数据耗时: 1ms
            list.add(t);
            System.out.println(Thread.currentThread().getName()+" end add.");
            return;
        }
        System.out.println(Thread.currentThread().getName()+" has nothing to do and quit.");
    }
}
/**
 * 测试方法
 */
public class E04SynOBJ {
    static final int t_num = 100;

    public static void main(String[] args) throws InterruptedException {
        SigEleList<Integer> eleList = new SigEleList<>();
        SigAddService service = new SigAddService();
        Thread[] threads = new Thread[t_num];
        for(int i=0; i<t_num; i++){
            threads[i] = new Thread(() -> {
                try {
                    service.addServiceA(eleList, 12); // 修改此处以切换方法
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            threads[i].setName("Thread00"+i);
            threads[i].start();
        }
        // 等待所有子线程执行完毕
        for(int i=0; i<t_num; i++) {
            threads[i].join();
        }
        System.out.println("Finally the size of list is "+eleList.size());
    }
}
```

实际上, 在使用多个线程并发地对 eleList 进行 operationA 操作时, eleList 的 size 无法保证为1, 另一方面, 取回数据耗时越短, 最终 eleList 的 size 往往更小.

分析其原因, 在于尽管 add 和 size 都是 synchronized 方法, 在进行 size 判断时不会出现其他线程修改 eleList 的情况, 但在一开始, eleList 的 size 是 0, 因此总会有一部分线程通过if判断并进入模拟取数据耗时阶段, 这个阶段耗时越长, 就会有越多的线程通过if判断. 通过if判断的线程都能执行后续的add方法, 只要这些通过判断的线程中有一个实际完成了add操作, 则此时条件判断不成立, 则后续线程将无法通过判断也就不会执行add.

在测试时, 假定有100个线程, 取数据耗时1ms. 一次输出结果是(仅最后的结果):
```java
Finally the size of list is 43
```

假如将原方法改为:
```java
public void addWithSyn(SigEleList<Integer> list, int t) throws InterruptedException {
    synchronized (list){
        if (list.size() < 1) {
            System.out.println(Thread.currentThread().getName() + " begin add.");
            Thread.sleep(1);      // 模拟取回数据耗时: 1ms
            list.add(t);
            System.out.println(Thread.currentThread().getName() + " end add.");
        }
    }
    System.out.println(Thread.currentThread().getName()+" has nothing to do and quit.");
}

```
则可以保证size始终为1

### 8. 任意对象作为锁
对象类:
```java
public class MethodStatic {
    synchronized static void ssvA() {
        System.out.println(Thread.currentThread().getName() + " enter A");
        try{
            Thread.sleep(200);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        System.out.println(Thread.currentThread().getName() + " quit A");
    }

    synchronized static void ssvB() {
        System.out.println(Thread.currentThread().getName() + " enter B");
        try{
            Thread.sleep(200);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        System.out.println(Thread.currentThread().getName() + " quit B");
    }

    synchronized void svC() {
        System.out.println(Thread.currentThread().getName() + " enter C");
        try{
            Thread.sleep(200);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        System.out.println(Thread.currentThread().getName() + " quit C");
    }
}
```
测试类
```java
public class E05Static {
    public static void main(String[] args) {
        Thread[] threads = new Thread[3];
        MethodStatic a = new MethodStatic();
        MethodStatic b = new MethodStatic();
        threads[0] = new Thread(){
            @Override
            public void run() {
                a.ssvA();       // 等价于 MethodStatic.ssvA
            }
        };
        threads[1] = new Thread(){
            @Override
            public void run() {
                b.ssvB();       // 等价于 MethodStatic.ssvB
            }
        };
        threads[2] = new Thread(){
            @Override
            public void run() {
                b.svC();
            }
        };
        // a. 静态Class锁与对象锁不一样: 假如 1 和 2 是异步的
        // b. 静态Class锁对所有的实例对象都起作用: 假如 0 和 1同步
        for(Thread t: threads)
            t.start();
        // 由于线程较少, 因此可能运行多次才可观察到现象(主要是a)
    }
    /**
     * Thread-1 enter B
     * Thread-2 enter C
     * Thread-1 quit B
     * Thread-2 quit C
     * Thread-0 enter A
     * Thread-0 quit A
     */
}
```

### 10. 无限等待与线程死锁
线程类
```java
public class DataSource {
    private int var = 0;
    final Object lockA = new Object();
    final Object lockB = new Object();

    public void opa() throws InterruptedException {
        Thread.sleep((int)(Math.random()*100));
        synchronized (lockA){
            System.out.println(Thread.currentThread().getName()+" enter lockA.");
            Thread.sleep((int)(Math.random()*200));
            var = 1;
            System.out.println(Thread.currentThread().getName()+" try lockB.");
            opb();
        }
    }

    public void opb() throws InterruptedException {
        Thread.sleep((int)(Math.random()*120));
        synchronized (lockB){
            System.out.println(Thread.currentThread().getName()+" enter lockB.");
            Thread.sleep((int)(Math.random()*180));
            var = -1;
            System.out.println(Thread.currentThread().getName()+" try lockA.");
            opa();
        }
    }
}
```
测试类
```java
public class E06Dead {
    public static void main(String[] args) throws InterruptedException {
        DataSource source = new DataSource();
        Thread a = new Thread(()->{
            try {
                source.opa();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        });
        Thread b = new Thread(()->{
            try {
                source.opb();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        });
        a.setName("a");
        b.setName("b");
        a.start();
        b.start();
        a.join();
        b.join();
        System.out.println();
    }
}
```

