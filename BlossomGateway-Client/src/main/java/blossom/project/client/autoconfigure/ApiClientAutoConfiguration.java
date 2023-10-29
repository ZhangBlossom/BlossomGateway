package blossom.project.client.autoconfigure;

import blossom.project.client.manager.DubboClientRegisterManager;
import blossom.project.client.manager.SpringMVCClientRegisterManager;
import blossom.project.client.api.ApiProperties;
import org.apache.dubbo.config.spring.ServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Servlet;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/31 12:22
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * Spring自动装配
 * 如果使用2.7以上版本springboot会更加方便实现
 */

@Configuration
@EnableConfigurationProperties(ApiProperties.class)
//要求有registerAddress 否则不会进行自动注册
@ConditionalOnProperty(prefix = "api", name = {"registerAddress"})
public class ApiClientAutoConfiguration {

    @Autowired
    private ApiProperties apiProperties;

    /**
     * springmvc环境下才会配置
     * @return
     */
    @Bean
    @ConditionalOnClass({Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class})
    @ConditionalOnMissingBean(SpringMVCClientRegisterManager.class)
    public SpringMVCClientRegisterManager springMVCClientRegisterManager() {
        return new SpringMVCClientRegisterManager(apiProperties);
    }

    /**
     * Dubbo环境下才会进行配置
     * @return
     */
    @Bean
    @ConditionalOnClass({ServiceBean.class})
    @ConditionalOnMissingBean(DubboClientRegisterManager.class)
    public DubboClientRegisterManager dubboClientRegisterManager() {
        return new DubboClientRegisterManager(apiProperties);
    }
}
