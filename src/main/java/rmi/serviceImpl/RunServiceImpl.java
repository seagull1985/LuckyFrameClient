package rmi.serviceImpl;

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



//此为远程对象的实现类，须继承UnicastRemoteObject
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
	public String runtask(RunTaskEntity task) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("启动任务模式测试程序...测试项目："+task.getProjectname()+"  任务ID："+task.getTaskid());
		try{
			Runtime run = Runtime.getRuntime();
			run.exec("cmd.exe /k start " + "task.cmd" +" "+ task.getTaskid(), null,new File(System.getProperty("user.dir")+"\\"));
			
		} catch (Exception e) {		
			e.printStackTrace();
			return "启动任务模式测试程序异常！！！";
		} 
		return "启动任务模式测试程序正常";
	}
	
	@Override
	public String runcase(RunCaseEntity onecase) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("启动单用例模式测试程序...测试项目："+onecase.getProjectname()+"  任务ID："+onecase.getTaskid());
		System.out.println("测试用例编号："+onecase.getTestCaseExternalId()+"  用例版本："+onecase.getVersion());
		try{
			Runtime run = Runtime.getRuntime();
			StringBuffer sb=new StringBuffer();
			sb.append(onecase.getTaskid()).append(" ");
			sb.append(onecase.getTestCaseExternalId()).append(" ");
			sb.append(onecase.getVersion());
			run.exec("cmd.exe /k start " + "task_onecase.cmd" + " " +sb.toString(), null,new File(System.getProperty("user.dir")+"\\"));			
		} catch (Exception e) {		
			e.printStackTrace();
			return "启动单用例模式测试程序异常！！！";
		} 
		return "启动单用例模式测试程序正常";
	}
	
	@Override
	public String runbatchcase(RunBatchCaseEntity batchcase) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("启动批量用例模式测试程序...测试项目："+batchcase.getProjectname()+"  任务ID："+batchcase.getTaskid());
		System.out.println("批量测试用例："+batchcase.getBatchcase());
		try{
			Runtime run = Runtime.getRuntime();
			StringBuffer sb=new StringBuffer();
			sb.append(batchcase.getTaskid()).append(" ");
			sb.append(batchcase.getBatchcase());
			System.out.println(sb.toString());
			run.exec("cmd.exe /k start " + "task_batch.cmd" + " " +sb.toString(), null,new File(System.getProperty("user.dir")+"\\"));		
		} catch (Exception e) {		
			e.printStackTrace();
			return "启动批量用例模式测试程序异常！！！";
		} 
		return "启动批量用例模式测试程序正常";
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
	public String uploadjar(byte[] fileContent,String name) throws RemoteException{
		String path = System.getProperty("user.dir")+"\\lib\\";
		String pathName = path + name;
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
	
	
	public static void main(String[] args) throws RemoteException {
	}
}
