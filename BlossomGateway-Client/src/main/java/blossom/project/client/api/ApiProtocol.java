package blossom.project.client.api;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/29 17:07
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * ApiProtocol类
 *
 * 这里提供对http和dubbo的支持
 */
public enum ApiProtocol {
    HTTP("http", "http协议"),
    DUBBO("dubbo", "dubbo协议");

    private String protocol;

    private String desc;

    ApiProtocol(String protocol, String desc) {
        this.protocol = protocol;
        this.desc = desc;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getDesc() {
        return desc;
    }
}
