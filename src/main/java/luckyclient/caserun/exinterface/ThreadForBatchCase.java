package luckyclient.caserun.exinterface;

import luckyclient.caserun.exinterface.testlink.TestLinkCaseExecution;

public class ThreadForBatchCase extends Thread{
	
	private String projectname;
	private String testCaseExternalId;
	private int version;
	private String tastid;
	
	public ThreadForBatchCase(String projectname,String testCaseExternalId,int version,String tastid){
		this.projectname = projectname;
		this.testCaseExternalId = testCaseExternalId;
		this.version = version;
		this.tastid = tastid;
	}
	
	public void run(){		
		 TestCaseExecution.OneCaseExecuteForTast(projectname, testCaseExternalId, version, tastid);
		 TestControl.Debugcount--;        //多线程计数--，用于检测线程是否全部执行完
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
