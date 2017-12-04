package luckyclient.jenkinsapi;

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
public class RestartServerInitialization {

	@SuppressWarnings("finally")
	public static String restartServerRun(String tastid){
		String result = "Status:true"+" 重启命令执行成功！";
		try{
			String[] command = LogOperation.getrestartcomm(tastid);
			if(command!=null){
				luckyclient.publicclass.LogUtil.APP.info("准备重启指定的TOMCAT！请稍等。。。参数个数："+command.length);
				if(command.length==5){
					luckyclient.publicclass.LogUtil.APP.info("开始调用重启TOMCAT方法。。。参数0："+command[0]+" 参数1："+command[1]
							+" 参数2："+command[2]+" 参数3："+command[3]+" 参数4："+command[4]);
					result = RmtShellExecutor.sshShell(command[0], command[1], command[2], Integer.valueOf(command[3]), command[4]);
				}else{
					luckyclient.publicclass.LogUtil.APP.error("重启TOMCAT命令行参数出现异常，请检查配置信息！");
					result = "重启TOMCAT命令行参数出现异常，请检查配置信息！";
				}				
			}else{
				result = "Status:true"+" 当前任务没有找到需要重启的TOMCAT命令！";
				luckyclient.publicclass.LogUtil.APP.info("当前任务没有指定需要重启TOMCAT！");
			}
		}catch(Throwable e){
			luckyclient.publicclass.LogUtil.APP.error("重启TOMCAT过程中出现异常");
			luckyclient.publicclass.LogUtil.APP.error(e.getMessage(),e);
			result = "重启TOMCAT过程中出现异常";
			return result;
		}
		return result;

	}
	
	public static void main(String[] args) {

	}

}
