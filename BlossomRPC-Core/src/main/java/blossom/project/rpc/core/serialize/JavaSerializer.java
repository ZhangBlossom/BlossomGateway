package blossom.project.rpc.core.serialize;

import blossom.project.rpc.core.enums.AlgorithmTypeEnum;

import java.io.*;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 17:18
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * JavaSerializer类
 */
public class JavaSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T obj) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        ObjectInputStream ois = null;
        try {
            return (T) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public byte getSerializerType() {
        //默认就不用压缩算法了 直接返回JAVA序列化算法
        return AlgorithmTypeEnum.JAVA.getCode();
    }
}
