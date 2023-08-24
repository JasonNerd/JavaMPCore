package cpaThread.cp02syn.e03blk;

public class E03SynBLK {
    private final static int t_num = 3;       // 3 个线程
    // 测试多个线程执行一个耗时任务后的总耗时
    public static void main(String[] args) throws InterruptedException {
        SynBlock synBlock = new SynBlock();
        Thread[] threads = new Thread[t_num];
        for(int i=0; i<t_num; i++){
            threads[i] = new Thread(() -> {
                try {
                    synBlock.operationA();      // 更改 operation 以查看输出差异
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
