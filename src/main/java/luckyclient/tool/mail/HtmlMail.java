package luckyclient.tool.mail;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import luckyclient.remote.entity.ProjectProtocolTemplate;
import luckyclient.utils.LogUtil;
import luckyclient.utils.config.SysConfig;
import luckyclient.utils.httputils.HttpClientTools;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 *
 * @author： seagull
 * @date 2017年12月1日 上午9:29:40
 */
public class HtmlMail {
    static FreemarkerEmailTemplate fet = new FreemarkerEmailTemplate();

    public static String htmlContentFormat(int[] taskcount, String taskid, String buildstatus, String restartstatus, String time, String jobname) {
        Map<Object, Object> parameters = new HashMap<>(0);
        parameters.put("buildstatus", buildstatus);
        parameters.put("restartstatus", restartstatus);
        parameters.put("casecount", taskcount[0]);
        parameters.put("casesuc", taskcount[1]);
        parameters.put("casefail", taskcount[2]);
        parameters.put("caselock", taskcount[3]);
        parameters.put("caseunex", taskcount[4]);
        parameters.put("time", time);
        parameters.put("taskid", taskid);
        parameters.put("jobname", jobname);
        try {
            Map<String, String> headmsg = new HashMap<>(0);
            Properties properties = SysConfig.getConfiguration();
            if ("true".equals(properties.getProperty("task.push.switch").toLowerCase())) {
                LogUtil.APP.info("开始向第三方平台推送任务执行情况...");
                Map<String, Object> pushparameters = new HashMap<>(0);
                pushparameters.put("buildstatus", buildstatus);
                pushparameters.put("restartstatus", restartstatus);
                pushparameters.put("casecount", taskcount[0]);
                pushparameters.put("casesuc", taskcount[1]);
                pushparameters.put("casefail", taskcount[2]);
                pushparameters.put("caselock", taskcount[3]);
                pushparameters.put("caseunex", taskcount[4]);
                pushparameters.put("time", time);
                pushparameters.put("taskid", taskid);
                pushparameters.put("jobname", jobname);

                String pushurl = properties.getProperty("task.push.url");
                ProjectProtocolTemplate ppt=new ProjectProtocolTemplate();
                ppt.setEncoding("utf-8");
                ppt.setTimeout(60);
                HttpClientTools hct = new HttpClientTools();
                hct.httpClientPostJson(pushurl, pushparameters, headmsg,ppt);
            }
        } catch (Exception e) {
            LogUtil.APP.error("向第三方平台推送任务执行情况出现异常，请检查！", e);
            return fet.getText("task-body", parameters);
        }
        return fet.getText("task-body", parameters);
    }

    public static String htmlSubjectFormat(String jobname) {
        Map<Object, Object> parameters = new HashMap<>(0);
        parameters.put("jobname", jobname);
        return fet.getText("task-title", parameters);
    }

}
