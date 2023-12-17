package blossom.project.rpc.client.proxy;

import blossom.project.rpc.core.constants.RpcCommonConstants;
import blossom.project.rpc.core.entity.*;
import blossom.project.rpc.core.enums.AlgorithmTypeEnum;
import blossom.project.rpc.core.enums.ReqTypeEnum;
import blossom.project.rpc.core.starter.NettyRpcClient;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 22:26
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * JdkRpcProxyInvocationHandler类
 * 这个类的作用就是对原本要调用的方法进行代理
 * 1：根据设定的参数找到服务端上的服务然后调用
 * 2: Netty是异步的，所以我调用之后得异步等待结果
 * 3: 异步等待结果的时候得考虑对ChannelFuture的处理（异步的）
 * 4：Client的Handler负责接收Server的返回值，可以考虑再ClientHandler里面
 * 做一下处理，比如设定某个值然后使得ChannelFuture的阻塞结束
 * 这里可以考虑用一下Promise/Future类型
 * 5: 这个类最后的返回结果就是我的代理类成功代理原方法之后
 * 使用RPC调用从server拿到的结果
 * 6： promise/future都是没有拿到结果阻塞(考虑一下这里能不能做文章）
 */
@Slf4j
public class JdkRpcProxyInvocationHandler implements InvocationHandler {

    //TODO 这里暂时先考虑硬编码 后续有时间可以考虑用注册中心
    private String host;
    private int port;

    public JdkRpcProxyInvocationHandler(String host, int port) {
        this.host = host;
        this.port = port;
    }


    /**
     *
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("client start to invoke the server' function!!!");

        RpcDto<RpcRequest> dto=new RpcDto<>();
        //构建请求头
        long reqId = RpcCache.getRequestId();
        RpcHeader header=new RpcHeader(RpcCommonConstants.VERSION_ID,
                AlgorithmTypeEnum.JSON.getCode(),
                ReqTypeEnum.REQUEST.getCode(),
                reqId,0);
        dto.setHeader(header);
        //设定请求内容
        RpcRequest request=new RpcRequest();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParamsTypes(method.getParameterTypes());
        request.setParams(args);
        dto.setData(request);
        //TODO 不应该每次都new一个Client，Client应该是复用的
        // 考虑用final把 time：2023/12/16 01：12
        NettyRpcClient nettyClient=new NettyRpcClient(host,port);
        //得到一个promise对象，先存起来，等doRequest拿到数据之后就会设定值进去
        //那么此时promise的get的阻塞就会结束
        DefaultPromise<RpcResponse> promise = new DefaultPromise(new DefaultEventLoop());
        RpcCache.RESPONSE_CACHE.put(reqId,promise);
        nettyClient.doRequest(dto);
        //TODO 方便debug time：2023/12/16 01：14
        Object data = promise.get().getData();
        return data;
    }
}
