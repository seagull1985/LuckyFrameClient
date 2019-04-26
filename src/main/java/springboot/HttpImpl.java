package springboot;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

import luckyclient.publicclass.remoterinterface.HttpRequest;
import springboot.model.RunBatchCaseEntity;
import springboot.model.RunTaskEntity;
import springboot.model.WebDebugCaseEntity;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * @author seagull
 * @date 2018年7月27日 上午10:28:32
 */
@RestController
public class HttpImpl {

	private static final String os=System.getProperty("os.name").toLowerCase();
	/**
	 * 运行自动化任务
	 * @param req
	 * @param res
	 * @return
	 * @throws RemoteException
	 */
	@PostMapping("/runTask")
	private String runTask(HttpServletRequest req) throws RemoteException {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader reader = req.getReader();) {
			char[] buff = new char[1024];
			int len;
			while ((len = reader.read(buff)) != -1) {
				sb.append(buff, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		RunTaskEntity runTaskEntity = JSONObject.parseObject(sb.toString(), RunTaskEntity.class);
		
		luckyclient.publicclass.LogUtil.APP.info("启动任务模式测试程序...调度名称：【"+runTaskEntity.getSchedulingName()+"】  任务ID："+runTaskEntity.getTaskId());
		try{
			File file =new File(System.getProperty("user.dir")+runTaskEntity.getLoadPath()); 	   
			if  (!file .isDirectory())      
			{       
				luckyclient.publicclass.LogUtil.APP.error("客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】");
				return "客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】";
			}
			Runtime run = Runtime.getRuntime();
			StringBuffer sbf=new StringBuffer();
			sbf.append(runTaskEntity.getTaskId()).append(" ");
			sbf.append(runTaskEntity.getLoadPath());
			if(os.startsWith("win")){
				run.exec("cmd.exe /k start " + "task.cmd" +" "+ sbf.toString(), null,new File(System.getProperty("user.dir")+File.separator));				
			}else{
				Process ps = Runtime.getRuntime().exec(System.getProperty("user.dir")+File.separator+"task.sh"+ " " +sbf.toString());
		        ps.waitFor();
			}			
		} catch (Exception e) {		
			e.printStackTrace();
			luckyclient.publicclass.LogUtil.APP.error("启动任务模式测试程序异常！！！",e);
			return "启动任务模式测试程序异常！！！";
		}
		return "启动任务模式测试程序正常";
	}
	
	/**
	 * 运行单个用例
	 * @param req
	 * @param res
	 * @return
	 * @throws RemoteException
	 */
	@PostMapping("/runcase")
	@Deprecated
	private String runcase(HttpServletRequest req) throws RemoteException {
		StringBuilder sbd = new StringBuilder();
		try (BufferedReader reader = req.getReader();) {
			char[] buff = new char[1024];
			int len;
			while ((len = reader.read(buff)) != -1) {
				sbd.append(buff, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONObject jsonObject = JSONObject.parseObject(sbd.toString());
		String projectname = jsonObject.getString("projectname");
		String taskid = jsonObject.getString("taskid");
		String loadpath = jsonObject.getString("loadpath");
		String testCaseExternalId = jsonObject.getString("testCaseExternalId");
		String version = jsonObject.getString("version");
		luckyclient.publicclass.LogUtil.APP.info("启动单用例模式测试程序...测试项目："+projectname+"  任务ID："+taskid);
		luckyclient.publicclass.LogUtil.APP.info("测试用例编号："+testCaseExternalId+"  用例版本："+version);
		try{
			File file =new File(System.getProperty("user.dir")+loadpath); 	   
			if  (!file .isDirectory())      
			{   
				luckyclient.publicclass.LogUtil.APP.error("客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】");
				return "客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】";
			}
			Runtime run = Runtime.getRuntime();
			StringBuffer sb=new StringBuffer();
			sb.append(taskid).append(" ");
			sb.append(testCaseExternalId).append(" ");
			sb.append(version).append(" ");
			sb.append(loadpath);
			if(os.startsWith("win")){
				run.exec("cmd.exe /k start " + "task_onecase.cmd" + " " +sb.toString(), null,new File(System.getProperty("user.dir")+File.separator));				
			}else{
				Process ps = Runtime.getRuntime().exec(System.getProperty("user.dir")+File.separator+"task_onecase.sh"+ " " +sb.toString());
		        ps.waitFor();
			}	
		} catch (Exception e) {		
			e.printStackTrace();
			luckyclient.publicclass.LogUtil.APP.error("启动单用例模式测试程序异常！！！",e);
			return "启动单用例模式测试程序异常！！！";
		} 
		return "启动单用例模式测试程序正常";
	}
	
	/**
	 * 批量运行用例
	 * @param req
	 * @return
	 * @throws RemoteException
	 */
	@PostMapping("/runBatchCase")
	private String runBatchCase(HttpServletRequest req) throws RemoteException {
		StringBuilder sbd = new StringBuilder();
		try (BufferedReader reader = req.getReader();) {
			char[] buff = new char[1024];
			int len;
			while ((len = reader.read(buff)) != -1) {
				sbd.append(buff, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		RunBatchCaseEntity runBatchCaseEntity = JSONObject.parseObject(sbd.toString(), RunBatchCaseEntity.class);
		
		String projectname = runBatchCaseEntity.getProjectname();
		String taskid = runBatchCaseEntity.getTaskid();
		String loadpath = runBatchCaseEntity.getLoadpath();
		String batchcase = runBatchCaseEntity.getBatchcase();
		luckyclient.publicclass.LogUtil.APP.info("启动批量用例模式测试程序...测试项目："+projectname+"  任务ID："+taskid);
		luckyclient.publicclass.LogUtil.APP.info("批量测试用例："+batchcase);
		try{
			File file =new File(System.getProperty("user.dir")+loadpath); 	   
			if  (!file .isDirectory())      
			{    
				luckyclient.publicclass.LogUtil.APP.error("客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】");
				return "客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】";
			}
			Runtime run = Runtime.getRuntime();
			StringBuffer sb=new StringBuffer();
			sb.append(taskid).append(" ");
			sb.append(batchcase).append(" ");
			sb.append(loadpath);
			if(os.startsWith("win")){
				run.exec("cmd.exe /k start " + "task_batch.cmd" + " " +sb.toString(), null,new File(System.getProperty("user.dir")+File.separator));				
			}else{
				Process ps = Runtime.getRuntime().exec(System.getProperty("user.dir")+File.separator+"task_batch.sh"+ " " +sb.toString());
		        ps.waitFor();
			}		
		} catch (Exception e) {		
			e.printStackTrace();
			luckyclient.publicclass.LogUtil.APP.error("启动批量用例模式测试程序异常！！！",e);
			return "启动批量用例模式测试程序异常！！！";
		} 
		return "启动批量用例模式测试程序正常";
	}
	
	/**
	 * web界面调度接口
	 * @param req
	 * @return
	 * @throws RemoteException
	 */
	@PostMapping("/webdebugcase")
	private String webdebugcase(HttpServletRequest req) throws RemoteException {
		StringBuilder sbd = new StringBuilder();
		try (BufferedReader reader = req.getReader();) {
			char[] buff = new char[1024];
			int len;
			while ((len = reader.read(buff)) != -1) {
				sbd.append(buff, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		WebDebugCaseEntity webDebugCaseEntity = JSONObject.parseObject(sbd.toString(), WebDebugCaseEntity.class);
		luckyclient.publicclass.LogUtil.APP.info("Web端调试用例ID："+webDebugCaseEntity.getCaseId()+" 发起人ID："+webDebugCaseEntity.getUserId());
		try{
			File file =new File(System.getProperty("user.dir")+webDebugCaseEntity.getLoadpath()); 	   
			if  (!file .isDirectory())      
			{    
				luckyclient.publicclass.LogUtil.APP.error("客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】");
				return "客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】";
			}
			Runtime run = Runtime.getRuntime();
			StringBuffer sb=new StringBuffer();
			sb.append(webDebugCaseEntity.getCaseId()).append(" ");
			sb.append(webDebugCaseEntity.getUserId()).append(" ");
			sb.append(webDebugCaseEntity.getLoadpath());
			if(os.startsWith("win")){
				run.exec("cmd.exe /k start " + "web_debugcase.cmd" + " " +sb.toString(), null,new File(System.getProperty("user.dir")+File.separator));			
			}else{
				Process ps = Runtime.getRuntime().exec(System.getProperty("user.dir")+File.separator+"web_debugcase.sh"+ " " +sb.toString());
	            ps.waitFor();  
			}	
		} catch (Exception e) {		
			e.printStackTrace();
			luckyclient.publicclass.LogUtil.APP.error("启动Web调试模式测试程序异常！！！",e);
			return "启动Web调试模式测试程序异常！！！";
		} 
		return "启动Web调试模式测试程序正常";
	}
	
	/**
	 * 获取客户端本地日志
	 * @param req
	 * @return
	 * @throws RemoteException
	 */
	@GetMapping("/getLogdDetail")
	private String getLogdDetail(HttpServletRequest req) throws RemoteException{
		String fileName=req.getParameter("filename");
		String ctxPath = System.getProperty("user.dir")+File.separator+"log";
		String downLoadPath = ctxPath +File.separator+ fileName;

		String str = "";
		InputStreamReader isr=null;
		try {
			isr = new InputStreamReader(new FileInputStream(downLoadPath), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			luckyclient.publicclass.LogUtil.APP.error("读取日志路径错误，请检查客户端日志路径是否存在!downLoadPath: "+downLoadPath,e);
			return "读取日志路径错误，请检查客户端日志路径是否存在!downLoadPath: "+downLoadPath;
		}
		BufferedReader bos = new BufferedReader(isr);
		StringBuffer sb = new StringBuffer();
		try {
			while ((str = bos.readLine()) != null)
			{
				sb.append(str).append("##n##");
			}
			bos.close();
			luckyclient.publicclass.LogUtil.APP.info("服务端读取本地日志成功!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			luckyclient.publicclass.LogUtil.APP.error("客户端转BufferedReader失败！请检查原因！",e);
			return "客户端转BufferedReader失败！请检查原因！";
		}
		return sb.toString();
	}
	
	/**
	 * 获取错误截图
	 * @param req
	 * @return
	 * @throws RemoteException
	 */
	@GetMapping("/getLogImg")
	private byte[] getLogImg(HttpServletRequest req,HttpServletResponse res) throws RemoteException{
		String imgName=req.getParameter("imgName");
		String ctxPath = System.getProperty("user.dir")+File.separator+"log"+File.separator+"ScreenShot";
		String downLoadPath = ctxPath+File.separator+imgName;
        byte[] b = null;
        try {
            File file = new File(downLoadPath);
            b = new byte[(int) file.length()];
            BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
            is.read(b);
            is.close();
        	luckyclient.publicclass.LogUtil.APP.info("服务端获取本地图片："+downLoadPath);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            luckyclient.publicclass.LogUtil.APP.error("此文件不存在，请检查："+downLoadPath,e);
            return b;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return b;
        }     
        return b;
	}
	
	@PostMapping("/uploadjar")
	private String uploadjar(HttpServletRequest req,HttpServletResponse res, HttpSession session,@RequestParam("jarfile") MultipartFile jarfile) throws IOException, ServletException{
		if (!jarfile.isEmpty()){
            if (!FilenameUtils.getExtension(jarfile.getOriginalFilename())
                    .equalsIgnoreCase("jar")) {
            	luckyclient.publicclass.LogUtil.APP.error("文件格式后续不是.jar，上传失败");
                return "文件格式后续不是.jar，上传失败";
            }
		}else{
			luckyclient.publicclass.LogUtil.APP.error("上传文件为空，请检查！");
            return "上传文件为空，请检查！";
		}

		String name = jarfile.getOriginalFilename();
		String loadpath = req.getParameter("loadpath");
		String path = System.getProperty("user.dir")+loadpath;
		if  (!new File(path) .isDirectory())      
		{    
			luckyclient.publicclass.LogUtil.APP.error("客户端测试驱动桩路径不存在，请检查【"+path+"】");
			return "客户端测试驱动桩路径不存在，请检查【"+path+"】";
		}	
		String pathName = path +File.separator+ name;

		File file = new File(pathName);
        try { 
            if (file.exists()){
            	file.deleteOnExit();
            }
            file.createNewFile();
            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));
            byte[] jarfileByte = jarfile.getBytes();
            os.write(jarfileByte);
            os.flush();
            os.close();
            luckyclient.publicclass.LogUtil.APP.info("上传JAR包【"+name+"】到客户端驱动目录【"+file.getAbsolutePath()+"】成功!");
            return "上传JAR包【"+name+"】到客户端驱动目录【"+file.getAbsolutePath()+"】成功!";
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            luckyclient.publicclass.LogUtil.APP.error("客户端未找到正确路径或文件，上传失败！文件路径名称："+pathName,e);
            return "客户端未找到正确路径或文件，上传失败！文件路径名称："+pathName;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            luckyclient.publicclass.LogUtil.APP.error("客户端IOExceptiona或是未找到驱动路径！文件路径名称："+pathName,e);
            return "客户端IOExceptiona或是未找到驱动路径！文件路径名称："+pathName;
        }
	}
	
	/**
	 * 检查客户端心跳
	 * @param req
	 * @return
	 * @throws RemoteException
	 */
	@GetMapping("/getclientstatus")
	private String getClientStatus(HttpServletRequest req) throws RemoteException{
		Properties properties = luckyclient.publicclass.SysConfig.getConfiguration();
		String verison=properties.getProperty("client.verison");
		return "{\"status\":\"success\",\"version\":\""+verison+"\"}";
	}
	
	public static boolean checkhostnet() {
		luckyclient.publicclass.LogUtil.APP.info("检查客户端配置中,请稍后......");
		Properties properties = luckyclient.publicclass.SysConfig.getConfiguration();
		String version=properties.getProperty("client.verison");
		String webip=properties.getProperty("server.web.ip");
		int webport=Integer.valueOf(properties.getProperty("server.web.port"));
        try {
        	String result = HttpRequest.loadJSON(webip+"/openGetApi/clientGetServerVersion.do");
        	if(version.equals(result)){
            	luckyclient.publicclass.LogUtil.APP.info("客户端访问Web端配置："+webip+":"+webport+"   检测通过......");
        	}else{
        		luckyclient.publicclass.LogUtil.APP.error("客户端版本："+version);
        		luckyclient.publicclass.LogUtil.APP.error("服务端版本："+result);
        		luckyclient.publicclass.LogUtil.APP.error("客户端与服务端版本不一致，有可能会导致未知问题，请检查...");
        	}

        } catch (Exception e) {
        	luckyclient.publicclass.LogUtil.APP.error("客户端配置检测异常，请确认您项目根目录下的客户端配置文件(sys_config.properties)是否已经正确配置。",e);
            return false;
        }
        return true;
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		checkhostnet();
	}

}
