package luckyclient.caserun.exinterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import luckyclient.caserun.exinterface.analyticsteps.InterfaceAnalyticCase;
import luckyclient.caserun.publicdispose.ActionManageForSteps;
import luckyclient.caserun.publicdispose.ChangString;
import luckyclient.caserun.publicdispose.ParamsManageForSteps;
import luckyclient.dblog.LogOperation;
import luckyclient.planapi.entity.ProjectCase;
import luckyclient.planapi.entity.ProjectCasesteps;
import luckyclient.planapi.entity.PublicCaseParams;
import luckyclient.publicclass.InvokeMethod;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 *
 * @ClassName: ThreadForExecuteCase
 * @Description: �̳߳ط�ʽִ������
 * @author�� seagull
 * @date 2018��3��1��
 */
public class ThreadForExecuteCase extends Thread {
    private static final String ASSIGNMENT_SIGN = "$=";
    private static final String ASSIGNMENT_GLOBALSIGN = "$A=";
    private static final String FUZZY_MATCHING_SIGN = "%=";
    private static final String REGULAR_MATCHING_SIGN = "~=";

    private String caseid;
    private ProjectCase testcaseob;
    private String taskid;
    private List<ProjectCasesteps> steps;
    private List<PublicCaseParams> pcplist;
    private LogOperation caselog;

    public ThreadForExecuteCase(ProjectCase projectcase, List<ProjectCasesteps> steps, String taskid, List<PublicCaseParams> pcplist, LogOperation caselog) {
        this.caseid = projectcase.getSign();
        this.testcaseob = projectcase;
        this.taskid = taskid;
        this.steps = steps;
        this.pcplist = pcplist;
        this.caselog = caselog;
    }

    @Override
    public void run() {
        Map<String, String> variable = new HashMap<>(0);
        // �ѹ����������뵽MAP��
        for (PublicCaseParams pcp : pcplist) {
            variable.put(pcp.getParamsname(), pcp.getParamsvalue());
        }
        // ����ȫ�ֱ���
        variable.putAll(ParamsManageForSteps.GLOBAL_VARIABLE);
        String functionname = null;
        String packagename = null;
        String expectedresults = null;
        Integer setcaseresult = 0;
        Object[] getParameterValues = null;
        String testnote = "��ʼ�����Խ��";
        int k = 0;
        // ����ѭ�������������������в���
        // ���뿪ʼִ�е�����
        caselog.addCaseDetail(taskid, caseid, "1", testcaseob.getName(), 4);
        for (int i = 0; i < steps.size(); i++) {
            // �������������еĽű�
            Map<String, String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcaseob, steps.get(i), taskid, caselog);
            try {
                packagename = casescript.get("PackageName");
                packagename = ChangString.changparams(packagename, variable, "��·��");
                functionname = casescript.get("FunctionName");
                functionname = ChangString.changparams(functionname, variable, "������");
            } catch (Exception e) {
                k = 0;
                luckyclient.publicclass.LogUtil.APP.error("������" + testcaseob.getSign() + "�����������Ƿ�����ʧ�ܣ����飡");
                caselog.caseLogDetail(taskid, caseid, "�����������Ƿ�����ʧ�ܣ����飡", "error", String.valueOf(i + 1), "");
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
                    caselog.caseLogDetail(taskid, caseid, "����������" + packagename + " ��������" + functionname + " ��" + (j + 1) + "��������" + parameterValues, "info", String.valueOf(i + 1), "");
                    getParameterValues[j] = parameterValues;
                }
            } else {
                getParameterValues = null;
            }
            // ���ö�̬������ִ�в�������
            try {
                luckyclient.publicclass.LogUtil.APP.info("������" + testcaseob.getSign() + "��ʼ���÷�����" + functionname + " .....");
                caselog.caseLogDetail(taskid, caseid, "��ʼ���÷�����" + functionname + " .....", "info", String.valueOf(i + 1), "");

                testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues, steps.get(i).getSteptype(), steps.get(i).getExtend());
                testnote = ActionManageForSteps.actionManage(casescript.get("Action"), testnote);
                if (null != expectedresults && !expectedresults.isEmpty()) {
                    luckyclient.publicclass.LogUtil.APP.info("expectedResults=��" + expectedresults + "��");
                    // ��ֵ����
                    if (expectedresults.length() > ASSIGNMENT_SIGN.length() && expectedresults.startsWith(ASSIGNMENT_SIGN)) {
                        variable.put(expectedresults.substring(ASSIGNMENT_SIGN.length()), testnote);
                        luckyclient.publicclass.LogUtil.APP.info("������" + testcaseob.getSign() + " ��" + (i + 1) + "���������Խ����" + testnote + "����ֵ��������" + expectedresults.substring(ASSIGNMENT_SIGN.length()) + "��");
                        caselog.caseLogDetail(taskid, caseid, "�����Խ����" + testnote + "����ֵ��������" + expectedresults.substring(ASSIGNMENT_SIGN.length()) + "��", "info", String.valueOf(i + 1), "");
                    }
                    // ��ֵȫ�ֱ���
                    else if (expectedresults.length() > ASSIGNMENT_GLOBALSIGN.length() && expectedresults.startsWith(ASSIGNMENT_GLOBALSIGN)) {
                        variable.put(expectedresults.substring(ASSIGNMENT_GLOBALSIGN.length()), testnote);
                        ParamsManageForSteps.GLOBAL_VARIABLE.put(expectedresults.substring(ASSIGNMENT_GLOBALSIGN.length()), testnote);
                        luckyclient.publicclass.LogUtil.APP.info("������" + testcaseob.getSign() + " ��" + (i + 1) + "���������Խ����" + testnote + "����ֵ��ȫ�ֱ�����" + expectedresults.substring(ASSIGNMENT_GLOBALSIGN.length()) + "��");
                        caselog.caseLogDetail(taskid, caseid, "�����Խ����" + testnote + "����ֵ��ȫ�ֱ�����" + expectedresults.substring(ASSIGNMENT_GLOBALSIGN.length()) + "��", "info", String.valueOf(i + 1), "");
                    }
                    // ģ��ƥ��
                    else if (expectedresults.length() > FUZZY_MATCHING_SIGN.length() && expectedresults.startsWith(FUZZY_MATCHING_SIGN)) {
                        if (testnote.contains(expectedresults.substring(FUZZY_MATCHING_SIGN.length()))) {
                            luckyclient.publicclass.LogUtil.APP.info("������" + testcaseob.getSign() + " ��" + (i + 1) + "����ģ��ƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote);
                            caselog.caseLogDetail(taskid, caseid, "ģ��ƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote, "info", String.valueOf(i + 1), "");
                        } else {
                            setcaseresult = 1;
                            luckyclient.publicclass.LogUtil.APP.error("������" + testcaseob.getSign() + " ��" + (i + 1) + "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults.substring(FUZZY_MATCHING_SIGN.length()) + "�����Խ����" + testnote);
                            caselog.caseLogDetail(taskid, caseid, "��" + (i + 1) + "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults.substring(FUZZY_MATCHING_SIGN.length()) + "�����Խ����" + testnote, "error", String.valueOf(i + 1), "");
                            testnote = "������" + (i + 1) + "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�";
                            if (testcaseob.getFailcontinue() == 0) {
                                luckyclient.publicclass.LogUtil.APP.error("������"+testcaseob.getSign()+"���ڡ�"+(i + 1)+"������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......");
                                break;
                            } else {
                                luckyclient.publicclass.LogUtil.APP.error("������"+testcaseob.getSign()+"���ڡ�"+(i + 1)+"������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......");
                            }
                        }
                    }
                    // ����ƥ��
                    else if (expectedresults.length() > REGULAR_MATCHING_SIGN.length() && expectedresults.startsWith(REGULAR_MATCHING_SIGN)) {
                        Pattern pattern = Pattern.compile(expectedresults.substring(REGULAR_MATCHING_SIGN.length()));
                        Matcher matcher = pattern.matcher(testnote);
                        if (matcher.find()) {
                            luckyclient.publicclass.LogUtil.APP.info("������" + testcaseob.getSign() + " ��" + (i + 1) + "��������ƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote);
                            caselog.caseLogDetail(taskid, caseid, "����ƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote, "info", String.valueOf(i + 1), "");
                        } else {
                            setcaseresult = 1;
                            luckyclient.publicclass.LogUtil.APP.error("������" + testcaseob.getSign() + " ��" + (i + 1) + "��������ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults.substring(REGULAR_MATCHING_SIGN.length()) + "�����Խ����" + testnote);
                            caselog.caseLogDetail(taskid, caseid, "��" + (i + 1) + "��������ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults.substring(REGULAR_MATCHING_SIGN.length()) + "�����Խ����" + testnote, "error", String.valueOf(i + 1), "");
                            testnote = "������" + (i + 1) + "��������ƥ��Ԥ�ڽ��ʧ�ܣ�";
                            if (testcaseob.getFailcontinue() == 0) {
                                luckyclient.publicclass.LogUtil.APP.error("������"+testcaseob.getSign()+"���ڡ�"+(i + 1)+"������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......");
                                break;
                            } else {
                                luckyclient.publicclass.LogUtil.APP.error("������"+testcaseob.getSign()+"���ڡ�"+(i + 1)+"������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......");
                            }
                        }
                    }
                    // ��ȫ���
                    else {
                        if (expectedresults.equals(testnote)) {
                            luckyclient.publicclass.LogUtil.APP.info("������" + testcaseob.getSign() + " ��" + (i + 1) + "������ȷƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote);
                            caselog.caseLogDetail(taskid, caseid, "��ȷƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote, "info", String.valueOf(i + 1), "");
                        } else {
                            setcaseresult = 1;
                            luckyclient.publicclass.LogUtil.APP.error("������" + testcaseob.getSign() + " ��" + (i + 1) + "������ȷƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults + "�����Խ����" + testnote);
                            caselog.caseLogDetail(taskid, caseid, "��" + (i + 1) + "������ȷƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults + "�����Խ����" + testnote, "error", String.valueOf(i + 1), "");
                            testnote = "������" + (i + 1) + "������ȷƥ��Ԥ�ڽ��ʧ�ܣ�";
                            if (testcaseob.getFailcontinue() == 0) {
                                luckyclient.publicclass.LogUtil.APP.error("������"+testcaseob.getSign()+"���ڡ�"+(i + 1)+"������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......");
                                break;
                            } else {
                                luckyclient.publicclass.LogUtil.APP.error("������"+testcaseob.getSign()+"���ڡ�"+(i + 1)+"������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                luckyclient.publicclass.LogUtil.ERROR.error("������" + testcaseob.getSign() + "���÷������̳�����������" + functionname + " �����¼��ű����������Լ�������");
                caselog.caseLogDetail(taskid, caseid, "���÷������̳�����������" + functionname + " �����¼��ű����������Լ�������", "error", String.valueOf(i + 1), "");
                luckyclient.publicclass.LogUtil.ERROR.error(e, e);
                testnote = "CallCase���ó������÷������̳�����������" + functionname + " �����¼��ű����������Լ�������";
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
        // ������÷���������δ�����������ò��Խ������
        try {
            // �ɹ���ʧ�ܵ������ߴ�����
            if (!testnote.contains("CallCase���ó���") && !testnote.contains("������������")) {
                caselog.updateCaseDetail(taskid, caseid, setcaseresult);
            } else {
                // �����������ǵ��÷�������ȫ����������Ϊ����
                luckyclient.publicclass.LogUtil.ERROR.error("������" + testcaseob.getSign() + "����ִ�н��Ϊ��������ο�������־��������������ԭ��.....");
                caselog.caseLogDetail(taskid, caseid, "����ִ�н��Ϊ��������ο�������־��������������ԭ��.....","error", "SETCASERESULT...", "");
                setcaseresult = 2;
                caselog.updateCaseDetail(taskid, caseid, setcaseresult);
            }
            if (0 == setcaseresult) {
                luckyclient.publicclass.LogUtil.APP.info("������" + testcaseob.getSign() + "ִ�н���ɹ�......");
                caselog.caseLogDetail(taskid, caseid, "��������ִ��ȫ���ɹ�......", "info", "ending", "");
                luckyclient.publicclass.LogUtil.APP.info("*********������" + testcaseob.getSign() + "��ִ�����,���Խ�����ɹ�*********");
            } else if (1 == setcaseresult) {
                luckyclient.publicclass.LogUtil.ERROR.error("������" + testcaseob.getSign() + "ִ�н��ʧ��......");
                caselog.caseLogDetail(taskid, caseid, "����ִ�н��ʧ��......", "error", "ending", "");
                luckyclient.publicclass.LogUtil.APP.info("*********������" + testcaseob.getSign() + "��ִ�����,���Խ����ʧ��*********");
            } else {
                luckyclient.publicclass.LogUtil.ERROR.error("������" + testcaseob.getSign() + "ִ�н������......");
                caselog.caseLogDetail(taskid, caseid, "����ִ�н������......", "error", "ending", "");
                luckyclient.publicclass.LogUtil.APP.info("*********������" + testcaseob.getSign() + "��ִ�����,���Խ��������*********");
            }
        } catch (Exception e) {
            luckyclient.publicclass.LogUtil.ERROR.error("������" + testcaseob.getSign() + "����ִ�н�����̳���......");
            caselog.caseLogDetail(taskid, caseid, "����ִ�н�����̳���......", "error", "ending", "");
            luckyclient.publicclass.LogUtil.ERROR.error(e, e);
            e.printStackTrace();
        } finally {
            variable.clear(); // һ��������������ձ����洢�ռ�
            TestControl.THREAD_COUNT--; // ���̼߳���--�����ڼ���߳��Ƿ�ȫ��ִ����
        }
    }

    public static void main(String[] args) {
    }

}
