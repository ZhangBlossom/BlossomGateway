package blossom.project.rpc.core.proxy.jdk;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 20:48
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * JdkRpcServiceImpl类
 */
public class JdkRpcServiceImpl implements JdkRpcService{
    @Override
    public String testJdkRpc(String msg) {
        System.out.println("the msg is : "+msg);
        return "success！！！ \n looks like it could work！！！";
    }
}
