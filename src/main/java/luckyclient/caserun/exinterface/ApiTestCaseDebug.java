package luckyclient.caserun.exinterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;
import luckyclient.caserun.exinterface.analyticsteps.InterfaceAnalyticCase;
import luckyclient.caserun.publicdispose.ActionManageForSteps;
import luckyclient.caserun.publicdispose.ChangString;
import luckyclient.dblog.LogOperation;
import luckyclient.planapi.api.GetServerAPI;
import luckyclient.planapi.entity.ProjectCase;
import luckyclient.planapi.entity.ProjectCasesteps;
import luckyclient.planapi.entity.PublicCaseParams;
import luckyclient.publicclass.InvokeMethod;
import luckyclient.publicclass.remoterinterface.HttpRequest;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸� ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @ClassName: TestCaseDebug
 * @Description: ����Զ��������ڱ�д�����У��������ű����е��� @author�� seagull
 * @date 2018��3��1��
 * 
 */
public class ApiTestCaseDebug {
	private static final String ASSIGNMENT_SIGN = "$=";
	private static final String FUZZY_MATCHING_SIGN = "%=";
	private static final String REGULAR_MATCHING_SIGN = "~=";

	/**
	 * �����ڱ�����������������
	 * 
	 * @param projectname
	 * @param testCaseExternalId
	 */
	public static void oneCaseDebug(String projectname, String testCaseExternalId) {
		Map<String, String> variable = new HashMap<String, String>(0);
		String packagename = null;
		String functionname = null;
		String expectedresults = null;
		Integer setcaseresult = 0;
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
			setcaseresult = 2;
			luckyclient.publicclass.LogUtil.APP.error("������δ�ҵ����裬���飡");
			testnote = "������δ�ҵ����裬���飡";
		}
		// ����ѭ���������������в���
		for (int i = 0; i < steps.size(); i++) {
			Map<String, String> casescript = InterfaceAnalyticCase.analyticCaseStep(testcaseob, steps.get(i), "888888",
					null);
			try {
				packagename = casescript.get("PackageName").toString();
				packagename = ChangString.changparams(packagename, variable, "��·��");
				functionname = casescript.get("FunctionName").toString();
				functionname = ChangString.changparams(functionname, variable, "������");
			} catch (Exception e) {
				k = 0;
				luckyclient.publicclass.LogUtil.APP.error("������" + testcaseob.getSign() + "�����������Ƿ�����ʧ�ܣ����飡");
				e.printStackTrace();
				break; // ĳһ����ʧ�ܺ󣬴���������Ϊʧ���˳�
			}
			// �������ƽ��������쳣���ǵ���������������쳣
			if (functionname.indexOf("�����쳣") > -1 || k == 1) {
				k = 0;
				testnote = "������" + (i + 1) + "��������������";
				break;
			}
			expectedresults = casescript.get("ExpectedResults").toString();
			expectedresults = ChangString.changparams(expectedresults, variable, "Ԥ�ڽ��");
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
					parameterValues = ChangString.changparams(parameterValues, variable, "��������");
					luckyclient.publicclass.LogUtil.APP.info("������" + testcaseob.getSign() + "����������" + packagename
							+ " ��������" + functionname + " ��" + (j + 1) + "��������" + parameterValues);
					getParameterValues[j] = parameterValues;
				}
			} else {
				getParameterValues = null;
			}
			// ���ö�̬������ִ�в�������
			try {
				luckyclient.publicclass.LogUtil.APP.info("��ʼ���÷�����" + functionname + " .....");
				testnote = InvokeMethod.callCase(packagename, functionname, getParameterValues,
						steps.get(i).getSteptype(), steps.get(i).getExtend());
				testnote = ActionManageForSteps.actionManage(casescript.get("Action"), testnote);
				if (null != expectedresults && !expectedresults.isEmpty()) {
					luckyclient.publicclass.LogUtil.APP.info("expectedResults=��" + expectedresults + "��");
					// ��ֵ����
					if (expectedresults.length() > ASSIGNMENT_SIGN.length()
							&& expectedresults.startsWith(ASSIGNMENT_SIGN)) {
						variable.put(expectedresults.substring(ASSIGNMENT_SIGN.length()), testnote);
						luckyclient.publicclass.LogUtil.APP
								.info("������" + testcaseob.getSign() + " ��" + (i + 1) + "���������Խ����" + testnote + "����ֵ��������"
										+ expectedresults.substring(ASSIGNMENT_SIGN.length()) + "��");
					}
					// ģ��ƥ��
					else if (expectedresults.length() > FUZZY_MATCHING_SIGN.length()
							&& expectedresults.startsWith(FUZZY_MATCHING_SIGN)) {
						if (testnote.contains(expectedresults.substring(FUZZY_MATCHING_SIGN.length()))) {
							luckyclient.publicclass.LogUtil.APP.info(
									"������" + testcaseob.getSign() + " ��" + (i + 1) + "����ģ��ƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote);
						} else {
							setcaseresult = 1;
							luckyclient.publicclass.LogUtil.APP.error("������" + testcaseob.getSign() + " ��" + (i + 1)
									+ "����ģ��ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults.substring(FUZZY_MATCHING_SIGN.length())
									+ "�����Խ����" + testnote);
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
					else if (expectedresults.length() > REGULAR_MATCHING_SIGN.length()
							&& expectedresults.startsWith(REGULAR_MATCHING_SIGN)) {
						Pattern pattern = Pattern.compile(expectedresults.substring(REGULAR_MATCHING_SIGN.length()));
						Matcher matcher = pattern.matcher(testnote);
						if (matcher.find()) {
							luckyclient.publicclass.LogUtil.APP.info(
									"������" + testcaseob.getSign() + " ��" + (i + 1) + "��������ƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote);
						} else {
							setcaseresult = 1;
							luckyclient.publicclass.LogUtil.APP.error("������" + testcaseob.getSign() + " ��" + (i + 1)
									+ "��������ƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults.substring(REGULAR_MATCHING_SIGN.length())
									+ "�����Խ����" + testnote);
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
							luckyclient.publicclass.LogUtil.APP.info(
									"������" + testcaseob.getSign() + " ��" + (i + 1) + "������ȷƥ��Ԥ�ڽ���ɹ���ִ�н����" + testnote);
						} else {
							setcaseresult = 1;
							luckyclient.publicclass.LogUtil.APP.error("������" + testcaseob.getSign() + " ��" + (i + 1)
									+ "������ȷƥ��Ԥ�ڽ��ʧ�ܣ�Ԥ�ڽ����" + expectedresults + "�����Խ����" + testnote);
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
				setcaseresult = 1;
				luckyclient.publicclass.LogUtil.APP.error("���÷������̳�����������" + functionname + " �����¼��ű����������Լ�������");
				luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
				testnote = "CallCase���ó���";
				e.printStackTrace();
                if (testcaseob.getFailcontinue() == 0) {
                    luckyclient.publicclass.LogUtil.APP.error("������"+testcaseob.getSign()+"���ڡ�"+(i + 1)+"������ִ��ʧ�ܣ��жϱ���������������ִ�У����뵽��һ������ִ����......");
                    break;
                } else {
                    luckyclient.publicclass.LogUtil.APP.error("������"+testcaseob.getSign()+"���ڡ�"+(i + 1)+"������ִ��ʧ�ܣ���������������������ִ�У������¸�����ִ����......");
                }
			}
		}
		variable.clear(); // ��մ���MAP
		// ������÷���������δ�����������ò��Խ������
		if (testnote.indexOf("CallCase���ó���") <= -1 && testnote.indexOf("������������") <= -1) {
			luckyclient.publicclass.LogUtil.APP.info("���� " + testCaseExternalId + "�����ɹ������ɹ����������з�����������鿴ִ�н����");
		} else {
			luckyclient.publicclass.LogUtil.APP.error("���� " + testCaseExternalId + "�������ǵ��ò����еķ�������");
		}
		if (0 == setcaseresult) {
			luckyclient.publicclass.LogUtil.APP.info("���� " + testCaseExternalId + "����ȫ��ִ�гɹ���");
		} else {
			luckyclient.publicclass.LogUtil.APP.error("���� " + testCaseExternalId + "��ִ�й�����ʧ�ܣ�������־��");
		}
	}

	/**
	 * �����ڱ����������������е���
	 * 
	 * @param projectname
	 * @param addtestcase
	 */
	public static void moreCaseDebug(String projectname, List<String> addtestcase) {
		System.out.println("��ǰ���������ܹ���"+addtestcase.size());
		for(String testCaseExternalId:addtestcase) {
			try {
				luckyclient.publicclass.LogUtil.APP
						.info("��ʼ���÷�������Ŀ����" + projectname + "��������ţ�" + testCaseExternalId);
				oneCaseDebug(projectname, testCaseExternalId);
			} catch (Exception e) {
				continue;
			}
		}
	}

	/**
	 * ��ȡָ�����������Լ������ű�����־�е�ִ��Ԥ�ڽ�� casestatus˵�� pass:0 fail:1 lock:2 unexcute:4
	 */
	public static String getLogDetailExpectresult(String taskname, String caseno, int casestatus) {
		int taskid = LogOperation.getTaskExcuteTaskid(taskname);
		return LogOperation.getLogDetailExpectResult(taskid, caseno, casestatus);
	}

	/**
	 * ��ȡָ�����������Լ������ű�����־�е�ִ�в��Խ�� casestatus˵�� pass:0 fail:1 lock:2 unexcute:4
	 */
	public static String getLogDetailRunresult(String taskname, String caseno, int casestatus) {
		int taskid = LogOperation.getTaskExcuteTaskid(taskname);
		return LogOperation.getLogDetailTestResult(taskid, caseno, casestatus);
	}

	/**
	 * ����ϵͳ������ָ�������Ԥ�ڽ��
	 */
	public static String setExpectedResults(String testCaseSign, int steps, String expectedResults) {
		String results = "���ý��ʧ��";
		String params = "";
		try {
			expectedResults = expectedResults.replace("%", "BBFFHH");
			expectedResults = expectedResults.replace("=", "DHDHDH");
			expectedResults = expectedResults.replace("&", "ANDAND");
			params = "caseno=" + testCaseSign;
			params += "&stepnum=" + steps;
			params += "&expectedresults=" + expectedResults;
			results = HttpRequest.sendPost("/projectCasesteps/cUpdateStepExpectedResults.do", params);
		} catch (TestLinkAPIException te) {
			te.printStackTrace(System.err);
			results = te.getMessage().toString();
			return results;
		}
		return results;

	}

	public static void main(String[] args) throws Exception {

	}
}
