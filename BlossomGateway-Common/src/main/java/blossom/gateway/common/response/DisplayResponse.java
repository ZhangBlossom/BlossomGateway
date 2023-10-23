package blossom.gateway.common.response;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 18:01
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * Response类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisplayResponse {
    private HttpResponseStatus status;
    private int code;
    private String message;
    private String data;
}
