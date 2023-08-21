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

两个线程同时访问一个没有同步的方法, 如果两个线程同时操作业务对象中的实例变量, 则有可能会出现非线程安全问题. 例如丢失修改, 线程a将变量修改为 100, 线程b又将变量改为200, 这导致后续线程a再次读取该变量时无法得到100的值.




## 代码附录
### 1. synchronized 修饰方法
#### lost write


