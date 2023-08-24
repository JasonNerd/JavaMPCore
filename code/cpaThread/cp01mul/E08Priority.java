package cpaThread.cp01mul;

import cpaThread.cp01mul.exm.PriorT;

public class E08Priority extends Thread{

    public static void main(String[] args) {
        // 创建多个 PriorT 线程 并启动它们
        int n = 20;
        for(int i=0; i<n; i++)
            new PriorT(i).start();
    }
}
