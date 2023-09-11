package cpaThread.cp03wn.e02while;

public class ConditionChangeExp {
    public static void main(String[] args) {
        MList l = new MList();
        final int th_num = 10;
        // 新建 th_num 个 添加线程
        for(int i=0; i<th_num; i++) {
            Thread adder = new Thread(() -> {
                while (true) {
                    int t = (int) (Math.random() * 300);
                    l.add(t + "");
                    try {
                        Thread.sleep(t);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            adder.setName("adder("+i+")");
            adder.start();
        }
        // 新建 th_num 个 删除
        for(int i=0; i<th_num; i++) {
            Thread remover = new Thread(() -> {
                while (true){
                    int t = (int) (Math.random()*10);
                    try {
                        l.removeA();
                        Thread.sleep(t);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            remover.setName("remover("+i+")");
            remover.start();
        }
    }
}
