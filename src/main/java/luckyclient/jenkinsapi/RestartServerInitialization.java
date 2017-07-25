package luckyclient.jenkinsapi;

import luckyclient.dblog.LogOperation;

public class RestartServerInitialization {

	@SuppressWarnings("finally")
	public static String RestartServerRun(String tastid){
		String result = "Status:true"+" 重启命令执行成功！";
		try{
			String[] command = LogOperation.Getrestartcomm(tastid);
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
		}finally{
			return result;
		}

	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RestartServerRun("1460");
	}

}
