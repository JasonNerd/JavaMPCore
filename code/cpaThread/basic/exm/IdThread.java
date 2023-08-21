package cpaThread.basic.exm;

public class IdThread extends Thread{
    // 具有 id, 可以将自己的id输出
    private final int id;
    public IdThread(int id){
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("✦✦✦✦IdThread "+id+" start.✦✦✦✦");
        for(int i=0; i<10; i++){
            System.out.println("IdThread "+id+" is running.");
            try {
                Thread.sleep((int) (Math.random()*500));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("✧✧✧✧IdThread "+id+" end.✧✧✧✧");
    }
}
