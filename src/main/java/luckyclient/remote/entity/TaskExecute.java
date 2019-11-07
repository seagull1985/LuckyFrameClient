package luckyclient.remote.entity;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 任务执行实体
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019年4月13日
 */
public class TaskExecute extends BaseEntity
{
	private static final long serialVersionUID = 1L;
	
	/** 任务ID */
	private Integer taskId;
	/** 调度ID */
	private Integer schedulingId;
	/** 项目ID */
	private Integer projectId;
	/** 任务名称 */
	private String taskName;
	/** 状态 0未执行 1执行中 2 成功 4失败 5 唤起客户端失败 */
	private Integer taskStatus;
	/** 总用例数 */
	private Integer caseTotalCount;
	/** 成功数 */
	private Integer caseSuccCount;
	/** 失败数 */
	private Integer caseFailCount;
	/** 锁定数 */
	private Integer caseLockCount;
	/** 未执行用例 */
	private Integer caseNoexecCount;
	/** 任务结束时间 */
	private Date finishTime;
	/** 关联项目实体 */
	private Project project;
	/** 任务执行百分比 */
	private Integer taskProgress;

	public Integer getTaskProgress() {
		return taskProgress;
	}

	public void setTaskProgress(Integer taskProgress) {
		this.taskProgress = taskProgress;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void setTaskId(Integer taskId) 
	{
		this.taskId = taskId;
	}

	public Integer getTaskId() 
	{
		return taskId;
	}
	public void setSchedulingId(Integer schedulingId) 
	{
		this.schedulingId = schedulingId;
	}

	public Integer getSchedulingId() 
	{
		return schedulingId;
	}
	public void setProjectId(Integer projectId) 
	{
		this.projectId = projectId;
	}

	public Integer getProjectId() 
	{
		return projectId;
	}
	public void setTaskName(String taskName) 
	{
		this.taskName = taskName;
	}

	public String getTaskName() 
	{
		return taskName;
	}
	public void setTaskStatus(Integer taskStatus) 
	{
		this.taskStatus = taskStatus;
	}

	public Integer getTaskStatus() 
	{
		return taskStatus;
	}
	public void setCaseTotalCount(Integer caseTotalCount) 
	{
		this.caseTotalCount = caseTotalCount;
	}

	public Integer getCaseTotalCount() 
	{
		return caseTotalCount;
	}
	public void setCaseSuccCount(Integer caseSuccCount) 
	{
		this.caseSuccCount = caseSuccCount;
	}

	public Integer getCaseSuccCount() 
	{
		return caseSuccCount;
	}
	public void setCaseFailCount(Integer caseFailCount) 
	{
		this.caseFailCount = caseFailCount;
	}

	public Integer getCaseFailCount() 
	{
		return caseFailCount;
	}
	public void setCaseLockCount(Integer caseLockCount) 
	{
		this.caseLockCount = caseLockCount;
	}

	public Integer getCaseLockCount() 
	{
		return caseLockCount;
	}
	public void setCaseNoexecCount(Integer caseNoexecCount) 
	{
		this.caseNoexecCount = caseNoexecCount;
	}

	public Integer getCaseNoexecCount() 
	{
		return caseNoexecCount;
	}
	public void setFinishTime(Date finishTime) 
	{
		this.finishTime = finishTime;
	}

	public Date getFinishTime() 
	{
		return finishTime;
	}

	@Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("taskId", getTaskId())
            .append("schedulingId", getSchedulingId())
            .append("projectId", getProjectId())
            .append("taskName", getTaskName())
            .append("taskStatus", getTaskStatus())
            .append("caseTotalCount", getCaseTotalCount())
            .append("caseSuccCount", getCaseSuccCount())
            .append("caseFailCount", getCaseFailCount())
            .append("caseLockCount", getCaseLockCount())
            .append("caseNoexecCount", getCaseNoexecCount())
            .append("finishTime", getFinishTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
