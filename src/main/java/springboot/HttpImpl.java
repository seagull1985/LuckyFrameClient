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
import java.net.InetSocketAddress;
import java.net.Socket;
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

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 * @author seagull
 * @date 2018��7��27�� ����10:28:32
 */
@RestController
public class HttpImpl {

	private static final String os=System.getProperty("os.name").toLowerCase();
	/**
	 * �����Զ�������
	 * @param req
	 * @param res
	 * @return
	 * @throws RemoteException
	 */
	@PostMapping("/runtask")
	private String runtask(HttpServletRequest req) throws RemoteException {
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
		luckyclient.publicclass.LogUtil.APP.info("��������ģʽ���Գ���...������Ŀ��"+projectname+"  ����ID��"+taskid);
		try{
			File file =new File(System.getProperty("user.dir")+loadpath); 	   
			if  (!file .isDirectory())      
			{       
				luckyclient.publicclass.LogUtil.APP.error("�ͻ��˲�������׮·�������ڣ����顾"+file.getPath()+"��");
				return "�ͻ��˲�������׮·�������ڣ����顾"+file.getPath()+"��";
			}
			Runtime run = Runtime.getRuntime();
			StringBuffer sbf=new StringBuffer();
			sbf.append(taskid).append(" ");
			sbf.append(loadpath);
			if(os.startsWith("win")){
				run.exec("cmd.exe /k start " + "task.cmd" +" "+ sbf.toString(), null,new File(System.getProperty("user.dir")+File.separator));				
			}else{
				Process ps = Runtime.getRuntime().exec(System.getProperty("user.dir")+File.separator+"task.sh"+ " " +sbf.toString());
		        ps.waitFor();
			}			
		} catch (Exception e) {		
			e.printStackTrace();
			luckyclient.publicclass.LogUtil.APP.error("��������ģʽ���Գ����쳣������",e);
			return "��������ģʽ���Գ����쳣������";
		}
		return "��������ģʽ���Գ�������";
	}
	
	/**
	 * ���е�������
	 * @param req
	 * @param res
	 * @return
	 * @throws RemoteException
	 */
	@PostMapping("/runcase")
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
		luckyclient.publicclass.LogUtil.APP.info("����������ģʽ���Գ���...������Ŀ��"+projectname+"  ����ID��"+taskid);
		luckyclient.publicclass.LogUtil.APP.info("����������ţ�"+testCaseExternalId+"  �����汾��"+version);
		try{
			File file =new File(System.getProperty("user.dir")+loadpath); 	   
			if  (!file .isDirectory())      
			{   
				luckyclient.publicclass.LogUtil.APP.error("�ͻ��˲�������׮·�������ڣ����顾"+file.getPath()+"��");
				return "�ͻ��˲�������׮·�������ڣ����顾"+file.getPath()+"��";
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
			luckyclient.publicclass.LogUtil.APP.error("����������ģʽ���Գ����쳣������",e);
			return "����������ģʽ���Գ����쳣������";
		} 
		return "����������ģʽ���Գ�������";
	}
	
	/**
	 * ������������
	 * @param req
	 * @return
	 * @throws RemoteException
	 */
	@PostMapping("/runbatchcase")
	private String runbatchcase(HttpServletRequest req) throws RemoteException {
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
		luckyclient.publicclass.LogUtil.APP.info("������������ģʽ���Գ���...������Ŀ��"+projectname+"  ����ID��"+taskid);
		luckyclient.publicclass.LogUtil.APP.info("��������������"+batchcase);
		try{
			File file =new File(System.getProperty("user.dir")+loadpath); 	   
			if  (!file .isDirectory())      
			{    
				luckyclient.publicclass.LogUtil.APP.error("�ͻ��˲�������׮·�������ڣ����顾"+file.getPath()+"��");
				return "�ͻ��˲�������׮·�������ڣ����顾"+file.getPath()+"��";
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
			luckyclient.publicclass.LogUtil.APP.error("������������ģʽ���Գ����쳣������",e);
			return "������������ģʽ���Գ����쳣������";
		} 
		return "������������ģʽ���Գ�������";
	}
	
	/**
	 * web������Ƚӿ�
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
		JSONObject jsonObject = JSONObject.parseObject(sbd.toString());
		String sign = jsonObject.getString("sign");
		String executor = jsonObject.getString("executor");
		String loadpath = jsonObject.getString("loadpath");
		luckyclient.publicclass.LogUtil.APP.info("Web�˵���������"+sign+" �����ˣ�"+executor);
		try{
			File file =new File(System.getProperty("user.dir")+loadpath); 	   
			if  (!file .isDirectory())      
			{    
				luckyclient.publicclass.LogUtil.APP.error("�ͻ��˲�������׮·�������ڣ����顾"+file.getPath()+"��");
				return "�ͻ��˲�������׮·�������ڣ����顾"+file.getPath()+"��";
			}
			Runtime run = Runtime.getRuntime();
			StringBuffer sb=new StringBuffer();
			sb.append(sign).append(" ");
			sb.append(executor).append(" ");
			sb.append(loadpath);
			if(os.startsWith("win")){
				run.exec("cmd.exe /k start " + "web_debugcase.cmd" + " " +sb.toString(), null,new File(System.getProperty("user.dir")+File.separator));			
			}else{
				Process ps = Runtime.getRuntime().exec(System.getProperty("user.dir")+File.separator+"web_debugcase.sh"+ " " +sb.toString());
	            ps.waitFor();  
			}	
		} catch (Exception e) {		
			e.printStackTrace();
			luckyclient.publicclass.LogUtil.APP.error("����Web����ģʽ���Գ����쳣������",e);
			return "����Web����ģʽ���Գ����쳣������";
		} 
		return "����Web����ģʽ���Գ�������";
	}
	
	/**
	 * ��ȡ�ͻ��˱�����־
	 * @param req
	 * @return
	 * @throws RemoteException
	 */
	@GetMapping("/getlogdetail")
	private String getlogdetail(HttpServletRequest req) throws RemoteException{
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
			luckyclient.publicclass.LogUtil.APP.error("��ȡ��־·����������ͻ�����־·���Ƿ����!downLoadPath: "+downLoadPath,e);
			return "��ȡ��־·����������ͻ�����־·���Ƿ����!downLoadPath: "+downLoadPath;
		}
		BufferedReader bos = new BufferedReader(isr);
		StringBuffer sb = new StringBuffer();
		try {
			while ((str = bos.readLine()) != null)
			{
				sb.append(str).append("##n##");
			}
			bos.close();
			luckyclient.publicclass.LogUtil.APP.info("����˶�ȡ������־�ɹ�!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			luckyclient.publicclass.LogUtil.APP.error("�ͻ���תBufferedReaderʧ�ܣ�����ԭ��",e);
			return "�ͻ���תBufferedReaderʧ�ܣ�����ԭ��";
		}
		return sb.toString();
	}
	
	/**
	 * ��ȡ�����ͼ
	 * @param req
	 * @return
	 * @throws RemoteException
	 */
	@GetMapping("/getlogimg")
	private byte[] getlogimg(HttpServletRequest req,HttpServletResponse res) throws RemoteException{
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
        	luckyclient.publicclass.LogUtil.APP.info("����˻�ȡ����ͼƬ��"+downLoadPath);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            luckyclient.publicclass.LogUtil.APP.error("���ļ������ڣ����飺"+downLoadPath,e);
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
            	luckyclient.publicclass.LogUtil.APP.error("�ļ���ʽ��������.jar���ϴ�ʧ��");
                return "�ļ���ʽ��������.jar���ϴ�ʧ��";
            }
		}else{
			luckyclient.publicclass.LogUtil.APP.error("�ϴ��ļ�Ϊ�գ����飡");
            return "�ϴ��ļ�Ϊ�գ����飡";
		}

		String name = jarfile.getOriginalFilename();
		String loadpath = req.getParameter("loadpath");
		String path = System.getProperty("user.dir")+loadpath;
		if  (!new File(path) .isDirectory())      
		{    
			luckyclient.publicclass.LogUtil.APP.error("�ͻ��˲�������׮·�������ڣ����顾"+path+"��");
			return "�ͻ��˲�������׮·�������ڣ����顾"+path+"��";
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
            luckyclient.publicclass.LogUtil.APP.info("�ϴ�JAR����"+name+"�����ͻ�������Ŀ¼��"+file.getAbsolutePath()+"���ɹ�!");
            return "�ϴ�JAR����"+name+"�����ͻ�������Ŀ¼��"+file.getAbsolutePath()+"���ɹ�!";
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            luckyclient.publicclass.LogUtil.APP.error("�ͻ���δ�ҵ���ȷ·�����ļ����ϴ�ʧ�ܣ��ļ�·�����ƣ�"+pathName,e);
            return "�ͻ���δ�ҵ���ȷ·�����ļ����ϴ�ʧ�ܣ��ļ�·�����ƣ�"+pathName;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            luckyclient.publicclass.LogUtil.APP.error("�ͻ���IOExceptiona����δ�ҵ�����·�����ļ�·�����ƣ�"+pathName,e);
            return "�ͻ���IOExceptiona����δ�ҵ�����·�����ļ�·�����ƣ�"+pathName;
        }
	}
	
	/**
	 * ���ͻ�������
	 * @param req
	 * @return
	 * @throws RemoteException
	 */
	@GetMapping("/getclientstatus")
	private String getClientStatus(HttpServletRequest req) throws RemoteException{
		return "success";
	}
	
	public static boolean checkhostnet() {
		luckyclient.publicclass.LogUtil.APP.info("���ͻ���������,���Ժ�......");
		Properties properties = luckyclient.publicclass.SysConfig.getConfiguration();
		String dbip=properties.getProperty("mysql.db.ip");
		int dbport=Integer.valueOf(properties.getProperty("mysql.db.port"));
		String webip=properties.getProperty("server.web.ip");
		int webport=Integer.valueOf(properties.getProperty("server.web.port"));
        Socket dbsocket = new Socket();
        Socket websocket = new Socket();
        try {
        	dbsocket.connect(new InetSocketAddress(dbip, dbport));
        	luckyclient.publicclass.LogUtil.APP.info("�ͻ��˷������ݿ����ã�"+dbip+":"+dbport+"   ���ͨ��......");
        	websocket.connect(new InetSocketAddress(webip, webport));
        	luckyclient.publicclass.LogUtil.APP.info("�ͻ��˷���Web�����ã�"+webip+":"+webport+"   ���ͨ��......");
        } catch (IOException e) {
        	luckyclient.publicclass.LogUtil.APP.error("�ͻ������ü���쳣����ȷ������Ŀ��Ŀ¼�µĿͻ��������ļ�(sys_config.properties)�Ƿ��Ѿ���ȷ���á�",e);
            return false;
        } finally {
            try {
            	dbsocket.close();
            	websocket.close();
            } catch (IOException e) {
            	luckyclient.publicclass.LogUtil.APP.error("�ر�Socket�����쳣......",e);
            }
        }
        return true;
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
