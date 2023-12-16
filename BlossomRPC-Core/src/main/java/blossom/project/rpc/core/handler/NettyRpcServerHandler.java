package blossom.project.rpc.core.handler;


import blossom.project.rpc.core.entity.RpcDto;
import blossom.project.rpc.core.entity.RpcHeader;
import blossom.project.rpc.core.entity.RpcRequest;
import blossom.project.rpc.core.entity.RpcResponse;
import blossom.project.rpc.core.enums.ReqTypeEnum;
import blossom.project.rpc.core.proxy.SpringBeanManager;
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
 * 2.2：分析使用那种动态代理 JDK/CGLIB/SpringAOP
 */
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<RpcDto<RpcRequest>> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcDto<RpcRequest> msg) throws Exception {
        RpcHeader header = msg.getHeader();
        //当前是响应数据
        header.setReqType(ReqTypeEnum.RESPONSE.getCode());
        //使用反射的方式在运行时调用对应的类的方法
        //TODO 这里的思考一下用什么方式可以最快的得到我想要的类
        Object data = invoke(msg.getData());

        RpcDto<RpcResponse> dto = new RpcDto();
        RpcResponse response = new RpcResponse();
        response.setData(data);
        response.setMsg("success!!!");
        dto.setData(response);
        dto.setHeader(header);
        //写出数据
        ctx.writeAndFlush(dto);
    }


    /**
     * 反射方法调用
     * @param request
     * @return
     */
    private Object invoke(RpcRequest request) {
        try {
            Class<?> clazz = Class.forName(request.getClassName());
            Object bean = SpringBeanManager.getBean(clazz);
            Method method = clazz.getDeclaredMethod(request.getMethodName(), request.getParamsTypes());
            return method.invoke(bean, request.getParams());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
