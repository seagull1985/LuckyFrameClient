package luckyclient.mail;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import luckyclient.planapi.entity.ProjectProtocolTemplate;
import luckyclient.publicclass.remoterinterface.HttpClientHelper;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 *
 * @author�� seagull
 * @date 2017��12��1�� ����9:29:40
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
            Properties properties = luckyclient.publicclass.SysConfig.getConfiguration();
            if ("true".equals(properties.getProperty("task.push.switch").toLowerCase())) {
                luckyclient.publicclass.LogUtil.APP.info("��ʼ�������ƽ̨��������ִ�����....");
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
                ppt.setContentencoding("utf-8");
                ppt.setConnecttimeout(60);
                HttpClientHelper.httpClientPostJson(pushurl, pushparameters, headmsg,ppt);
            }
        } catch (Exception e) {
            luckyclient.publicclass.LogUtil.APP.error("�������ƽ̨��������ִ����������쳣�����飡", e);
            e.printStackTrace();
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
