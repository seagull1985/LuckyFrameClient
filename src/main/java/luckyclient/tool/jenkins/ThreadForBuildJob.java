package luckyclient.tool.jenkins;

import com.offbytwo.jenkins.model.BuildResult;

/**
 * 
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019年12月2日
 */
public class ThreadForBuildJob extends Thread{
	
	private String jobName;
	
	public ThreadForBuildJob(String jobName){
		this.jobName = jobName;
	}
	
	@Override
	public void run(){
		JobBuildApi jobBuildApi=new JobBuildApi();
		BuildResult buildResult = jobBuildApi.buildAndGetResultForJobName(jobName);
		if(BuildResult.SUCCESS.equals(buildResult)){
			BuildingInitialization.THREAD_SUCCOUNT++;
		}
		BuildingInitialization.THREAD_COUNT--;        //多线程计数--，用于检测线程是否全部执行完
	}

}
