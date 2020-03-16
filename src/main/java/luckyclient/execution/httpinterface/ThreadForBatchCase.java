package luckyclient.execution.httpinterface;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @author： seagull
 * @date 2017年12月1日 上午9:29:40
 * 
 */
public class ThreadForBatchCase extends Thread{

	private Integer caseId;
	private String taskid;
	
	public ThreadForBatchCase(Integer caseId,String taskid){
		this.caseId = caseId;
		this.taskid = taskid;
	}
	
	@Override
	public void run(){
		TestCaseExecution testCaseExecution=new TestCaseExecution();
		testCaseExecution.oneCaseExecuteForTask(caseId, taskid);
		TestControl.THREAD_COUNT--;        //多线程计数--，用于检测线程是否全部执行完
	}

}
