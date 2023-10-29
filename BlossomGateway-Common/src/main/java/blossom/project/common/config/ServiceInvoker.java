package blossom.project.common.config;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 19:57
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * 服务调用的接口模型描述
 */
public interface ServiceInvoker {

	/**
	 * 获取真正的服务调用的全路径
	 */
	String getInvokerPath();
	
	void setInvokerPath(String invokerPath);
	
	/**
	 * 获取该服务调用(方法)的超时时间
	 */
	int getTimeout();
	
	void setTimeout(int timeout);
	
}
