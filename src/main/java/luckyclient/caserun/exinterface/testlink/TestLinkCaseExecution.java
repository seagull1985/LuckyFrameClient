package luckyclient.caserun.exinterface.testlink;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import luckyclient.caserun.exinterface.TestControl;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.publicclass.DBOperation;
import luckyclient.publicclass.InvokeMethod;
import luckyclient.testlinkapi.InterfaceAnalyticTestLinkCase;
import luckyclient.testlinkapi.TestCaseApi;

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
public class TestLinkCaseExecution{	
	/**
	 * @param ��Ŀ��
	 * @param �������
	 * @param �����汾��
	 * ������testlink�����ú������������������������ԣ���ͨ����־���д��־��UTP�ϣ�����UTP�ϵ�����������
	 */
	@SuppressWarnings("static-access")
	public static void oneCaseExecuteForTast(String projectname,String testCaseExternalId,int version,String taskid){
		Map<String,String> variable = new HashMap<String,String>(0);
		TestControl.TASKID = taskid;
		DbLink.exetype = 0;
		LogOperation caselog = new LogOperation();       
		String packagename =null;
		String functionname = null;
		String expectedresults = null;
		Integer setresult = null;
		Object[] getParameterValues = null;
    	String testnote = null;
		int k = 0;
		LogOperation.deleteCaseLogDetail(testCaseExternalId, taskid);    
		TestCase testcaseob = TestCaseApi.getTestCaseByExternalId(testCaseExternalId, version);
		if(testcaseob.getExecutionType()==ExecutionType.AUTOMATED){
		    //����ѭ���������������в���
		    for(int i=0;i<testcaseob.getSteps().size();i++){		    	
		    	Map<String,String> casescript = InterfaceAnalyticTestLinkCase.analyticCaseStep(testcaseob, i+1,taskid,caselog);    
		    	packagename = casescript.get("PackageName").toString();
		    	functionname = casescript.get("FunctionName").toString();
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
		    					caselog.caseLogDetail(taskid, testCaseExternalId, "�������һ�������������˳���2�����ϵı���Ŷ���Ҵ�����������", "error",String.valueOf(i+1), "");
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
					    		caselog.caseLogDetail(taskid, testCaseExternalId, "����������"+packagename+" ��������"+functionname
					    				+" ��"+(j+1)+"��������"+parameterValues, "info",String.valueOf(i+1), "");
					    		getParameterValues[j] = parameterValues;
			    			}else{
			    				luckyclient.publicclass.LogUtil.APP.error("û���ҵ���Ҫ�ı���Ŷ�������°ɣ���һ�����������ǣ�"+uservariable+"����"
			    						+ "�������������ǣ�"+uservariable1+"�����������������ǣ�"+uservariable2);
			    				caselog.caseLogDetail(taskid, testCaseExternalId, "û���ҵ���Ҫ�ı���Ŷ�������°ɣ��ڶ����������ǣ�"+uservariable+"����"
			    						+ "�������������ǣ�"+uservariable1+"�����������������ǣ�"+uservariable2, "error",String.valueOf(i+1), "");
			    			}

			    		}else{
				    		String parameterValues1 = casescript.get("FunctionParams"+(j+1));
					    	if(parameterValues1.indexOf("&quot;")>-1 || parameterValues1.indexOf("&#39;")>-1 || parameterValues1.indexOf("@@")>-1){         
					    		parameterValues1 = parameterValues1.replaceAll("&quot;", "\"");
					    		parameterValues1 = parameterValues1.replaceAll("&#39;", "\'");
					    		parameterValues1 = parameterValues1.replaceAll("@@", "@");
					    	}
				    		luckyclient.publicclass.LogUtil.APP.info("����������"+packagename+" ��������"+functionname
				    				+" ��"+(j+1)+"��������"+parameterValues1);
				    		caselog.caseLogDetail(taskid, testCaseExternalId, "����������"+packagename+" ��������"+functionname
				    				+" ��"+(j+1)+"��������"+parameterValues1, "info",String.valueOf(i+1), "");
				    		getParameterValues[j] = parameterValues1;
			    		}
			    	}
		    	}else{
		    		getParameterValues = null;
		    	}
		    	//���ö�̬������ִ�в�������
			    try{
			    	luckyclient.publicclass.LogUtil.APP.info("��ʼ���÷�����"+functionname+" .....");
			    	caselog.caseLogDetail(taskid, testCaseExternalId, "��ʼ���÷�����"+functionname+" .....", "info",String.valueOf(i+1), "");
			    	if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("$=")>-1){                             
			    		String expectedResultVariable = casescript.get("ExpectedResults").toString().substring(2);
			    		String temptestnote = InvokeMethod.callCase(packagename,functionname,getParameterValues,0,"");
			    		variable.put(expectedResultVariable, temptestnote);
			    	}else if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("%=")>-1){                    
				    	testnote = InvokeMethod.callCase(packagename,functionname,getParameterValues,0,"");
				    	if(testnote.indexOf(expectedresults.substring(2))>-1){
				    		setresult = 0;
				    		luckyclient.publicclass.LogUtil.APP.info("����ִ�н���ǣ�"+testnote+"����Ԥ�ڽ��ƥ��ɹ���");
				    		caselog.caseLogDetail(taskid, testCaseExternalId, "����ִ�н���ǣ�"+testnote+"����Ԥ�ڽ��ƥ��ɹ���", "info",String.valueOf(i+1), "");
				    	}else{
				    		setresult = 1;
				    		luckyclient.publicclass.LogUtil.APP.error("������"+(i+1)+"��ִ�н����Ԥ�ڽ��ƥ��ʧ�ܣ�");
				    		caselog.caseLogDetail(taskid, testCaseExternalId, "������"+(i+1)+"��ִ�н����Ԥ�ڽ��ƥ��ʧ�ܣ�Ԥ�ڽ����"+expectedresults+"      ���Խ����"+testnote, "error",String.valueOf(i+1), "");
				    		luckyclient.publicclass.LogUtil.APP.error("Ԥ�ڽ����"+expectedresults+"      ���Խ����"+testnote);
				    		testnote = "������"+(i+1)+"��ִ�н����Ԥ�ڽ��ƥ��ʧ�ܣ�";
				    		break;        //ĳһ����ʧ�ܺ󣬴���������Ϊʧ���˳�
				    	}
			    	}else{                                                                                                                          //��Ԥ�ڽ������Խ������ȷƥ��
				    	testnote = InvokeMethod.callCase(packagename,functionname,getParameterValues,0,"");
				    	if(expectedresults.equals(testnote)){
				    		setresult = 0;
				    		luckyclient.publicclass.LogUtil.APP.info("����ִ�н���ǣ�"+testnote+"����Ԥ�ڽ��ƥ��ɹ���");
				    		caselog.caseLogDetail(taskid, testCaseExternalId, "����ִ�н���ǣ�"+testnote+"����Ԥ�ڽ��ƥ��ɹ���", "info",String.valueOf(i+1), "");
				    	}else{
				    		setresult = 1;
				    		luckyclient.publicclass.LogUtil.APP.error("������"+(i+1)+"��ִ�н����Ԥ�ڽ��ƥ��ʧ�ܣ�");
				    		caselog.caseLogDetail(taskid, testCaseExternalId, "������"+(i+1)+"��ִ�н����Ԥ�ڽ��ƥ��ʧ�ܣ�Ԥ�ڽ����"
						    		+expectedresults+"      ���Խ����"+testnote, "error",String.valueOf(i+1), "");
				    		luckyclient.publicclass.LogUtil.APP.error("Ԥ�ڽ����"+expectedresults+"      ���Խ����"+testnote);
							StringBuilder stringBuilder = new StringBuilder();
							stringBuilder.append("������"+(i+1)+"��ִ�н����Ԥ�ڽ��ƥ��ʧ�ܣ�Ԥ�ڽ����"+expectedresults+"      ���Խ����");
							stringBuilder.append(testnote);

				    		break;        //ĳһ����ʧ�ܺ󣬴���������Ϊʧ���˳�
				    	}
			    	}
			    	int waitsec = Integer.parseInt(casescript.get("StepWait").toString());   
			    	if(waitsec!=0){
			    		Thread.sleep(waitsec*1000);
			    	}
			    }catch(Exception e){
			    	luckyclient.publicclass.LogUtil.APP.error("���÷������̳�����������"+functionname+" �����¼��ű����������Լ�������");
			    	caselog.caseLogDetail(taskid, testCaseExternalId, "���÷������̳�����������"+functionname+" �����¼��ű����������Լ�������", "error",String.valueOf(i+1), "");
					luckyclient.publicclass.LogUtil.APP.error(e,e);
					testnote = "CallCase���ó���";
					setresult = 1;
					e.printStackTrace();
	    			break;
			    }			    
		    }
		    variable.clear();               //��մ���MAP
		    //������÷���������δ�����������ò��Խ������
		    if(testnote.indexOf("CallCase���ó���")<=-1&&testnote.indexOf("������������")<=-1){
		    	luckyclient.publicclass.LogUtil.APP.info("���� "+testCaseExternalId+"�����ɹ������ɹ����������з�����������鿴ִ�н����"); 
		    	caselog.caseLogDetail(taskid, testCaseExternalId, "�����ɹ������ɹ����������з�����������鿴ִ�н����", "info","SETCASERESULT...", "");
		    	//TCResult = TestCaseApi.setTCResult(projectname,testCaseExternalId, testnote, version,setresult);	
		    	caselog.updateCaseDetail(taskid, testCaseExternalId, setresult);
		     }else{
		    	 setresult = 1;
		    	 luckyclient.publicclass.LogUtil.APP.error("���� "+testCaseExternalId+"�������ǵ��ò����еķ�������"); 
		    	 caselog.caseLogDetail(taskid, testCaseExternalId, "�������ǵ��ò����еķ�������", "error","SETCASERESULT...", "");
		    	// TCResult = TestCaseApi.setTCResult(projectname,testCaseExternalId, testnote, version,2);
		    	 caselog.updateCaseDetail(taskid, testCaseExternalId, 2);
		     }
		    if(0==setresult){
		    	luckyclient.publicclass.LogUtil.APP.info("���� "+testCaseExternalId+"����ȫ��ִ�гɹ���"); 
		    	caselog.caseLogDetail(taskid, testCaseExternalId, "����ȫ��ִ�гɹ���", "info","EXECUTECASESUC...", "");
		    }else{
		    	luckyclient.publicclass.LogUtil.APP.error("���� "+testCaseExternalId+"��ִ�й�����ʧ�ܣ�������־��"); 
		    	caselog.caseLogDetail(taskid, testCaseExternalId, "��ִ�й�����ʧ�ܣ�������־��", "error","EXECUTECASESUC...", "");
		    }
		}else{
			luckyclient.publicclass.LogUtil.APP.error("���� "+testCaseExternalId+"����һ���Զ���������Ŷ����ȥ��������һ�°ɣ�"); 
			caselog.caseLogDetail(taskid, testCaseExternalId, "����һ���Զ���������Ŷ����ȥ��������һ�°ɣ�", "error","EXECUTECASEFAIL...", "");
		}
		LogOperation.updateTastdetail(taskid, 0);
	}

	
	/**
	 * @param ��Ŀ��
	 * @param �������
	 * @param �����汾��
	 * ������UI�Ĳ��Թ����У���Ҫ���ýӿڵĲ�������
	 */
	protected static String oneCaseExecuteForWebDriver(String testCaseExternalId,int version,String taskid,LogOperation caselog){
		Map<String,String> variable = new HashMap<String,String>(0);
		String packagename =null;
		String functionname = null;
		String expectedresults = null;
		Integer setresult = null;
		Object[] getParameterValues = null;
    	String testnote = null;
		int k = 0;
		TestCase testcaseob = TestCaseApi.getTestCaseByExternalId(testCaseExternalId, version);
		if(testcaseob.getExecutionType()==ExecutionType.AUTOMATED){
		    //����ѭ���������������в���
		    for(int i=0;i<testcaseob.getSteps().size();i++){		    	
		    	Map<String,String> casescript = InterfaceAnalyticTestLinkCase.analyticCaseStep(testcaseob, i+1,taskid,caselog);    
		    	packagename = casescript.get("PackageName").toString();
		    	functionname = casescript.get("FunctionName").toString();
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
				    				if(uservariable1.indexOf(key)>-1){
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
				    				if(uservariable2.indexOf(key)>-1){
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
					    		getParameterValues[j] = parameterValues;
			    			}else{
			    				luckyclient.publicclass.LogUtil.APP.error("û���ҵ���Ҫ�ı���Ŷ�������°ɣ���һ�����������ǣ�"+uservariable+"����"
			    						+ "�������������ǣ�"+uservariable1+"�����������������ǣ�"+uservariable2);			    		
			    			}

			    		}else{
				    		String parameterValues1 = casescript.get("FunctionParams"+(j+1));
					    	if(parameterValues1.indexOf("&quot;")>-1 || parameterValues1.indexOf("&#39;")>-1 || parameterValues1.indexOf("@@")>-1){        
					    		parameterValues1 = parameterValues1.replaceAll("&quot;", "\"");
					    		parameterValues1 = parameterValues1.replaceAll("&#39;", "\'");
					    		parameterValues1 = parameterValues1.replaceAll("@@", "@");
					    	}
				    		luckyclient.publicclass.LogUtil.APP.info("����������"+packagename+" ��������"+functionname
				    				+" ��"+(j+1)+"��������"+parameterValues1);				    		
				    		getParameterValues[j] = parameterValues1;
			    		}
			    	}
		    	}else{
		    		getParameterValues = null;
		    	}
		    	//���ö�̬������ִ�в�������
			    try{
			    	luckyclient.publicclass.LogUtil.APP.info("��ʼ���÷�����"+functionname+" .....");
		    		if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("$=")>-1){                            
			    		String expectedResultVariable = casescript.get("ExpectedResults").toString().substring(2);
			    		String temptestnote = InvokeMethod.callCase(packagename,functionname,getParameterValues,0,"");
			    		variable.put(expectedResultVariable, temptestnote);
			    	}else if(expectedresults.length()>2 && expectedresults.substring(0, 2).indexOf("%=")>-1){                    
				    	testnote = InvokeMethod.callCase(packagename,functionname,getParameterValues,0,"");
				    	if(testnote.indexOf(expectedresults.substring(2))>-1){
				    		setresult = 0;
				    		luckyclient.publicclass.LogUtil.APP.info("����ִ�н���ǣ�"+testnote+"����Ԥ�ڽ��ƥ��ɹ���");
				    	}else{
				    		setresult = 1;
				    		luckyclient.publicclass.LogUtil.APP.error("������"+(i+1)+"��ִ�н����Ԥ�ڽ��ƥ��ʧ�ܣ�");
				    		luckyclient.publicclass.LogUtil.APP.error("Ԥ�ڽ����"+expectedresults+"      ���Խ����"+testnote);
				    		testnote = "������"+(i+1)+"��ִ�н����Ԥ�ڽ��ƥ��ʧ�ܣ�";
				    		break;        //ĳһ����ʧ�ܺ󣬴���������Ϊʧ���˳�
				    	}
			    	}else{                                                                                                                          //��Ԥ�ڽ������Խ������ȷƥ��
				    	testnote = InvokeMethod.callCase(packagename,functionname,getParameterValues,0,"");
				    	if("".equals(expectedresults)||testnote.equals(expectedresults)){
				    		setresult = 0;
				    		luckyclient.publicclass.LogUtil.APP.info("����ִ�н���ǣ�"+testnote+"����Ԥ�ڽ��ƥ��ɹ���");
				    	}else{
				    		setresult = 1;
				    		luckyclient.publicclass.LogUtil.APP.error("������"+(i+1)+"��ִ�н����Ԥ�ڽ��ƥ��ʧ�ܣ�");
				    		luckyclient.publicclass.LogUtil.APP.error("Ԥ�ڽ����"+expectedresults+"      ���Խ����"+testnote);
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
			    	luckyclient.publicclass.LogUtil.APP.error("���÷������̳�����������"+functionname+" �����¼��ű����������Լ�������");
			    	luckyclient.publicclass.LogUtil.APP.error(e,e);
					testnote = "CallCase���ó���";
					setresult = 1;
					e.printStackTrace();
	    			break;
			    }			    
		    }
		    variable.clear();               //��մ���MAP
		    if(0==setresult){
		    	luckyclient.publicclass.LogUtil.APP.info("���� "+testcaseob.getFullExternalId()+"����ȫ��ִ�гɹ���"); 
		    }else{
		    	luckyclient.publicclass.LogUtil.APP.error("���� "+testcaseob.getFullExternalId()+"��ִ�й�����ʧ�ܣ�������־��"); 
		    }
		}else{
			luckyclient.publicclass.LogUtil.APP.error("���� "+testcaseob.getFullExternalId()+"����һ���Զ���������Ŷ����ȥ��������һ�°ɣ�");			
		}
		return testnote;
	}
	
}
