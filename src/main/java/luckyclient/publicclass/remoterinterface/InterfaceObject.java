package luckyclient.publicclass.remoterinterface;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @author： seagull
 * @date 2017年12月1日 上午9:29:40
 * 
 */
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
