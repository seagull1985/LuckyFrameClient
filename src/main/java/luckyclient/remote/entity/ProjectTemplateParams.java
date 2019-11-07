package luckyclient.remote.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 协议模板参数实体
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019年4月13日
 */
public class ProjectTemplateParams extends BaseEntity
{
	private static final long serialVersionUID = 1L;
	
	/** 模板参数ID */
	private Integer paramsId;
	/** 模板ID */
	private Integer templateId;
	/** 参数名 */
	private String paramName;
	/** 参数默认值 */
	private String paramValue;
	/** 0 String 1 JSON对象 2 JSONARR对象 3 文件类型 */
	private Integer paramType;

	public void setParamsId(Integer paramsId) 
	{
		this.paramsId = paramsId;
	}

	public Integer getParamsId() 
	{
		return paramsId;
	}
	public void setTemplateId(Integer templateId) 
	{
		this.templateId = templateId;
	}

	public Integer getTemplateId() 
	{
		return templateId;
	}
	public void setParamName(String paramName) 
	{
		this.paramName = paramName;
	}

	public String getParamName() 
	{
		return paramName;
	}
	public void setParamValue(String paramValue) 
	{
		this.paramValue = paramValue;
	}

	public String getParamValue() 
	{
		return paramValue;
	}
	public void setParamType(Integer paramType) 
	{
		this.paramType = paramType;
	}

	public Integer getParamType() 
	{
		return paramType;
	}

	@Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("paramsId", getParamsId())
            .append("templateId", getTemplateId())
            .append("paramName", getParamName())
            .append("paramValue", getParamValue())
            .append("paramType", getParamType())
            .toString();
    }
}
