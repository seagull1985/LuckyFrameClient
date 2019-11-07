package luckyclient.remote.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 测试计划用例实体
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019年4月13日
 */
public class ProjectPlanCase extends BaseEntity
{
	private static final long serialVersionUID = 1L;
	
	/** 计划用例ID */
	private Integer planCaseId;
	/** 用例ID */
	private Integer caseId;
	/** 测试计划ID */
	private Integer planId;
	/** 用例优先级 数字越小，优先级越高 */
	private Integer priority;

	public void setPlanCaseId(Integer planCaseId) 
	{
		this.planCaseId = planCaseId;
	}

	public Integer getPlanCaseId() 
	{
		return planCaseId;
	}
	public void setCaseId(Integer caseId) 
	{
		this.caseId = caseId;
	}

	public Integer getCaseId() 
	{
		return caseId;
	}
	public void setPlanId(Integer planId) 
	{
		this.planId = planId;
	}

	public Integer getPlanId() 
	{
		return planId;
	}
	public void setPriority(Integer priority) 
	{
		this.priority = priority;
	}

	public Integer getPriority() 
	{
		return priority;
	}

	@Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("planCaseId", getPlanCaseId())
            .append("caseId", getCaseId())
            .append("planId", getPlanId())
            .append("priority", getPriority())
            .toString();
    }
}
