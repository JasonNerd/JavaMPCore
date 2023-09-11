package cpaThread.cp03wn.e03pc.m2m1;

import cpaThread.cp03wn.e03pc.C;
import cpaThread.cp03wn.e03pc.P;
import cpaThread.cp03wn.e03pc.Test;
import cpaThread.cp03wn.e03pc.V;

public class TestM2M1 extends Test {
    /**
     * 使用 wait/notify 机制实现生产者-消费者模式
     *      P: 生产变量, 多个
     *      C: 消费变量, 多个
     *      V: 仅允许存放一个值
     */
    public static void test() {
        V v = new V(1);     // 仅允许放1个值
        P p = new P(v);
        C c = new C(v);
        int thread_num = 2;
        test(p, c, thread_num);
    }
}
