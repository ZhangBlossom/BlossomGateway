package blossom.project.client.api;

import java.lang.annotation.*;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/29 17:04
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * ApiService类
 * 服务定义
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiService {

    String serviceId();

    String version() default "1.0.0";

    ApiProtocol protocol();

    String patternPath();

    String interfaceName() default "";
}
