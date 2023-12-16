package blossom.project.rpc.core.serialize;

import blossom.project.rpc.core.enums.AlgorithmTypeEnum;
import java.util.HashMap;
import java.util.Map;


/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 18:40
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * SerializerManager管理器
 * 使用简单策略模式的方式来对多种序列化器的实现进行管理
 *
 */


public class SerializerManager {


    private static final Map<Byte, Serializer> serializerMap = new HashMap<>();
    // 定义默认序列化器
    private static final Serializer DEFAULT_SERIALIZER = new JavaSerializer();

    static {
        // 注册所有支持的序列化器
        serializerMap.put(AlgorithmTypeEnum.JSON.getCode(), new JsonSerializer());
        serializerMap.put(AlgorithmTypeEnum.JAVA.getCode(), new JavaSerializer());
        // 可以添加更多的序列化器
    }

    /**
     * 获取序列化器
     * @param type 序列化类型
     * @return Serializer 序列化器
     */
    public static Serializer getSerializer(byte type) {
        return serializerMap.getOrDefault(type, DEFAULT_SERIALIZER);
    }
}
