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
public class BuildingInitialization {
	
	public static String booleanBuildingOver(String[] buildname) throws InterruptedException{
		String buildresult = "Status:true"+" 项目全部构建成功！";
		int k;
		for(int i=0;i<300;i++){
			k=0;
			for(int j=0;j<buildname.length;j++){
				String result = JenkinsBuilding.buildingResult(buildname[i]);
				if(result.indexOf("alt=\"Failed\"")>-1){
					buildresult = "项目"+buildname[i]+"构建失败，自动化测试退出！";
					luckyclient.publicclass.LogUtil.APP.error("项目"+buildname[i]+"构建失败，自动化测试退出！");
					break;
				}else if(result.indexOf("alt=\"Success\"")>-1){
					k++;
				}
			}
			if(buildresult.indexOf("Status:true")<=-1){
				break;
			}
			luckyclient.publicclass.LogUtil.APP.info("正在检查构建中的项目(每6秒检查一次)。。。需要构建项目"+buildname.length+"个，目前成功"+k+"个");
			if(k==buildname.length){
				break;
			}			
			Thread.sleep(6000);
		}
		return buildresult;
	}

	public static String buildingRun(String tastid) throws InterruptedException{
		String result = "Status:true"+" 当前任务没有找到需要构建的项目！";
		try{
		String[] buildurl = LogOperation.getBuildName(tastid);
		
		if(buildurl!=null){
			luckyclient.publicclass.LogUtil.APP.info("准备将配置的测试项目进行构建！请稍等。。。。");
			for(int i=0;i<buildurl.length;i++){
				JenkinsBuilding.sendBuilding(buildurl[i]);
			}
			//等待构建检查
			Thread.sleep(10000);  
			result = booleanBuildingOver(buildurl);
		}else{
			luckyclient.publicclass.LogUtil.APP.info("当前任务没有找到需要构建的项目！");
		}
		}catch(Exception e){
			luckyclient.publicclass.LogUtil.APP.error("项目构建过程中出现异常");
			luckyclient.publicclass.LogUtil.APP.error(e);
			result = "项目构建过程中出现异常";
			return result;
		}
		return result;

	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
