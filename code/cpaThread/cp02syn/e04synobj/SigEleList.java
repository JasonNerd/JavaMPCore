package cpaThread.cp02syn.e04synobj;

import java.util.ArrayList;
import java.util.List;

/** 仅包含一个元素的数组 */
public class SigEleList <T> {
    private List<T> list = new ArrayList<T>();
    public synchronized void add(T t){
        list.add(t);
    }
    public synchronized int size(){
        return list.size();
    }
}
