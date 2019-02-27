package luckyclient.caserun.exinterface.testlink;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.dblog.LogOperation;
import luckyclient.publicclass.DBOperation;
import luckyclient.publicclass.InvokeMethod;
import luckyclient.testlinkapi.InterfaceAnalyticTestLinkCase;
/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 * @ClassName: ThreadForExecuteCase 
 * @Description: �̳߳ط�ʽִ������
 * @author�� seagull
 * @date 2015��4��13�� ����9:29:40  
 * 
 */
public class ThreadForTestLinkExecuteCase extends Thread{
	private String caseid;
	private TestCase testcaseob;
	private String tastid;
	
	public ThreadForTestLinkExecuteCase(String projectname,String caseid,TestCase testcaseob,String tastid){
		this.caseid = caseid;
		this.testcaseob = testcaseob;
		this.tastid = tastid;
	}
	
	@Override
	public void run(){
		Map<String,String> variable = new HashMap<String,String>(0);
		LogOperation caselog = new LogOperation();
		String functionname = null;
		String packagename =null;
		String expectedresults = null;
		Integer setresult = null;
		Object[] getParameterValues = null;
    	String testnote = null;
		int k = 0;
		 //����ѭ�������������������в���
		System.out.println(caseid);
		//���뿪ʼִ�е�����
		caselog.addCaseDetail(tastid, testcaseob.getFullExternalId(), testcaseob.getVersion().toString(), testcaseob.getName(), 4);
	    for(int i=0;i<testcaseob.getSteps().size();i++){
	    	 //�������������еĽű�
	    	Map<String,String> casescript = InterfaceAnalyticTestLinkCase.analyticCaseStep(testcaseob, i+1,tastid,caselog);   
	    	try{
		    	packagename = casescript.get("PackageName").toString();
		    	functionname = casescript.get("FunctionName").toString();
	    	}catch(Exception e){
	    		k=0;
	    		luckyclient.publicclass.LogUtil.APP.error("������"+testcaseob.getFullExternalId()+"�����������Ƿ�����ʧ�ܣ����飡");
				caselog.caseLogDetail(tastid, caseid, "�����������Ƿ�����ʧ�ܣ����飡","error",String.valueOf(i+1),"");
	    		e.printStackTrace();
	    		break;        //ĳһ����ʧ�ܺ󣬴���������Ϊʧ���˳�
	    	}
	    	//�������ƽ��������쳣���ǵ���������������쳣
	    	if(functionname.indexOf("�����쳣")>-1||k==1){
	    		k=0;
	    		testnote = "������"+(i+1)+"��������������";
	    		break;
	    	}
	    	expectedresults = casescript.get("ExpectedResults").toString();
	    	if(expectedresults.indexOf("&quot;")>-1||expectedresults.indexOf("&#39;")>-1){
	    		expectedresults = expectedresults.replaceAll("&quot;", "\"");
	    		expectedresults = expectedresults.replaceAll("&#39;", "\'");
	    	}
	    	//�жϷ����Ƿ������
	    	if(casescript.size()>4){
		    	//��ȡ������������������
		    	getParameterValues = new Object[casescript.size()-4];
		    	for(int j=0;j<casescript.size()-4;j++){		    		
		    		if(casescript.get("FunctionParams"+(j+1))==null){
		    			k = 1;
		    			break;
		    		}
		    		if(casescript.get("FunctionParams"+(j+1)).indexOf("@")>-1
		    				&&casescript.get("FunctionParams"+(j+1)).indexOf("@@")<0){
		    			int keyexistidentity = 0;
		    			//ȡ�������������ñ�������
		    			int sumvariable = DBOperation.sumString(casescript.get("FunctionParams"+(j+1)), "@");     
		    			String uservariable = null;
		    			String uservariable1 = null;
		    			String uservariable2 = null;
		    			
		    			if(sumvariable==1){
		    				uservariable = casescript.get("FunctionParams"+(j+1)).substring(
			    					casescript.get("FunctionParams"+(j+1)).indexOf("@")+1);
		    			}else if(sumvariable==2){
		    				uservariable = casescript.get("FunctionParams"+(j+1)).substring(casescript.get("FunctionParams"+(j+1)).indexOf("@")+1,
		    						casescript.get("FunctionParams"+(j+1)).lastIndexOf("@"));
		    				uservariable1 = casescript.get("FunctionParams"+(j+1)).substring(
			    					casescript.get("FunctionParams"+(j+1)).lastIndexOf("@")+1);
		    			}else if(sumvariable==3){
		    				String temp = casescript.get("FunctionParams"+(j+1)).substring(casescript.get("FunctionParams"+(j+1)).indexOf("@")+1,
		    						casescript.get("FunctionParams"+(j+1)).lastIndexOf("@"));
	    					uservariable1 = temp.substring(temp.indexOf("@")+1);
	    					uservariable2 = casescript.get("FunctionParams"+(j+1)).substring(
			    					casescript.get("FunctionParams"+(j+1)).lastIndexOf("@")+1);
	    					uservariable = casescript.get("FunctionParams"+(j+1)).substring(casescript.get("FunctionParams"+(j+1)).indexOf("@")+1,
			    					casescript.get("FunctionParams"+(j+1)).indexOf(uservariable1)-1);
	    				}else{
		    				luckyclient.publicclass.LogUtil.APP.error("�������һ�������������˳���3�����ϵı���Ŷ���Ҵ�����������");
		    				caselog.caseLogDetail(tastid, caseid, "�������һ�������������˳���2�����ϵı���Ŷ���Ҵ�����������","error",String.valueOf(i+1),"");
		    			}
		    			@SuppressWarnings("rawtypes")
						Iterator keys = variable.keySet().iterator();
		    			String key = null;
		    			while(keys.hasNext()){
		    				key = (String)keys.next();
		    				if(uservariable.indexOf(key)>-1){
		    					keyexistidentity = 1;
		    					uservariable = key;
					    		break;
		    				}
		    			}
		    			if(sumvariable==2||sumvariable==3){
		    				keys = variable.keySet().iterator();
			    			while(keys.hasNext()){
			    				keyexistidentity = 0;
			    				key = (String)keys.next();
			    				if(uservariable.indexOf(key)>-1){
			    					keyexistidentity = 1;
			    					uservariable1 = key;
						    		break;
			    				}
			    			}
		    			}
		    			if(sumvariable==3){
		    				keys = variable.keySet().iterator();
			    			while(keys.hasNext()){
			    				keyexistidentity = 0;
			    				key = (String)keys.next();
			    				if(uservariable.indexOf(key)>-1){
			    					keyexistidentity = 1;
			    					uservariable2 = key;
						    		break;
			    				}
			    			}
		    			}
		    			if(keyexistidentity == 1){
				    		//ƴװ����������+ԭ���ַ�����
				    		String parameterValues =casescript.get("FunctionParams"+(j+1)).replaceAll("@"+uservariable, variable.get(uservariable).toString());
				    		//����ڶ�������
				    		if(sumvariable==2||sumvariable==3){
				    			parameterValues = parameterValues.replaceAll("@"+uservariable1, variable.get(uservariable1).toString());
				    		}
				    		//�������������
				    		if(sumvariable==3){
				    			parameterValues = parameterValues.replaceAll("@"+uservariable2, variable.get(uservariable2).toString());
				    		}
					    	if(parameterValues.indexOf("&quot;")>-1 || parameterValues.indexOf("&#39;")>-1){
					    		parameterValues = parameterValues.replaceAll("&quot;", "\"");
					    		parameterValues = parameterValues.replaceAll("&#39;", "\'");
					    	}
				    		luckyclient.publicclass.LogUtil.APP.info("����������"+packagename+" ��������"+functionname
				    				+" ��"+(j+1)+"��������"+parameterValues);
				    		caselog.caseLogDetail(tastid, caseid, "����������"+packagename+" ��������"+functionname
				    				+" ��"+(j+1)+"��������"+parameterValues,"info",String.valueOf(i+1),"");
				    		getParameterValues[j] = parameterValues;
		    			}else{
		    				luckyclient.publicclass.LogUtil.APP.error("û���ҵ���Ҫ�ı���Ŷ�������°ɣ���һ�����������ǣ�"+uservariable+"����"
		    						+ "�������������ǣ�"+uservariable1+"�����������������ǣ�"+uservariable2);
		    				caselog.caseLogDetail(tastid, caseid, "û���ҵ���Ҫ�ı���Ŷ�������°ɣ��ڶ����������ǣ�"+uservariable+"����"
		    						+ "�������������ǣ�"+uservariable1,"error",String.valueOf(i+1),"");
		    			}

		    		}else{
			    		String parameterValues1 = casescript.get("FunctionParams"+(j+1));
				    	if(parameterValues1.indexOf("&quot;")>-1 || parameterValues1.indexOf("&#39;")>-1 || parameterValues1.indexOf("@@")>-1){
				    		parameterValues1 = parameterValues1.replaceAll("&quot;", "\"");
				    		parameterValues1 = parameterValues1.replaceAll("&#39;", "\'");
				    		parameterValues1 = parameterValues1.replaceAll("@@", "@");
				    	}
			    		luckyclient.publicclass.LogUtil.APP.info("������"+testcaseob.getFullExternalId()+"����������"+packagename+" ��������"+functionname
			    				+" ��"+(j+1)+"��������"+parameterValues1);
			    		caselog.caseLogDetail(tastid, caseid,"����������"+packagename+" ��������"+functionname
			    				+" ��"+(j+1)+"��������"+parameterValues1,"info",String.valueOf(i+1),"");
			    		getParameterValues[j] = parameterValues1;
		    		}
		    	}
	    	}else{
	    		getParameterValues = null;
	    	}
	    	//���ö�̬������ִ�в�������
		    try{
		    	luckyclient.publicclass.LogUtil.APP.info("������"+testcaseob.getFullExternalId()+"��ʼ���÷�����"+functionname+" .....");
		    	caselog.caseLogDetail(tastid, caseid,"��ʼ���÷�����"+functionname+" .....","info",String.valueOf(i+1),"");
		    	//��Ԥ�ڽ��ǰ�����ַ��ж��Ƿ���Ҫ�ѽ���������
		    	if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("$=")>-1){
		    		String expectedResultVariable = casescript.get("ExpectedResults").toString().substring(2);
		    		String temptestnote = InvokeMethod.callCase(packagename,functionname,getParameterValues,0,"");
		    		variable.put(expectedResultVariable, temptestnote);
		    		//��Ԥ�ڽ������Խ����ģ��ƥ��
		    	}else if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("%=")>-1){
			    	testnote = InvokeMethod.callCase(packagename,functionname,getParameterValues,0,"");
			    	if(testnote.indexOf(expectedresults.substring(2))>-1){
			    		setresult = 0;
			    		luckyclient.publicclass.LogUtil.APP.info("������"+testcaseob.getFullExternalId()+"ִ�н���ǣ�"+testnote+"����Ԥ�ڽ��ƥ��ɹ���");
			    		caselog.caseLogDetail(tastid, caseid,"ִ�н���ǣ�"+testnote+"����Ԥ�ڽ��ƥ��ɹ���","info",String.valueOf(i+1),"");
			    	}else{
			    		setresult = 1;
			    		luckyclient.publicclass.LogUtil.APP.error("������"+testcaseob.getFullExternalId()+"��"+(i+1)+"��ִ�н����Ԥ�ڽ��ƥ��ʧ�ܣ�");
			    		luckyclient.publicclass.LogUtil.APP.error("������"+testcaseob.getFullExternalId()+"Ԥ�ڽ����"+expectedresults+"      ���Խ����"+testnote);
			    		caselog.caseLogDetail(tastid, caseid,"��"+(i+1)+"��ִ�н����Ԥ�ڽ��ƥ��ʧ�ܣ�"+"Ԥ�ڽ����"+expectedresults+"      ���Խ����"+testnote,"error",String.valueOf(i+1),"");
			    		testnote = "������"+(i+1)+"��ִ�н����Ԥ�ڽ��ƥ��ʧ�ܣ�";
			    		break;        //ĳһ����ʧ�ܺ󣬴���������Ϊʧ���˳�
			    	}
		    	}else{                                                                                                                    //��Ԥ�ڽ������Խ������ȷƥ��
			    	testnote = InvokeMethod.callCase(packagename,functionname,getParameterValues,0,"");
			    	if(expectedresults.equals(testnote)){
			    		setresult = 0;
			    		luckyclient.publicclass.LogUtil.APP.info("������"+testcaseob.getFullExternalId()+"ִ�н���ǣ�"+testnote+"����Ԥ�ڽ��ƥ��ɹ���");
			    		caselog.caseLogDetail(tastid, caseid,"ִ�н���ǣ�"+testnote+"����Ԥ�ڽ��ƥ��ɹ���","info",String.valueOf(i+1),"");
			    	}else{
			    		setresult = 1;
			    		luckyclient.publicclass.LogUtil.APP.error("������"+testcaseob.getFullExternalId()+"��"+(i+1)+"��ִ�н����Ԥ�ڽ��ƥ��ʧ�ܣ�");
			    		luckyclient.publicclass.LogUtil.APP.error("������"+testcaseob.getFullExternalId()+"Ԥ�ڽ����"+expectedresults+"      ���Խ����"+testnote);
			    		caselog.caseLogDetail(tastid, caseid,"��"+(i+1)+"��ִ�н����Ԥ�ڽ��ƥ��ʧ�ܣ�"+"Ԥ�ڽ����"+expectedresults+"      ���Խ����"+testnote,"error",String.valueOf(i+1),"");
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append("������"+(i+1)+"��ִ�н����Ԥ�ڽ��ƥ��ʧ�ܣ�Ԥ�ڽ����"+expectedresults+"      ���Խ����");
						stringBuilder.append(testnote);
						testnote = stringBuilder.toString();
			    		break;        //ĳһ����ʧ�ܺ󣬴���������Ϊʧ���˳�
			    	}
		    	}
		    	int waitsec = Integer.parseInt(casescript.get("StepWait").toString());  
		    	if(waitsec!=0){
		    		Thread.sleep(waitsec*1000);
		    	}
		    }catch(Exception e){
		    	luckyclient.publicclass.LogUtil.ERROR.error("������"+testcaseob.getFullExternalId()+"���÷������̳�����������"+functionname+" �����¼��ű����������Լ�������");
		    	caselog.caseLogDetail(tastid, caseid,"���÷������̳�����������"+functionname+" �����¼��ű����������Լ�������","error",String.valueOf(i+1),"");
				luckyclient.publicclass.LogUtil.ERROR.error(e,e);
				testnote = "CallCase���ó������÷������̳�����������"+functionname+" �����¼��ű����������Լ�������";
				setresult = 1;
				e.printStackTrace();
    			break;
		    }			    
	    }
	    //������÷���������δ�����������ò��Խ������
	    try{
	    //�ɹ���ʧ�ܵ������ߴ�����
	    if(testnote.indexOf("CallCase���ó���")<=-1&&testnote.indexOf("������������")<=-1){       
		    	//luckyclient.publicclass.LogUtil.APP.info("������"+testcaseob.getFullExternalId()+"��ʼ���ò�������ִ�н�� .....");
		    	//caselog.caseLogDetail(tastid, caseid,"��ʼ���ò�������ִ�н�� .....","info","SETCASERESULT...");
		    	//TCResult = TestCaseApi.setTCResult(projectname,caseid, testnote, testcaseob.getVersion(),setresult);
		    	caselog.updateCaseDetail(tastid, caseid, setresult);
	     }else{
	    	//�����������ǵ��÷�������ȫ����������Ϊ����
	    	 luckyclient.publicclass.LogUtil.ERROR.error("������"+testcaseob.getFullExternalId()+"����ִ�н��Ϊ��������ο�������־��������������ԭ��.....");
	    	 caselog.caseLogDetail(tastid, caseid,"����ִ�н��Ϊ��������ο�������־��������������ԭ��.....","error","SETCASERESULT...","");
		     //TCResult = TestCaseApi.setTCResult(projectname,caseid, testnote, testcaseob.getVersion(),2);
	    	 setresult = 2;
		     caselog.updateCaseDetail(tastid, caseid, setresult);
	     }
    	if(setresult==0){
    		luckyclient.publicclass.LogUtil.APP.info("������"+testcaseob.getFullExternalId()+"ִ�н���ɹ�......");
    		caselog.caseLogDetail(tastid, caseid,"��������ִ��ȫ���ɹ�......","info","ending","");
    		luckyclient.publicclass.LogUtil.APP.info("******************************�ָ���*************************************");
    	}else if(setresult==1){
    		luckyclient.publicclass.LogUtil.ERROR.error("������"+testcaseob.getFullExternalId()+"ִ�н��ʧ��......");
    		caselog.caseLogDetail(tastid, caseid,"����ִ�н��ʧ��......","error","ending","");
    		luckyclient.publicclass.LogUtil.APP.info("******************************�ָ���*************************************");
    	}else{
    		luckyclient.publicclass.LogUtil.ERROR.error("������"+testcaseob.getFullExternalId()+"ִ�н������......");
    		caselog.caseLogDetail(tastid, caseid,"����ִ�н������......","error","ending","");
    		luckyclient.publicclass.LogUtil.APP.info("******************************�ָ���*************************************");
    	}
	    }catch(Exception e){
	    	luckyclient.publicclass.LogUtil.ERROR.error("������"+testcaseob.getFullExternalId()+"����ִ�н�����̳���......");
	    	caselog.caseLogDetail(tastid, caseid,"����ִ�н�����̳���......","error","ending","");
			luckyclient.publicclass.LogUtil.ERROR.error(e,e);
			e.printStackTrace();
	    }finally{
	    	variable.clear();                     //һ��������������ձ����洢�ռ�
	    	TestControl.THREAD_COUNT--;        //���̼߳���--�����ڼ���߳��Ƿ�ȫ��ִ����
	    }
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
