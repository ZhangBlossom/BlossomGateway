package blossom.project.rpc.core.proxy.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
/**
 * @author: ZhangBlossom
 * @date: 2023/7/18 11:43
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * SpringBeanManager类
 * 通用工具类，用于从Spring容器中获取制定Bean
 */
@Component
public class SpringBeanManager implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanManager.applicationContext=applicationContext;
    }
    public static <T> T getBean(Class<T> clzz){
        return applicationContext.getBean(clzz);
    }
}
