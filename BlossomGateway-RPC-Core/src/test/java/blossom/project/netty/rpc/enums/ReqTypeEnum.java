package blossom.project.netty.rpc.enums;


public enum ReqTypeEnum {

    GET((byte)0),
    POST((byte)1),
    DELETE((byte)2),
    PUT((byte)3);

    private byte code;

    ReqTypeEnum(byte code){
        this.code=code;
    }

    public byte getCode(){
        return this.code;
    }

}
