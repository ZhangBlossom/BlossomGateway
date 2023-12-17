package blossom.project.rpc.core.proxy.jdk.register;

import java.util.HashMap;
import java.util.Map;

public class ServiceRegistry {
    private static final Map<String, Object> serviceMap = new HashMap<>();

    public static void register(String interfaceName, Object serviceInstance) {
        serviceMap.put(interfaceName, serviceInstance);
    }

    public static Object getService(String interfaceName) {
        return serviceMap.get(interfaceName);
    }
}
