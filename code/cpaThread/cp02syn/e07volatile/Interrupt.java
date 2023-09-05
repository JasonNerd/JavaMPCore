package cpaThread.cp02syn.e07volatile;

/**
 * 线程 1 检查某一布尔变量(初始为True), 若为真则持续执行, 否则退出循环
 * 线程 2 试图改变这一布尔变量为 false, 线程 1 会停下来吗
 */
public class Interrupt {
    private boolean run = true;

    public void setRun(boolean run) {
        try {
            Thread.sleep(800);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        this.run = run;
    }

    public void prt(){
        try{
            while (run) {
                System.out.println("Thread " + Thread.currentThread().getName() + " run in prt().");
                Thread.sleep(500);
            }
            System.out.println("Thread " + Thread.currentThread().getName() + " get out");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
