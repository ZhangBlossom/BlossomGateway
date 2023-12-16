package blossom.project.rpc.core.enums;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 16:55
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * AlgorithmTypeEnum类
 * 一个字节8个bit 0000 | 0000
 * 2^4 = 16 也就是高低位分别可以对应16种数据
 * 1：可以高位用于存放压缩算法
 * 2：低位存储序列化算法
 * 3：使用位运算进行高低位计算即可得到具体的算法类型
 */
public enum AlgorithmTypeEnum {

    // 压缩算法枚举定义
    NO_COMPRESSION((byte)0x00), // 无压缩（默认操作，这样子就算不选也无所谓了）
    GZIP((byte)0x10),           // GZIP压缩
    // ... 其他压缩算法

    // 序列化算法枚举定义
    PROTOBUF((byte)0x01),       // Protobuf序列化
    ARVO((byte)0x02),           // Avro序列化
    JSON((byte)0x03),           // JSON序列化
    JAVA((byte)0x04);           // Java原生序列化

    private byte code;

    AlgorithmTypeEnum(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return this.code;
    }

    // 结合压缩算法和序列化算法的code
    public static byte combine(AlgorithmTypeEnum compression, AlgorithmTypeEnum serialization) {
        return (byte) (compression.code | serialization.code);
    }

    // 分离组合code为压缩和序列化算法的code
    public static AlgorithmTypeEnum[] split(byte combinedCode) {
        byte compressionCode = (byte) (combinedCode & 0xF0); // 获取高4位
        byte serializationCode = (byte) (combinedCode & 0x0F); // 获取低4位

        return new AlgorithmTypeEnum[]{
                findByCode(compressionCode), // 压缩算法
                findByCode(serializationCode) // 序列化算法
        };
    }

    // 根据code查找对应的枚举值
    private static AlgorithmTypeEnum findByCode(byte code) {
        for (AlgorithmTypeEnum type : AlgorithmTypeEnum.values()) {
            if (type.code == code) {
                return type;
            }
        }
        //没有找到对应的枚举类型就报错
        throw new RuntimeException("No enumeration type was found");
    }
}