package luckyclient.testlinkapi;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.dblog.LogOperation;
/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 * @ClassName: TestBuildApi 
 * @Description: ���η�װ���ڲ��԰汾���ֵ�API 
 * @author�� seagull
 * @date 2014��6��24�� ����9:29:40  
 * @deprecated
 */
public class TestBuildApi extends TestLinkBaseApi {
	/**
	 * @param args
	 */
	public static Integer createBuild(String projectname){
	    Build result = new Build();
		Date nowTime = new Date(System.currentTimeMillis());
		SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String retStrFormatNowDate = sdFormatter.format(nowTime);
	    String buildName = "�Զ�������-"+retStrFormatNowDate;
		try{
		    //�ܹ���Ŀ�����Լ��ƻ����ƻ�ȡ�ƻ�ID
		    TestPlan testplanob = new TestPlan();
		  //  testplanob = api.getTestPlanByName(TestPlanName(projectname), projectname);
		    if(TestControl.TASKID.indexOf("NULL")>-1||LogOperation.getTestPlanName(TestControl.TASKID).indexOf("NULL")>-1){
		    	testplanob = api.getTestPlanByName(testPlanName(projectname), projectname);
		    }else{
		    	testplanob = api.getTestPlanByName(LogOperation.getTestPlanName(TestControl.TASKID), projectname);
		    }
		    Integer planid = testplanob.getId();
		    
		    //�������������Լƻ�
	        result = api.createBuild(planid, buildName, "�Զ����������ڣ�"+retStrFormatNowDate);     	        

	        luckyclient.publicclass.LogUtil.APP.info(projectname+"��Ŀ�İ汾�����ɹ����汾ID��"+result.getId()+" �汾���ƣ�"+buildName);
			return result.getId();
	         
		}catch( TestLinkAPIException te) {
            te.printStackTrace( System.err );
            luckyclient.publicclass.LogUtil.ERROR.error(projectname+"�汾����ʧ�ܣ�");
            return 999999;
     }
	}
	
	/**
	 * ��û�ڲ��Լƻ����ҵ��汾���Զ�����һ���汾
	 */
	public static void getBuild(String projectname){
	    //�ܹ���Ŀ�����Լ��ƻ����ƻ�ȡ�ƻ�ID
	    TestPlan testplanob = new TestPlan();
	   // testplanob = api.getTestPlanByName(TestPlanName(projectname), projectname);
	    if(TestControl.TASKID.indexOf("NULL")>-1||LogOperation.getTestPlanName(TestControl.TASKID).indexOf("NULL")>-1){
	    	testplanob = api.getTestPlanByName(testPlanName(projectname), projectname);
	    }else{
	    	testplanob = api.getTestPlanByName(LogOperation.getTestPlanName(TestControl.TASKID), projectname);
	    }
	    Integer planid = testplanob.getId();
	    try{
			Build lastbuild = api.getLatestBuildForTestPlan(planid);
			luckyclient.publicclass.LogUtil.APP.info("���Լƻ� "+testplanob.getName()+" ���ҵ�һ�����԰汾���������ڴ˰汾��ִ�У��汾����"+lastbuild.getName());
	    }catch( TestLinkAPIException te){
	    	luckyclient.publicclass.LogUtil.APP.error("���Լƻ� "+testplanob.getName()+" ��û���ҵ����԰汾���޷����ò��Խ�������ڽ���Ϊ�㴴��һ�����԰汾��");
	    	TestBuildApi.createBuild(projectname);
	    }
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//��������
//		System.out.println(createBuild(""));
	}

}
