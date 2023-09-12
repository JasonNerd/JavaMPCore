package cpaThread.cp03wn.e04other.piped;

import java.io.IOException;
import java.io.PipedOutputStream;

public class WriteService {
    PipedOutputStream writer;
    public WriteService(PipedOutputStream out){
        writer = out;
    }
    public void write(String data){
        // 将字符串 data 写入到 out 管道, 此处演示时是单字符单字符的写入
        int n = data.length();
        System.out.println("Write begin");
        try {
            for (int i = 0; i < n; i++) {
                char c = data.charAt(i);
                writer.write(c);
            }
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Write end");
    }
}
