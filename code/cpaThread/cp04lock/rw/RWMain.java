package cpaThread.cp04lock.rw;

public class RWMain {
    public static void main(String[] args) {
        // 测试读写锁
        final int thread_num = 10;
        RWLock service = new RWLock();
        int service_num = 3;
        Thread[] threads = new Thread[thread_num];
        for(int i=0; i<thread_num; i++){
            if(i%service_num==0)threads[i]=new Thread(service::readAdd);     // 读服务1
            if(i%service_num==1)threads[i]=new Thread(service::readMul);     // 读服务2
            if(i%service_num==2)threads[i]=new Thread(service::addMul);      // 写服务
            threads[i].setName("Thread"+i);
        }
        for(int i=0; i<thread_num; i++){
            threads[i].start();
        }
    }
}
