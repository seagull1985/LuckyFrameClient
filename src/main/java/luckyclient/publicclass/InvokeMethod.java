package luckyclient.publicclass;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import luckyclient.planapi.entity.ProjectProtocolTemplate;
import luckyclient.planapi.entity.ProjectTemplateParams;
import luckyclient.publicclass.remoterinterface.HttpClientHelper;
import luckyclient.publicclass.remoterinterface.HttpRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 *
 * @ClassName: InvokeMethod
 * @Description: ��̬���÷���
 * @author�� seagull
 * @date 2017��9��24�� ����9:29:40
 */
public class InvokeMethod {

    /**
     * @throws Throwable
     */
    public static String callCase(String packagename, String functionname, Object[] getParameterValues, int steptype, String action) {
        String result = "�����쳣����鿴������־��";
        try {
            if (steptype == 0) {
                // ���÷Ǿ�̬�����õ�
                Object server = Class.forName(packagename).newInstance();
                @SuppressWarnings("rawtypes")
                Class[] getParameterTypes = null;
                if (getParameterValues != null) {
                    int paramscount = getParameterValues.length;
                    // ��ֵ���飬��������
                    getParameterTypes = new Class[paramscount];
                    for (int i = 0; i < paramscount; i++) {
                        getParameterTypes[i] = String.class;
                    }
                }
                Method method = getMethod(server.getClass().getMethods(), functionname, getParameterTypes);
                if (method == null) {
                    throw new Exception("�ͻ��˱�������Ŀ¼��û���ڰ���Ϊ��" + packagename + "�����ҵ������õķ�����" + functionname + "��,���鷽�������Լ����������Ƿ�һ�£�");
                }
                Object str = method.invoke(server, getParameterValues);
                if (str == null) {
                    result = "�����쳣�����ؽ����null";
                } else {
                    result = str.toString();
                }
            } else if (steptype == 2) {
                String templateidstr = action.substring(1, action.indexOf("��"));
                String templatenamestr = action.substring(action.indexOf("��") + 1);
                luckyclient.publicclass.LogUtil.APP.info("����ʹ��ģ�塾" + templatenamestr + "����ID��" + templateidstr + "������HTTP����");

                String httpppt = HttpRequest.loadJSON("/projectprotocolTemplate/cgetPTemplateById.do?templateid=" + templateidstr);
                JSONObject jsonpptObject = JSONObject.fromObject(httpppt);
                ProjectProtocolTemplate ppt = (ProjectProtocolTemplate) JSONObject.toBean(jsonpptObject,
                        ProjectProtocolTemplate.class);
                if (null == ppt) {
                    luckyclient.publicclass.LogUtil.APP.error("Э��ģ��Ϊ�գ���������ʹ�õ�Э��ģ���Ƿ��Ѿ�ɾ����");
                    return "Э��ģ��Ϊ�գ���ȷ������ʹ�õ�ģ���Ƿ��Ѿ�ɾ����";
                }

                String httpptp = HttpRequest.loadJSON("/projectTemplateParams/cgetParamsByTemplate.do?templateid=" + templateidstr);
                JSONObject jsonptpObject = JSONObject.fromObject(httpptp);
                JSONArray jsonarr = JSONArray.fromObject(jsonptpObject.getString("params"));

                //����json-lib 2.4�汾������json��ʽ�ַ���ʱ���������ɶ������bug
                for (int i = 0; i < jsonarr.size(); i++) {
                    JSONObject tempobj = (JSONObject) jsonarr.get(i);
                    String str = tempobj.get("param").toString();
                    if (str.length() > 0 && "[".equals(str.substring(0, 1)) && "]".equals(str.substring(str.length() - 1))) {
                        tempobj.element("param", "***" + str);
                        jsonarr.set(i, tempobj);
                    }
                }

                //����ͷ��
                Map<String, String> headmsg = new HashMap<>(0);
                if (null != ppt.getHeadmsg() && !ppt.getHeadmsg().equals("") && ppt.getHeadmsg().indexOf("=") > 0) {
                    String headmsgtemp = ppt.getHeadmsg().replace("\\;", "!!!fhzh");
                    String[] temp = headmsgtemp.split(";", -1);
                    for (int i = 0; i < temp.length; i++) {
                        if (null != temp[i] && !temp[i].equals("") && temp[i].indexOf("=") > 0) {
                            String key = temp[i].substring(0, temp[i].indexOf("="));
                            String value = temp[i].substring(temp[i].indexOf("=") + 1);
                            value = value.replace("!!!fhzh",";");
                            headmsg.put(key, value);
                        }
                    }
                }

                @SuppressWarnings("unchecked")
                List<ProjectTemplateParams> paramslist = JSONArray.toList(jsonarr, new ProjectTemplateParams(),
                        new JsonConfig());
                //�����������
                if (null != getParameterValues) {
                    String booleanheadmsg = "headmsg(";
                    String msgend = ")";
                    for (Object obp : getParameterValues) {
                        String paramob = obp.toString();
                        String key = paramob.substring(0, paramob.indexOf("#"));
                        String value = paramob.substring(paramob.indexOf("#") + 1);
                        if (key.contains(booleanheadmsg) && key.contains(msgend)) {
                            String head = key.substring(key.indexOf(booleanheadmsg) + 8, key.lastIndexOf(msgend));
                            headmsg.put(head, value);
                            continue;
                        }
                        for (int i = 0; i < paramslist.size(); i++) {
                            ProjectTemplateParams ptp = paramslist.get(i);
                            if (ptp.getParamname().equals(key)) {
                                ptp.setParam(value);
                                paramslist.set(i, ptp);
                            }
                        }
                    }
                }
                //�������
                Map<String, Object> params = new HashMap<String, Object>(0);
                for (ProjectTemplateParams ptp : paramslist) {
                    if (ptp.getParam().contains("***[") && "***[".equals(ptp.getParam().substring(0, 4))) {
                        ptp.setParam(ptp.getParam().substring(3));
                    }
                    //�����������
                    if (ptp.getParamtype() == 1) {
                        String tempparam = ptp.getParam().replace("&quot;", "\"");
                        JSONObject json = JSONObject.fromObject(tempparam);
                        params.put(ptp.getParamname().replace("&quot;", "\""), json);
                        luckyclient.publicclass.LogUtil.APP.info("ģ�������" + ptp.getParamname() + "��  JSONObject���Ͳ���ֵ:��" + json.toString() + "��");
                    } else if (ptp.getParamtype() == 2) {
                        String tempparam = ptp.getParam().replace("&quot;", "\"");
                        JSONArray jarr = JSONArray.fromObject(tempparam);
                        params.put(ptp.getParamname().replace("&quot;", "\""), jarr);
                        luckyclient.publicclass.LogUtil.APP.info("ģ�������" + ptp.getParamname() + "��  JSONArray���Ͳ���ֵ:��" + jarr.toString() + "��");
                    } else if (ptp.getParamtype() == 3) {
                        String tempparam = ptp.getParam().replace("&quot;", "\"");
                        File file = new File(tempparam);
                        params.put(ptp.getParamname().replace("&quot;", "\""), file);
                        luckyclient.publicclass.LogUtil.APP.info("ģ�������" + ptp.getParamname() + "��  File���Ͳ���ֵ:��" + file.getAbsolutePath() + "��");
                    } else if (ptp.getParamtype() == 4) {
                        String tempparam = ptp.getParam().replace("&quot;", "\"");
                        Double dp = Double.valueOf(tempparam);
                        params.put(ptp.getParamname().replace("&quot;", "\""), dp);
                        luckyclient.publicclass.LogUtil.APP.info("ģ�������" + ptp.getParamname() + "��  �������Ͳ���ֵ:��" + tempparam + "��");
                    } else if (ptp.getParamtype() == 5) {
                        String tempparam = ptp.getParam().replace("&quot;", "\"");
                        Boolean bn = Boolean.valueOf(tempparam);
                        params.put(ptp.getParamname().replace("&quot;", "\""), bn);
                        luckyclient.publicclass.LogUtil.APP.info("ģ�������" + ptp.getParamname() + "��  Boolean���Ͳ���ֵ:��" + bn + "��");
                    } else {
                        params.put(ptp.getParamname().replace("&quot;", "\""), ptp.getParam().replace("&quot;", "\""));
                        luckyclient.publicclass.LogUtil.APP.info("ģ�������" + ptp.getParamname() + "��  String���Ͳ���ֵ:��" + ptp.getParam().replace("&quot;", "\"") + "��");
                    }
                }

                if (functionname.toLowerCase().equals("httpurlpost")) {
                    result = HttpClientHelper.sendHttpURLPost(packagename, params, ppt.getContentencoding().toLowerCase(), ppt.getConnecttimeout(), headmsg);
                } else if (functionname.toLowerCase().equals("urlpost")) {
                    result = HttpClientHelper.sendURLPost(packagename, params, ppt.getContentencoding().toLowerCase(), ppt.getConnecttimeout(), headmsg);
                } else if (functionname.toLowerCase().equals("getandsavefile")) {
                    String fileSavePath = System.getProperty("user.dir") + "\\HTTPSaveFile\\";
                    HttpClientHelper.sendGetAndSaveFile(packagename, params, fileSavePath, ppt.getConnecttimeout(), headmsg);
                    result = "�����ļ��ɹ�����ǰ���ͻ���·��:" + fileSavePath + " �鿴������";
                } else if (functionname.toLowerCase().equals("httpurlget")) {
                    result = HttpClientHelper.sendHttpURLGet(packagename, params, ppt.getContentencoding().toLowerCase(), ppt.getConnecttimeout(), headmsg);
                } else if (functionname.toLowerCase().equals("urlget")) {
                    result = HttpClientHelper.sendURLGet(packagename, params, ppt.getContentencoding().toLowerCase(), ppt.getConnecttimeout(), headmsg);
                } else if (functionname.toLowerCase().equals("httpclientpost")) {
                    result = HttpClientHelper.httpClientPost(packagename, params, ppt.getContentencoding().toLowerCase(), headmsg , ppt.getCerpath());
                } else if (functionname.toLowerCase().equals("httpclientuploadfile")) {
                    result = HttpClientHelper.httpClientUploadFile(packagename, params, ppt.getContentencoding().toLowerCase(), headmsg , ppt.getCerpath());
                } else if (functionname.toLowerCase().equals("httpclientpostjson")) {
                    result = HttpClientHelper.httpClientPostJson(packagename, params, ppt.getContentencoding().toLowerCase(), headmsg , ppt.getCerpath());
                } else if (functionname.toLowerCase().equals("httpurldelete")) {
                    result = HttpClientHelper.sendHttpURLDel(packagename, params, ppt.getContentencoding().toLowerCase(), ppt.getConnecttimeout(), headmsg);
                } else if (functionname.toLowerCase().equals("httpclientputjson")) {
                    result = HttpClientHelper.httpClientPutJson(packagename, params, ppt.getContentencoding().toLowerCase(), headmsg , ppt.getCerpath());
                } else if (functionname.toLowerCase().equals("httpclientput")) {
                    result = HttpClientHelper.httpClientPut(packagename, params, ppt.getContentencoding().toLowerCase(), headmsg , ppt.getCerpath());
                } else if (functionname.toLowerCase().equals("httpclientget")) {
                    result = HttpClientHelper.httpClientGet(packagename, params, ppt.getContentencoding().toLowerCase(), headmsg, ppt.getCerpath());
                } else {
                    luckyclient.publicclass.LogUtil.APP.error("����HTTP���������쳣����⵽�Ĳ��������ǣ�" + functionname);
                    result = "�����쳣����鿴������־��";
                }
            } else if (steptype == 3) {
                String templateidstr = action.substring(1, action.indexOf("��"));
                String templatenamestr = action.substring(action.indexOf("��") + 1);
                luckyclient.publicclass.LogUtil.APP.info("����ʹ��ģ�塾" + templatenamestr + "��  ID:��" + templateidstr + "�� ����SOCKET����");

                String httpppt = HttpRequest.loadJSON("/projectprotocolTemplate/cgetPTemplateById.do?templateid=" + templateidstr);
                JSONObject jsonpptObject = JSONObject.fromObject(httpppt);
                ProjectProtocolTemplate ppt = (ProjectProtocolTemplate) JSONObject.toBean(jsonpptObject,
                        ProjectProtocolTemplate.class);

                String httpptp = HttpRequest.loadJSON("/projectTemplateParams/cgetParamsByTemplate.do?templateid=" + templateidstr);
                JSONObject jsonptpObject = JSONObject.fromObject(httpptp);
                JSONArray jsonarr = JSONArray.fromObject(jsonptpObject.getString("params"));

                //����json-lib 2.4�汾������json��ʽ�ַ���ʱ���������ɶ������bug
                for (int i = 0; i < jsonarr.size(); i++) {
                    JSONObject tempobj = (JSONObject) jsonarr.get(i);
                    String str = tempobj.get("param").toString();
                    if (str.length() > 0 && "[".equals(str.substring(0, 1)) && "]".equals(str.substring(str.length() - 1))) {
                        tempobj.element("param", "***" + str);
                        jsonarr.set(i, tempobj);
                    }
                }

                //����ͷ��
                Map<String, String> headmsg = new HashMap<String, String>(0);
                if (null != ppt.getHeadmsg() && !ppt.getHeadmsg().equals("") && ppt.getHeadmsg().indexOf("=") > 0) {
                    String headmsgtemp = ppt.getHeadmsg().replace("\\;", "!!!fhzh");
                    String[] temp = headmsgtemp.split(";", -1);
                    for (int i = 0; i < temp.length; i++) {
                        if (null != temp[i] && !temp[i].equals("") && temp[i].indexOf("=") > 0) {
                            String key = temp[i].substring(0, temp[i].indexOf("="));
                            String value = temp[i].substring(temp[i].indexOf("=") + 1);
                            value = value.replace("!!!fhzh",";");
                            headmsg.put(key, value);
                        }
                    }
                }

                @SuppressWarnings("unchecked")
                List<ProjectTemplateParams> paramslist = JSONArray.toList(jsonarr, new ProjectTemplateParams(), new JsonConfig());
                //�����������
                if (null != getParameterValues) {
                    String booleanheadmsg = "headmsg(";
                    String msgend = ")";
                    for (Object obp : getParameterValues) {
                        String paramob = obp.toString();
                        String key = paramob.substring(0, paramob.indexOf("#"));
                        String value = paramob.substring(paramob.indexOf("#") + 1);
                        if (key.contains(booleanheadmsg) && key.contains(msgend)) {
                            String head = key.substring(key.indexOf(booleanheadmsg) + 8, key.lastIndexOf(msgend));
                            headmsg.put(head, value);
                            continue;
                        }
                        for (int i = 0; i < paramslist.size(); i++) {
                            ProjectTemplateParams ptp = paramslist.get(i);
                            if (ptp.getParamname().equals(key)) {
                                ptp.setParam(value);
                                paramslist.set(i, ptp);
                            }
                        }
                    }
                }
                Map<String, Object> params = new HashMap<String, Object>(0);
                for (ProjectTemplateParams ptp : paramslist) {
                    if (ptp.getParam().contains("***[") && "***[".equals(ptp.getParam().substring(0, 4))) {
                        ptp.setParam(ptp.getParam().substring(3));
                    }
                    //�����������
                    if (ptp.getParamtype() == 1) {
                        String tempparam = ptp.getParam().replace("&quot;", "\"");
                        JSONObject json = JSONObject.fromObject(tempparam);
                        params.put(ptp.getParamname().replace("&quot;", "\""), json);
                    } else if (ptp.getParamtype() == 2) {
                        String tempparam = ptp.getParam().replace("&quot;", "\"");
                        JSONArray jarr = JSONArray.fromObject(tempparam);
                        params.put(ptp.getParamname().replace("&quot;", "\""), jarr);
                    } else if (ptp.getParamtype() == 3) {
                        String tempparam = ptp.getParam().replace("&quot;", "\"");
                        File file = new File(tempparam);
                        params.put(ptp.getParamname().replace("&quot;", "\""), file);
                    } else {
                        params.put(ptp.getParamname().replace("&quot;", "\""), ptp.getParam().replace("&quot;", "\""));
                    }
                }


                if (functionname.toLowerCase().equals("socketpost")) {
                    result = HttpClientHelper.sendSocketPost(packagename, params, ppt.getContentencoding().toLowerCase(), headmsg);
                } else if (functionname.toLowerCase().equals("socketget")) {
                    result = HttpClientHelper.sendSocketGet(packagename, params, ppt.getContentencoding().toLowerCase(), headmsg);
                } else {
                    luckyclient.publicclass.LogUtil.APP.error("����SOCKET���������쳣����⵽�Ĳ��������ǣ�" + functionname);
                    result = "�����쳣����鿴������־��";
                }
            }
        } catch (Throwable e) {
            luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
            return "�����쳣����鿴������־��";
        }
        return result;
    }

    public static Method getMethod(Method[] methods, String methodName, @SuppressWarnings("rawtypes") Class[] parameterTypes) {
        for (int i = 0; i < methods.length; i++) {
            if (!methods[i].getName().equals(methodName)) {
                continue;
            }
            if (compareParameterTypes(parameterTypes, methods[i].getParameterTypes())) {
                return methods[i];
            }

        }
        return null;
    }

    public static boolean compareParameterTypes(@SuppressWarnings("rawtypes") Class[] parameterTypes, @SuppressWarnings("rawtypes") Class[] orgParameterTypes) {
        // parameterTypes ���棬int->Integer
        // orgParameterTypes��ԭʼ��������
        if (parameterTypes == null && orgParameterTypes == null) {
            return true;
        }
        if (parameterTypes == null && orgParameterTypes != null) {
            if (orgParameterTypes.length == 0) {
                return true;
            } else {
                return false;
            }
        }
        if (parameterTypes != null && orgParameterTypes == null) {
            if (parameterTypes.length == 0) {
                return true;
            } else {
                return false;
            }

        }
        if (parameterTypes.length != orgParameterTypes.length) {
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
