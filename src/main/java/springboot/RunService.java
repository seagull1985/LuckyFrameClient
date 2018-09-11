package springboot;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * @author seagull
 * @date 2018年7月27日 上午10:16:40
 */
@SpringBootApplication
public class RunService {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PropertyConfigurator.configure(System.getProperty("user.dir") + File.separator +"bootlog4j.conf");
        try {
        	String host = InetAddress.getLocalHost().getHostAddress();
    		luckyclient.publicclass.LogUtil.APP.info("启动客户端监听,请稍后......监听IP："+host);
        } catch (UnknownHostException e) {
        	luckyclient.publicclass.LogUtil.APP.error("获取服务IP出现异常......", e);
        }
		SpringApplication.run(RunService.class, args);
		HttpImpl.checkhostnet();
	}

}
