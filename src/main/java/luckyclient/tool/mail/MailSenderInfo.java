package luckyclient.tool.mail;

import java.security.GeneralSecurityException;
import java.util.Properties;

import com.sun.mail.util.MailSSLSocketFactory;

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
 */
public class MailSenderInfo {
    /**
     * 发送邮件的服务器的IP和端口
     */
    private String mailServerHost;
    private String mailServerPort = "25";
    /**
     * 邮件发送者的地址
     */
    private String fromAddress;
    /**
     * 邮件接收者的地址
     */
    private String toAddress;
    /**
     * 多个邮件接收者的地址
     */
    private String[] toAddresses;
    /**
     * 登陆邮件发送服务器的用户名和密码
     */
    private String userName;
    private String password;
    /**
     * 是否使用SSL连接
     */
    private boolean sslenable = true;
    /**
     * 是否需要身份验证
     */
    private boolean validate = false;

    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 邮件的文本内容
     */
    private String content;
    /**
     * 邮件附件的文件名
     */
    private String[] attachFileNames;

    /**
     * 获得邮件会话属性
     */
    public Properties getProperties() {
        Properties p = new Properties();
        try {
            p.put("mail.smtp.host", this.mailServerHost);
            p.put("mail.smtp.port", this.mailServerPort);
            p.put("mail.smtp.auth", validate ? "true" : "false");
            if (this.sslenable) {
                p.put("mail.smtp.ssl.enable", "true");
                MailSSLSocketFactory sf = new MailSSLSocketFactory();
                sf.setTrustAllHosts(true);
                p.put("mail.smtp.ssl.socketFactory", sf);
            }
        } catch (GeneralSecurityException e) {
            LogUtil.APP.error("获得邮件会话属性失败或异常: ",e);
            return null;
        }
        return p;
    }

    public String getMailServerHost() {
        return mailServerHost;
    }

    public void setMailServerHost(String mailServerHost) {
        this.mailServerHost = mailServerHost;
    }

    public String getMailServerPort() {
        return mailServerPort;
    }

    public void setMailServerPort(String mailServerPort) {
        this.mailServerPort = mailServerPort;
    }

    public boolean isSslenable() {
        return this.sslenable;
    }

    public void setSslenable(boolean sslenable) {
        this.sslenable = sslenable;
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public String[] getAttachFileNames() {
        return attachFileNames;
    }

    public void setAttachFileNames(String[] fileNames) {
        this.attachFileNames = fileNames;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String[] getToAddresses() {
        return toAddresses;
    }

    public void setToAddresses(String[] toAddresses) {
        this.toAddresses = toAddresses;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String textContent) {
        this.content = textContent;
    }


}
