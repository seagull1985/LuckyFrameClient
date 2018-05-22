package rmi.remotingservice;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;

import rmi.service.RunService;
import rmi.serviceimpl.RunServiceImpl;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @author�� seagull
 * @date 2017��12��1�� ����9:29:40
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
			//ע��ͨѶ�˿�
			LocateRegistry.createRegistry(6633);
			//ע��ͨѶ·��
			Naming.rebind("rmi://"+localhostip+":6633/RunService", runService);
			System.out.println("�����ͻ��˼���...IP:"+localhostip+"  �˿�:6633");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}