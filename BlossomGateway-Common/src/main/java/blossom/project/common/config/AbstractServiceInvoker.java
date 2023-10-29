package blossom.project.common.config;

/**
 * 抽象的服务调用接口实现类
 */
public class AbstractServiceInvoker implements ServiceInvoker {
	
	protected String invokerPath;
	
	protected int timeout = 5000;

	@Override
	public String getInvokerPath() {
		return invokerPath;
	}

	@Override
	public void setInvokerPath(String invokerPath) {
		this.invokerPath = invokerPath;
	}

	@Override
	public int getTimeout() {
		return timeout;
	}

	@Override
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
