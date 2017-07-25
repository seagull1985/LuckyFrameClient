package luckyclient.planapi.entity;

import java.util.Set;

public class TestJobs implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String taskName;
	private String startDate;
	private String startTime;
	private String endDate;
	private String endTime;
	private String runTime;
	private String remark;
	
	private String planproj;
	private String state;
	private String state_str;
	private int  threadCount;
	private int time;
	private String timeType;
	
	private String isSendMail;
	private String emailer;
	private String testlinkname;
	
	private String isbuilding;
	private String buildname;
	
	private String isrestart;
	private String restartcomm;
	private int extype;  
	private Integer browsertype; 
	private Integer timeout;  
	private String clientip; 
	private Integer projecttype; 
	private Integer projectid;  
	private Integer planid; 
	
	public Integer getProjecttype() {
		return projecttype;
	}

	public void setProjecttype(Integer projecttype) {
		this.projecttype = projecttype;
	}

	public Integer getProjectid() {
		return projectid;
	}

	public void setProjectid(Integer projectid) {
		this.projectid = projectid;
	}

	public Integer getPlanid() {
		return planid;
	}

	public void setPlanid(Integer planid) {
		this.planid = planid;
	}

	public String getClientip() {
		return clientip;
	}

	public void setClientip(String clientip) {
		this.clientip = clientip;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Integer getBrowsertype() {
		return browsertype;
	}

	public void setBrowsertype(Integer browsertype) {
		this.browsertype = browsertype;
	}

	public int getExtype() {
		return extype;
	}

	public void setExtype(int extype) {
		this.extype = extype;
	}

	public String getIsrestart() {
		return isrestart;
	}

	public void setIsrestart(String isrestart) {
		this.isrestart = isrestart;
	}

	public String getRestartcomm() {
		return restartcomm;
	}

	public void setRestartcomm(String restartcomm) {
		this.restartcomm = restartcomm;
	}

	public String getIsbuilding() {
		return isbuilding;
	}

	public void setIsbuilding(String isbuilding) {
		this.isbuilding = isbuilding;
	}

	public String getBuildname() {
		return buildname;
	}

	public void setBuildname(String buildname) {
		this.buildname = buildname;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public String getTestlinkname() {
		return testlinkname;
	}

	public void setTestlinkname(String testlinkname) {
		this.testlinkname = testlinkname;
	}

	public String getIsSendMail() {
		return isSendMail;
	}

	public void setIsSendMail(String isSendMail) {
		this.isSendMail = isSendMail;
	}

	public String getEmailer() {
		return emailer;
	}

	public void setEmailer(String emailer) {
		this.emailer = emailer;
	}

	public String getTimeType() {
		return timeType;
	}

	public void setTimeType(String timeType) {
		this.timeType = timeType;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String getState_str() {
		return state_str;
	}

	public void setState_str(String state_str) {
		this.state_str = state_str;
	}

	private String taskType;
	private String startTimestr;
	private String endTimestr;
	private String createTime;
	private String noEndDate;
	
	public Set<TestTaskexcute> getTast() {
		return tast;
	}

	public void setTast(Set<TestTaskexcute> tast) {
		this.tast = tast;
	}
	private Set<TestTaskexcute> tast;

	private boolean showRun = true;
	

	public TestJobs() {
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	//@NotEmpty(message="任务名称不能为空")
	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	//@NotEmpty(message="�?始日期的时间格式应为：yyyy-MM-dd")
	//@DateTimeFormat(pattern="yyyy-MM-dd")
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	//@NotEmpty(message="�?始时间的时间格式应为：HH:mm:ss")
	//@DateTimeFormat(pattern="HH:mm:ss")
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getRunTime() {
		return runTime;
	}

	public void setRunTime(String runTime) {
		this.runTime = runTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPlanproj() {
		return planproj;
	}

	public void setPlanproj(String planproj) {
		this.planproj = planproj;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getStartTimestr() {
		return startTimestr;
	}

	public void setStartTimestr(String startTimestr) {
		this.startTimestr = startTimestr;
	}

	public String getEndTimestr() {
		return endTimestr;
	}

	public void setEndTimestr(String endTimestr) {
		this.endTimestr = endTimestr;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getNoEndDate() {
		return noEndDate;
	}

	public void setNoEndDate(String noEndDate) {
		this.noEndDate = noEndDate;
	}

	public boolean isShowRun() {
		return showRun;
	}

	public void setShowRun(boolean showRun) {
		this.showRun = showRun;
	}

}
