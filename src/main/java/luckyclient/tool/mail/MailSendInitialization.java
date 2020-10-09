package luckyclient.tool.mail;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import luckyclient.remote.api.serverOperation;
import luckyclient.remote.entity.ProjectProtocolTemplate;
import luckyclient.remote.entity.TaskScheduling;
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
 * @date 2018年3月1日
 */
public class MailSendInitialization {

    public static void sendMailInitialization(String subject, String content, String taskid, TaskScheduling taskScheduling, int[] taskCount, String time, String buildStatus, String restartStatus) {
        boolean isSend = false;
        if (null == taskCount) {
            isSend = true;
        } else {
            if (taskCount.length == 5 && null != taskScheduling) {
                Integer sendCondition = taskScheduling.getEmailSendCondition();
                // 用例全部成功了发送, casecount != casesuc
                if (null != sendCondition && 1 == sendCondition) {
                    if (taskCount[0] == taskCount[1]) {
                        isSend = true;
                    }
                }
                // 用例部分失败了发送
                if (null != sendCondition && 2 == sendCondition) {
                    if (taskCount[2] > 0) {
                        isSend = true;
                    }
                }
                // 全发
                if (null != sendCondition && 0 == sendCondition) {
                    isSend = true;
                }
            }
        }
        if (!isSend) {
            LogUtil.APP.info("当前任务不需要发送邮件或是推送通知!");
            return;
        }
        //向第三方推送消息
        MailSendInitialization msi= new MailSendInitialization();
        msi.pushMessage(taskScheduling, content, taskCount, time, buildStatus, restartStatus);

        String[] addresses = serverOperation.getEmailAddress(taskScheduling,taskid);
        Properties properties = SysConfig.getConfiguration();
        if (addresses != null) {
            LogUtil.APP.info("准备将测试结果发送邮件通知！请稍等...");
            //这个类主要是设置邮件
            MailSenderInfo mailInfo = new MailSenderInfo();
            //这个类主要来发送邮件
            SimpleMailSender sms = new SimpleMailSender();
            mailInfo.setMailServerHost(properties.getProperty("mail.smtp.ip"));
            mailInfo.setMailServerPort(properties.getProperty("mail.smtp.port"));
            mailInfo.setSslenable(properties.getProperty("mail.smtp.ssl.enable").equals("true"));
            mailInfo.setValidate(true);
            mailInfo.setUserName(properties.getProperty("mail.smtp.username"));
            //您的邮箱密码
            mailInfo.setPassword(properties.getProperty("mail.smtp.password"));
            mailInfo.setFromAddress(properties.getProperty("mail.smtp.username"));
            //标题
            mailInfo.setSubject(subject);
            //内容
            mailInfo.setContent(content);
            mailInfo.setToAddresses(addresses);
            //sms.sendHtmlMail(mailInfo);

            StringBuilder stringBuilder = new StringBuilder();
            for (String address : addresses) {
                stringBuilder.append(address).append(";");
            }
            String addressesmail = stringBuilder.toString();
            if (sms.sendHtmlMail(mailInfo)) {
                LogUtil.APP.info("给{}的测试结果通知邮件发送完成！", addressesmail);
            } else {
                LogUtil.APP.warn("给{}的测试结果通知邮件发送失败！", addressesmail);
            }
        } else {
            LogUtil.APP.info("当前任务不需要发送邮件通知！");
        }
    }

    private void pushMessage(TaskScheduling taskScheduling, String content, int[] taskCount, String time, String buildStatus, String restartStatus) {
        try {
            Map<String, String> headmsg = new HashMap<>(0);
            Properties properties = SysConfig.getConfiguration();
            LogUtil.APP.info("准备初始化第三方消息推送的数据...");

            String pushUrl = taskScheduling.getPushUrl();

            if(StrUtil.isNotBlank(pushUrl)){
                String ip = properties.getProperty("server.web.ip");
                String port = properties.getProperty("server.web.port");
                String path = properties.getProperty("server.web.path");

                ProjectProtocolTemplate ppt = new ProjectProtocolTemplate();
                ppt.setEncoding("utf-8");
                ppt.setTimeout(60);
                ppt.setIsResponseHead(1);
                ppt.setIsResponseCode(1);
                HttpClientTools hct = new HttpClientTools();

                Map<String, Object> parameters = new HashMap<>(0);
                if(null != taskCount){
                    content = "LuckyFrame自动化测试任务【" + taskScheduling.getSchedulingName() + "】执行结果\n" +
                            "自动构建状态：【" + buildStatus + "】\n" +
                            "自动重启TOMCAT状态：【" + restartStatus + "】\n" +
                            "本次任务预期执行用例共【" + taskCount[0] + "】条,耗時【" + time + "】\n" +
                            "用例执行成功：【" + taskCount[1] + "】\n" +
                            "用例执行失败：【" + taskCount[2] + "】\n" +
                            "用例有可能由于脚本原因未成功解析被锁定：【" + taskCount[3] + "】\n" +
                            "用例由于长时间未收到接口Response未执行完成：【" + taskCount[4] + "】\n" +
                            "详情请前往自动化测试平台查看！http://" + ip + ":" + port + path;
                }
                JSONObject contentJson = JSON.parseObject("{\"content\": \"" + content + "\"}");

                JSONObject atJson = JSON.parseObject("{\"atMobiles\": [],\"isAtAll\":true}");

                parameters.put("msgtype", "text");
                parameters.put("text", contentJson);
                parameters.put("at", atJson);
                LogUtil.APP.info("开始向第三方平台推送任务执行情况...");
                String result=hct.httpClientPostJson(pushUrl, parameters, headmsg, ppt);
                if(result.startsWith("使用HttpClient以JSON格式发送post请求出现异常")){
                    LogUtil.APP.error("向第三方平台推送任务执行数据失败...请检查原因");
                    LogUtil.APP.error("如出现：javax.net.ssl.SSLKeyException: RSA premaster secret error  异常，" +
                            "请找到你的jre环境的lib/ext/sunjce_provider.jar，把此包放到客户端编译的lib目录下。");
                } else {
                    LogUtil.APP.info("向第三方平台推送任务执行数据成功...");
                }
            }else{
                LogUtil.APP.warn("推送地址配置为空，取消第三方推送...");
            }

        } catch (Exception e) {
            LogUtil.APP.error("向第三方平台推送任务执行情况出现异常，请检查！", e);
        }
    }

}
