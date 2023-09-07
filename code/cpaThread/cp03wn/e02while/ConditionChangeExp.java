package cpaThread.cp03wn.e02while;

public class ConditionChangeExp {
    public static void main(String[] args) {
        MList l = new MList();
        Thread ad = new Thread(){
            @Override
            public void run() {
                while (true){
                    int t = (int) (Math.random()*300);
                    l.add(t+"");
                    try {
                        Thread.sleep(t);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        Thread rm = new Thread(){
            @Override
            public void run() {
                while (true){
                    int t = (int) (Math.random()*10);
                    try {
                        l.removeA();
                        Thread.sleep(t);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        ad.setName("Adder");
        rm.setName("Remover");
        ad.start();
        rm.start();
    }
}
