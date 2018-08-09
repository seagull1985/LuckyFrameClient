package luckyclient.testlinkapi;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 * @ClassName: TestLinkBaseApi 
 * @Description: ��ʼ��TESTLINK�ӿ� 
 * @author�� seagull
 * @date 2014��6��24�� ����9:29:40  
 * @deprecated
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
