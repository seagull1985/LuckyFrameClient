package luckyclient.caserun.exwebdriver;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import luckyclient.caserun.exwebdriver.ex.WebCaseExecution;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.planapi.api.GetServerAPI;
import luckyclient.planapi.entity.ProjectCase;
import luckyclient.planapi.entity.ProjectCasesteps;
import luckyclient.planapi.entity.PublicCaseParams;

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
public class CaseLocalDebug{

	
	public static void oneCasedebug(WebDriver wd,String testCaseExternalId){
		 //����¼��־�����ݿ�
		DbLink.exetype = 1;  
		LogOperation caselog = new LogOperation();
		try {
			ProjectCase testcase = GetServerAPI.cgetCaseBysign(testCaseExternalId);
			List<PublicCaseParams> pcplist=GetServerAPI.cgetParamsByProjectid(String.valueOf(testcase.getProjectid()));
			luckyclient.publicclass.LogUtil.APP.info("��ʼִ����������"+testCaseExternalId+"��......");
			List<ProjectCasesteps> steps=GetServerAPI.getStepsbycaseid(testcase.getId());
			WebCaseExecution.caseExcution(testcase,steps, "888888",wd,caselog,pcplist);
			luckyclient.publicclass.LogUtil.APP.info("��ǰ��������"+testcase.getSign()+"��ִ�����......������һ��");
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error("�û�ִ�й������׳��쳣��", e);
			e.printStackTrace();
		}
        //�ر������
        wd.quit();
	}
	
	/**
	 * @param ��Ŀ��
	 * @param �������
	 * @param �����汾��
	 * ������testlink�����ú������������������������е���
	 */
	public static void moreCaseDebug(WebDriver wd,String projectname,List<String> addtestcase){
		System.out.println("��ǰ���������ܹ���"+addtestcase.size());
		for(String testCaseExternalId:addtestcase) {
		    try{
		    luckyclient.publicclass.LogUtil.APP.info("��ʼ���÷�������Ŀ����"+projectname+"��������ţ�" + testCaseExternalId); 
		    oneCasedebug(wd,testCaseExternalId);
		    }catch(Exception e){
		    	continue;
		    }
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub	

	}

}
