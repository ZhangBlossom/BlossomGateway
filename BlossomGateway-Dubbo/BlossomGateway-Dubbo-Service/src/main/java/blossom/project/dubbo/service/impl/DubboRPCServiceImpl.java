package blossom.project.dubbo.service.impl;

import blossom.project.client.api.ApiProtocol;
import blossom.project.client.api.ApiService;
import blossom.project.dubbo.service.DubboRPCService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author: ZhangBlossom
 * @date: 2023/11/19 16:11
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * DubboServiceImpl类
 */

@ApiService(serviceId = "backend-dubbo-server", protocol = ApiProtocol.DUBBO,
        patternPath = "/**")
@DubboService
public class DubboRPCServiceImpl implements DubboRPCService {
    @Override
    public String testDubboRPC(String msg) {
        return "hello dubbo!!!"+ msg;
    }
}
