package cpaThread.cp02syn.e04synobj;

/**
 * 注意 add() 和 size() 都是 synchronized
 * 那么 addWithoutSyn 也会是安全的吗 ?
 */
public class SigAddService {
    public void addWithoutSyn(SigEleList<Integer> list, int t) throws InterruptedException {
        if(list.size() < 1){
            System.out.println(Thread.currentThread().getName()+" begin add.");
            Thread.sleep(1);      // 模拟取回数据耗时: 1ms
            list.add(t);
            System.out.println(Thread.currentThread().getName()+" end add.");
            return;
        }
        System.out.println(Thread.currentThread().getName()+" has nothing to do and quit.");
    }

    public void addWithSyn(SigEleList<Integer> list, int t) throws InterruptedException {
        synchronized (list){
            if (list.size() < 1) {
                System.out.println(Thread.currentThread().getName() + " begin add.");
                Thread.sleep(1);      // 模拟取回数据耗时: 1ms
                list.add(t);
                System.out.println(Thread.currentThread().getName() + " end add.");
            }
        }
        System.out.println(Thread.currentThread().getName()+" has nothing to do and quit.");
    }
}
