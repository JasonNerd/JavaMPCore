package cpaThread.cp06sig;

public class LazyMan{
    private volatile static LazyMan instance;
    private LazyMan(){}
    public static LazyMan getInstance(){
        try{
            if(instance == null){
                Thread.sleep(100);
                synchronized (String.class){
                    if(instance == null)
                        instance = new LazyMan();
                }
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        return instance;
    }
}