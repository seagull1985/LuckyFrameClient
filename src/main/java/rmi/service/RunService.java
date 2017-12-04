package rmi.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

import rmi.model.RunBatchCaseEntity;
import rmi.model.RunCaseEntity;
import rmi.model.RunTaskEntity;

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
//此为远程对象调用的接口，必须继承Remote类
public interface RunService extends Remote {
    public String runtask(RunTaskEntity task,String loadpath) throws RemoteException;
    public String runcase(RunCaseEntity onecase,String loadpath) throws RemoteException;
    public String runbatchcase(RunBatchCaseEntity batchcase,String loadpath) throws RemoteException;
    public String getlogdetail(String storeName) throws RemoteException;
    public byte[] getlogimg(String imgName) throws RemoteException;
    public String uploadjar(byte[] fileContent,String name,String loadpath) throws RemoteException;
    public String webdebugcase(String sign,String executor,String loadpath) throws RemoteException;
    public String getClientStatus() throws RemoteException;
}
