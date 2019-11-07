package luckyclient.remote.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 协议模板实体
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019年4月13日
 */
public class ProjectProtocolTemplate extends BaseEntity
{
	private static final long serialVersionUID = 1L;
	
	/** 模板ID */
	private Integer templateId;
	/** 模板名称 */
	private String templateName;
	/** 项目ID */
	private Integer projectId;
	/** 消息头 */
	private String headMsg;
	/** 客户端中的证书路径 */
	private String cerificatePath;
	/** 编码格式 */
	private String encoding;
	/** 超时时间 */
	private Integer timeout;
	/** 请求响应返回值是否带头域信息 0不带 1带 */
	private Integer isResponseHead;
	/** 请求响应返回值是否带状态码 0不带 1带 */
	private Integer isResponseCode;
	/** 关联项目实体 */
	private Project project;

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void setTemplateId(Integer templateId) 
	{
		this.templateId = templateId;
	}

	public Integer getTemplateId() 
	{
		return templateId;
	}
	public void setTemplateName(String templateName) 
	{
		this.templateName = templateName;
	}

	public String getTemplateName() 
	{
		return templateName;
	}
	public void setProjectId(Integer projectId) 
	{
		this.projectId = projectId;
	}

	public Integer getProjectId() 
	{
		return projectId;
	}
	public void setHeadMsg(String headMsg) 
	{
		this.headMsg = headMsg;
	}

	public String getHeadMsg() 
	{
		return headMsg;
	}
	public void setCerificatePath(String cerificatePath) 
	{
		this.cerificatePath = cerificatePath;
	}

	public String getCerificatePath() 
	{
		return cerificatePath;
	}
	public void setEncoding(String encoding) 
	{
		this.encoding = encoding;
	}

	public String getEncoding() 
	{
		return encoding;
	}
	public void setTimeout(Integer timeout) 
	{
		this.timeout = timeout;
	}

	public Integer getTimeout() 
	{
		return timeout;
	}
	public void setIsResponseHead(Integer isResponseHead) 
	{
		this.isResponseHead = isResponseHead;
	}

	public Integer getIsResponseHead() 
	{
		return isResponseHead;
	}
	public void setIsResponseCode(Integer isResponseCode) 
	{
		this.isResponseCode = isResponseCode;
	}

	public Integer getIsResponseCode() 
	{
		return isResponseCode;
	}

	@Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("templateId", getTemplateId())
            .append("templateName", getTemplateName())
            .append("projectId", getProjectId())
            .append("headMsg", getHeadMsg())
            .append("cerificatePath", getCerificatePath())
            .append("encoding", getEncoding())
            .append("timeout", getTimeout())
            .append("isResponseHead", getIsResponseHead())
            .append("isResponseCode", getIsResponseCode())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("project", getProject())
            .toString();
    }
}