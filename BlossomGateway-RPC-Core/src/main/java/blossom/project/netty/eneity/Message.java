package blossom.project.netty.eneity;

import lombok.Data;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/14 20:20
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * Message类
 */
@Data
public class Message {
    //消息请求头
    private Header header;
    //消息体
    private Object body;

}
