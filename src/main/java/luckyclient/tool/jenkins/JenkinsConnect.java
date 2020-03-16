package luckyclient.tool.jenkins;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;

import luckyclient.utils.LogUtil;
import luckyclient.utils.config.SysConfig;

/**
 * Jenkins链接
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019年10月29日
 */
public class JenkinsConnect {

	private String JENKINS_URL;
	private String JENKINS_USERNAME;
	private String JENKINS_PASSWORD;

	JenkinsConnect() {
		Properties properties = SysConfig.getConfiguration();
		this.JENKINS_URL=properties.getProperty("jenkins.url");
		this.JENKINS_USERNAME=properties.getProperty("jenkins.username");
		this.JENKINS_PASSWORD=properties.getProperty("jenkins.password");
	}

	/**
	 * 如果有些 API 该Jar工具包未提供，可以用此Http客户端操作远程接口，执行命令
	 * @return 返回jenkins客户端对象
	 */
	public JenkinsHttpClient getClient() {
		JenkinsHttpClient jenkinsHttpClient = null;
		try {
			jenkinsHttpClient = new JenkinsHttpClient(new URI(JENKINS_URL), JENKINS_USERNAME, JENKINS_PASSWORD);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return jenkinsHttpClient;
	}

	/**
	 * Jenkins API链接
	 * @return 返回jenkins服务对象
	 * @author Seagull
	 * @date 2019年10月29日
	 */
	public JenkinsServer connection() {
		JenkinsServer jenkinsServer = null;
		try {
			LogUtil.APP.info("准备连接Jenkins...URL:{}  用户名:{}  密码:{}",JENKINS_URL,JENKINS_USERNAME,JENKINS_PASSWORD);
			jenkinsServer = new JenkinsServer(new URI(JENKINS_URL), JENKINS_USERNAME, JENKINS_PASSWORD);
			LogUtil.APP.info("连接Jenkins成功！");
			LogUtil.APP.info("Jenkins版本:{}",jenkinsServer.getVersion());
		} catch (URISyntaxException e) {
			LogUtil.APP.error("连接Jenkins出现异常",e);
		}
		return jenkinsServer;
	}

}