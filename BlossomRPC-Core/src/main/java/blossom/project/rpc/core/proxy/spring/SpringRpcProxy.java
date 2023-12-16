package blossom.project.rpc.core.proxy.spring;

import blossom.project.rpc.core.entity.RpcRequest;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 21:56
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * SpringRpcProxy类
 */
@Component
public class SpringRpcProxy {
    /**
     * 反射方法调用
     *
     * @param request
     * @return
     */
    public static Object invoke(RpcRequest request) {
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
