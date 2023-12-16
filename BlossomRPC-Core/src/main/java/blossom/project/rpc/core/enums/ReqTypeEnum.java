package blossom.project.rpc.core.enums;

import java.util.Arrays;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 16:53
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * ReqTypeEnum类
 * 1: RPC调用有多种请求类型
 * 2：但是对于框架应该只要考虑一个是请求 一个是响应
 */
public enum ReqTypeEnum {

    //GET((byte)0x01),
    //POST((byte)0x02),
    //DELETE((byte)0x03),
    //PUT((byte)0x04),

    REQUEST((byte)0x01),
    RESPONSE((byte)0x02),

    //心跳类型请求
    HEARTBEAT((byte)0x09);

    private byte code;

    ReqTypeEnum(byte code){
        this.code=code;
    }

    public byte getCode(){
        return this.code;
    }

    /**
     * 根据code值返回相应的枚举值
     * @param code 要查询的code值
     * @return 对应的ReqTypeEnum枚举值，如果没有找到则返回null
     */
    public static ReqTypeEnum getReqTypeByCode(byte code) {
        return Arrays.stream(ReqTypeEnum.values())
                .filter(type -> type.code == code)
                .findFirst()
                .orElse(null);
    }
}
