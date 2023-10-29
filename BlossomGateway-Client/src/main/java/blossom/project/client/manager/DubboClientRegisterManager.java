package blossom.project.client.manager;

import blossom.project.client.api.ApiAnnotationScanner;
import blossom.project.client.api.ApiProperties;
import blossom.project.common.config.ServiceDefinition;
import blossom.project.common.config.ServiceInstance;
import blossom.project.common.utils.NetUtils;
import blossom.project.common.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.ServiceBean;
import org.apache.dubbo.config.spring.context.event.ServiceBeanExportedEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.HashSet;
import java.util.Set;

import static blossom.project.common.constant.BasicConst.COLON_SEPARATOR;
import static blossom.project.common.constant.GatewayConst.DEFAULT_WEIGHT;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/30 12:36
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * Dubbo客户端注册管理器
 *
 */
@Slf4j
public class DubboClientRegisterManager extends AbstractClientRegisterManager implements ApplicationListener<ApplicationEvent> {

    /**
     * 存储处理过的bean
     */
    private Set<Object> set = new HashSet<>();

    public DubboClientRegisterManager(ApiProperties apiProperties) {
        super(apiProperties);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        //这里和mvc有区别 毕竟mvc可以直接监听star容器启动事件
        //而dubbo的导出事件会在spring容器初始化之后执行 所以也可以使用applicationlistener去监听
        if (applicationEvent instanceof ServiceBeanExportedEvent) {
            try {
                ServiceBean serviceBean = ((ServiceBeanExportedEvent) applicationEvent).getServiceBean();
                doRegisterDubbo(serviceBean);
            } catch (Exception e) {
                log.error("doRegisterDubbo error", e);
                throw new RuntimeException(e);
            }
        } else if (applicationEvent instanceof ApplicationStartedEvent) {
            log.info("dubbo api started");
        }
    }


    /**
     * 实际注册方法
     * @param serviceBean 包含了服务的接口、实现类、版本、分组、协议、注册中心等配置
     */
    private void doRegisterDubbo(ServiceBean serviceBean) {
        //拿到真正bean对象  是我们真正的service
        //TODO 可以debug看一眼
        Object bean = serviceBean.getRef();

        if (set.contains(bean)) {
            return;
        }

        ServiceDefinition serviceDefinition = ApiAnnotationScanner.getInstance().scanner(bean, serviceBean);

        if (serviceDefinition == null) {
            return;
        }

        serviceDefinition.setEnvType(getApiProperties().getEnv());

        //服务实例
        ServiceInstance serviceInstance = new ServiceInstance();
        String localIp = NetUtils.getLocalIp();
        int port = serviceBean.getProtocol().getPort();
        String serviceInstanceId = localIp + COLON_SEPARATOR + port;
        String uniqueId = serviceDefinition.getUniqueId();
        String version = serviceDefinition.getVersion();

        serviceInstance.setServiceInstanceId(serviceInstanceId);
        serviceInstance.setUniqueId(uniqueId);
        serviceInstance.setIp(localIp);
        serviceInstance.setPort(port);
        serviceInstance.setRegisterTime(TimeUtil.currentTimeMillis());
        serviceInstance.setVersion(version);
        serviceInstance.setWeight(DEFAULT_WEIGHT);

        register(serviceDefinition, serviceInstance);
    }
}
