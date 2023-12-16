package blossom.project.rpc.core.proxy.jdk;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 20:47
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * JdkRpcService接口
 */
public interface JdkRpcService {
    /**
     * 当前方法用于测试JDK的代理然后实现rpc调用是否可行
     * @param msg
     * @return
     */
    String testJdkRpc(String msg);
}
