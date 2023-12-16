package blossom.project.netty.rpc.protocol;

import lombok.Data;


@Data
public class Header {

    private long reqId;  //id，8个字节
    private byte reqType;  //消息类型， 1个字节
    private int length; //消息体的长度  4个字节
}
