package blossom.project.rpc.client;

import blossom.project.rpc.client.proxy.RpcClientProxyFactory;
import blossom.project.rpc.core.service.RpcService;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 15:48
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * ClientRpcBoostrap类
 * client端的启动类
 */
//@SpringBootApplication
public class ClientRpcBoostrap {
    public static void main(String[] args) {

        RpcClientProxyFactory proxy = new RpcClientProxyFactory();
        RpcService clientProxy = proxy.getClientProxy
                (RpcService.class, "127.0.0.1", 8080);
        System.out.println(clientProxy.testRpcRequest("hello"));


    }
}
