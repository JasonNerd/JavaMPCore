package cpaThread.cp03wn.e01basic;

public class E01Basic {
    public static void testWaiter() throws InterruptedException {
        WNUsage usage = new WNUsage();
        final int thread_num = 10;
        Thread[] threads = new Thread[thread_num];
        // 5个线程进入等待, 5个线程进行通知
        for(int i=0; i<thread_num/2; i++){
            threads[i] = new Thread(()->{
                try{
                    usage.wtMessage();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            });
            threads[i].setName("Thread(wait): "+i);
            threads[thread_num-i-1] = new Thread(usage::nfMessage);
            threads[thread_num-i-1].setName("Thread(notify): "+(thread_num-i-1));
        }
        // 依次启动它们
        for(Thread t: threads)
            t.start();
        // main 线程等待这些线程执行结束(真的可以等到吗)
        for(Thread t: threads)
            t.join();
        System.out.println("All thread exit normally.");
    }

    public static void testNotifyAll(){
        WNUsage usage = new WNUsage();
        final int thread_num = 5;
        Thread[] th_w = new Thread[thread_num];
        // 1. 创建5个 waiting 线程
        for(int i=0; i<thread_num; i++){
            th_w[i] = new Thread(()->{
                try{
                    usage.wtMessage();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            });
            th_w[i].setName("Waiter "+i);
        }
        // 2. waiting 线程先启动
        for(int i=0; i<thread_num; i++)
            th_w[i].start();
        // 3. 等待一些时日
        try{
            Thread.sleep(300);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        // 4. 发出通知
        Thread n = new Thread(usage::nfMessageAll);
        n.setName("Notifier");
        n.start();
    }
    public static void main(String[] args) throws InterruptedException {
//        testWaiter();
        testNotifyAll();
    }
}
