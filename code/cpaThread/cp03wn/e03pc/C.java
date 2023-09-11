package cpaThread.cp03wn.e03pc;

public class C {
    private final V sharedBins;
    public C(V sharedBins){
        this.sharedBins = sharedBins;
    }
    /* 如果 sharedBins 空则等待, 否则从 sharedBins 消费一个值 */
    public void consume() throws InterruptedException {
        synchronized (sharedBins){
            while (sharedBins.empty()) {
                System.out.println("\t[consume]: "+Thread.currentThread().getName()+" waiting ...");
                sharedBins.wait();  // 立即放弃锁并等待
            }
            Object o = sharedBins.pop();
            sharedBins.notifyAll();    // 通知所有等待线程
            System.out.println("[consume]: "+Thread.currentThread().getName()+" consumes "+o);
        }
    }
}
