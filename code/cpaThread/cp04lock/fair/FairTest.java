package cpaThread.cp04lock.fair;

public class FairTest {
    public static void main(String[] args) throws InterruptedException {
        IfFairService service = new IfFairService(false);    // 调整锁是否公平锁
        final int thread_num = 1000;
        Thread[] fts = new Thread[thread_num];
        for (int i=0; i<thread_num; i++){
            fts[i] = new Thread(service::service);
            fts[i].setName("First@"+i);
        }
        Thread[] sts = new Thread[thread_num];
        for (int i=0; i<thread_num; i++) {
            sts[i] = new Thread(service::service);
            sts[i].setName("Second@" + i);
        }
        // 先启动 fts
        for (int i=0; i<thread_num; i++)
            fts[i].start();
        // 再启动 sts
        for (int i=0; i<thread_num; i++)
            sts[i].start();
    }
}
