package blossom.project.netty.eneity;

import lombok.Data;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/14 20:20
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * Header类
 */
@Data
public class Header {
    //消息id
    private long reqId;

    //消息类型 类似于POST\GET\DELETE\PUT
    private byte reqType;

    //消息长度  其实大部分消息长度不会超过short
    //但是使用int更加方便
    private int length;
}
