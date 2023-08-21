package cpaThread.basic;

public class E06CurrentT extends Thread{
    public E06CurrentT(){
        System.out.println("Constructor---begin");
        System.out.println("Thread.currentThread().getName()="+ Thread.currentThread().getName ());
        System.out.println("Thread.currentThread().isAlive()="+ Thread.currentThread().isAlive());
        System.out.println("this.getName()=" + this.getName());
        System.out.println("this.isAlive()=" + this.isAlive());
        System.out.println("Constructor---end");
    }

    @Override
    public void run() {
        System.out.println("run---begin");
        System.out.println("Thread.currentThread().getName()="+ Thread.currentThread().getName ());
        System.out.println("Thread.currentThread().isAlive()="+ Thread.currentThread().isAlive());
        System.out.println("this.getName()=" + this.getName());
        System.out.println("this.isAlive()=" + this.isAlive());
        System.out.println("run---end");
    }

    public static void main(String[] args) {
        Thread t = new E06CurrentT();
        t.setName("red");
        t.start();
    }
}
