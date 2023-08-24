package cpaThread.cp01mul;

import cpaThread.cp01mul.exm.IdThread;

public class E03Random {
    public static void main(String[] args) {
        // 1. 创建很多个线程
        int thread_num = 10;
        Thread[] threads = new Thread[thread_num];
        for(int i=0; i<thread_num; i++)
            threads[i] = new IdThread(i);
        // 2. 依次调用start()(但启动次序未知)
        for(int i=0; i<thread_num; i++)
            threads[i].start();
        // 3. 从结果看出各线程的启动、执行都具有随机性.
    }
}
