package cpaThread.cp04lock.cond;

/**
 * 有条件的唤醒线程
 */
public class ConditionalSignal {
    public static void main(String[] args) throws InterruptedException {
        Service service = new Service();
        // 创建4个线程, 2个将在ca处等待, 2个在cb处等待
        final int thread_num = 2;
        Thread[] threads = new Thread[thread_num*2];
        for(int i=0; i<thread_num; i++) {
            threads[2*i] = new Thread(service::apple);      // ca
            threads[2*i].setName("CA@"+(2*i));
            threads[2*i+1] = new Thread(service::juice);    // cb
            threads[2*i+1].setName("CB@"+(2*i+1));
        }
        // 启动这些线程
        for(int i=0; i<thread_num*2; i++)
            threads[i].start();
        // main线程先通知 ca
        Thread.sleep(500);
        service.walk();
        Thread.sleep(500);
        service.drink();
    }
}
