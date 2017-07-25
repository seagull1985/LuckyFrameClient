package luckyclient.caserun.exinterface.testlink;

import luckyclient.caserun.exinterface.TestControl;

public class ThreadForTestLinkBatchCase extends Thread{
	
	private String projectname;
	private String testCaseExternalId;
	private int version;
	private String tastid;
	
	public ThreadForTestLinkBatchCase(String projectname,String testCaseExternalId,int version,String tastid){
		this.projectname = projectname;
		this.testCaseExternalId = testCaseExternalId;
		this.version = version;
		this.tastid = tastid;
	}
	
	public void run(){		
		 TestLinkCaseExecution.OneCaseExecuteForTast(projectname, testCaseExternalId, version, tastid);
		 TestControl.Debugcount--;        //多线程计数--，用于检测线程是否全部执行完
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
