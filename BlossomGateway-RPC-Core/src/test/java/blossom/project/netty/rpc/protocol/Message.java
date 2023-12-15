package blossom.project.netty.rpc.protocol;

import lombok.Data;


@Data
public class Message {
    private Header header;
    private Object body;
}
