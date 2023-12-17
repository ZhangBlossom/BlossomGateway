package blossom.project.rpc.server.service.impl;

import blossom.project.rpc.core.service.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
@Service
public class RpcServiceImpl implements RpcService {

    /**
     * 当前方法用于测试RPC请求
     * @param info
     * @return
     */
    @Override
    public String testRpcRequest(String info) {
        log.info("the receive info is :{}",info);

        return "success response info :"+info;
    }
}
