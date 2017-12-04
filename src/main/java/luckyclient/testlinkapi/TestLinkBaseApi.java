package luckyclient.testlinkapi;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * @ClassName: TestLinkBaseApi 
 * @Description: 初始化TESTLINK接口 
 * @author： seagull
 * @date 2014年6月24日 上午9:29:40  
 * 
 */
class TestLinkBaseApi{

	/**
	 * @param args
	 */
	final static Properties PROPERTIES = luckyclient.publicclass.SysConfig.getConfiguration();
	private final static String TESTLINK_URL = "http://"+PROPERTIES.getProperty("testlink.api.ip")+":80/testlink/lib/api/xmlrpc/v1/xmlrpc.php";
	protected final static String TESTLINK_DEVKEY = PROPERTIES.getProperty("testlink.api.devkey");
	protected final static Integer PLATFORMID = 0;
	protected final static String PLATFORMNAME = null;
	protected static TestLinkAPI api= iniTestlinkApi();

	private static TestLinkAPI iniTestlinkApi() {
	    URL testlinkURL = null;	
	    try     {
	            testlinkURL = new URL(TESTLINK_URL);
	    } catch ( MalformedURLException mue )   {
	            mue.printStackTrace( System.err );
	            System.exit(-1);
	    }
	    return new TestLinkAPI(testlinkURL, TESTLINK_DEVKEY);
	}
	
	protected static String testPlanName(String projectname){
		return api.getTestProjectByName(projectname).getNotes().substring(
				api.getTestProjectByName(projectname).getNotes().indexOf("<p>")+3,
				api.getTestProjectByName(projectname).getNotes().indexOf("</p>")).trim();
	}
	
	protected static Integer projectID(String projectname){
		return api.getTestProjectByName(projectname).getId();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
       System.out.println(api.ping());
	}
}
