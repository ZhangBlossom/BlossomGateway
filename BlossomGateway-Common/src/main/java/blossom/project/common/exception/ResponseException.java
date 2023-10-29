
package blossom.project.common.exception;


import blossom.project.common.enums.ResponseCode;

public class ResponseException extends BaseException {

    private static final long serialVersionUID = -5658789202509039759L;

    public ResponseException() {
        this(ResponseCode.INTERNAL_ERROR);
    }

    public ResponseException(ResponseCode code) {
        super(code.getMessage(), code);
    }

    public ResponseException(Throwable cause, ResponseCode code) {
        super(code.getMessage(), cause, code);
        this.code = code;
    }

}
