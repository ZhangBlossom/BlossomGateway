package blossom.project.rpc.core.serialize;

import blossom.project.rpc.core.enums.AlgorithmTypeEnum;
import com.alibaba.fastjson.JSON;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 17:19
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * JsonSerializer类
 */
public class JsonSerializer implements Serializer{
    @Override
    public <T> byte[] serialize(T obj) throws Exception {
        //使用fastjson进行序列化
        return JSON.toJSONString(obj).getBytes();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        //return JSON.parseObject(bytes,clazz);
        return JSON.parseObject(new String(bytes),clazz);
    }

    @Override
    public byte getSerializerType() {
        return AlgorithmTypeEnum.JSON.getCode();
    }
}
