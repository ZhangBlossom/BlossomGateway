package blossom.project.common.config;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * 一个服务定义会对应多个服务实例
 */
public class ServiceInstance implements Serializable {

	private static final long serialVersionUID = -7559569289189228478L;

	/**
	 * 	服务实例ID: ip:port
	 */
	protected String serviceInstanceId;
	
	/**
	 * 	服务定义唯一id： uniqueId
	 */
	protected String uniqueId;

	/**
	 * 	服务实例地址： ip:port
	 */
	protected String ip;

	protected int port;
	
	/**
	 * 	标签信息
	 */
	protected String tags;
	
	/**
	 * 	权重信息
	 */
	protected Integer weight;
	
	/**
	 * 	服务注册的时间戳：后面我们做负载均衡，warmup预热
	 */
	protected long registerTime;
	
	/**
	 * 	服务实例启用禁用
	 */
	protected boolean enable = true;
	
	/**
	 * 	服务实例对应的版本号
	 */
	protected String version;

	/**
	 * 服务实例是否是灰度的
	 */
	@Getter
	@Setter
	protected boolean gray;
	public ServiceInstance() {
		super();
	}

	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	public void setServiceInstanceId(String serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getAddress() {
		return uniqueId;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public long getRegisterTime() {
		return registerTime;
	}

	public void setRegisterTime(long registerTime) {
		this.registerTime = registerTime;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(this == null || getClass() != o.getClass()) {
			return false;
		}
		ServiceInstance serviceInstance = (ServiceInstance)o;
		return Objects.equals(serviceInstanceId, serviceInstance.serviceInstanceId);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(serviceInstanceId);
	}
	
}
