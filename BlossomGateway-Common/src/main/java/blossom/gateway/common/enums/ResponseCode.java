package blossom.gateway.common.enums;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Data;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 17:39
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * ResponseCode类
 */

public enum ResponseCode {

    SUCCESS(HttpResponseStatus.OK, 200, "成功"),
    INTERNAL_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR, 500, "网关内部异常");;


    private HttpResponseStatus status;
    private int code;
    private String message;

    ResponseCode(HttpResponseStatus status, int code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HttpResponseStatus status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
