package cpaThread.cp03wn.e03pc;

public class P {
    private final V sharedBins;
    private int cnt=0;
    public P(V sharedBins){
        this.sharedBins = sharedBins;
    }
    /* 如果 sharedBins 满则等待, 否则向 sharedBins 添加一个值 */
    public void produce() throws InterruptedException {
        synchronized (sharedBins){
            while (sharedBins.full()) {
                System.out.println("\t[produce]: "+Thread.currentThread().getName()+" waiting ...");
                sharedBins.wait();
            }
//            // 生产总量达到26个就停止
//            if(cnt == 26)
//                return;
            sharedBins.notifyAll();     // 通知所有等待线程
            cnt++;
            sharedBins.add("val(" + cnt + ")");
            System.out.println("[produce]: " + Thread.currentThread().getName() + " produces " + cnt);
        }
    }
}
