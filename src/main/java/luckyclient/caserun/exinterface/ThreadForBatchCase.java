package luckyclient.caserun.exinterface;

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
	
	@Override
	public void run(){		
		 TestCaseExecution.oneCaseExecuteForTast(projectname, testCaseExternalId, version, tastid);
		 TestControl.THREAD_COUNT--;        //���̼߳���--�����ڼ���߳��Ƿ�ȫ��ִ����
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
