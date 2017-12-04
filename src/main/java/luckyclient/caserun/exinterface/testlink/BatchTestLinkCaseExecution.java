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
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @author： seagull
 * @date 2017年12月1日 上午9:29:40
 * 
 */
public class BatchTestLinkCaseExecution {
	
	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * 创建线程池，多线程执行用例
	 */
	
	public static void batchCaseExecuteForTast(String projectname,String taskid,String batchcase) throws Exception{
		TestTaskexcute task=GetServerAPI.cgetTaskbyid(Integer.valueOf(taskid));
		int threadcount = task.getTestJob().getThreadCount();
		ThreadPoolExecutor	threadExecute	= new ThreadPoolExecutor(threadcount, 30, 3, TimeUnit.SECONDS,
	            new ArrayBlockingQueue<Runnable>(1000),
	            new ThreadPoolExecutor.CallerRunsPolicy());
		 //执行全部非成功状态用例
		if(batchcase.indexOf("ALLFAIL")>-1){   
			LogOperation caselog = new LogOperation();        
			String casemore = caselog.unSucCaseUpdate(taskid);
			String[] temp=casemore.split("\\#",-1);
			for(int i=0;i<temp.length;i++){
  			   String testCaseExternalId = temp[i].substring(0, temp[i].indexOf("%"));
			   int version = Integer.parseInt(temp[i].substring(temp[i].indexOf("%")+1,temp[i].length()-1));
			   TestControl.Debugcount++;   //多线程计数++，用于检测线程是否全部执行完
			   threadExecute.execute(new ThreadForTestLinkBatchCase(projectname,testCaseExternalId,version,taskid));
			}			
		}else{                                           //批量执行用例
			String[] temp=batchcase.split("\\#",-1);
			for(int i=0;i<temp.length;i++){
				String testCaseExternalId = temp[i].substring(0, temp[i].indexOf("%"));
				int version = Integer.parseInt(temp[i].substring(temp[i].indexOf("%")+1,temp[i].length()));
				TestControl.Debugcount++;   //多线程计数++，用于检测线程是否全部执行完
				threadExecute.execute(new ThreadForTestLinkBatchCase(projectname,testCaseExternalId,version,taskid));
			}
		}
		//多线程计数，用于检测线程是否全部执行完
		int i=0;
		while(TestControl.Debugcount!=0){
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
//		BatchTestCaseExecution.BatchCaseExecuteForTast("清算项目", "35", "ALLFAIL");
	}

}
