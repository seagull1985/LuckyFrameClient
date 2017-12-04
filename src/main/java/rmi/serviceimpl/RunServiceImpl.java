package rmi.serviceimpl;

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
import java.rmi.server.UnicastRemoteObject;

import rmi.model.RunBatchCaseEntity;
import rmi.model.RunCaseEntity;
import rmi.model.RunTaskEntity;
import rmi.service.RunService;


/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * 此为远程对象的实现类，须继承UnicastRemoteObject
 * @author： seagull
 * @date 2017年12月1日 上午9:29:40
 * 
 */
public class RunServiceImpl extends UnicastRemoteObject implements RunService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RunServiceImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String runtask(RunTaskEntity task,String loadpath) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("启动任务模式测试程序...测试项目："+task.getProjectname()+"  任务ID："+task.getTaskid());
		try{
			File file =new File(System.getProperty("user.dir")+loadpath); 	   
			if  (!file .isDirectory())      
			{       
				System.out.println("客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】");
				return "客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】";
			}
			Runtime run = Runtime.getRuntime();
			StringBuffer sb=new StringBuffer();
			sb.append(task.getTaskid()).append(" ");
			sb.append(loadpath);
			run.exec("cmd.exe /k start " + "task.cmd" +" "+ sb.toString(), null,new File(System.getProperty("user.dir")+"\\"));
			
		} catch (Exception e) {		
			e.printStackTrace();
			return "启动任务模式测试程序异常！！！";
		} 
		return "启动任务模式测试程序正常";
	}
	
	@Override
	public String runcase(RunCaseEntity onecase,String loadpath) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("启动单用例模式测试程序...测试项目："+onecase.getProjectname()+"  任务ID："+onecase.getTaskid());
		System.out.println("测试用例编号："+onecase.getTestCaseExternalId()+"  用例版本："+onecase.getVersion());
		try{
			File file =new File(System.getProperty("user.dir")+loadpath); 	   
			if  (!file .isDirectory())      
			{   
				System.out.println("客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】");
				return "客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】";
			}
			Runtime run = Runtime.getRuntime();
			StringBuffer sb=new StringBuffer();
			sb.append(onecase.getTaskid()).append(" ");
			sb.append(onecase.getTestCaseExternalId()).append(" ");
			sb.append(onecase.getVersion()).append(" ");
			sb.append(loadpath);
			run.exec("cmd.exe /k start " + "task_onecase.cmd" + " " +sb.toString(), null,new File(System.getProperty("user.dir")+"\\"));			
		} catch (Exception e) {		
			e.printStackTrace();
			return "启动单用例模式测试程序异常！！！";
		} 
		return "启动单用例模式测试程序正常";
	}
	
	@Override
	public String runbatchcase(RunBatchCaseEntity batchcase,String loadpath) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("启动批量用例模式测试程序...测试项目："+batchcase.getProjectname()+"  任务ID："+batchcase.getTaskid());
		System.out.println("批量测试用例："+batchcase.getBatchcase());
		try{
			File file =new File(System.getProperty("user.dir")+loadpath); 	   
			if  (!file .isDirectory())      
			{    
				System.out.println("客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】");
				return "客户端测试驱动桩路径不存在，请检查【"+file.getPath()+"】";
			}
			Runtime run = Runtime.getRuntime();
			StringBuffer sb=new StringBuffer();
			sb.append(batchcase.getTaskid()).append(" ");
			sb.append(batchcase.getBatchcase()).append(" ");
			sb.append(loadpath);
			System.out.println(sb.toString());
			run.exec("cmd.exe /k start " + "task_batch.cmd" + " " +sb.toString(), null,new File(System.getProperty("user.dir")+"\\"));		
		} catch (Exception e) {		
			e.printStackTrace();
			return "启动批量用例模式测试程序异常！！！";
		} 
		return "启动批量用例模式测试程序正常";
	}
	
	@Override
	public String webdebugcase(String sign,String executor,String loadpath) throws RemoteException {
		// TODO Auto-generated method stub
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
	 * 获取客户端日志
	 * 
	 * @param request
	 * @param response
	 * @param storeName
	 * @param contentType
	 * @param realName
	 * @throws Exception
	 */
	@Override
	public String getlogdetail(String storeName) throws RemoteException{
		BufferedReader bos = null;
		String ctxPath = System.getProperty("user.dir")+"\\log\\";
		String downLoadPath = ctxPath + storeName;

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
		bos = new BufferedReader(isr);
		StringBuffer sb = new StringBuffer();
		try {
			while ((str = bos.readLine()) != null)
			{
				sb.append(str).append("\n");
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
	 * 获取客户端截图
	 * 
	 * @param request
	 * @param response
	 * @param storeName
	 * @param contentType
	 * @param realName
	 * @throws Exception
	 */
	@Override
	public byte[] getlogimg(String imgName) throws RemoteException{
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
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
	}
	
	/**
	 * 上传JAR包
	 * 
	 * @param request
	 * @param response
	 * @param storeName
	 * @param contentType
	 * @param realName
	 * @throws Exception
	 */
	@Override
	public String uploadjar(byte[] fileContent,String name,String loadpath) throws RemoteException{
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
            os.write(fileContent);
            os.flush();
            os.close();
            System.out.println("服务端上传JAR包("+name+")到本地客户端lib目录成功!");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "客户端未找到正确路径或文件，上传失败！";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "客户端IOException";
        }
         return "上传"+name+"至客户端成功！";
	}
	
	@Override
	public String getClientStatus() throws RemoteException{
		return "success";
	}
	
	public static void main(String[] args) throws RemoteException {
	}
}
