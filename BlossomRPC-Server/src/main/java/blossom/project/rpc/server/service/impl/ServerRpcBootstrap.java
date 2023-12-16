package blossom.project.rpc.server.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: ZhangBlossom
 * @date: 2023/12/16 16:30
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * ServerRpcBootstrap类
 */
@Slf4j
@SpringBootApplication
public class ServerRpcBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(ServerRpcBootstrap.class, args);
        log.info("Server startup successfully!!!");
    }
}
