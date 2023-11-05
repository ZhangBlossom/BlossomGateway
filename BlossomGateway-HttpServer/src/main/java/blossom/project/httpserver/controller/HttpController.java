package blossom.project.httpserver.controller;

import blossom.project.client.api.ApiInvoker;
import blossom.project.client.api.ApiProperties;
import blossom.project.client.api.ApiProtocol;
import blossom.project.client.api.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: ZhangBlossom
 * @date: 2023/11/2 20:30
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * HttpController类
 */

@Slf4j
@RestController
@ApiService(serviceId = "backend-http-server", protocol = ApiProtocol.HTTP, patternPath = "/http-server/**")
public class HttpController {
    @Autowired
    private ApiProperties apiProperties;

    @ApiInvoker(path = "/http-server/ping")
    @GetMapping("/http-server/ping")
    public String ping() {
        log.info("{}", apiProperties);
        try {
            Thread.sleep(10000000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "pong";
    }
}
