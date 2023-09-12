package cpaThread.cp03wn.e04other.piped;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * 程序演示线程间通过管道进行通信
 */
public class Main {
    public static void main(String[] args) {
        try {
            PipedInputStream in = new PipedInputStream();
            PipedOutputStream out = new PipedOutputStream();
            in.connect(out);    // 连接两个管道
            WriteService w = new WriteService(out);
            ReadService  r = new ReadService(in);
            // 新建写管道线程
            Thread wt = new Thread(() -> w.write("ALittleHotToday"));
            // 新建读管道线程
            Thread rd = new Thread(r::read);
            // 启动两个线程
            wt.start();
            rd.start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
