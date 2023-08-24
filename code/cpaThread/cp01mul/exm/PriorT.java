package cpaThread.basic.exm;

import java.util.Random;

public class PriorT extends Thread{
    private final int id;
    private final int p;
    private final String c;
    public PriorT(int id){
        this.id = id;
        this.c = id % 2 == 0 ? "✦✦✦✦" : "✧✧✧✧";
        this.p = id % 2 == 0 ? 2 : 8;
        this.setPriority(this.p);
    }
    @Override
    public void run() {
//        System.out.println(c+"PriorT "+id+" start!["+p+"]"+c);
        for(int i=0; i<2e5; i++)
            new Random().nextDouble();
        System.out.println(c+"PriorT "+id+" finished!["+p+"]"+c);
    }

}
