package cpaThread.cp03wn.e04other.piped;

import java.io.IOException;
import java.io.PipedInputStream;

public class ReadService {
    private PipedInputStream reader;
    public ReadService(PipedInputStream in){
        reader = in;
    }
    public void read(){
        // 从 in 字节流中读取字节, 转为字符串并打印
        System.out.println("read begin");
        byte[] buffer = new byte[10];
        try {
            int l = reader.read(buffer);
            while (l > 0) {
                String data = new String(buffer, 0, l);
                System.out.println(l+": "+data);
                l = reader.read(buffer);
            }
            System.out.println("read end");
            reader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
