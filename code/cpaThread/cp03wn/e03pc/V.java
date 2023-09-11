package cpaThread.cp03wn.e03pc;

import java.util.ArrayList;
import java.util.List;

public class V {
    private final List<Object> sharedBins = new ArrayList<>();
    private final int maxSize;

    public V(int sz){
        maxSize = sz;
    }

    public void add(Object e){
        sharedBins.add(e);
    }

    public Object pop(){
        return sharedBins.remove(0);
    }

    public boolean empty(){
        return sharedBins.size() == 0;
    }

    public boolean full(){
        return sharedBins.size() == maxSize;
    }
}
