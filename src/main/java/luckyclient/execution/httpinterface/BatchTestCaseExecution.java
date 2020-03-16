package luckyclient.execution.httpinterface;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import luckyclient.remote.api.GetServerApi;
import luckyclient.remote.api.serverOperation;
import luckyclient.remote.entity.ProjectCase;
import luckyclient.utils.LogUtil;

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
public class BatchTestCaseExecution {

	/**
	 * 创建线程池，多线程执行用例
	 * @param taskid 任务ID
	 * @param batchcase 批量用例字符串，#隔断
	 * @throws Exception 抛异常
	 */
	
	public static void batchCaseExecuteForTast(String taskid, String batchcase) throws Exception{
		int threadcount = GetServerApi.cGetTaskSchedulingByTaskId(Integer.parseInt(taskid)).getExThreadCount();
		ThreadPoolExecutor	threadExecute	= new ThreadPoolExecutor(threadcount, 30, 3, TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(1000),
	            new ThreadPoolExecutor.CallerRunsPolicy());
		//执行全部非成功状态用例
		if(batchcase.contains("ALLFAIL")){
			//初始化写用例结果以及日志模块 
			serverOperation caselog = new serverOperation();        
			List<Integer> caseIdList = caselog.getCaseListForUnSucByTaskId(taskid);
			for (Integer integer : caseIdList) {
				ProjectCase testcase = GetServerApi.cGetCaseByCaseId(integer);
				TestControl.THREAD_COUNT++;   //多线程计数++，用于检测线程是否全部执行完
				threadExecute.execute(new ThreadForBatchCase(testcase.getCaseId(), taskid));
			}			
		}else{                                           //批量执行用例
			String[] temp=batchcase.split("#");
			LogUtil.APP.info("当前批量执行任务中共有【{}】条待测试用例...",temp.length);
			for (String s : temp) {
				TestControl.THREAD_COUNT++;   //多线程计数++，用于检测线程是否全部执行完
				threadExecute.execute(new ThreadForBatchCase(Integer.valueOf(s), taskid));
			}
		}
		//多线程计数，用于检测线程是否全部执行完
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

}
