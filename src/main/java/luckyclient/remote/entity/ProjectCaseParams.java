package luckyclient.remote.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 公共参数实体
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019年4月13日
 */
public class ProjectCaseParams extends BaseEntity
{
	private static final long serialVersionUID = 1L;
	
	/** 用例参数ID */
	private Integer paramsId;
	/** 参数名称 */
	private String paramsName;
	/** 参数值 */
	private String paramsValue;
	/** 项目ID */
	private Integer projectId;
	/** 关联项目实体 */
	private Project project;

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void setParamsId(Integer paramsId) 
	{
		this.paramsId = paramsId;
	}

	public Integer getParamsId() 
	{
		return paramsId;
	}
	public void setParamsName(String paramsName) 
	{
		this.paramsName = paramsName;
	}

	public String getParamsName() 
	{
		return paramsName;
	}
	public void setParamsValue(String paramsValue) 
	{
		this.paramsValue = paramsValue;
	}

	public String getParamsValue() 
	{
		return paramsValue;
	}
	public void setProjectId(Integer projectId) 
	{
		this.projectId = projectId;
	}

	public Integer getProjectId() 
	{
		return projectId;
	}

	@Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("paramsId", getParamsId())
            .append("paramsName", getParamsName())
            .append("paramsValue", getParamsValue())
            .append("projectId", getProjectId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
