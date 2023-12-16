package blossom.project.rpc.core.proxy.cglib;

import blossom.project.rpc.core.entity.RpcRequest;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import java.lang.reflect.Method;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 20:47
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * RpcCglibProxy类用来测试是否可以用CGLIB实现动态代理
 *
 */
public class CglibRpcProxy implements MethodInterceptor {

    private Object target;

    public CglibRpcProxy(Object target) {
        this.target = target;
    }

    public static Object createProxy(Object target) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new CglibRpcProxy(target));
        return enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        // 在调用实际方法前可以添加自定义逻辑
        System.out.println("CGLIB proxy: Invoking method: " + method.getName());

        // 调用目标方法
        return method.invoke(target, args);
    }

    public static Object invoke(RpcRequest request) throws Exception {
        Class<?> clazz = Class.forName(request.getClassName());
        Object serviceInstance = clazz.getDeclaredConstructor().newInstance();

        Object proxyInstance = createProxy(serviceInstance);

        Method method = clazz.getMethod(request.getMethodName(), request.getParamsTypes());
        return method.invoke(proxyInstance, request.getParams());
    }

    /**
     * 特别注意，由于我的JDK版本是JDK19，所以运行的时候会有一些问题。
     * 从Java 9开始，Java引入了模块系统（Jigsaw项目），增加了对类加载器的访问限制。
     * 错误 java.lang.reflect.InaccessibleObjectException
     * 表明CGLIB试图访问一个它无权访问的类或方法。
     *
     * 这个问题通常发生在使用CGLIB或其他需要动态生成类的库时，尤其是在Java 9及以上版本。
     * 解决这个问题的一种方法是在JVM启动参数中添加一个--add-opens参数，以放宽对特定模块的访问限制。
     *
     * 可以尝试在JVM启动参数中添加以下内容：
     * --add-opens java.base/java.lang=ALL-UNNAMED
     * 然后就可以成功运行了
     * 这里CGLIB的代理也可以成功
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        RpcRequest request = new RpcRequest();
        request.setClassName("blossom.project.rpc.core.proxy.jdk.JdkRpcServiceImpl");
        request.setMethodName("testJdkRpc");
        request.setParams(new Object[]{"Hello, CGLIB RPC!"});
        request.setParamsTypes(new Class<?>[]{String.class});

        Object result = CglibRpcProxy.invoke(request);
        System.out.println(result);
    }
}
