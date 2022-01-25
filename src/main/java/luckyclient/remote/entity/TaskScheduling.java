package luckyclient.remote.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 任务调度实体
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019年4月13日
 */
public class TaskScheduling extends BaseEntity
{
	private static final long serialVersionUID = 1L;

	/** 预约调度ID */
	private Integer schedulingId;
	/** 预约调度名称 */
	private String schedulingName;
	/** 任务ID */
	private Integer jobId;
	/** 项目ID */
	private Integer projectId;
	/** 测试计划ID */
	private Integer planId;
	/** 聚合计划ID */
	private Integer suiteId;
	/** 客户端ID */
	private Integer clientId;
	/** 环境 */
	private String envName;
	/** 邮件通知地址 */
	private String emailAddress;
	/** 第三方推送地址 */
	private String pushUrl;
	/** 发送邮件通知时的具体逻辑, 0-全部，1-成功，-1-失败 */
	private Integer emailSendCondition;
	/** jenkins构建链接 */
	private String buildingLink;
	/** 远程执行Shell脚本 */
	private String remoteShell;
	/** 客户端执行线程数 */
	private Integer exThreadCount;
	/** 任务类型 0 接口 1 Web UI 2 移动 */
	private Integer taskType;
	/** UI自动化浏览器类型 0 IE 1 火狐 2 谷歌 3 Edge */
	private Integer browserType;
	/** 计划类型 1 单计划 2 聚合计划 */
	private Integer planType;
	/** 任务超时时间(分钟) */
	private Integer taskTimeout;
	/** 客户端测试驱动桩路径 */
	private String clientDriverPath;
	/** 关联项目实体 */
	private Project project;
	/** 关联项目计划 */
	private ProjectPlan projectPlan;
	/** 关联聚合计划 */
	private ProjectSuite projectSuite;
	/** 任务名称 */
	private String jobName;
	/** cron执行表达式 */
	private String cronExpression;
	/** 任务状态（0正常 1暂停） */
	private String status;

	public Integer getSchedulingId() {
		return schedulingId;
	}

	public void setSchedulingId(Integer schedulingId) {
		this.schedulingId = schedulingId;
	}

	public String getSchedulingName() {
		return schedulingName;
	}

	public void setSchedulingName(String schedulingName) {
		this.schedulingName = schedulingName;
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Integer getPlanId() {
		return planId;
	}

	public void setPlanId(Integer planId) {
		this.planId = planId;
	}

	public Integer getSuiteId() {
		return suiteId;
	}

	public void setSuiteId(Integer suiteId) {
		this.suiteId = suiteId;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public String getEnvName() {
		return envName;
	}

	public void setEnvName(String envName) {
		this.envName = envName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPushUrl() {
		return pushUrl;
	}

	public void setPushUrl(String pushUrl) {
		this.pushUrl = pushUrl;
	}

	public Integer getEmailSendCondition() {
		return emailSendCondition;
	}

	public void setEmailSendCondition(Integer emailSendCondition) {
		this.emailSendCondition = emailSendCondition;
	}

	public String getBuildingLink() {
		return buildingLink;
	}

	public void setBuildingLink(String buildingLink) {
		this.buildingLink = buildingLink;
	}

	public String getRemoteShell() {
		return remoteShell;
	}

	public void setRemoteShell(String remoteShell) {
		this.remoteShell = remoteShell;
	}

	public Integer getExThreadCount() {
		return exThreadCount;
	}

	public void setExThreadCount(Integer exThreadCount) {
		this.exThreadCount = exThreadCount;
	}

	public Integer getTaskType() {
		return taskType;
	}

	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}

	public Integer getBrowserType() {
		return browserType;
	}

	public void setBrowserType(Integer browserType) {
		this.browserType = browserType;
	}

	public Integer getPlanType() {
		return planType;
	}

	public void setPlanType(Integer planType) {
		this.planType = planType;
	}

	public Integer getTaskTimeout() {
		return taskTimeout;
	}

	public void setTaskTimeout(Integer taskTimeout) {
		this.taskTimeout = taskTimeout;
	}

	public String getClientDriverPath() {
		return clientDriverPath;
	}

	public void setClientDriverPath(String clientDriverPath) {
		this.clientDriverPath = clientDriverPath;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public ProjectPlan getProjectPlan() {
		return projectPlan;
	}

	public void setProjectPlan(ProjectPlan projectPlan) {
		this.projectPlan = projectPlan;
	}

	public ProjectSuite getProjectSuite() {
		return projectSuite;
	}

	public void setProjectSuite(ProjectSuite projectSuite) {
		this.projectSuite = projectSuite;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	@Override
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
				.append("schedulingId", getSchedulingId())
				.append("jobId", getJobId())
				.append("projectId", getProjectId())
				.append("planId", getPlanId())
				.append("suiteId", getSuiteId())
				.append("clientId", getClientId())
				.append("envName", getEnvName())
				.append("emailAddress", getEmailAddress())
				.append("pushUrl", getPushUrl())
				.append("emailSendCondition", getEmailSendCondition())
				.append("buildingLink", getBuildingLink())
				.append("remoteShell", getRemoteShell())
				.append("exThreadCount", getExThreadCount())
				.append("taskType", getTaskType())
				.append("planType", getPlanType())
				.append("browserType", getBrowserType())
				.append("taskTimeout", getTaskTimeout())
				.append("clientDriverPath", getClientDriverPath())
				.toString();
	}
}