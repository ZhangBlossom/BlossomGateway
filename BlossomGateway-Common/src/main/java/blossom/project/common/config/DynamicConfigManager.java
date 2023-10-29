package blossom.project.common.config;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 动态服务缓存配置管理类
 * 这个类可能不太好理解 如果有了解Nacos源码的可能会好理解一点
 * 推荐在理解这个类的作用之前先查看我的博客
 */
public class DynamicConfigManager {
	
	//	服务的定义集合：uniqueId代表服务的唯一标识
	private ConcurrentHashMap<String /* uniqueId */ , ServiceDefinition> serviceDefinitionMap = new ConcurrentHashMap<>();
	
	//	服务的实例集合：uniqueId与一对服务实例对应
	private ConcurrentHashMap<String /* uniqueId */ , Set<ServiceInstance>>  serviceInstanceMap = new ConcurrentHashMap<>();

	//	规则集合
	private ConcurrentHashMap<String /* ruleId */ , Rule>  ruleMap = new ConcurrentHashMap<>();
	
	private DynamicConfigManager() {
	}
	
	private static class SingletonHolder {
		private static final DynamicConfigManager INSTANCE = new DynamicConfigManager();
	}
	
	
	/***************** 	对服务定义缓存进行操作的系列方法 	***************/
	
	public static DynamicConfigManager getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public void putServiceDefinition(String uniqueId, 
			ServiceDefinition serviceDefinition) {
		
		serviceDefinitionMap.put(uniqueId, serviceDefinition);;
	}
	
	public ServiceDefinition getServiceDefinition(String uniqueId) {
		return serviceDefinitionMap.get(uniqueId);
	}
	
	public void removeServiceDefinition(String uniqueId) {
		serviceDefinitionMap.remove(uniqueId);
	}
	
	public ConcurrentHashMap<String, ServiceDefinition> getServiceDefinitionMap() {
		return serviceDefinitionMap;
	}
	
	/***************** 	对服务实例缓存进行操作的系列方法 	***************/

	public Set<ServiceInstance> getServiceInstanceByUniqueId(String uniqueId){
		return serviceInstanceMap.get(uniqueId);
	}
	
	public void addServiceInstance(String uniqueId, ServiceInstance serviceInstance) {
		Set<ServiceInstance> set = serviceInstanceMap.get(uniqueId);
		set.add(serviceInstance);
	}
	
	public void addServiceInstance(String uniqueId, Set<ServiceInstance> serviceInstanceSet) {
		serviceInstanceMap.put(uniqueId, serviceInstanceSet);
	}
	
	public void updateServiceInstance(String uniqueId, ServiceInstance serviceInstance) {
		Set<ServiceInstance> set = serviceInstanceMap.get(uniqueId);
		Iterator<ServiceInstance> it = set.iterator();
		while(it.hasNext()) {
			ServiceInstance is = it.next();
			if(is.getServiceInstanceId().equals(serviceInstance.getServiceInstanceId())) {
				it.remove();
				break;
			}
		}
		set.add(serviceInstance);
	}
	
	public void removeServiceInstance(String uniqueId, String serviceInstanceId) {
		Set<ServiceInstance> set = serviceInstanceMap.get(uniqueId);
		Iterator<ServiceInstance> it = set.iterator();
		while(it.hasNext()) {
			ServiceInstance is = it.next();
			if(is.getServiceInstanceId().equals(serviceInstanceId)) {
				it.remove();
				break;
			}
		}
	}
	
	public void removeServiceInstancesByUniqueId(String uniqueId) {
		serviceInstanceMap.remove(uniqueId);
	}
	
		
	/***************** 	对规则缓存进行操作的系列方法 	***************/
	
	public void putRule(String ruleId, Rule rule) {
		ruleMap.put(ruleId, rule);
	}

	public void putAllRule(List<Rule> ruleList) {
		Map<String, Rule> map = ruleList.stream()
				.collect(Collectors.toMap(Rule::getId, r -> r));
		ruleMap = new ConcurrentHashMap<>(map);
	}
	
	public Rule getRule(String ruleId) {
		return ruleMap.get(ruleId);
	}
	
	public void removeRule(String ruleId) {
		ruleMap.remove(ruleId);
	}
	
	public ConcurrentHashMap<String, Rule> getRuleMap() {
		return ruleMap;
	}
	

}
