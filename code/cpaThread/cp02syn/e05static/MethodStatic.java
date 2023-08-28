package cpaThread.cp02syn.e05static;

public class MethodStatic {
    synchronized static void ssvA() {
        System.out.println(Thread.currentThread().getName() + " enter A");
        try{
            Thread.sleep(200);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        System.out.println(Thread.currentThread().getName() + " quit A");
    }

    synchronized static void ssvB() {
        System.out.println(Thread.currentThread().getName() + " enter B");
        try{
            Thread.sleep(200);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        System.out.println(Thread.currentThread().getName() + " quit B");
    }

    synchronized void svC() {
        System.out.println(Thread.currentThread().getName() + " enter C");
        try{
            Thread.sleep(200);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        System.out.println(Thread.currentThread().getName() + " quit C");
    }
}
