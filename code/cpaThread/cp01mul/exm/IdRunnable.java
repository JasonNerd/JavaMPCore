package cpaThread.cp01mul.exm;

public class IdRunnable implements Runnable{
    private int id;
    public IdRunnable(int id){
        this.id = id;
    }
    @Override
    public void run() {
        System.out.println("✦✦✦✦IdRunnable "+id+" start.✦✦✦✦");
        for(int i=0; i<3; i++){
            System.out.println("IdRunnable "+id+" is running.");
            try {
                Thread.sleep((int) (Math.random()*500));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("✧✧✧✧IdThread "+id+" end.✧✧✧✧");
    }
}
