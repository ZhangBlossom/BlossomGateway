package blossom.project.register.center.api;


import blossom.project.common.config.ServiceDefinition;
import blossom.project.common.config.ServiceInstance;

import java.util.Set;


/**
 * @author: ZhangBlossom
 * @date: 2023/10/28 19:57
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * 注册中心的监听器
 * 用来监听注册中心的一些变化
 */
public interface RegisterCenterListener {

    void onChange(ServiceDefinition serviceDefinition,
                  Set<ServiceInstance> serviceInstanceSet);
}
