package rmi.remotingservice;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;

import rmi.service.RunService;
import rmi.serviceimpl.RunServiceImpl;

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
public class Program{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        try {
        	Properties properties= luckyclient.publicclass.SysConfig.getConfiguration();
        	String localhostip=properties.getProperty("client.localhost.ip");
        	if(!"localhost".toLowerCase().equals(localhostip)){
            	System.setProperty("java.rmi.server.hostname", localhostip);
        	}
        	RunService runService=new RunServiceImpl();
			//注册通讯端口
			LocateRegistry.createRegistry(6633);
			//注册通讯路径
			Naming.rebind("rmi://"+localhostip+":6633/RunService", runService);
			System.out.println("启动客户端监听...IP:"+localhostip+"  端口:6633");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}