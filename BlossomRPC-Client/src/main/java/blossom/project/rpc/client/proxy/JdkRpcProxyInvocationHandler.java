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

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("client start to invoke the server' function!!!");

        RpcDto<RpcRequest> dto=new RpcDto<>();
        //构建请求头
        long reqId = RpcRequestCache.getRequestId();
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

        NettyRpcClient nettyClient=new NettyRpcClient(host,port);
        RpcPromise<RpcResponse> future=new RpcPromise<>(new DefaultPromise<RpcResponse>(new DefaultEventLoop()));
        RpcRequestCache.REQUEST_CACHE_MAP.put(reqId,future);
        nettyClient.doRequest(dto);
        return future.getPromise().get().getData();
        //return "success!!!";
    }
}
