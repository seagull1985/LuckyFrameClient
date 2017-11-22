package luckyclient.publicclass.remoterInterface;

public class InterfaceParamObject {
	/**
	 * 参数类
	 */
	String classname;
	/**
	 * 参数json value
	 */
	String value;

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */

	@Override
	public String toString() {
		return "InterfaceParamObject [classname=" + classname + ", value="
				+ value + "]";
	}

}
