package luckyclient.tool.jenkins;

import java.io.IOException;
import java.util.List;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.helper.Range;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildCause;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.ConsoleLog;
import com.offbytwo.jenkins.model.JobWithDetails;

/**
 *  * Job Build(任务构建) 相关操作
 * 例如对任务 Build 相关的信息进行获取操作、例如获取构建日志
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
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
     * 获取 最后一次Build的详细信息
     * @param jobName
     * @author Seagull
     * @date 2019年10月30日
     */
    public void getJobLastBuildDetail(String jobName){
        try {
            // 获取 Job 信息
            JobWithDetails job = jenkinsServer.getJob(jobName);
            // 这里用最后一次编译来示例
            BuildWithDetails build = job.getLastBuild().details();
            // 获取构建的显示名称
            System.out.println(build.getDisplayName());
            // 获取构建的参数信息
            System.out.println(build.getParameters());
            // 获取构建编号
            System.out.println(build.getNumber());
            // 获取构建结果，如果构建未完成则会显示为null
            System.out.println(build.getResult());
            // 获取执行构建的活动信息
            System.out.println(build.getActions());
            // 获取构建持续多少时间(ms)
            System.out.println(build.getDuration());
            // 获取构建开始时间戳
            System.out.println(build.getTimestamp());
            // 获取构建头信息，里面包含构建的用户，上游信息，时间戳等
            List<BuildCause> buildCauses = build.getCauses();
            for (BuildCause bc:buildCauses){
                System.out.println(bc.getUserId());
                System.out.println(bc.getShortDescription());
                System.out.println(bc.getUpstreamBuild());
                System.out.println(bc.getUpstreamProject());
                System.out.println(bc.getUpstreamUrl());
                System.out.println(bc.getUserName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    /**
     * 获取 Build Log 日志信息
     */
    public void getJobBuildLog(){
        try {
            // 获取 Job 信息
            JobWithDetails job = jenkinsServer.getJob("test-job");
            // 这里用最后一次编译来示例
            BuildWithDetails build = job.getLastBuild().details();
            // 获取构建的日志，如果正在执行构建，则会只获取已经执行的过程日志
 
            // Text格式日志
            System.out.println(build.getConsoleOutputText());
            // Html格式日志
            System.out.println(build.getConsoleOutputHtml());
 
            // 获取部分日志,一般用于正在执行构建的任务
            ConsoleLog consoleLog = build.getConsoleOutputText(0);
            // 获取当前日志大小
            System.out.println(consoleLog.getCurrentBufferSize());
            // 是否已经构建完成，还有更多日志信息
            System.out.println(consoleLog.getHasMoreData());
            // 获取当前截取的日志信息
            System.out.println(consoleLog.getConsoleLog());
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    /**
     * 获取正在执行构建任务的日志信息
     */
    public void getBuildActiveLog(){
        try {
            // 这里用最后一次编译来示例
            BuildWithDetails build = jenkinsServer.getJob("test-job").getLastBuild().details();
            // 当前日志
            ConsoleLog currentLog = build.getConsoleOutputText(0);
            // 输出当前获取日志信息
            System.out.println(currentLog.getConsoleLog());
            // 检测是否还有更多日志,如果是则继续循环获取
            while (currentLog.getHasMoreData()){
                // 获取最新日志信息
                ConsoleLog newLog = build.getConsoleOutputText(currentLog.getCurrentBufferSize());
                // 输出最新日志
                System.out.println(newLog.getConsoleLog());
                currentLog = newLog;
                // 睡眠1s
                Thread.sleep(1000);
            }
        }catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
 
    public static void main(String[] args) {
        JobBuildApi jobBuildApi = new JobBuildApi();
        jobBuildApi.getJobLastBuildDetail("");
    }
}
