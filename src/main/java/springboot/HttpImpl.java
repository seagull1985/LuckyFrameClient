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

	/**
	 * 运行自动化任务
	 * @param req
	 * @param res
	 * @return
	 * @throws RemoteException
	 */
	@PostMapping("/runtask")
	public String runtask(HttpServletRequest req) throws RemoteException {
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
		JSONObject jsonObject = JSONObject.parseObject(sb.toString());
		String projectname = jsonObject.getString("projectname");
		String taskid = jsonObject.getString("taskid");
		String loadpath = jsonObject.getString("loadpath");
		System.out.println("启动任务模式测试程序...测试项目："+projectname+"  任务ID："+taskid);
		try{
			File file =new File(System.getProperty("user.dir")+loadpath); 	   
			if  (!file .isDirectory())      
			{       
				System.out.println("客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】");
				return "客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】";
			}
			Runtime run = Runtime.getRuntime();
			StringBuffer sbf=new StringBuffer();
			sbf.append(taskid).append(" ");
			sbf.append(loadpath);
			run.exec("cmd.exe /k start " + "task.cmd" +" "+ sbf.toString(), null,new File(System.getProperty("user.dir")+"\\"));
			
		} catch (Exception e) {		
			e.printStackTrace();
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
	public String runcase(HttpServletRequest req) throws RemoteException {
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
		System.out.println("启动单用例模式测试程序...测试项目："+projectname+"  任务ID："+taskid);
		System.out.println("测试用例编号："+testCaseExternalId+"  用例版本："+version);
		try{
			File file =new File(System.getProperty("user.dir")+loadpath); 	   
			if  (!file .isDirectory())      
			{   
				System.out.println("客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】");
				return "客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】";
			}
			Runtime run = Runtime.getRuntime();
			StringBuffer sb=new StringBuffer();
			sb.append(taskid).append(" ");
			sb.append(testCaseExternalId).append(" ");
			sb.append(version).append(" ");
			sb.append(loadpath);
			run.exec("cmd.exe /k start " + "task_onecase.cmd" + " " +sb.toString(), null,new File(System.getProperty("user.dir")+"\\"));			
		} catch (Exception e) {		
			e.printStackTrace();
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
	@PostMapping("/runbatchcase")
	public String runbatchcase(HttpServletRequest req) throws RemoteException {
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
		String batchcase = jsonObject.getString("batchcase");
		System.out.println("启动批量用例模式测试程序...测试项目："+projectname+"  任务ID："+taskid);
		System.out.println("批量测试用例："+batchcase);
		try{
			File file =new File(System.getProperty("user.dir")+loadpath); 	   
			if  (!file .isDirectory())      
			{    
				System.out.println("客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】");
				return "客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】";
			}
			Runtime run = Runtime.getRuntime();
			StringBuffer sb=new StringBuffer();
			sb.append(taskid).append(" ");
			sb.append(batchcase).append(" ");
			sb.append(loadpath);
			System.out.println(sb.toString());
			run.exec("cmd.exe /k start " + "task_batch.cmd" + " " +sb.toString(), null,new File(System.getProperty("user.dir")+"\\"));		
		} catch (Exception e) {		
			e.printStackTrace();
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
	public String webdebugcase(HttpServletRequest req) throws RemoteException {
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
		String sign = jsonObject.getString("sign");
		String executor = jsonObject.getString("executor");
		String loadpath = jsonObject.getString("loadpath");
		System.out.println("Web端调试用例："+sign+" 发起人："+executor);
		try{
			File file =new File(System.getProperty("user.dir")+loadpath); 	   
			if  (!file .isDirectory())      
			{    
				System.out.println("客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】");
				return "客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】";
			}
			Runtime run = Runtime.getRuntime();
			StringBuffer sb=new StringBuffer();
			sb.append(sign).append(" ");
			sb.append(executor).append(" ");
			sb.append(loadpath);
			run.exec("cmd.exe /k start " + "web_debugcase.cmd" + " " +sb.toString(), null,new File(System.getProperty("user.dir")+"\\"));			
		} catch (Exception e) {		
			e.printStackTrace();
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
	@GetMapping("/getlogdetail")
	public String getlogdetail(HttpServletRequest req) throws RemoteException{
		String fileName=req.getParameter("filename");
		String ctxPath = System.getProperty("user.dir")+"\\log\\";
		String downLoadPath = ctxPath + fileName;

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
			System.out.println("服务端读取本地日志成功!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	@GetMapping("/getlogimg")
	public byte[] getlogimg(HttpServletRequest req,HttpServletResponse res) throws RemoteException{
		String imgName=req.getParameter("imgName");
		String ctxPath = System.getProperty("user.dir")+"\\log\\ScreenShot\\";
		String downLoadPath = ctxPath+imgName;
        byte[] b = null;
        try {
            File file = new File(downLoadPath);
            b = new byte[(int) file.length()];
            BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
            is.read(b);
            is.close();
            System.out.println("服务端获取本地图片："+downLoadPath);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return b;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return b;
        }     
        return b;
	}
	
	@PostMapping("/uploadjar")
	public String uploadjar(HttpServletRequest req,HttpServletResponse res, HttpSession session,@RequestParam("jarfile") MultipartFile jarfile) throws IOException, ServletException{
		if (!jarfile.isEmpty()){
            if (!FilenameUtils.getExtension(jarfile.getOriginalFilename())
                    .equalsIgnoreCase("jar")) {
                return "文件格式后续不是.jar，上传失败";
            }
		}else{
            return "上传文件为空，请检查！";
		}

		String name = jarfile.getOriginalFilename();
		String loadpath = req.getParameter("loadpath");
		String path = System.getProperty("user.dir")+loadpath;
		if  (!new File(path) .isDirectory())      
		{    
			System.out.println("客户端测试驱动桩路径不存在，请检查【"+path+"】");
			return "客户端测试驱动桩路径不存在，请检查【"+path+"】";
		}	
		String pathName = path +"\\"+ name;
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
            System.out.println("上传JAR包【"+name+"】到客户端驱动目录【"+path+"】成功!");
            return "上传JAR包【"+name+"】到客户端驱动目录【"+path+"】成功!";
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "客户端未找到正确路径或文件，上传失败！";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "客户端IOException";
        }
	}
	
	/**
	 * 检查客户端心跳
	 * @param req
	 * @return
	 * @throws RemoteException
	 */
	@GetMapping("/getclientstatus")
	public String getClientStatus(HttpServletRequest req) throws RemoteException{
		return "success";
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
