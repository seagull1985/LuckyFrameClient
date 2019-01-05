package luckyclient.planapi.entity;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @author�� seagull
 * @date 2017��12��1�� ����9:29:40
 * 
 */
public class TestTaskexcute implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String taskId;
	private int casetotalCount = 0;
	private int casesuccCount = 0;
	private int casefailCount = 0;
	private int caselockCount = 0;
	private int casenoexecCount = 0;
	private TestJobs testJob = new TestJobs();

	private int jobid;
	private int projectid;
	private String caseIsExec;
	private String finishtime;
	private String taskStatus;

	private String startDate;
	private String endDate;
	private String name;
	
	private String taskName;

	public int getProjectid() {
		return projectid;
	}

	public void setProjectid(int projectid) {
		this.projectid = projectid;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public int getCasenoexecCount() {
		return casenoexecCount;
	}

	public void setCasenoexecCount(int casenoexecCount) {
		this.casenoexecCount = casenoexecCount;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	private String taskStatus_str;

	public String getTaskStatus_str() {
		return taskStatus_str;
	}

	public void setTaskStatus_str(String taskStatus_str) {
		this.taskStatus_str = taskStatus_str;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCaseIsExec() {
		return caseIsExec;
	}

	public void setCaseIsExec(String caseIsExec) {
		this.caseIsExec = caseIsExec;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getFinishtime() {
		return finishtime;
	}

	public void setFinishtime(String finishtime) {
		this.finishtime = finishtime;
	}

	public int getJobid() {
		return jobid;
	}

	public void setJobid(int jobid) {
		this.jobid = jobid;
	}

	public TestTaskexcute() {

	}

	public TestTaskexcute(int id, String taskId, int casetotalCount,
			int casesuccCount, int casefailCount, int caselockCount,
			TestJobs testJob, String createTime) {
		super();
		this.id = id;
		this.taskId = taskId;
		this.casetotalCount = casetotalCount;
		this.casesuccCount = casesuccCount;
		this.casefailCount = casefailCount;
		this.caselockCount = caselockCount;
		this.testJob = testJob;
		this.createTime = createTime;
	}

	private String createTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCasetotalCount() {
		return casetotalCount;
	}

	public void setCasetotalCount(int casetotalCount) {
		this.casetotalCount = casetotalCount;
	}

	public int getCasesuccCount() {
		return casesuccCount;
	}

	public void setCasesuccCount(int casesuccCount) {
		this.casesuccCount = casesuccCount;
	}

	public int getCasefailCount() {
		return casefailCount;
	}

	public void setCasefailCount(int casefailCount) {
		this.casefailCount = casefailCount;
	}

	public int getCaselockCount() {
		return caselockCount;
	}

	public void setCaselockCount(int caselockCount) {
		this.caselockCount = caselockCount;
	}

	public TestJobs getTestJob() {
		return testJob;
	}

	public void setTestJob(TestJobs testJob) {
		this.testJob = testJob;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

}
