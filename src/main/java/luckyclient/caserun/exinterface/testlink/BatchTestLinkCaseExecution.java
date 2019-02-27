package luckyclient.caserun.exinterface.testlink;

import luckyclient.caserun.exinterface.TestControl;
import luckyclient.dblog.LogOperation;
import luckyclient.planapi.api.GetServerAPI;
import luckyclient.planapi.entity.TestTaskexcute;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
public class BatchTestLinkCaseExecution {
	
	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * �����̳߳أ����߳�ִ������
	 */
	
	public static void batchCaseExecuteForTast(String projectname,String taskid,String batchcase) throws Exception{
		TestTaskexcute task=GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
		int threadcount = task.getTestJob().getThreadCount();
		ThreadPoolExecutor	threadExecute	= new ThreadPoolExecutor(threadcount, 30, 3, TimeUnit.SECONDS,
	            new ArrayBlockingQueue<Runnable>(1000),
	            new ThreadPoolExecutor.CallerRunsPolicy());
		 //ִ��ȫ���ǳɹ�״̬����
		if(batchcase.indexOf("ALLFAIL")>-1){   
			LogOperation caselog = new LogOperation();        
			String casemore = caselog.unSucCaseUpdate(taskid);
			String[] temp=casemore.split("\\#",-1);
			for(int i=0;i<temp.length;i++){
  			   String testCaseExternalId = temp[i].substring(0, temp[i].indexOf("%"));
			   int version = Integer.parseInt(temp[i].substring(temp[i].indexOf("%")+1,temp[i].length()-1));
			   TestControl.THREAD_COUNT++;   //���̼߳���++�����ڼ���߳��Ƿ�ȫ��ִ����
			   threadExecute.execute(new ThreadForTestLinkBatchCase(projectname,testCaseExternalId,version,taskid));
			}			
		}else{                                           //����ִ������
			String[] temp=batchcase.split("\\#",-1);
			for(int i=0;i<temp.length;i++){
				String testCaseExternalId = temp[i].substring(0, temp[i].indexOf("%"));
				int version = Integer.parseInt(temp[i].substring(temp[i].indexOf("%")+1,temp[i].length()));
				TestControl.THREAD_COUNT++;   //���̼߳���++�����ڼ���߳��Ƿ�ȫ��ִ����
				threadExecute.execute(new ThreadForTestLinkBatchCase(projectname,testCaseExternalId,version,taskid));
			}
		}
		//���̼߳��������ڼ���߳��Ƿ�ȫ��ִ����
		int i=0;
		while(TestControl.THREAD_COUNT!=0){
			i++;
			if(i>600){
				break;
			}
			Thread.sleep(6000);
		}
		threadExecute.shutdown();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		BatchTestCaseExecution.BatchCaseExecuteForTast("������Ŀ", "35", "ALLFAIL");
	}

}
