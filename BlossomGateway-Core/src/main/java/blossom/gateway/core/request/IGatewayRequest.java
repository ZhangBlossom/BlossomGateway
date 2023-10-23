package blossom.gateway.core.request;

import org.asynchttpclient.Request;
import org.asynchttpclient.cookie.Cookie;


/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 15:12
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * IGatewayRequest类
 * 提高可供修改的request参数操作
 */
public interface IGatewayRequest {


    /**
     * 因为我们需要从注册中心进行获取服务信息
     * 修改请求的host,改变目标服务主机
     * @param host
     */
    void setModifyHost(String host);

    /**
     * 获取目标服务host
     * @return
     */
    String getModifyHost();

    /**
     * 因为我们需要从注册中心进行获取服务信息
     * 修改请求的path,改变目标服务路径
     * @param path
     */
    void setModifyPath(String path);

    /**
     * 获取目标服务path
     * @return
     */
    String getModifyPath();


    /**
     * 添加请求头
     * @param name
     * @param value
     */
    void addHeader(String name,String value);


    /**
     * 设置请求头信息
     * @param name
     * @param value
     */
    void setHeader(String name,String value);


    /**
     * 添加请求参数
     * @param name
     * @param value
     */
    void addQueryParam(String name,String value);

    /**
     * 请求体添加参数
     * @param name
     * @param value
     */
    void addBodyParam(String name,String value);

    /**
     * 添加或者替换cookie
     * @param cookie
     */
    void addOrReplaceCookie(Cookie cookie);


    /**
     * 设置超时时间
     * @param requestTimeout
     */
    void setRequestTimeout(int requestTimeout);

    /**
     * 获取最终的请求,包含请求参数
     * http://localhost:8080/v1/hello?name=1&password=2
     * @return
     */
    String getFinallyUrl();

    /**
     * 构建request对象
     * @return
     */
    Request build();
}
