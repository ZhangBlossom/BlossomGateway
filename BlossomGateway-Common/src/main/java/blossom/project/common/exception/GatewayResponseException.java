
package blossom.project.common.exception;


import blossom.project.common.enums.ResponseCode;

public class GatewayResponseException extends GatewayBaseException {

    private static final long serialVersionUID = -5658789202509039759L;

    public GatewayResponseException() {
        this(ResponseCode.INTERNAL_ERROR);
    }

    public GatewayResponseException(ResponseCode code) {
        super(code.getMessage(), code);
    }

    public GatewayResponseException(Throwable cause, ResponseCode code) {
        super(code.getMessage(), cause, code);
        this.code = code;
    }

}
