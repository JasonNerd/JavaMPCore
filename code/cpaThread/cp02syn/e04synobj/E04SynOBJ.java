package cpaThread.cp02syn.e04synobj;

public class E04SynOBJ {
    static final int t_num = 100;

    public static void main(String[] args) throws InterruptedException {
        SigEleList<Integer> eleList = new SigEleList<>();
        SigAddService service = new SigAddService();
        Thread[] threads = new Thread[t_num];
        for(int i=0; i<t_num; i++){
            threads[i] = new Thread(() -> {
                try {
                    service.addWithSyn(eleList, 12); // 修改此处以切换方法
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            threads[i].setName("Thread00"+i);
            threads[i].start();
        }
        // 等待所有子线程执行完毕
        for(int i=0; i<t_num; i++) {
            threads[i].join();
        }
        System.out.println("Finally the size of list is "+eleList.size());
    }
}
