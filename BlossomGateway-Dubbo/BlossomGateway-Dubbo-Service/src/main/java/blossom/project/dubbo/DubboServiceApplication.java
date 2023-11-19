package blossom.project.dubbo;

import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableDubbo
public class DubboServiceApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(DubboServiceApplication.class,args);
    }
}
