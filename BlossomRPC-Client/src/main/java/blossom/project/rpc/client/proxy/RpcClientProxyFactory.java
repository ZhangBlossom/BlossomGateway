package blossom.project.rpc.client.proxy;

import java.lang.reflect.Proxy;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 22:17
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * RpcClientProxy类
 * 客户端代理类 用于获取客户端代理类
 */
public class RpcClientProxyFactory {
    /**
     * 当前方法用于返回需要调用的接口的客户端代理对象
     * @param interfaceName
     * @param host
     * @param port
     * @return
     * @param <T>
     */
    public <T> T getClientProxy(final Class<T> interfaceName, final String host, int port) {
        return (T) Proxy.newProxyInstance(interfaceName.getClassLoader(),
                new Class<?>[]{interfaceName},
                new JdkRpcProxyInvocationHandler(host, port));
    }
}
