package blossom.project.rpc.server.service.impl;

import blossom.project.rpc.core.service.BlossomRpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 15:46
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * BlossomRpcServiceImpl类
 * 远程被调用的服务的具体实现
 */
@Slf4j
public class BlossomRpcServiceImpl implements BlossomRpcService {
    @Override
    public String saveInfo(String info) {
        log.info("the receive info is :{}",info);

        return "success response info :"+info;
    }
}
