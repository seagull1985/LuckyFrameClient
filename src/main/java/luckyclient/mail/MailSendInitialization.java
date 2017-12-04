package luckyclient.mail;

import java.util.Properties;

import luckyclient.dblog.LogOperation;

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
public class MailSendInitialization {
	
	public static void sendMailInitialization(String subject,String content,String taskid){
		String[] addresses = LogOperation.getEmailAddress(taskid);
		Properties properties = luckyclient.publicclass.SysConfig.getConfiguration();
		if(addresses!=null){
			 luckyclient.publicclass.LogUtil.APP.info("准备将测试结果发送邮件通知！请稍等。。。。");
			 //这个类主要是设置邮件   
		      MailSenderInfo mailInfo = new MailSenderInfo(); 
		         //这个类主要来发送邮件   
		      SimpleMailSender sms = new SimpleMailSender();   
		      mailInfo.setMailServerHost(properties.getProperty("mail.smtp.ip"));    
		      mailInfo.setMailServerPort(properties.getProperty("mail.smtp.port"));    
		      mailInfo.setValidate(true);    
		      mailInfo.setUserName(properties.getProperty("mail.smtp.username"));    
		      mailInfo.setPassword(properties.getProperty("mail.smtp.password"));//您的邮箱密码    
		      mailInfo.setFromAddress(properties.getProperty("mail.smtp.username"));    
		      mailInfo.setSubject(subject);    //标题
		      mailInfo.setContent(content);     //内容
		      mailInfo.setToAddresses(addresses);
			  sms.sendHtmlMail(mailInfo);
			  String addressesmail = "";
			  for(int i=0;i<addresses.length;i++){
				  addressesmail =  addressesmail+addresses[i]+";";
			  }
			  luckyclient.publicclass.LogUtil.APP.info("给"+addressesmail+"的测试结果通知邮件发送完成！");
		}else{
			luckyclient.publicclass.LogUtil.APP.info("当前任务不需要发送邮件通知！");
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
