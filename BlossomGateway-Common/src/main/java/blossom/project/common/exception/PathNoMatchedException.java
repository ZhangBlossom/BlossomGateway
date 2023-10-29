package blossom.project.common.exception;


import blossom.project.common.enums.ResponseCode;

public class PathNoMatchedException extends BaseException {

	private static final long serialVersionUID = -6695383751311763169L;

	
	public PathNoMatchedException() {
		this(ResponseCode.PATH_NO_MATCHED);
	}
	
	public PathNoMatchedException(ResponseCode code) {
		super(code.getMessage(), code);
	}
	
	public PathNoMatchedException(Throwable cause, ResponseCode code) {
		super(code.getMessage(), cause, code);
	}
}
