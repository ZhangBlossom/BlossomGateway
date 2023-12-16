package blossom.project.rpc.core.serialize;



/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 16:40
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * Serializer类
 * 自己的序列化接口 实现这个接口来自定义自己的序列化规则
 */

public interface Serializer{
    /**
     * 序列化方法，将对象转换为字节流
     * @param obj 要序列化的对象
     * @return 字节数组
     * @throws Exception 序列化过程中可能抛出的异常
     */
    //byte[] serialize(Object obj) throws Exception;
    <T> byte[] serialize(T obj) throws Exception;


    /**
     * 反序列化方法，将字节流转换回特定类型的对象
     * @param bytes 字节数组
     * @param clazz 目标对象的类类型
     * @return 反序列化后的对象
     * @throws Exception 反序列化过程中可能抛出的异常
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception;

    /**
     * 当前方法用于获得当前使用的序列化算法类型
     * @return
     */
    byte getSerializerType();
}
