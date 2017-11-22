package luckyclient.publicclass.remoterInterface;

public class InterfaceObject {
	/**
	 * 接口类
	 */
	String interfaceClass;
	/**
	 * 接口方法
	 */
	String interfaceMethod;
	/**
	 * 远程调用地址
	 */
	String remoteUrl;
	/**
	 * 接口参数类型和值的json串，数组结构，有多个按顺序传
	 */
	String params;
	/**
	 * 接口rpc协议类型
	 */
	String testProtocolType;

	public String getInterfaceClass() {
		return interfaceClass;
	}

	public void setInterfaceClass(String interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	public String getInterfaceMethod() {
		return interfaceMethod;
	}

	public void setInterfaceMethod(String interfaceMethod) {
		this.interfaceMethod = interfaceMethod;
	}

	public String getRemoteUrl() {
		return remoteUrl;
	}

	public void setRemoteUrl(String remoteUrl) {
		this.remoteUrl = remoteUrl;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getTestProtocolType() {
		return testProtocolType;
	}

	public void setTestProtocolType(String testProtocolType) {
		this.testProtocolType = testProtocolType;
	}

	
	 /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	 
	@Override
	public String toString() {
		return "InterfaceObject [interfaceClass=" + interfaceClass
				+ ", interfaceMethod=" + interfaceMethod + ", remoteUrl="
				+ remoteUrl + ", params=" + params + ", testProtocolType="
				+ testProtocolType + "]";
	}

	
}
