package luckyclient.tool.jenkins;

import java.io.IOException;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.ConsoleLog;
import com.offbytwo.jenkins.model.JobWithDetails;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import luckyclient.utils.LogUtil;

/**
 * * Job Build(任务构建) 相关操作 例如对任务 Build 相关的信息进行获取操作、例如获取构建日志
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
 * 
 * @author Seagull
 * @date 2019年10月30日
 */
public class JobBuildApi {

	// Jenkins 对象
	private JenkinsServer jenkinsServer;
	// http 客户端对象
	//private JenkinsHttpClient jenkinsHttpClient;

	/**
	 * 构造方法中调用连接 Jenkins 方法
	 * 
	 * 2019年10月30日
	 */
	JobBuildApi() {
		JenkinsConnect jenkinsConnect = new JenkinsConnect();
		// 连接 Jenkins
		jenkinsServer = jenkinsConnect.connection();
		// 设置客户端连接 Jenkins
		//jenkinsHttpClient = jenkinsConnect.getClient();
	}

	/**
	 * 通过job名称触发构建并获取构建结果
	 * @param jobName 任务名称
	 * @return 返回构建结果
	 * @author Seagull
	 * @date 2019年11月29日
	 */
	public BuildResult buildAndGetResultForJobName(String jobName) {
		BuildResult buildResult = null;
		try {
			//触发构建
			jenkinsServer.getJob(jobName).build(false);
			// 获取 Job 信息
			JobWithDetails job = jenkinsServer.getJob(jobName);
			// 这里用最后一次编译来示例
			BuildWithDetails build = job.getLastBuild().details();
			// 获取构建的显示名称
			LogUtil.APP.info("构建项目：{}, 构建名称:{}", jobName,build.getDisplayName());
			// 获取构建的参数信息
			LogUtil.APP.info("构建项目：{}, 构建参数:{}", jobName,build.getParameters());
			// 获取构建编号
			LogUtil.APP.info("构建项目：{}, 构建编号:{}", jobName,build.getNumber());
			// 获取执行构建的活动信息
			LogUtil.APP.info("构建项目：{}, 构建活动信息:{}", jobName,build.getActions());
			// 获取构建开始时间戳
			LogUtil.APP.info("构建项目：{}, 构建时间:{}", jobName,DateUtil.format(DateUtil.date(build.getTimestamp()), "yyyy-MM-dd HH:mm:ss"));
			// 当前日志
			ConsoleLog currentLog = build.getConsoleOutputText(0);
			// 输出当前获取日志信息
			//LogUtil.APP.info(currentLog.getConsoleLog());
			// 检测是否还有更多日志,如果是则继续循环获取
			while (currentLog.getHasMoreData()) {
				// 获取最新日志信息
				ConsoleLog newLog = build.getConsoleOutputText(currentLog.getCurrentBufferSize());
				// 输出最新日志
				if(!StrUtil.isBlank(newLog.getConsoleLog())){
					LogUtil.APP.info("构建项目：{}, 构建日志：{}",jobName,newLog.getConsoleLog());
				}
				currentLog = newLog;

			}
			buildResult = job.getBuildByNumber(build.getNumber()).details().getResult();
			LogUtil.APP.info("构建项目：{}, 构建结果：>>>>>>>>>{}",jobName,buildResult.toString());
		} catch (IOException e) {
			LogUtil.APP.error("获取执行任务状态出现异常", e);
		}
		return buildResult;
	}
    
}
