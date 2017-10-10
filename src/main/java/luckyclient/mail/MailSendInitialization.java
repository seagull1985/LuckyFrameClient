package luckyclient.mail;

import java.util.Properties;

import luckyclient.dblog.LogOperation;

public class MailSendInitialization {
	
	public static void SendMailInitialization(String subject,String content,String taskid){
		String[] addresses = LogOperation.GetEmailAddress(taskid);
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
		int[] taskcount = {79,78,1,0,0};
		String test = HtmlMail.HtmlContentFormat(taskcount, "220", "Status:true 当前任务没有找到需要构建的项目！", "Status:true 当前任务没有找到需要重启的TOMCAT命令！", "0小时1分1秒","testtask");
		MailSendInitialization.SendMailInitialization("test", test, "220");
	}

}
