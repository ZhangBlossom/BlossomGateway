package blossom.project.rpc.core.handler;


import blossom.project.rpc.core.entity.RpcDto;
import blossom.project.rpc.core.entity.RpcHeader;
import blossom.project.rpc.core.entity.RpcRequest;
import blossom.project.rpc.core.entity.RpcResponse;
import blossom.project.rpc.core.enums.ReqTypeEnum;
import blossom.project.rpc.core.proxy.spring.SpringBeanManager;
import blossom.project.rpc.core.proxy.spring.SpringRpcProxy;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 19:43
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * NettyRpcServerHandler类
 * 1:服务端接收到请求数据之后，需要进行解析
 * 2:解析后确定具体要调用的请求服务是哪一个
 * 2.1：这里应该要用到动态代理了
 * 2.2：分析使用那种动态代理 JDK/CGLIB/SpringIoC
 * 2.3：分析这三种方法的代码实现
 * 1：对于JDK直接用正常的反射
 * 2：对于CGLIB那么就是走CGLIB的常规写法
 * 3：对于Spring就要考虑把这些类存到容器中，
 * 然后要使用的时候从容器中进行获取
 */
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<RpcDto<RpcRequest>> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcDto<RpcRequest> msg) throws Exception {
        RpcHeader header = msg.getHeader();
        //当前是响应数据
        header.setReqType(ReqTypeEnum.RESPONSE.getCode());
        //使用反射的方式在运行时调用对应的类的方法
        //这里你可以思考一下用什么方式可以最快的找到我想要的类并且调用方法
        //目前我提供了：JDK CGLIB SpringIOC容器 HashMap自制工厂
        Object data = SpringRpcProxy.invoke(msg.getData());
        //使用JDK动态代理
        //Object data = RpcInvocationHandler.invoke(msg.getData());
        //使用CGLIB动态代理
        //Object data = RpcCglibProxy.invoke(msg.getData());
        RpcDto<RpcResponse> dto = new RpcDto();
        RpcResponse response = new RpcResponse();
        response.setData(data);
        response.setMsg("success!!!");
        dto.setData(response);
        dto.setHeader(header);
        //写出数据
        ctx.writeAndFlush(dto);
    }

}
