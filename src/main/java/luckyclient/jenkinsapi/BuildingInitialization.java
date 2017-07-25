package luckyclient.jenkinsapi;

import luckyclient.dblog.LogOperation;

public class BuildingInitialization {
	
	public static String BooleanBuildingOver(String[] buildname) throws InterruptedException{
		String buildresult = "Status:true"+" 项目全部构建成功！";
		int k;
		for(int i=0;i<300;i++){
			k=0;
			for(int j=0;j<buildname.length;j++){
				String result = JenkinsBuilding.BuildingResult(buildname[i]);
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

	@SuppressWarnings("finally")
	public static String BuildingRun(String tastid) throws InterruptedException{
		String result = "Status:true"+" 当前任务没有找到需要构建的项目！";
		try{
		String[] buildname = LogOperation.GetBuildName(tastid);
		
		if(buildname!=null){
			luckyclient.publicclass.LogUtil.APP.info("准备将配置的测试项目进行构建！请稍等。。。。");
			for(int i=0;i<buildname.length;i++){
				JenkinsBuilding.sendBuilding(buildname[i], 0);
			}
			Thread.sleep(10000);  //等待构建检查
			result = BooleanBuildingOver(buildname);
		}else{
			luckyclient.publicclass.LogUtil.APP.info("当前任务没有找到需要构建的项目！");
		}
		}catch(Exception e){
			luckyclient.publicclass.LogUtil.APP.error("项目构建过程中出现异常");
			luckyclient.publicclass.LogUtil.APP.error(e);
			result = "项目构建过程中出现异常";
		}finally{
			return result;
		}

	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
