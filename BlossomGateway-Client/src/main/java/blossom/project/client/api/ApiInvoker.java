package blossom.project.client.api;/**
 * @Author:Serendipity
 * @Date:
 * @Description:
 */

import java.lang.annotation.*;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/29 17:11
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * ApiInvoker注解
 * 当前注解必须在服务的方法上面强制声明
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiInvoker {
    String path();
}
