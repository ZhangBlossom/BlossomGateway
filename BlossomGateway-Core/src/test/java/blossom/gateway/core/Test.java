package blossom.gateway.core;


import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 19:57
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * Test类
 */
public class Test {
    public static final AtomicInteger ai = new AtomicInteger(10);

    public static void main(String[] args) {
        //System.out.println(ai);
        //System.out.println(ai.incrementAndGet());
        //
        //String protocol = null;
        //Assert.isNull(protocol, "protocal can not be null!");
        // new GatewayContext.GatewayContextBuilder().setNettyContext(null)
        //         .setRule(new Rule())
        //        .build();

        Properties properties = System.getProperties();
        System.out.println(properties);
    }
}
