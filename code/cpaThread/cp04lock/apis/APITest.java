package cpaThread.cp04lock.apis;

public class APITest {
    public static void main(String[] args) {
        final int thread_num = 3;
        APIService service = new APIService();
        // 初始化了几个线程, 各个线程执行哪一个方法不确定, 执行次数也不确定
        Thread[] threads = new Thread[thread_num];
        for(int i=0; i<thread_num; i++){
            int rd = (int)(Math.random()*10);   // 10以内的随机数
            boolean even = rd % 2 == 0;      // 是不是偶数
            if(even){
                threads[i] = new Thread(()->{
                    for(int j=0; j<rd; j++)service.c1p();
                });
                threads[i].setName("C1@"+i);
            }else {
                threads[i] = new Thread(()->{
                    for(int j=0; j<rd; j++)service.c2p();
                });
                threads[i].setName("C2@"+i);
            }
            threads[i].start();
        }
        // 再来个打印线程信息的线程
        Thread info = new Thread(()->{
            int i = 0;
            while (true) {
                service.info();
                service.ts_info(threads);
                ++i;
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if(i>=10)break;
            }
        });
        info.setName("InfoPrintThread");
        info.start();
    }
}
