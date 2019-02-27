package luckyclient.caserun.exinterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import luckyclient.caserun.exappium.AppDriverAnalyticCase;
import luckyclient.caserun.exappium.androidex.AndroidCaseExecution;
import luckyclient.caserun.exappium.iosex.IosCaseExecution;
import luckyclient.caserun.exinterface.analyticsteps.InterfaceAnalyticCase;
import luckyclient.caserun.exwebdriver.ex.WebCaseExecution;
import luckyclient.caserun.exwebdriver.ex.WebDriverAnalyticCase;
import luckyclient.caserun.publicdispose.ActionManageForSteps;
import luckyclient.caserun.publicdispose.ChangString;
import luckyclient.caserun.publicdispose.ParamsManageForSteps;
import luckyclient.dblog.DbLink;
import luckyclient.dblog.LogOperation;
import luckyclient.planapi.api.GetServerAPI;
import luckyclient.planapi.entity.ProjectCase;
import luckyclient.planapi.entity.ProjectCasesteps;
import luckyclient.planapi.entity.PublicCaseParams;
import luckyclient.publicclass.InvokeMethod;
import luckyclient.publicclass.LogUtil;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 *
 * @author�� seagull
 * @date 2018��3��1��
 */
public class TestCaseExecution {
    protected static final String ASSIGNMENT_SIGN = "$=";
    protected static final String FUZZY_MATCHING_SIGN = "%=";
    protected static final String REGULAR_MATCHING_SIGN = "~=";
    protected static final String ASSIGNMENT_GLOBALSIGN = "$A=";
    private static Map<String, String> VARIABLE = new HashMap<String, String>(0);
    
    /**
     * @param projectname        ��Ŀ��
     * @param testCaseExternalId �������
     * @param version            �����汾��
     *                           ���ڵ����������ԣ���ͨ����־���д��־��UTP�ϣ�����UTP�ϵ�����������
     */
    public static void oneCaseExecuteForTast(String projectname, String testCaseExternalId, int version, String taskid) {
        TestControl.TASKID = taskid;
        DbLink.exetype = 0;
        // ��ʼ��д��������Լ���־ģ��
        LogOperation caselog = new LogOperation();
        String packagename = null;
        String functionname = null;
        String expectedresults = null;
        Integer setcaseresult = 0;
        Object[] getParameterValues = null;
        String testnote = "��ʼ�����Խ��";
        int k = 0;
        // ɾ���ɵ���־
        LogOperation.deleteCaseLogDetail(testCaseExternalId, taskid);
        ProjectCase testcaseob = GetServerAPI.cgetCaseBysign(testCaseExternalId);
        List<PublicCaseParams> pcplist = GetServerAPI.cgetParamsByProjectid(String.valueOf(testcaseob.getProjectid()));
        // �ѹ����������뵽MAP��
        for (PublicCaseParams pcp : pcplist) {
        	VARIABLE.put(pcp.getParamsname(), pcp.getParamsvalue());
        }
        // ����ȫ�ֱ���
        VARIABLE.putAll(ParamsManageForSteps.GLOBAL_VARIABLE);
        List<ProjectCasesteps> steps = GetServerAPI.getStepsbycaseid(testcaseob.getId());
        if (steps.size() == 0) {
            setcaseresult = 2;
            luckyclient.publicclass.LogUtil.APP.error("������δ�ҵ����裬���飡");
            caselog.caseLogDetail(taskid, testCaseExternalId, "������δ�ҵ����裬���飡", "error", "1", "");
            testnote = "������δ�ҵ����裬���飡";
        }
        // ����ѭ���������������в���
        for (int i = 0; i < steps.size(); i++) {
            Map<String, String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcaseob, steps.get(i), taskid, caselog);
            try {
                packagename = casescript.get("PackageName");
                packagename = ChangString.changparams(packagename, VARIABLE, "��·��");
                functionname = casescript.get("FunctionName");
                functionname = ChangString.changparams(functionname, VARIABLE, "������");
            } catch (Exception e) {
                k = 0;
                luckyclient.publicclass.LogUtil.APP.error("������" + testcaseob.getSign() + "�����������Ƿ�����ʧ�ܣ����飡");
                caselog.caseLogDetail(taskid, testcaseob.getSign(), "�����������Ƿ�����ʧ�ܣ����飡", "error", String.valueOf(i + 1), "");
                e.printStackTrace();
                break; // ĳһ����ʧ�ܺ󣬴���������Ϊʧ���˳�
            }
            // �������ƽ��������쳣���ǵ���������������쳣
            if ((null != functionname && functionname.contains("�����쳣")) || k == 1) {
                k = 0;
                testnote = "������" + (i + 1) + "��������������";
                break;
            }
            expectedresults = casescript.get("ExpectedResults");
            expectedresults = ChangString.changparams(expectedresults, VARIABLE, "Ԥ�ڽ��");
            // �жϷ����Ƿ������
            if (casescript.size() > 4) {
                // ��ȡ�����������������У���ʼ�������������
                getParameterValues = new Object[casescript.size() - 4];
                for (int j = 0; j < casescript.size() - 4; j++) {
                    if (casescript.get("FunctionParams" + (j + 1)) == null) {
                        k = 1;
                        break;
                    }

                    String parameterValues = casescript.get("FunctionParams" + (j + 1));
                    parameterValues = ChangString.changparams(parameterValues, VARIABLE, "��������");
                    luckyclient.publicclass.LogUtil.APP.info("������" + testcaseob.getSign() + "����������" + packagename + " ��������" + functionname + " ��" + (j + 1) + "��������" + parameterValues);
                    caselog.caseLogDetail(taskid, testcaseob.getSign(), "����������" + packagename + " ��������" + functionname + " ��" + (j + 1) + "��������" + parameterValues, "info", String.valueOf(i + 1), "");
                    getParameterValues[j] = parameterValues;
                }
            } else {
                getParameterValues = null;
            }
            // ���ö�̬������ִ�в�������
            try {
                luckyclient.publicclass.LogUtil.APP.info("��ʼ���÷�����" + functionname + " .....");
                caselog.caseLogDetail(taskid, testCaseExternalId, "��ʼ���÷�����" + functionname + " .....", "info", String.valueOf(i + 1), "");
                testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues, steps.get(i).getSteptype(), steps.get(i).getExtend());
                testnote = ActionManageForSteps.actionManage(casescript.get("Action"), testnote);
                // �жϽ��
                int stepresult = interfaceJudgeResult(testcaseob, steps.get(i), taskid, expectedresults, testnote, caselog);
    			// ʧ�ܣ����Ҳ��ڼ���,ֱ����ֹ
                if (0 != stepresult) {
                	setcaseresult = stepresult;
                    if (testcaseob.getFailcontinue() == 0) {
                        luckyclient.publicclass.LogUtil.APP.error("������"+testcaseob.getSign()+"���ڡ�"+steps.get(i).getStepnum()+"������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......");
                        break;
                    } else {
                        luckyclient.publicclass.LogUtil.APP.error("������"+testcaseob.getSign()+"���ڡ�"+steps.get(i).getStepnum()+"������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......");
                    }
                }
                
            } catch (Exception e) {
                luckyclient.publicclass.LogUtil.ERROR.error("���÷������̳�����������" + functionname + " �����¼��ű����������Լ�������");
                caselog.caseLogDetail(taskid, testCaseExternalId, "���÷������̳�����������" + functionname + " �����¼��ű����������Լ�������", "error", String.valueOf(i + 1), "");
                luckyclient.publicclass.LogUtil.ERROR.error(e, e);
                testnote = "CallCase���ó���";
                setcaseresult = 1;
                e.printStackTrace();
                if (testcaseob.getFailcontinue() == 0) {
                    luckyclient.publicclass.LogUtil.APP.error("������"+testcaseob.getSign()+"���ڡ�"+(i + 1)+"������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......");
                    break;
                } else {
                    luckyclient.publicclass.LogUtil.APP.error("������"+testcaseob.getSign()+"���ڡ�"+(i + 1)+"������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......");
                }
            }
        }
        
        VARIABLE.clear(); // ��մ���MAP
        // ������÷���������δ�����������ò��Խ������
        if (!testnote.contains("CallCase���ó���") && !testnote.contains("������������")) {
            luckyclient.publicclass.LogUtil.APP.info("���� " + testCaseExternalId + "�����ɹ������ɹ����������з�����������鿴ִ�н����");
            caselog.caseLogDetail(taskid, testCaseExternalId, "�����ɹ������ɹ����������з�����������鿴ִ�н����", "info", "SETCASERESULT...", "");
            caselog.updateCaseDetail(taskid, testCaseExternalId, setcaseresult);
        } else {
            setcaseresult = 1;
            luckyclient.publicclass.LogUtil.APP.error("���� " + testCaseExternalId + "�������ǵ��ò����еķ�������");
            caselog.caseLogDetail(taskid, testCaseExternalId, "�������ǵ��ò����еķ�������", "error", "SETCASERESULT...", "");
            caselog.updateCaseDetail(taskid, testCaseExternalId, 2);
        }
        if (0 == setcaseresult) {
            luckyclient.publicclass.LogUtil.APP.info("���� " + testCaseExternalId + "����ȫ��ִ�гɹ���");
            caselog.caseLogDetail(taskid, testCaseExternalId, "����ȫ��ִ�гɹ���", "info", "EXECUTECASESUC...", "");
        } else {
            luckyclient.publicclass.LogUtil.APP.error("���� " + testCaseExternalId + "��ִ�й�����ʧ�ܣ�������־��");
            caselog.caseLogDetail(taskid, testCaseExternalId, "��ִ�й�����ʧ�ܣ�������־��", "error", "EXECUTECASESUC...", "");
        }
        LogOperation.updateTastdetail(taskid, 0);
    }

    /**
     * @param testCaseExternalId �������
     * @param taskid             ����ID
     * @param caselog            ��־��������
     *                           ������UI�Ĳ��Թ����У���Ҫ���ýӿڵĲ�������
     * @deprecated
     */
    protected static String oneCaseExecuteForWebDriver(String testCaseExternalId, String taskid, LogOperation caselog) {
        Map<String, String> variable = new HashMap<String, String>(0);
        String packagename = null;
        String functionname = null;
        String expectedresults = null;
        Integer setresult = 1;
        Object[] getParameterValues = null;
        String testnote = "��ʼ�����Խ��";
        int k = 0;
        ProjectCase testcaseob = GetServerAPI.cgetCaseBysign(testCaseExternalId);
        List<PublicCaseParams> pcplist = GetServerAPI.cgetParamsByProjectid(String.valueOf(testcaseob.getProjectid()));
        // �ѹ����������뵽MAP��
        for (PublicCaseParams pcp : pcplist) {
            variable.put(pcp.getParamsname(), pcp.getParamsvalue());
        }
        List<ProjectCasesteps> steps = GetServerAPI.getStepsbycaseid(testcaseob.getId());
        if (steps.size() == 0) {
            setresult = 2;
            luckyclient.publicclass.LogUtil.APP.error("������δ�ҵ����裬���飡");
            caselog.caseLogDetail(taskid, testcaseob.getSign(), "������δ�ҵ����裬���飡", "error", "1", "");
            testnote = "������δ�ҵ����裬���飡";
        }
        // ����ѭ���������������в���
        for (int i = 0; i < steps.size(); i++) {
            Map<String, String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcaseob, steps.get(i), taskid, caselog);
            try {
                packagename = casescript.get("PackageName");
                packagename = ChangString.changparams(packagename, variable, "��·��");
                functionname = casescript.get("FunctionName");
                functionname = ChangString.changparams(functionname, variable, "������");
            } catch (Exception e) {
                k = 0;
                luckyclient.publicclass.LogUtil.APP.error("������" + testcaseob.getSign() + "�����������Ƿ�����ʧ�ܣ����飡");
                caselog.caseLogDetail(taskid, testcaseob.getSign(), "�����������Ƿ�����ʧ�ܣ����飡", "error", String.valueOf(i + 1), "");
                e.printStackTrace();
                break; // ĳһ����ʧ�ܺ󣬴���������Ϊʧ���˳�
            }
            // �������ƽ��������쳣���ǵ���������������쳣
            if ((null != functionname && functionname.contains("�����쳣")) || k == 1) {
                k = 0;
                testnote = "������" + (i + 1) + "��������������";
                break;
            }
            expectedresults = casescript.get("ExpectedResults");
            expectedresults = ChangString.changparams(expectedresults, variable, "Ԥ�ڽ��");
            // �жϷ����Ƿ������
            if (casescript.size() > 4) {
                // ��ȡ������������������
                getParameterValues = new Object[casescript.size() - 4];
                for (int j = 0; j < casescript.size() - 4; j++) {
                    if (casescript.get("FunctionParams" + (j + 1)) == null) {
                        k = 1;
                        break;
                    }
                    String parameterValues = casescript.get("FunctionParams" + (j + 1));
                    parameterValues = ChangString.changparams(parameterValues, variable, "��������");
                    luckyclient.publicclass.LogUtil.APP.info("������" + testcaseob.getSign() + "����������" + packagename + " ��������" + functionname + " ��" + (j + 1) + "��������" + parameterValues);
                    caselog.caseLogDetail(taskid, testcaseob.getSign(), "����������" + packagename + " ��������" + functionname + " ��" + (j + 1) + "��������" + parameterValues, "info", String.valueOf(i + 1), "");
                    getParameterValues[j] = parameterValues;
                }
            } else {
                getParameterValues = null;
            }
            // ���ö�̬������ִ�в�������
            try {
                luckyclient.publicclass.LogUtil.APP.info("��ʼ���÷�����" + functionname + " .....");

                testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues, steps.get(i).getSteptype(), steps.get(i).getExtend());
                testnote = ActionManageForSteps.actionManage(casescript.get("Action"), testnote);
                if (null != expectedresults && !expectedresults.isEmpty()) {
                    luckyclient.publicclass.LogUtil.APP.info("expectedResults=��" + expectedresults + "��");
                    // ��ֵ����
                    if (expectedresults.length() > ASSIGNMENT_SIGN.length() && expectedresults.startsWith(ASSIGNMENT_SIGN)) {
                        variable.put(expectedresults.substring(ASSIGNMENT_SIGN.length()), testnote);
                        luckyclient.publicclass.LogUtil.APP.info("������" + testcaseob.getSign() + " ��" + (i + 1) + "���������Խ����" + testnote + "����ֵ��������" + expectedresults.substring(ASSIGNMENT_SIGN.length()) + "��");
                    }
                    // ģ��ƥ��
                    else if (expectedresults.length() > FUZZY_MATCHING_SIGN.length() && expectedresults.startsWith(FUZZY_MATCHING_SIGN)) {
                        if (testnote.contains(expectedresults.substring(FUZZY_MATCHING_SIGN.length()))) {
                            setresult = 0;
                            luckyclient.publicclass.LogUtil.APP.info("������" + testcaseob.getSign() + " ��" + (i + 1) + "����ģ��ƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote);
                        } else {
                            setresult = 1;
                            luckyclient.publicclass.LogUtil.APP.error("������" + testcaseob.getSign() + " ��" + (i + 1) + "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults.substring(FUZZY_MATCHING_SIGN.length()) + "�����Խ����" + testnote);
                            testnote = "������" + (i + 1) + "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�";
                            break; // ĳһ����ʧ�ܺ󣬴���������Ϊʧ���˳�
                        }
                    }
                    // ����ƥ��
                    else if (expectedresults.length() > REGULAR_MATCHING_SIGN.length() && expectedresults.startsWith(REGULAR_MATCHING_SIGN)) {
                        Pattern pattern = Pattern.compile(expectedresults.substring(REGULAR_MATCHING_SIGN.length()));
                        Matcher matcher = pattern.matcher(testnote);
                        if (matcher.find()) {
                            setresult = 0;
                            luckyclient.publicclass.LogUtil.APP.info("������" + testcaseob.getSign() + " ��" + (i + 1) + "��������ƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote);
                        } else {
                            setresult = 1;
                            luckyclient.publicclass.LogUtil.APP.error("������" + testcaseob.getSign() + " ��" + (i + 1) + "��������ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults.substring(REGULAR_MATCHING_SIGN.length()) + "�����Խ����" + testnote);
                            testnote = "������" + (i + 1) + "��������ƥ��Ԥ�ڽ��ʧ�ܣ�";
                            break; // ĳһ����ʧ�ܺ󣬴���������Ϊʧ���˳�
                        }
                    }
                    // ��ȫ���
                    else {
                        if (expectedresults.equals(testnote)) {
                            setresult = 0;
                            luckyclient.publicclass.LogUtil.APP.info("������" + testcaseob.getSign() + " ��" + (i + 1) + "������ȷƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote);
                        } else {
                            setresult = 1;
                            luckyclient.publicclass.LogUtil.APP.error("������" + testcaseob.getSign() + " ��" + (i + 1) + "������ȷƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults + "�����Խ����" + testnote);
                            testnote = "������" + (i + 1) + "������ȷƥ��Ԥ�ڽ��ʧ�ܣ�";
                            break; // ĳһ����ʧ�ܺ󣬴���������Ϊʧ���˳�
                        }
                    }
                }
            } catch (Exception e) {
                LogUtil.ERROR.error("���÷������̳�����������" + functionname + " �����¼��ű����������Լ�������");
                LogUtil.ERROR.error(e, e);
                testnote = "CallCase���ó���";
                setresult = 1;
                e.printStackTrace();
                break;
            }
        }
        variable.clear(); // ��մ���MAP
        if (0 == setresult) {
            luckyclient.publicclass.LogUtil.APP.info("���� " + testcaseob.getSign() + "����ȫ��ִ�гɹ���");
        } else {
            luckyclient.publicclass.LogUtil.APP.error("���� " + testcaseob.getSign() + "��ִ�й�����ʧ�ܣ�������־��");
        }
        return testnote;
    }

    /**
     * 
     * @param testCaseExternalId
     * @param taskid
     * @param caselog
     * @param driver
     * @return
     * @throws InterruptedException
     * �ṩ��Web�����У�runcase��ʱ��ʹ��
     */
    protected static String oneCaseExecuteForUICase(String testCaseExternalId, String taskid, LogOperation caselog, Object driver) throws InterruptedException {
        String expectedresults = null;
        Integer setresult = 1;
        String testnote = "��ʼ�����Խ��";
        ProjectCase testcaseob = GetServerAPI.cgetCaseBysign(testCaseExternalId);
        List<PublicCaseParams> pcplist = GetServerAPI.cgetParamsByProjectid(String.valueOf(testcaseob.getProjectid()));
        // �ѹ����������뵽MAP��
        for (PublicCaseParams pcp : pcplist) {
        	VARIABLE.put(pcp.getParamsname(), pcp.getParamsvalue());
        }
        // ����ȫ�ֱ���
        VARIABLE.putAll(ParamsManageForSteps.GLOBAL_VARIABLE);
        List<ProjectCasesteps> steps = GetServerAPI.getStepsbycaseid(testcaseob.getId());
        if (steps.size() == 0) {
            setresult = 2;
            luckyclient.publicclass.LogUtil.APP.error("������δ�ҵ����裬���飡");
            caselog.caseLogDetail(taskid, testcaseob.getSign(), "������δ�ҵ����裬���飡", "error", "1", "");
            testnote = "������δ�ҵ����裬���飡";
        }

        // ����ѭ���������������в���
        for (ProjectCasesteps step : steps) {
            Map<String, String> params;
            String result;

            // ���ݲ��������������������
            if (1 == step.getSteptype()){
            	params = WebDriverAnalyticCase.analyticCaseStep(testcaseob, step, taskid, caselog);
            }else if (4 == step.getSteptype()){
            	params = AppDriverAnalyticCase.analyticCaseStep(testcaseob, step, taskid,caselog);
            } else{
            	params = InterfaceAnalyticCase.analyticCaseStep(testcaseob, step, taskid, caselog);
            } 

            // �жϷ�����������Ƿ����쳣
            if (params.get("exception") != null && params.get("exception").contains("�����쳣")) {
                setresult = 2;
                break;
            }

            expectedresults = params.get("ExpectedResults");
            expectedresults = ChangString.changparams(expectedresults, VARIABLE, "Ԥ�ڽ��");
            
            // ���ݲ���������ִ�в���
            if (1 == step.getSteptype()){
            	WebDriver wd=(WebDriver)driver;
            	result = WebCaseExecution.runWebStep(params, VARIABLE, wd, taskid, testcaseob.getSign(), step.getStepnum(), caselog);
                // �жϽ��
                setresult = WebCaseExecution.judgeResult(testcaseob, step, params, wd, taskid, expectedresults, result, caselog);
            }else if (4 == step.getSteptype()){
            	if (driver instanceof AndroidDriver){
            		AndroidDriver<AndroidElement> ad=(AndroidDriver<AndroidElement>)driver;
            		result = AndroidCaseExecution.androidRunStep(params, VARIABLE, ad, taskid, testcaseob.getSign(), step.getStepnum(), caselog);
            		// �жϽ��
                    setresult = AndroidCaseExecution.judgeResult(testcaseob, step, params, ad, taskid, expectedresults, result, caselog);
            	}else{
            		IOSDriver<IOSElement> ios=(IOSDriver<IOSElement>)driver;
            		result = IosCaseExecution.iosRunStep(params, VARIABLE, ios, taskid, testcaseob.getSign(), step.getStepnum(), caselog);
            		// �жϽ��
                    setresult = IosCaseExecution.judgeResult(testcaseob, step, params, ios, taskid, expectedresults, result, caselog);
            	}
            	
            } else{
            	result = runStep(params, VARIABLE, taskid, testcaseob.getSign(), step, caselog);
            	// �жϽ��
            	setresult = interfaceJudgeResult(testcaseob, step, taskid, expectedresults, testnote, caselog);
            } 

            if (0 != setresult){
            	break;
            }
        }

        VARIABLE.clear(); // ��մ���MAP
        if (0 == setresult) {
            testnote = "����������" + testcaseob.getSign() + "��ִ�гɹ���";
            luckyclient.publicclass.LogUtil.APP.info("���� " + testcaseob.getSign() + "����ȫ��ִ�гɹ���");
        } else {
            testnote = "����������" + testcaseob.getSign() + "��ִ��ʧ�ܣ�������־��";
            luckyclient.publicclass.LogUtil.APP.error("���� " + testcaseob.getSign() + "��ִ�й�����ʧ�ܣ�������־��");
        }
        return testnote;
    }
    
    /**
     * �������Ͳ��������е��ýӿڲ��Բ���
     * @param params
     * @param variable
     * @param taskid
     * @param casenum
     * @param step
     * @param caselog
     * @return
     */
    public static String runStep(Map<String, String> params, Map<String, String> variable, String taskid, String casenum, ProjectCasesteps step, LogOperation caselog) {
        String result = "";
        String packagename = "";
        String functionname = "";
        Object[] getParameterValues = null;

        try {
            packagename = params.get("PackageName");
            packagename = ChangString.changparams(packagename, variable, "��·��");
            functionname = params.get("FunctionName");
            functionname = ChangString.changparams(functionname, variable, "������");

            if (null != functionname && functionname.contains("�����쳣")) {
                LogUtil.APP.error("����: " + casenum + ", �������������" + functionname + "��ʧ�ܣ�");
                caselog.caseLogDetail(taskid, casenum, "����: " + casenum + ", �������������" + functionname + "��ʧ�ܣ�", "error", String.valueOf(step.getStepnum()), "");
                result = "����ִ��ʧ�ܣ���������ʧ��!";
            } else {
                // �жϷ����Ƿ������
                if (params.size() > 4) {
                    // ��ȡ������������������
                    getParameterValues = new Object[params.size() - 4];
                    for (int j = 0; j < params.size() - 4; j++) {
                        if (params.get("FunctionParams" + (j + 1)) == null) {
                            break;
                        }
                        String parameterValues = params.get("FunctionParams" + (j + 1));
                        parameterValues = ChangString.changparams(parameterValues, variable, "��������");
                        luckyclient.publicclass.LogUtil.APP.info("����: " + casenum + ", ������·����" + packagename + "; ��������" + functionname + " ��" + (j + 1) + "��������" + parameterValues);
                        caselog.caseLogDetail(taskid, casenum, "����: " + casenum + ", ����������" + packagename + " ��������" + functionname + " ��" + (j + 1) + "��������" + parameterValues, "info", String.valueOf(step.getStepnum()), "");
                        getParameterValues[j] = parameterValues;
                    }
                } else {
                    getParameterValues = null;
                }

                LogUtil.APP.info("���ν�������������ɣ��ȴ����нӿڲ���......");
                caselog.caseLogDetail(taskid, casenum, "��·��: " + packagename + "; ������: " + functionname, "info", String.valueOf(step.getStepnum()), "");

                result = InvokeMethod.callCase(packagename, functionname, getParameterValues, step.getSteptype(), step.getExtend());
                result = ActionManageForSteps.actionManage(step.getAction(), result);
            }
        } catch (Exception e) {
            LogUtil.APP.error("���÷������̳�����������" + functionname + "�������¼��ű����������Լ�������");
            result = "����ִ��ʧ�ܣ��ӿڵ��ó���";
        }
        if (result.contains("����ִ��ʧ�ܣ�")) caselog.caseLogDetail(taskid, casenum, result, "error", String.valueOf(step.getStepnum()), "");
        else caselog.caseLogDetail(taskid, casenum, result, "info", String.valueOf(step.getStepnum()), "");
        return result;
    }
    
    private static int interfaceJudgeResult(ProjectCase testcase, ProjectCasesteps step, String taskid, String expectedresults, String testnote, LogOperation caselog){
        int setresult = 0;
        try{
        	if (null != expectedresults && !expectedresults.isEmpty()) {
                luckyclient.publicclass.LogUtil.APP.info("expectedResults=��" + expectedresults + "��");
                // ��ֵ����
                if (expectedresults.length() > ASSIGNMENT_SIGN.length() && expectedresults.startsWith(ASSIGNMENT_SIGN)) {
                	VARIABLE.put(expectedresults.substring(ASSIGNMENT_SIGN.length()), testnote);
                    luckyclient.publicclass.LogUtil.APP.info("������" + testcase.getSign() + " ��" + step.getStepnum() + "���������Խ����" + testnote + "����ֵ��������" + expectedresults.substring(ASSIGNMENT_SIGN.length()) + "��");
                    caselog.caseLogDetail(taskid, testcase.getSign(), "�����Խ����" + testnote + "����ֵ��������" + expectedresults.substring(ASSIGNMENT_SIGN.length()) + "��", "info", String.valueOf(step.getStepnum()), "");
                }
                // ��ֵȫ�ֱ���
                else if (expectedresults.length() > ASSIGNMENT_GLOBALSIGN.length() && expectedresults.startsWith(ASSIGNMENT_GLOBALSIGN)) {
                	VARIABLE.put(expectedresults.substring(ASSIGNMENT_GLOBALSIGN.length()), testnote);
                    ParamsManageForSteps.GLOBAL_VARIABLE.put(expectedresults.substring(ASSIGNMENT_GLOBALSIGN.length()), testnote);
                    luckyclient.publicclass.LogUtil.APP.info("������" + testcase.getSign() + " ��" + step.getStepnum() + "���������Խ����" + testnote + "����ֵ��ȫ�ֱ�����" + expectedresults.substring(ASSIGNMENT_GLOBALSIGN.length()) + "��");
                    caselog.caseLogDetail(taskid, testcase.getSign(), "�����Խ����" + testnote + "����ֵ��ȫ�ֱ�����" + expectedresults.substring(ASSIGNMENT_GLOBALSIGN.length()) + "��", "info", String.valueOf(step.getStepnum()), "");
                }
                // ģ��ƥ��
                else if (expectedresults.length() > FUZZY_MATCHING_SIGN.length() && expectedresults.startsWith(FUZZY_MATCHING_SIGN)) {
                    if (testnote.contains(expectedresults.substring(FUZZY_MATCHING_SIGN.length()))) {
                        luckyclient.publicclass.LogUtil.APP.info("������" + testcase.getSign() + " ��" + step.getStepnum() + "����ģ��ƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote);
                        caselog.caseLogDetail(taskid, testcase.getSign(), "ģ��ƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote, "info", String.valueOf(step.getStepnum()), "");
                    } else {
                    	setresult = 1;
                        luckyclient.publicclass.LogUtil.APP.error("������" + testcase.getSign() + " ��" + step.getStepnum() + "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults.substring(FUZZY_MATCHING_SIGN.length()) + "�����Խ����" + testnote);
                        caselog.caseLogDetail(taskid, testcase.getSign(), "ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults.substring(FUZZY_MATCHING_SIGN.length()) + "�����Խ����" + testnote, "error", String.valueOf(step.getStepnum()), "");
                        testnote = "������" + step.getStepnum() + "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�";
                    }
                }
                // ����ƥ��
                else if (expectedresults.length() > REGULAR_MATCHING_SIGN.length() && expectedresults.startsWith(REGULAR_MATCHING_SIGN)) {
                    Pattern pattern = Pattern.compile(expectedresults.substring(REGULAR_MATCHING_SIGN.length()));
                    Matcher matcher = pattern.matcher(testnote);
                    if (matcher.find()) {
                        luckyclient.publicclass.LogUtil.APP.info("������" + testcase.getSign() + " ��" + step.getStepnum() + "��������ƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote);
                        caselog.caseLogDetail(taskid, testcase.getSign(), "����ƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote, "info", String.valueOf(step.getStepnum()), "");
                    } else {
                        setresult = 1;
                        luckyclient.publicclass.LogUtil.APP.error("������" + testcase.getSign() + " ��" + step.getStepnum() + "��������ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults.substring(REGULAR_MATCHING_SIGN.length()) + "�����Խ����" + testnote);
                        caselog.caseLogDetail(taskid, testcase.getSign(), "����ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults.substring(REGULAR_MATCHING_SIGN.length()) + "�����Խ����" + testnote, "error", String.valueOf(step.getStepnum()), "");
                        testnote = "������" + step.getStepnum() + "��������ƥ��Ԥ�ڽ��ʧ�ܣ�";
                    }
                }
                // ��ȫ���
                else {
                    if (expectedresults.equals(testnote)) {
                        luckyclient.publicclass.LogUtil.APP.info("������" + testcase.getSign() + " ��" + step.getStepnum() + "������ȷƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote);
                        caselog.caseLogDetail(taskid, testcase.getSign(), "��ȷƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote, "info", String.valueOf(step.getStepnum()), "");
                    } else {
                        setresult = 1;
                        luckyclient.publicclass.LogUtil.APP.error("������" + testcase.getSign() + " ��" + step.getStepnum() + "������ȷƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults + "�����Խ����" + testnote);
                        caselog.caseLogDetail(taskid, testcase.getSign(), "��ȷƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults + "�����Խ����" + testnote, "error", String.valueOf(step.getStepnum()), "");
                        testnote = "������" + step.getStepnum() + "������ȷƥ��Ԥ�ڽ��ʧ�ܣ�";
                    }
                }
            }
        }catch(Exception e){
        	e.printStackTrace();
        	setresult = 2; 
        	return setresult;
        }
        return setresult; 	
    }
    
    
}
