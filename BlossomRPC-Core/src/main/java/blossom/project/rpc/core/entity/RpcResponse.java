package blossom.project.rpc.core.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 16:51
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * RpcResponse类
 * 当前方法就是RPC的相应类信息
 */
@Data
public class RpcResponse implements Serializable {
    private Object data;
    private String msg;
}
