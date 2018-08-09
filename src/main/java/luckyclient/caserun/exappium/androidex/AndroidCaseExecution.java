package luckyclient.caserun.exappium.androidex;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebElement;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import luckyclient.caserun.exappium.AppDriverAnalyticCase;
import luckyclient.caserun.exinterface.TestCaseExecution;
import luckyclient.dblog.LogOperation;
import luckyclient.planapi.entity.ProjectCase;
import luckyclient.planapi.entity.ProjectCasesteps;
import luckyclient.planapi.entity.PublicCaseParams;
import luckyclient.publicclass.ChangString;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @author seagull
 * @date 2018��1��21�� ����15:12:48
 */
public class AndroidCaseExecution extends TestCaseExecution{
	static Map<String, String> variable = new HashMap<String, String>();

	public static void caseExcution(ProjectCase testcase, List<ProjectCasesteps> steps,String taskid, AndroidDriver<AndroidElement> appium,LogOperation caselog,List<PublicCaseParams> pcplist)
			throws InterruptedException, IOException {
		// 0:�ɹ� 1:ʧ�� 2:���� ����������
		int setresult = 0; 
		String casenote = "��ע��ʼ��";
		String imagname = "";
		// �ѹ����������뵽MAP��
		for (PublicCaseParams pcp : pcplist) {
			variable.put(pcp.getParamsname(), pcp.getParamsvalue());
		}
		//���뿪ʼִ�е�����
		caselog.addCaseDetail(taskid, testcase.getSign(), "1", testcase.getName(), 4);       
		
		for (ProjectCasesteps step : steps) {
			Map<String, String> params = AppDriverAnalyticCase.analyticCaseStep(testcase, step, taskid,caselog);
			
			if(params.get("exception")!=null&&params.get("exception").toString().indexOf("�����쳣")>-1){
				setresult = 2;
				break;
			}
			
			String result = runStep(params, appium, taskid, testcase.getSign(), step.getStepnum(), caselog);

			String expectedResults = params.get("ExpectedResults").toString();
			expectedResults=ChangString.changparams(expectedResults, variable,"Ԥ�ڽ��");
			// ���н������
			if (result.indexOf("����") < 0 && result.indexOf("ʧ��") < 0) { 
				// ��ȡ�����ȴ�ʱ��
				int waitsec = Integer.parseInt(params.get("StepWait").toString()); 
				if (waitsec != 0) {
					luckyclient.publicclass.LogUtil.APP.info("�������ߡ�"+waitsec+"����");
					Thread.sleep(waitsec * 1000);
				}
				// ��Ԥ�ڽ��
				if (!"".equals(expectedResults)) { 
					// �жϴ���
					luckyclient.publicclass.LogUtil.APP.info("expectedResults=��"+expectedResults+"��");
					if (expectedResults.length() > 2 && expectedResults.substring(0, 2).indexOf("$=") > -1) {
						String expectedResultVariable = expectedResults.substring(2);
						variable.put(expectedResultVariable, result);
						continue;
					}

					// �ж�Ԥ�ڽ��-���ģʽ
					if (params.get("checkproperty") != null && params.get("checkproperty_value") != null) {
						String checkproperty = params.get("checkproperty").toString();
						String checkPropertyValue = params.get("checkproperty_value").toString();

						WebElement we = isElementExist(appium, checkproperty, checkPropertyValue);
						if (null != we) {
							luckyclient.publicclass.LogUtil.APP.info("������" + testcase.getSign() + " ��" + step.getStepnum()
									+ "�����ڵ�ǰAPPҳ�����ҵ�Ԥ�ڽ���ж��󡣵�ǰ����ִ�гɹ���");
							caselog.caseLogDetail(taskid, testcase.getSign(), "�ڵ�ǰAPPҳ�����ҵ�Ԥ�ڽ���ж��󡣵�ǰ����ִ�гɹ���",
									"info", String.valueOf(step.getStepnum()),"");
							continue;
						} else {
							casenote = "��" + step.getStepnum() + "����û���ڵ�ǰAPPҳ�����ҵ�Ԥ�ڽ���ж���ִ��ʧ�ܣ�";
							setresult = 1;
							java.text.DateFormat timeformat = new java.text.SimpleDateFormat("MMdd-HHmmss");
							imagname = timeformat.format(new Date());
							AndroidBaseAppium.screenShot(appium, imagname);
							luckyclient.publicclass.LogUtil.APP.error("������" + testcase.getSign() + " ��" + step.getStepnum()
									+ "����û���ڵ�ǰAPPҳ�����ҵ�Ԥ�ڽ���ж��󡣵�ǰ����ִ��ʧ�ܣ�");
							caselog.caseLogDetail(taskid, testcase.getSign(), "�ڵ�ǰAPPҳ����û���ҵ�Ԥ�ڽ���ж��󡣵�ǰ����ִ��ʧ�ܣ�"
									+ "checkproperty��"+checkproperty+"��  checkproperty_value��"+checkPropertyValue+"��","error", String.valueOf(step.getStepnum()),imagname);
							break;
						}

					}else{
						// ģ��ƥ��Ԥ�ڽ��ģʽ
						if (expectedResults.length()>2 && expectedResults.substring(0, 2).indexOf("%=")>-1) {
							if(result.indexOf(expectedResults.substring(2))>-1){
								luckyclient.publicclass.LogUtil.APP.info("������" + testcase.getSign() + " ��" + step.getStepnum()
								+ "����ģ��ƥ��Ԥ�ڽ���ɹ���ִ�н����"+result);
						        caselog.caseLogDetail(taskid, testcase.getSign(), "����ģ��ƥ��Ԥ�ڽ���ɹ���",
								"info", String.valueOf(step.getStepnum()),"");
						        continue;
							}else{
								casenote = "��" + step.getStepnum() + "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�";
								setresult = 1;
								java.text.DateFormat timeformat = new java.text.SimpleDateFormat("MMdd-hhmmss");
								imagname = timeformat.format(new Date());
								AndroidBaseAppium.screenShot(appium, imagname);
								luckyclient.publicclass.LogUtil.APP.error("������" + testcase.getSign() + " ��" + step.getStepnum()
								+ "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�ִ�н����"+result);
						        caselog.caseLogDetail(taskid, testcase.getSign(), "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�ִ�н����"+result,
								"error", String.valueOf(step.getStepnum()),imagname);
								break;
							}
							// ֱ��ƥ��Ԥ�ڽ��ģʽ
						}else if(expectedResults.equals(result)) {    
							luckyclient.publicclass.LogUtil.APP.info("������" + testcase.getSign() + " ��" + step.getStepnum()
							+ "����ֱ��ƥ��Ԥ�ڽ���ɹ���ִ�н����"+result);
					        caselog.caseLogDetail(taskid, testcase.getSign(), "����ֱ��ƥ��Ԥ�ڽ���ɹ���",
							"info", String.valueOf(step.getStepnum()),"");
					        continue;
						} else {
							casenote = "��" + step.getStepnum() + "����ֱ��ƥ��Ԥ�ڽ��ʧ�ܣ�";
							setresult = 1;
							java.text.DateFormat timeformat = new java.text.SimpleDateFormat("MMdd-hhmmss");
							imagname = timeformat.format(new Date());
							AndroidBaseAppium.screenShot(appium, imagname);
							luckyclient.publicclass.LogUtil.APP.error("������" + testcase.getSign() + " ��" + step.getStepnum()
							+ "����ֱ��ƥ��Ԥ�ڽ��ʧ�ܣ�ִ�н����"+result);
					        caselog.caseLogDetail(taskid, testcase.getSign(), "����ֱ��ƥ��Ԥ�ڽ��ʧ�ܣ�ִ�н����"+result,
							"error", String.valueOf(step.getStepnum()),imagname);
							break;
						}
					}
				}

			} else {
				casenote = result;
				setresult = 2;
				java.text.DateFormat timeformat = new java.text.SimpleDateFormat("MMdd-hhmmss");
				imagname = timeformat.format(new Date());
				AndroidBaseAppium.screenShot(appium, imagname);
				luckyclient.publicclass.LogUtil.APP.error("������" + testcase.getSign() + " ��" + step.getStepnum()	+ "����"+result);
		        caselog.caseLogDetail(taskid, testcase.getSign(), "��ǰ������ִ�й����н���|��λԪ��|��������ʧ�ܣ�"+result,
				"error", String.valueOf(step.getStepnum()),imagname);
				break;
			}

		}

		variable.clear();
		caselog.updateCaseDetail(taskid, testcase.getSign(), setresult);
		if(setresult==0){
			luckyclient.publicclass.LogUtil.APP.info("������"+testcase.getSign()+"��ȫ������ִ�н���ɹ�...");
	        caselog.caseLogDetail(taskid, testcase.getSign(), "����ȫ������ִ�н���ɹ�","info", "ending","");
		}else{
			luckyclient.publicclass.LogUtil.APP.error("������"+testcase.getSign()+"������ִ�й�����ʧ�ܻ�������...��鿴����ԭ��"+casenote);
	        caselog.caseLogDetail(taskid, testcase.getSign(), "����ִ�й�����ʧ�ܻ�������"+casenote,"error", "ending","");
		}
		//LogOperation.UpdateTastdetail(taskid, 0);
	}

	private static String runStep(Map<String, String> params, AndroidDriver<AndroidElement> appium,String taskid,String casenum,int stepno,LogOperation caselog) {
		String result = "";
		String property;
		String propertyValue;
		String operation;
		String operationValue;

		try {
			property = params.get("property");
			propertyValue = params.get("property_value");
			operation = params.get("operation");
			operationValue = params.get("operation_value");

			// ����ֵ����
			property = ChangString.changparams(property, variable,"��λ��ʽ");
			propertyValue=ChangString.changparams(propertyValue, variable,"��λ·��");
			operation=ChangString.changparams(operation, variable,"����");
			operationValue=ChangString.changparams(operationValue, variable,"��������");
			
			luckyclient.publicclass.LogUtil.APP.info("���ν�������������ɣ��ȴ����ж������......");
			caselog.caseLogDetail(taskid, casenum, "�������:"+operation+"; ����ֵ:"+operationValue,"info", String.valueOf(stepno),"");
		} catch (Exception e) {
			e.printStackTrace();
			luckyclient.publicclass.LogUtil.APP.error("���ν������������׳��쳣��---"+e.getMessage());
			return "��������ʧ��!";
		}

		try {		
			//���ýӿ�����
			if(null != operation&&null != operationValue&&"runcase".equals(operation)){
				String[] temp=operationValue.split(",",-1);
				String ex = TestCaseExecution.oneCaseExecuteForWebCase(temp[0], taskid, caselog, appium);
				if(ex.indexOf("CallCase���ó���")<=-1&&ex.indexOf("������������")<=-1&&ex.indexOf("ƥ��ʧ��")<=-1){
					return ex;
				}else{
					return "���ýӿ���������ʧ��";
				}
			}
			
			AndroidElement ae = null;
			// ҳ��Ԫ�ز�
			if (null != property && null != propertyValue) { 
				ae = isElementExist(appium, property, propertyValue);
				// �жϴ�Ԫ���Ƿ����
				if (null==ae) {
					luckyclient.publicclass.LogUtil.APP.error("��λ����ʧ�ܣ�isElementExistΪnull!");
					return "isElementExist��λԪ�ع���ʧ�ܣ�";
				}

				if (operation.indexOf("select") > -1) {
					result = AndroidEncapsulateOperation.selectOperation(ae, operation, operationValue);
				} else if (operation.indexOf("get") > -1){
					result = AndroidEncapsulateOperation.getOperation(ae, operation,operationValue);
				} else {
					result = AndroidEncapsulateOperation.objectOperation(appium, ae, operation, operationValue, property, propertyValue);
				}
				// Driver�����
			} else if (null==property && null != operation) { 				
				// ���������¼�
				if (operation.indexOf("alert") > -1){
					result = AndroidEncapsulateOperation.alertOperation(appium, operation);
				}else{
					result = AndroidEncapsulateOperation.driverOperation(appium, operation, operationValue);
				} 				
			}else{
				luckyclient.publicclass.LogUtil.APP.error("Ԫ�ز�������ʧ�ܣ�");
				result =  "Ԫ�ز�������ʧ�ܣ�";
			}
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error("Ԫ�ض�λ���̻��ǲ�������ʧ�ܻ��쳣��"+e.getMessage());
			return "Ԫ�ض�λ���̻��ǲ�������ʧ�ܻ��쳣��" + e.getMessage();
		}
		caselog.caseLogDetail(taskid, casenum, result,"info", String.valueOf(stepno),"");
		
		if(result.indexOf("��ȡ����ֵ�ǡ�")>-1&&result.indexOf("��")>-1){
			result = result.substring(result.indexOf("��ȡ����ֵ�ǡ�")+7, result.length()-1);
		}
		return result;

	}

	public static AndroidElement isElementExist(AndroidDriver<AndroidElement> appium, String property, String propertyValue) {
		try {
			AndroidElement ae = null;
			property=property.toLowerCase();
			// ����WebElement����λ
			switch (property) {
			case "id":
				ae = appium.findElementById(propertyValue);
				break;
			case "name":
				ae = appium.findElementByAndroidUIAutomator("text(\""+propertyValue+"\")");
				break;
			case "androiduiautomator":
				ae = appium.findElementByAndroidUIAutomator(propertyValue);
				break;
			case "xpath":
				ae = appium.findElementByXPath(propertyValue);
				break;
			case "linktext":
				ae = appium.findElementByLinkText(propertyValue);
				break;
			case "tagname":
				ae = appium.findElementByTagName(propertyValue);
				break;
			case "cssselector":
				ae = appium.findElementByCssSelector(propertyValue);
				break;
			case "classname":
				ae = appium.findElementByClassName(propertyValue);
				break;
			case "partiallinktext":
				ae = appium.findElementByPartialLinkText(propertyValue);
				break;
			default:
				break;
			}

			return ae;

		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error("��ǰ����λʧ�ܣ�"+e.getMessage());
			return null;
		}
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
