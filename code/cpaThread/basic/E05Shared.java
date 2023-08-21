package cpaThread.basic;

import cpaThread.basic.exm.SharedT;

public class E05Shared {
    public static void main(String[] args) {
        // 多个线程对同一个变量进行操作
        int cnt = 5;
        Runnable r = new SharedT(cnt);
        // 创建多个线程并启动它们, 每个线程都对共享变量进行减1.
        for(int i=0; i<cnt; i++)
           new Thread(r).start();
    }
}
