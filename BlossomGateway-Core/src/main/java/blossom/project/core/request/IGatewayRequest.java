package blossom.project.core.request;

import org.asynchttpclient.Request;
import org.asynchttpclient.cookie.Cookie;

/**
 * @PROJECT_NAME: api-gateway
 * @DESCRIPTION: 网关请求类接口
* @USER: ZhangBlossom
 * @DATE: 2023/10/23/29 21:01
 */
public interface IGatewayRequest {

    /**
     * 修改域名
     * @param host
     */
    void setModifyHost(String host);

    /**
     * 获取域名
     */
    String getModifyHost();

    /**
     * 设置/获取路径
     * @param path
     */
    void setModifyPath(String path);
    String  getModifyPath();

    /**
     * 添加请求头信息
     * @param name
     * @param name
     */
    void addHeader(CharSequence name,String value);
    /**
     * 设置请求头信息
     * @param name
     * @param name
     */
    void setHeader(CharSequence name,String value);

    /**
     * Get 请求参数
     * @param name
     * @param value
     */
    void addQueryParam(String name ,String value);

    /**
     * POST 请求参数
     * @param name
     * @param value
     */
    void addFormParam(String name ,String value);

    /**
     * 添加或者替换Cookie
     * @param cookie
     */
    void addOrReplaceCookie(Cookie cookie);

    /**
     * 设置请求超时时间
     * @param requestTimeout
     */
    void setRequestTimeout(int requestTimeout);

    /**
     * 获取最终的请求路径
     * @return
     */
    String getFinalUrl();

    /**
     * 构造最终的请求对象
     * @return
     */
    Request build();

}
