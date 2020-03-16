package luckyclient.tool.shell;

import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import luckyclient.utils.LogUtil;

/**
 * 远程执行shell脚本类
 *
 * @author l
 */
public class RmtShellExecutor {
    /**
     * 利用JSch包实现远程主机SHELL命令执行
     *
     * @param ip      主机IP
     * @param user    主机登陆用户名
     * @param psw     主机登陆密码
     * @param port    主机ssh2登陆端口，如果取默认值，传-1
     * @param command Shell命令   cd /home/pospsettle/tomcat-7.0-7080/bin&&./restart.sh
     */
    public static String sshShell(String ip, String user, String psw
            , int port, String command) {

        Session session = null;
        Channel channel = null;
        String result = "Status:true" + " 重启命令执行成功！";
        try {
            JSch jsch = new JSch();
            LogUtil.APP.info("进入到重启TOMCAT方法...");

            if (port <= 0) {
                //连接服务器，采用默认端口
                LogUtil.APP.info("设置重启TOMCAT服务器IP及默认端口...");
                session = jsch.getSession(user, ip);
            } else {
                //采用指定的端口连接服务器
                LogUtil.APP.info("设置重启TOMCAT服务器IP及端口...");
                session = jsch.getSession(user, ip, port);
                LogUtil.APP.info("设置重启TOMCAT服务器IP及端口完成!");
            }

            //如果服务器连接不上，则抛出异常
            if (session == null) {
                LogUtil.APP.warn("重启TOMCAT过程中，链接服务器session is null");
                throw new Exception("session is null");
            }
            //设置登陆主机的密码
            session.setPassword(psw);
            //设置第一次登陆的时候提示，可选值：(ask | yes | no)
            session.setConfig("StrictHostKeyChecking", "no");
            //设置登陆超时时间
            session.connect(30000);

            //创建sftp通信通道
            channel = session.openChannel("shell");
            channel.connect(1000);

            //获取输入流和输出流
            InputStream instream = channel.getInputStream();
            OutputStream outstream = channel.getOutputStream();

            //发送需要执行的SHELL命令，需要用\n结尾，表示回车
            LogUtil.APP.info("准备往重启TOMCAT服务器发送命令!");
            String shellCommand = command + "  \n";
            outstream.write(shellCommand.getBytes());
            outstream.flush();

            Thread.sleep(10000);
            //获取命令执行的结果
            if (instream.available() > 0) {
                byte[] data = new byte[instream.available()];
                int nLen = instream.read(data);
                if (nLen < 0) {
                    LogUtil.APP.warn("重启TOMCAT过程中，获取命令执行结果出现异常！");
                }

                //转换输出结果并打印出来
                String temp = new String(data, 0, nLen, "iso8859-1");
                LogUtil.APP.info("开始打印重启TOMCAT命令执行结果...{}", temp);
            }
            outstream.close();
            instream.close();
        } catch (Exception e) {
            result = "重启TOMCAT过程中，出现异常！";
            LogUtil.APP.error("重启TOMCAT过程中，出现异常！", e);
            return result;
        } finally {
            if (null != session) {
                session.disconnect();
            }
            if (null != channel) {
                channel.disconnect();
            }

        }
        return result;
    }

}