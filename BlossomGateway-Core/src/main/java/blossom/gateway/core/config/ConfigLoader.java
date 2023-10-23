package blossom.gateway.core.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.util.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 21:11
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * ConfigLoader类
 */
public class ConfigLoader {
    public static final String CONFIG_FILE = "gateway.properties";
    public static final String ENV_PREFIX = "GATEWAY_";
    public static final String JVM_PREFIX = "gateway.";

    private static final ConfigLoader INSTANCE = new ConfigLoader();

    private GatewayConfig config;

    private ConfigLoader() {

    }

    public static ConfigLoader getInstance() {
        return INSTANCE;
    }

    public static GatewayConfig getConfig() {
        return INSTANCE.config;
    }

    /**
     * 加载配置方法 优先级高的覆盖优先级低的
     * 运行参数>jvm参数>环境变量>配置文件>配置对象默认值
     *
     * @param args
     * @return
     */
    public GatewayConfig load(String[] args) {
        //配置对象默认值
        config = new GatewayConfig();
        //配置文件 gateway.properties
        loadFromConfigFile();
        //环境变量  port=2222
        loadFromEnv();
        //jvm参数  -Dport=1234
        loadFromJvm();
        //运行参数 --port=1234
        loadFromRuntimeArgs(args);
        return config;
    }

    /**
     * 从运行时参数加载配置
     */
    private void loadFromRuntimeArgs(String[] args) {
        if (ArrayUtils.isNotEmpty(args)) {
            Properties properties = new Properties();
            for (String arg : args) {
                if (arg.startsWith("--") && arg.contains("=")) {
                    properties.put(arg.substring(2, arg.indexOf("=")), arg.substring(arg.indexOf("=") + 1));
                }
            }
            BeanUtil.copyProperties(properties, config);
        }
    }

    /**
     * 从JVM参数中加载配置
     */
    private void loadFromJvm() {
        Properties properties = System.getProperties();
        BeanUtil.copyProperties(properties, config);
    }

    /**
     * 从环境变量加载配置
     */
    private void loadFromEnv() {
        Map<String, String> env = System.getenv();
        Properties properties = new Properties();
        properties.putAll(env);
        BeanUtil.copyProperties(properties, config);
    }

    /**
     * 从配置文件进行加载配置
     */
    private void loadFromConfigFile() {
        InputStream stream = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        if (Objects.nonNull(stream)) {
            Properties properties = new Properties();
            try {
                properties.load(stream);
                BeanUtil.copyProperties(properties, config);
            } catch (Exception e) {
                e.printStackTrace();
                //log.error("load config file{} error!!!", CONFIG_FILE, e);
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}