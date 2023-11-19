package blossom.project.dubbo;

import java.util.Date;

import blossom.project.dubbo.service.DubboRPCService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Task implements CommandLineRunner {
    @DubboReference
    private DubboRPCService dubboRPCService;

    @Override
    public void run(String... args) throws Exception {
        String result = dubboRPCService.testDubboRPC("world");
        System.out.println("Receive result ======> " + result);

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    System.out.println(new Date() + " Receive result ======> " + dubboRPCService.testDubboRPC("world"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
}