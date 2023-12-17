package blossom.project.rpc.core.proxy.jdk;

import blossom.project.rpc.core.entity.RpcRequest;
import blossom.project.rpc.core.proxy.jdk.register.ServiceRegistry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 22:47
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * RpcInvocationHandler类
 * 当前类就是测试是否可以使用RpcRequest来进行运行时方法调用
 * 运行main方法后发现是可以的
 */
public class JdkRpcProxy implements InvocationHandler {

    private Object target;

    public JdkRpcProxy(Object target) {
        this.target = target;
    }
    //原生JDK动态代理的方法 应该用不太上
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 在调用实际方法前可以添加自定义逻辑，如日志、验证等
        System.out.println("Invoking method: " + method.getName());

        // 调用目标方法
        Object result = method.invoke(target, args);

        // 在调用实际方法后可以添加自定义逻辑
        return result;
    }

    /**
     * 当前方法可以解析RpcRequest对象
     * 并且找到对应的class的method然后进行执行得到返回值
     * @param request
     * @return
     * @throws Exception
     */
    public static Object invoke(RpcRequest request) throws Exception {
        // 通过接口名称找到对应的服务实现
        Object serviceInstance = ServiceRegistry.getService(request.getClassName());

        if (serviceInstance == null) {
            throw new ClassNotFoundException("Service implementation not found for: " + request.getClassName());
        }

        Class<?> clazz = serviceInstance.getClass();
        Method method = clazz.getMethod(request.getMethodName(), request.getParamsTypes());
        return method.invoke(serviceInstance, request.getParams());
    }


    //如下代码是使用自己写的invoke方法的代码
    //直接运行即可
    public static void main(String[] args) throws Exception {

        // 假设JdkRpcService是接口，JdkRpcServiceImpl是实现类
        ServiceRegistry.register
                (JdkRpcService.class.getName(), new JdkRpcServiceImpl());


        RpcRequest request = new RpcRequest();
        //这里要解决只用用全类路径的问题需要引入注册中心
        //这里我简单的用Map模拟了一个注册中心
        request.setClassName("blossom.project.rpc.core.proxy.jdk.JdkRpcService");
        request.setMethodName("testJdkRpc");
        request.setParams(new Object[]{"hello!!!JdkRpc!!!"});
        request.setParamsTypes(new Class<?>[]{String.class});

        // 使用自定义的 invoke 方法处理 RpcRequest
        Object result = JdkRpcProxy.invoke(request);
        System.out.println(result);
    }

    //使用原生JDK
    public static void main1(String[] args) {
        // 创建服务实例
        JdkRpcServiceImpl service = new JdkRpcServiceImpl();

        // 创建代理实例
        JdkRpcService serviceProxy = (JdkRpcService) Proxy.newProxyInstance(
                JdkRpcService.class.getClassLoader(),
                new Class<?>[]{JdkRpcService.class},
                new JdkRpcProxy(service)
        );

        // 通过代理对象调用方法
        String result = serviceProxy.testJdkRpc("Hello");
        System.out.println(result);
    }
}
