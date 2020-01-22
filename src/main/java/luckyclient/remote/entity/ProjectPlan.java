package luckyclient.remote.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 测试计划实体
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019年4月13日
 */
public class ProjectPlan extends BaseEntity
{
	private static final long serialVersionUID = 1L;
	
	/** 测试计划ID */
	private Integer planId;
	/** 测试计划名称 */
	private String planName;
	/** 计划中用例总数 */
	private Integer planCaseCount;
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

	public void setPlanId(Integer planId) 
	{
		this.planId = planId;
	}

	public Integer getPlanId() 
	{
		return planId;
	}
	public void setPlanName(String planName) 
	{
		this.planName = planName;
	}

	public String getPlanName() 
	{
		return planName;
	}
	public void setPlanCaseCount(Integer planCaseCount) 
	{
		this.planCaseCount = planCaseCount;
	}

	public Integer getPlanCaseCount() 
	{
		return planCaseCount;
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
            .append("planId", getPlanId())
            .append("planName", getPlanName())
            .append("planCaseCount", getPlanCaseCount())
            .append("projectId", getProjectId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("project", getProject())
            .toString();
    }
}
