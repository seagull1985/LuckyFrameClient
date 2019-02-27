package luckyclient.caserun.exwebdriver.ex;

import luckyclient.caserun.exinterface.TestCaseExecution;
import luckyclient.caserun.exinterface.analyticsteps.InterfaceAnalyticCase;
import luckyclient.caserun.exwebdriver.BaseWebDrive;
import luckyclient.caserun.exwebdriver.EncapsulateOperation;
import luckyclient.caserun.publicdispose.ActionManageForSteps;
import luckyclient.caserun.publicdispose.ChangString;
import luckyclient.caserun.publicdispose.ParamsManageForSteps;
import luckyclient.dblog.LogOperation;
import luckyclient.planapi.entity.ProjectCase;
import luckyclient.planapi.entity.ProjectCasesteps;
import luckyclient.planapi.entity.PublicCaseParams;
import luckyclient.publicclass.LogUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class WebCaseExecution extends TestCaseExecution {
    private static Map<String, String> variable = new HashMap<>();
    private static String casenote = "��ע��ʼ��";
    private static String imagname = "";

    public static void caseExcution(ProjectCase testcase, List<ProjectCasesteps> steps, String taskid, WebDriver wd, LogOperation caselog, List<PublicCaseParams> pcplist) throws InterruptedException {
        // �ѹ����������뵽MAP��
        for (PublicCaseParams pcp : pcplist) {
            variable.put(pcp.getParamsname(), pcp.getParamsvalue());
        }
        // ����ȫ�ֱ���
        variable.putAll(ParamsManageForSteps.GLOBAL_VARIABLE);
        // 0:�ɹ� 1:ʧ�� 2:���� ����������
        int setcaseresult = 0;
        for (ProjectCasesteps step : steps) {
            Map<String, String> params;
            String result;

            // ���ݲ��������������������
            if (1 == step.getSteptype()){
            	params = WebDriverAnalyticCase.analyticCaseStep(testcase, step, taskid, caselog);
            }else{
            	params = InterfaceAnalyticCase.analyticCaseStep(testcase, step, taskid, caselog);
            }

            // �жϷ�����������Ƿ����쳣
            if (null != params.get("exception") && params.get("exception").contains("�����쳣")) {
            	setcaseresult = 2;
                break;
            }

            // ���ݲ���������ִ�в���
            if (1 == step.getSteptype()){
            	result = runWebStep(params, variable, wd, taskid, testcase.getSign(), step.getStepnum(), caselog);
            }else{
            	result = TestCaseExecution.runStep(params, variable, taskid, testcase.getSign(), step, caselog);
            }

            String expectedResults = params.get("ExpectedResults");
            expectedResults = ChangString.changparams(expectedResults, variable, "Ԥ�ڽ��");

            // �жϽ��
			int stepresult = judgeResult(testcase, step, params, wd, taskid, expectedResults, result, caselog);
			// ʧ�ܣ����Ҳ��ڼ���,ֱ����ֹ
            if (0 != stepresult) {
            	setcaseresult = stepresult;
                if (testcase.getFailcontinue() == 0) {
                    luckyclient.publicclass.LogUtil.APP.error("������"+testcase.getSign()+"���ڡ�"+step.getStepnum()+"������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......");
                    break;
                } else {
                    luckyclient.publicclass.LogUtil.APP.error("������"+testcase.getSign()+"���ڡ�"+step.getStepnum()+"������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......");
                }
            }
        }

        variable.clear();
        caselog.updateCaseDetail(taskid, testcase.getSign(), setcaseresult);
        if (setcaseresult == 0) {
            luckyclient.publicclass.LogUtil.APP.info("������" + testcase.getSign() + "��ȫ������ִ�н���ɹ�...");
            caselog.caseLogDetail(taskid, testcase.getSign(), "����ȫ������ִ�н���ɹ�", "info", "ending", "");
        } else {
            luckyclient.publicclass.LogUtil.APP.error("������" + testcase.getSign() + "������ִ�й�����ʧ�ܻ�������...��鿴����ԭ��" + casenote);
            caselog.caseLogDetail(taskid, testcase.getSign(), "����ִ�й�����ʧ�ܻ�������" + casenote, "error", "ending", "");
        }
    }

    public static String runWebStep(Map<String, String> params, Map<String, String> variable, WebDriver wd, String taskid, String casenum, int stepno, LogOperation caselog) {
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
            property = ChangString.changparams(property, variable, "��λ��ʽ");
            propertyValue = ChangString.changparams(propertyValue, variable, "��λ·��");
            operation = ChangString.changparams(operation, variable, "����");
            operationValue = ChangString.changparams(operationValue, variable, "��������");

            luckyclient.publicclass.LogUtil.APP.info("���ν�������������ɣ��ȴ����ж������......");
            caselog.caseLogDetail(taskid, casenum, "�������:" + operation + "; ����ֵ:" + operationValue, "info", String.valueOf(stepno), "");
        } catch (Exception e) {
            e.printStackTrace();
            luckyclient.publicclass.LogUtil.APP.error("���ν������������׳��쳣��---" + e.getMessage());
            return "����ִ��ʧ�ܣ���������ʧ��!";
        }

        try {
            //������һ��������֧�ֽӿڣ�web��������
            if (null != operation && null != operationValue && "runcase".equals(operation)) {
                String[] temp = operationValue.split(",", -1);
                String ex = TestCaseExecution.oneCaseExecuteForUICase(temp[0], taskid, caselog, wd);
                if (!ex.contains("CallCase���ó���") && !ex.contains("������������") && !ex.contains("ʧ��")) {
                    return ex;
                } else {
                    return "����ִ��ʧ�ܣ������ⲿ��������ʧ��";
                }
            }

            // ҳ��Ԫ�ز�
            if (null != property && null != propertyValue && null != operation) {
                WebElement we = isElementExist(wd, property, propertyValue);
                // �жϴ�Ԫ���Ƿ����
                if (null == we) {
                    luckyclient.publicclass.LogUtil.APP.error("��λ����ʧ�ܣ�isElementExistΪnull!");
                    return "����ִ��ʧ�ܣ���λ��Ԫ�ز����ڣ�";
                }

                if (operation.contains("select")) {
                    result = EncapsulateOperation.selectOperation(we, operation, operationValue);
                } else if (operation.contains("get")) {
                    result = EncapsulateOperation.getOperation(wd, we, operation, operationValue);
                } else if (operation.contains("mouse")) {
                    result = EncapsulateOperation.actionWeOperation(wd, we, operation, operationValue, property, propertyValue);
                } else {
                    result = EncapsulateOperation.objectOperation(wd, we, operation, operationValue, property, propertyValue);
                }
                // Driver�����
            } else if (null == property && null != operation) {
                // ���������¼�
                if (operation.contains("alert")) {
                    result = EncapsulateOperation.alertOperation(wd, operation);
                } else if (operation.contains("mouse")) {
                    result = EncapsulateOperation.actionOperation(wd, operation, operationValue);
                } else {
                    result = EncapsulateOperation.driverOperation(wd, operation, operationValue);
                }
            } else {
                luckyclient.publicclass.LogUtil.APP.error("Ԫ�ز�������ʧ�ܣ�");
                result = "����ִ��ʧ�ܣ�Ԫ�ز�������ʧ�ܣ�";
            }
        } catch (Exception e) {
            luckyclient.publicclass.LogUtil.APP.error("Ԫ�ض�λ���̻��ǲ�������ʧ�ܻ��쳣��" + e.getMessage());
            return "����ִ��ʧ�ܣ�Ԫ�ض�λ���̻��ǲ�������ʧ�ܻ��쳣��" + e.getMessage();
        }

        if (result.contains("����ִ��ʧ�ܣ�")) caselog.caseLogDetail(taskid, casenum, result, "error", String.valueOf(stepno), "");
        else caselog.caseLogDetail(taskid, casenum, result, "info", String.valueOf(stepno), "");

        if (result.contains("��ȡ����ֵ�ǡ�") && result.contains("��")) {
            result = result.substring(result.indexOf("��ȡ����ֵ�ǡ�") + "��ȡ����ֵ�ǡ�".length(), result.length() - 1);
        }
        return result;

    }

    private static WebElement isElementExist(WebDriver wd, String property, String propertyValue) {
        try {
            WebElement we = null;
            property = property.toLowerCase();
            // ����WebElement����λ
            switch (property) {
                case "id":
                    we = wd.findElement(By.id(propertyValue));
                    break;
                case "name":
                    we = wd.findElement(By.name(propertyValue));
                    break;
                case "xpath":
                    we = wd.findElement(By.xpath(propertyValue));
                    break;
                case "linktext":
                    we = wd.findElement(By.linkText(propertyValue));
                    break;
                case "tagname":
                    we = wd.findElement(By.tagName(propertyValue));
                    break;
                case "cssselector":
                    we = wd.findElement(By.cssSelector(propertyValue));
                    break;
                default:
                    break;
            }

            return we;

        } catch (Exception e) {
            luckyclient.publicclass.LogUtil.APP.error("��ǰ����λʧ�ܣ�" + e.getMessage());
            return null;
        }

    }

    public static int judgeResult(ProjectCase testcase, ProjectCasesteps step, Map<String, String> params, WebDriver driver, String taskid, String expect, String result, LogOperation caselog) throws InterruptedException {
        int setresult = 0;
        java.text.DateFormat timeformat = new java.text.SimpleDateFormat("MMdd-hhmmss");
        imagname = timeformat.format(new Date());
        
        result = ActionManageForSteps.actionManage(step.getAction(), result);
        if (null != result && !result.contains("����ִ��ʧ�ܣ�")) {
            // ��Ԥ�ڽ��
            if (null != expect && !expect.isEmpty()) {
                luckyclient.publicclass.LogUtil.APP.info("�������Ϊ��" + expect + "��");
                // ��ֵ����ģʽ
                if (expect.length() > ASSIGNMENT_SIGN.length() && expect.startsWith(ASSIGNMENT_SIGN)) {
                    variable.put(expect.substring(ASSIGNMENT_SIGN.length()), result);
                    luckyclient.publicclass.LogUtil.APP.info("������" + testcase.getSign() + " ��" + step.getStepnum() + "���������Խ����" + result + "����ֵ��������" + expect.substring(ASSIGNMENT_SIGN.length()) + "��");
                    caselog.caseLogDetail(taskid, testcase.getSign(), "�����Խ����" + result + "����ֵ��������" + expect.substring(ASSIGNMENT_SIGN.length()) + "��", "info", String.valueOf(step.getStepnum()), "");
                }
                // ��ֵȫ�ֱ���
                else if (expect.length() > ASSIGNMENT_GLOBALSIGN.length() && expect.startsWith(ASSIGNMENT_GLOBALSIGN)) {
                	variable.put(expect.substring(ASSIGNMENT_GLOBALSIGN.length()), result);
                	ParamsManageForSteps.GLOBAL_VARIABLE.put(expect.substring(ASSIGNMENT_GLOBALSIGN.length()), result);
                    luckyclient.publicclass.LogUtil.APP.info("������" + testcase.getSign() + " ��" + step.getStepnum() + "���������Խ����" + result + "����ֵ��ȫ�ֱ�����" + expect.substring(ASSIGNMENT_GLOBALSIGN.length()) + "��");
                    caselog.caseLogDetail(taskid, testcase.getSign(), "�����Խ����" + result + "����ֵ��ȫ�ֱ�����" + expect.substring(ASSIGNMENT_GLOBALSIGN.length()) + "��", "info", String.valueOf(step.getStepnum()), "");
                }
                // WebUI���ģʽ
                else if (1 == step.getSteptype() && params.get("checkproperty") != null && params.get("checkproperty_value") != null) {
                    String checkproperty = params.get("checkproperty");
                    String checkPropertyValue = params.get("checkproperty_value");

                    WebElement we = isElementExist(driver, checkproperty, checkPropertyValue);
                    if (null != we) {
                        luckyclient.publicclass.LogUtil.APP.info("������" + testcase.getSign() + " ��" + step.getStepnum() + "�����ڵ�ǰҳ�����ҵ�Ԥ�ڽ���ж��󡣵�ǰ����ִ�гɹ���");
                        caselog.caseLogDetail(taskid, testcase.getSign(), "�ڵ�ǰҳ�����ҵ�Ԥ�ڽ���ж��󡣵�ǰ����ִ�гɹ���", "info", String.valueOf(step.getStepnum()), "");
                    } else {
                        casenote = "��" + step.getStepnum() + "����û���ڵ�ǰҳ�����ҵ�Ԥ�ڽ���ж���ִ��ʧ�ܣ�";
                        setresult = 1;
                        BaseWebDrive.webScreenShot(driver, imagname);
                        luckyclient.publicclass.LogUtil.APP.error("������" + testcase.getSign() + " ��" + step.getStepnum() + "����û���ڵ�ǰҳ�����ҵ�Ԥ�ڽ���ж��󡣵�ǰ����ִ��ʧ�ܣ�");
                        caselog.caseLogDetail(taskid, testcase.getSign(), "�ڵ�ǰҳ����û���ҵ�Ԥ�ڽ���ж��󡣵�ǰ����ִ��ʧ�ܣ�" + "checkproperty��" + checkproperty + "��  checkproperty_value��" + checkPropertyValue + "��", "error", String.valueOf(step.getStepnum()), imagname);
                    }
                }
                // ����ƥ��ģʽ
                else {
                    // ģ��ƥ��Ԥ�ڽ��ģʽ
                    if (expect.length() > FUZZY_MATCHING_SIGN.length() && expect.startsWith(FUZZY_MATCHING_SIGN)) {
                        if (result.contains(expect.substring(FUZZY_MATCHING_SIGN.length()))) {
                            luckyclient.publicclass.LogUtil.APP.info("������" + testcase.getSign() + " ��" + step.getStepnum() + "����ģ��ƥ��Ԥ�ڽ���ɹ���ִ�н����" + result);
                            caselog.caseLogDetail(taskid, testcase.getSign(), "ģ��ƥ��Ԥ�ڽ���ɹ���ִ�н����" + result, "info", String.valueOf(step.getStepnum()), "");
                        } else {
                            casenote = "��" + step.getStepnum() + "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�";
                            setresult = 1;
                            BaseWebDrive.webScreenShot(driver, imagname);
                            luckyclient.publicclass.LogUtil.APP.error("������" + testcase.getSign() + " ��" + step.getStepnum() + "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expect.substring(FUZZY_MATCHING_SIGN.length()) + "�����Խ����" + result);
                            caselog.caseLogDetail(taskid, testcase.getSign(), "ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expect.substring(FUZZY_MATCHING_SIGN.length()) + "�����Խ����" + result, "error", String.valueOf(step.getStepnum()), imagname);
                        }
                    }
                    // ����ƥ��Ԥ�ڽ��ģʽ
                    else if (expect.length() > REGULAR_MATCHING_SIGN.length() && expect.startsWith(REGULAR_MATCHING_SIGN)) {
                        Pattern pattern = Pattern.compile(expect.substring(REGULAR_MATCHING_SIGN.length()));
                        Matcher matcher = pattern.matcher(result);
                        if (matcher.find()) {
                            luckyclient.publicclass.LogUtil.APP.info("������" + testcase.getSign() + " ��" + step.getStepnum() + "��������ƥ��Ԥ�ڽ���ɹ���ִ�н����" + result);
                            caselog.caseLogDetail(taskid, testcase.getSign(), "����ƥ��Ԥ�ڽ���ɹ���", "info", String.valueOf(step.getStepnum()), "");
                        } else {
                            casenote = "��" + step.getStepnum() + "��������ƥ��Ԥ�ڽ��ʧ�ܣ�";
                            setresult = 1;
                            BaseWebDrive.webScreenShot(driver, imagname);
                            luckyclient.publicclass.LogUtil.APP.error("������" + testcase.getSign() + " ��" + step.getStepnum() + "��������ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expect.substring(REGULAR_MATCHING_SIGN.length()) + "�����Խ����" + result);
                            caselog.caseLogDetail(taskid, testcase.getSign(), "����ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expect.substring(REGULAR_MATCHING_SIGN.length()) + "�����Խ����" + result, "error", String.valueOf(step.getStepnum()), imagname);
                        }
                    }
                    // ��ȷƥ��Ԥ�ڽ��ģʽ
                    else {
                        if (expect.equals(result)) {
                            luckyclient.publicclass.LogUtil.APP.info("������" + testcase.getSign() + " ��" + step.getStepnum() + "������ȷƥ��Ԥ�ڽ���ɹ���ִ�н����" + result);
                            caselog.caseLogDetail(taskid, testcase.getSign(), "��ȷƥ��Ԥ�ڽ���ɹ���", "info", String.valueOf(step.getStepnum()), "");
                        } else {
                            casenote = "��" + step.getStepnum() + "������ȷƥ��Ԥ�ڽ��ʧ�ܣ�";
                            setresult = 1;
                            BaseWebDrive.webScreenShot(driver, imagname);
                            luckyclient.publicclass.LogUtil.APP.error("������" + testcase.getSign() + " ��" + step.getStepnum() + "������ȷƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ���ǣ���"+expect+"��  ִ�н������"+ result+"��");
                            caselog.caseLogDetail(taskid, testcase.getSign(), "��ȷƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ���ǣ���"+expect+"��  ִ�н������"+ result+"��", "error", String.valueOf(step.getStepnum()), imagname);
                        }
                    }
                }
            }
        } else {
            casenote = (null != result) ? result : "";
            setresult = 2;
            BaseWebDrive.webScreenShot(driver, imagname);
            LogUtil.APP.error("������" + testcase.getSign() + " ��" + step.getStepnum() + "����ִ�н����" + casenote);
            caselog.caseLogDetail(taskid, testcase.getSign(), "��ǰ������ִ�й����н���|��λԪ��|��������ʧ�ܣ�" + casenote, "error", String.valueOf(step.getStepnum()), imagname);
        }
        
        return setresult;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
    }

}