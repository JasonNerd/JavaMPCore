package cpaThread.cp03wn.e03pc;

public class Test {
    /**
     * 使用 wait/notify 机制实现生产者-消费者模式
     *      P: 生产变量, 多个
     *      C: 消费变量, 多个
     *      V: 仅允许存放一个值
     */
    public static void test(P p, C c, int thread_num) {
        // 生产者
        Thread[] producers = new Thread[thread_num];
        for(int i=0; i<thread_num; i++) {
            producers[i] = new Thread(() -> {
                while (true) {
                    int t = (int) (Math.random() * 10);
                    try {
                        Thread.sleep(t);
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
                    int t = (int) (Math.random() * 1000);
                    try {
                        Thread.sleep(t);
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
