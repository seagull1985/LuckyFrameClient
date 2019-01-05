package luckyclient.publicclass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import luckyclient.planapi.entity.ProjectProtocolTemplate;
import luckyclient.planapi.entity.ProjectTemplateParams;
import luckyclient.publicclass.remoterinterface.HttpClientHelper;
import luckyclient.publicclass.remoterinterface.HttpRequest;

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
     * ��̬����JAVA
     * @param packagename
     * @param functionname
     * @param getParameterValues
     * @param steptype
     * @param action
     * @return
     */
    public static String callCase(String packagename, String functionname, Object[] getParameterValues, int steptype, String action) {
        String result = "�����쳣����鿴������־��";
        try {
            if (steptype == 0) {                
                if(functionname.toLowerCase().endsWith(".py")){
                	//����Python�ű�
                	luckyclient.publicclass.LogUtil.APP.info("׼����ʼ����Python�ű�......");
                	result = callPy(packagename, functionname, getParameterValues);
                }else{
                	//����JAVA
                    // ���÷Ǿ�̬�����õ�
                	luckyclient.publicclass.LogUtil.APP.info("׼����ʼ����JAVA����׮����......");
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
                }
            } else if (steptype == 2) {
            	if(null==action||"".equals(action)||!action.contains("��")){
            		result = "����ǰ������HTTP������ȷ���Ƿ�û�����ö�Ӧ��HTTPЭ��ģ��...";
            		luckyclient.publicclass.LogUtil.APP.error("����ǰ������HTTP������ȷ���Ƿ�û�����ö�Ӧ��HTTPЭ��ģ��...");
            		return result;
            	}
                String templateidstr = action.substring(1, action.indexOf("��"));
                String templatenamestr = action.substring(action.indexOf("��") + 1);
                luckyclient.publicclass.LogUtil.APP.info("����ʹ��ģ�塾" + templatenamestr + "����ID:��" + templateidstr + "������HTTP����");

                String httpppt = HttpRequest.loadJSON("/projectprotocolTemplate/cgetPTemplateById.do?templateid=" + templateidstr);
                ProjectProtocolTemplate ppt = JSONObject.parseObject(httpppt,ProjectProtocolTemplate.class);
                if (null == ppt) {
                    luckyclient.publicclass.LogUtil.APP.error("Э��ģ��Ϊ�գ���������ʹ�õ�Э��ģ���Ƿ��Ѿ�ɾ����");
                    return "Э��ģ��Ϊ�գ���ȷ������ʹ�õ�ģ���Ƿ��Ѿ�ɾ����";
                }

                String httpptp = HttpRequest.loadJSON("/projectTemplateParams/cgetParamsByTemplate.do?templateid=" + templateidstr);
                JSONObject jsonptpObject = JSONObject.parseObject(httpptp);
                List<ProjectTemplateParams> paramslist = new ArrayList<ProjectTemplateParams>();
                paramslist = JSONObject.parseArray(jsonptpObject.getString("params"), ProjectTemplateParams.class);  

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

                //�����������
                if (null != getParameterValues) {
                    String booleanheadmsg = "headmsg(";
                    String msgend = ")";
                    for (Object obp : getParameterValues) {
                        String paramob = obp.toString();
                        if(paramob.contains("#")){
                            String key = paramob.substring(0, paramob.indexOf("#"));
                            String value = paramob.substring(paramob.indexOf("#") + 1);
                            if (key.contains(booleanheadmsg) && key.contains(msgend)) {
                                String head = key.substring(key.indexOf(booleanheadmsg) + 8, key.lastIndexOf(msgend));
                                headmsg.put(head, value);
                                continue;
                            }
                            int replaceflag=0;
                            for (int i = 0; i < paramslist.size(); i++) {
                                ProjectTemplateParams ptp = paramslist.get(i);
                                if("_forTextJson".equals(ptp.getParamname())){
                            		//���������滻���
                            		int index = 1;
                            		if (key.contains("[") && key.endsWith("]")) {
                            			index = Integer.valueOf(key.substring(key.lastIndexOf("[") + 1, key.lastIndexOf("]")));
                            			key = key.substring(0, key.lastIndexOf("["));
                            			luckyclient.publicclass.LogUtil.APP.info("׼���滻JSON�����еĲ���ֵ���滻ָ����" + index + "������...");
                            		} else {
                            			luckyclient.publicclass.LogUtil.APP.info("׼���滻JSON�����еĲ���ֵ��δ��⵽ָ����������ţ�Ĭ���滻��1������...");                       			
                            		}
                            		
                                	if(ptp.getParam().indexOf("\""+key+"\":")>=0){
                                		Map<String,String> map=ChangString.changjson(ptp.getParam(), key, value,index);
                                		if("true".equals(map.get("boolean"))){
                                            ptp.setParam(map.get("json"));
                                            paramslist.set(i, ptp);
                                            replaceflag=1;
                                            luckyclient.publicclass.LogUtil.APP.info("�滻����"+key+"���...");
                                            break;
                                		}
                                	}else if(ptp.getParam().indexOf(key)>=0){
                                		ptp.setParam(ptp.getParam().replace(key, value));
                                		paramslist.set(i, ptp);
                                        replaceflag=1;
                                        luckyclient.publicclass.LogUtil.APP.info("��鵱ǰ�ı�������JSON,���ַ�����"+ptp.getParam()+"����ֱ�Ӱѡ�"+key+"���滻�ɡ�"+value+"��...");
                                        break;
                                	}else{
                                		luckyclient.publicclass.LogUtil.APP.error("�������Ĵ��ı�ģ���Ƿ���������JSON��ʽ�����ı����Ƿ�������滻�Ĺؼ��֡�");
                                	}
                                }else{
                                    if (ptp.getParamname().equals(key)) {
                                        ptp.setParam(value);
                                        paramslist.set(i, ptp);
                                        replaceflag=1;
                                        luckyclient.publicclass.LogUtil.APP.info("��ģ���в�����"+key+"����ֵ���óɡ�"+value+"��");
                                        break;
                                    }
                                }
                            }
                            if(replaceflag==0){
                            	luckyclient.publicclass.LogUtil.APP.error("���������"+key+"��û����ģ�����ҵ����滻�Ĳ�����ӦĬ��ֵ��"
                            			+ "�����������ʧ�ܣ�����Э��ģ���д˲����Ƿ���ڡ�");
                            }
                        }else{
                        	luckyclient.publicclass.LogUtil.APP.error("�滻ģ�����ͷ�����ʧ�ܣ�ԭ������Ϊû�м�⵽#��"
                        			+ "ע��HTTP�����滻������ʽ�ǡ�headmsg(ͷ����#ͷ��ֵ)|������#����ֵ|������2#����ֵ2��");
                        }

                    }
                }
                //�������
                Map<String, Object> params = new HashMap<String, Object>(0);
                for (ProjectTemplateParams ptp : paramslist) {
                    //�����������
                    if (ptp.getParamtype() == 1) {
                        String tempparam = ptp.getParam().replace("&quot;", "\"");
                        JSONObject json = JSONObject.parseObject(tempparam);
                        params.put(ptp.getParamname().replace("&quot;", "\""), json);
                        luckyclient.publicclass.LogUtil.APP.info("ģ�������" + ptp.getParamname() + "��  JSONObject���Ͳ���ֵ:��" + json.toString() + "��");
                    } else if (ptp.getParamtype() == 2) {
                        String tempparam = ptp.getParam().replace("&quot;", "\"");
                        JSONArray jarr = JSONArray.parseArray(tempparam);
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
                    result = HttpClientHelper.sendHttpURLPost(packagename, params, ppt.getContentencoding().toLowerCase(), ppt.getConnecttimeout(), headmsg,ppt.getResponsehead(),ppt.getResponsecode());
                } else if (functionname.toLowerCase().equals("urlpost")) {
                    result = HttpClientHelper.sendURLPost(packagename, params, ppt.getContentencoding().toLowerCase(), ppt.getConnecttimeout(), headmsg,ppt.getResponsehead(),ppt.getResponsecode());
                } else if (functionname.toLowerCase().equals("getandsavefile")) {
                    String fileSavePath = System.getProperty("user.dir") + "\\HTTPSaveFile\\";
                    result = HttpClientHelper.sendGetAndSaveFile(packagename, params, fileSavePath, ppt.getConnecttimeout(), headmsg,ppt.getResponsehead(),ppt.getResponsecode());
                } else if (functionname.toLowerCase().equals("httpurlget")) {
                    result = HttpClientHelper.sendHttpURLGet(packagename, params, ppt.getContentencoding().toLowerCase(), ppt.getConnecttimeout(), headmsg,ppt.getResponsehead(),ppt.getResponsecode());
                } else if (functionname.toLowerCase().equals("urlget")) {
                    result = HttpClientHelper.sendURLGet(packagename, params, ppt.getContentencoding().toLowerCase(), ppt.getConnecttimeout(), headmsg,ppt.getResponsehead(),ppt.getResponsecode());
                } else if (functionname.toLowerCase().equals("httpclientpost")) {
                    result = HttpClientHelper.httpClientPost(packagename, params, ppt.getContentencoding().toLowerCase(), headmsg , ppt.getCerpath(),ppt.getResponsehead(),ppt.getResponsecode());
                } else if (functionname.toLowerCase().equals("httpclientuploadfile")) {
                    result = HttpClientHelper.httpClientUploadFile(packagename, params, ppt.getContentencoding().toLowerCase(), headmsg , ppt.getCerpath(),ppt.getResponsehead(),ppt.getResponsecode());
                } else if (functionname.toLowerCase().equals("httpclientpostjson")) {
                    result = HttpClientHelper.httpClientPostJson(packagename, params, ppt.getContentencoding().toLowerCase(), headmsg , ppt.getCerpath(),ppt.getResponsehead(),ppt.getResponsecode());
                } else if (functionname.toLowerCase().equals("httpurldelete")) {
                    result = HttpClientHelper.sendHttpURLDel(packagename, params, ppt.getContentencoding().toLowerCase(), ppt.getConnecttimeout(), headmsg,ppt.getResponsehead(),ppt.getResponsecode());
                } else if (functionname.toLowerCase().equals("httpclientputjson")) {
                    result = HttpClientHelper.httpClientPutJson(packagename, params, ppt.getContentencoding().toLowerCase(), headmsg , ppt.getCerpath(),ppt.getResponsehead(),ppt.getResponsecode());
                } else if (functionname.toLowerCase().equals("httpclientput")) {
                    result = HttpClientHelper.httpClientPut(packagename, params, ppt.getContentencoding().toLowerCase(), headmsg , ppt.getCerpath(),ppt.getResponsehead(),ppt.getResponsecode());
                } else if (functionname.toLowerCase().equals("httpclientget")) {
                    result = HttpClientHelper.httpClientGet(packagename, params, ppt.getContentencoding().toLowerCase(), headmsg, ppt.getCerpath(),ppt.getResponsehead(),ppt.getResponsecode());
                } else {
                    luckyclient.publicclass.LogUtil.APP.error("����HTTP���������쳣����⵽�Ĳ��������ǣ�" + functionname);
                    result = "�����쳣����鿴������־��";
                }
            } else if (steptype == 3) {
                String templateidstr = action.substring(1, action.indexOf("��"));
                String templatenamestr = action.substring(action.indexOf("��") + 1);
                luckyclient.publicclass.LogUtil.APP.info("����ʹ��ģ�塾" + templatenamestr + "����ID:��" + templateidstr + "�� ����SOCKET����");

                String httpppt = HttpRequest.loadJSON("/projectprotocolTemplate/cgetPTemplateById.do?templateid=" + templateidstr);
                ProjectProtocolTemplate ppt = JSONObject.parseObject(httpppt,ProjectProtocolTemplate.class);
                if (null == ppt) {
                    luckyclient.publicclass.LogUtil.APP.error("Э��ģ��Ϊ�գ���������ʹ�õ�Э��ģ���Ƿ��Ѿ�ɾ����");
                    return "Э��ģ��Ϊ�գ���ȷ������ʹ�õ�ģ���Ƿ��Ѿ�ɾ����";
                }
                
                String httpptp = HttpRequest.loadJSON("/projectTemplateParams/cgetParamsByTemplate.do?templateid=" + templateidstr);
                JSONObject jsonptpObject = JSONObject.parseObject(httpptp);
                List<ProjectTemplateParams> paramslist = new ArrayList<ProjectTemplateParams>();
                paramslist = JSONObject.parseArray(jsonptpObject.getString("params"), ProjectTemplateParams.class);  
                
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

                //�����������
                if (null != getParameterValues) {
                    String booleanheadmsg = "headmsg(";
                    String msgend = ")";
                    for (Object obp : getParameterValues) {
                        String paramob = obp.toString();
                        if(paramob.contains("#")){
                            String key = paramob.substring(0, paramob.indexOf("#"));
                            String value = paramob.substring(paramob.indexOf("#") + 1);
                            if (key.contains(booleanheadmsg) && key.contains(msgend)) {
                                String head = key.substring(key.indexOf(booleanheadmsg) + 8, key.lastIndexOf(msgend));
                                headmsg.put(head, value);
                                continue;
                            }
                            int replaceflag=0;
                            for (int i = 0; i < paramslist.size(); i++) {
                                ProjectTemplateParams ptp = paramslist.get(i);
                                if("_forTextJson".equals(ptp.getParamname())){
                                	if(ptp.getParam().indexOf("\""+key+"\":")>=0){
                                 		//���������滻���
                                		int index = 1;
                                		if (key.indexOf("[") >= 0 && key.endsWith("]")) {
                                			index = Integer.valueOf(key.substring(key.lastIndexOf("[") + 1, key.lastIndexOf("]")));
                                			key = key.substring(0, key.lastIndexOf("["));
                                			luckyclient.publicclass.LogUtil.APP.info("׼���滻JSON�����еĲ���ֵ��δ��⵽ָ����������ţ�Ĭ���滻��1������...");
                                		} else {
                                			luckyclient.publicclass.LogUtil.APP.info("׼���滻JSON�����еĲ���ֵ���滻ָ����" + index + "������...");
                                		}
                                		
                                		Map<String,String> map=ChangString.changjson(ptp.getParam(), key, value,index);
                                		if("true".equals(map.get("boolean"))){
                                            ptp.setParam(map.get("json"));
                                            paramslist.set(i, ptp);
                                            replaceflag=1;
                                            luckyclient.publicclass.LogUtil.APP.info("�滻����"+key+"���...");
                                            break;
                                		}
                                	}else if(ptp.getParam().indexOf(key)>=0){
                                		ptp.setParam(ptp.getParam().replace(key, value));
                                		paramslist.set(i, ptp);
                                        replaceflag=1;
                                        luckyclient.publicclass.LogUtil.APP.info("��鵱ǰ�ı�������JSON,���ַ�����"+ptp.getParam()+"����ֱ�Ӱѡ�"+key+"���滻�ɡ�"+value+"��...");
                                        break;
                                	}else{
                                		luckyclient.publicclass.LogUtil.APP.error("�������Ĵ��ı�ģ���Ƿ���������JSON��ʽ�����ı����Ƿ�������滻�Ĺؼ��֡�");
                                	}
                                }else{
                                    if (ptp.getParamname().equals(key)) {
                                        ptp.setParam(value);
                                        paramslist.set(i, ptp);
                                        replaceflag=1;
                                        luckyclient.publicclass.LogUtil.APP.info("��ģ���в�����"+key+"����ֵ���óɡ�"+value+"��");
                                        break;
                                    }
                                }
                            }
                            if(replaceflag==0){
                            	luckyclient.publicclass.LogUtil.APP.error("���������"+key+"��û����ģ�����ҵ����滻�Ĳ�����ӦĬ��ֵ��"
                            			+ "�����������ʧ�ܣ�����Э��ģ���д˲����Ƿ���ڡ�");
                            }
                        }else{
                        	luckyclient.publicclass.LogUtil.APP.error("�滻ģ�����ͷ�����ʧ�ܣ�ԭ������Ϊû�м�⵽#��"
                        			+ "ע��HTTP�����滻������ʽ�ǡ�headmsg(ͷ����#ͷ��ֵ)|������#����ֵ|������2#����ֵ2��");
                        }

                    }
                }
                //�������
                Map<String, Object> params = new HashMap<String, Object>(0);
                for (ProjectTemplateParams ptp : paramslist) {
                    //�����������
                    if (ptp.getParamtype() == 1) {
                        String tempparam = ptp.getParam().replace("&quot;", "\"");
                        JSONObject json = JSONObject.parseObject(tempparam);
                        params.put(ptp.getParamname().replace("&quot;", "\""), json);
                        luckyclient.publicclass.LogUtil.APP.info("ģ�������" + ptp.getParamname() + "��  JSONObject���Ͳ���ֵ:��" + json.toString() + "��");
                    } else if (ptp.getParamtype() == 2) {
                        String tempparam = ptp.getParam().replace("&quot;", "\"");
                        JSONArray jarr = JSONArray.parseArray(tempparam);
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
    
    /**
     * ����Python�ű�
     * @param packagename
     * @param functionname
     * @param getParameterValues
     * @return
     */
    private static String callPy(String packagename, String functionname, Object[] getParameterValues){
    	String result = "����Python�ű������쳣�����ؽ����null";
    	try {
    		// ���建��������������������������Ϣ�����
    		byte[] buffer = new byte[1024];
    		ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
    		ByteArrayOutputStream outerrStream = new ByteArrayOutputStream();
    		int params=0;
    		if(getParameterValues!=null){
    			params=getParameterValues.length;
    		}
    		String[] args = new String[2+params];
    		args[0]="python";
            if(packagename.endsWith(File.separator)){
            	args[1]=packagename+functionname;
            }else{
            	args[1]=packagename+File.separator+functionname;
            }
            luckyclient.publicclass.LogUtil.APP.info("����Python�ű�·��:"+args[1]);
    		for(int i=0;i < params;i++){
    			args[2+i]=getParameterValues[i].toString();
    		}

            Process proc=Runtime.getRuntime().exec(args);
            InputStream errStream = proc.getErrorStream();
            InputStream stream = proc.getInputStream();
            
            // ����ȡ��д��
            int len = -1;  
            while ((len = errStream.read(buffer)) != -1) {  
                outerrStream.write(buffer, 0, len);  
            }  
            while ((len = stream.read(buffer)) != -1) {  
                outStream.write(buffer, 0, len);  
            }
            
            proc.waitFor();
            // ��ӡ����Ϣ
            if(outerrStream.toString().equals("")){
            	result = outStream.toString().trim();
            	luckyclient.publicclass.LogUtil.APP.info("�ɹ�����Python�ű������ؽ��:"+result);
            }else{
            	result = outerrStream.toString().trim();
            	if(result.indexOf("ModuleNotFoundError")>-1){
            		luckyclient.publicclass.LogUtil.APP.error("����Python�ű������쳣�������Pythonģ��δ���õ�������Python�ű���ע������ϵͳ����·��(��: sys.path.append(\"E:\\PycharmProjects\\untitled\\venv\\Lib\\site-packages\"))��"
            				+ "��ϸ������Ϣ:"+result);
            	}else if(result.indexOf("No such file or directory")>-1){
            		luckyclient.publicclass.LogUtil.APP.error("����Python�ű������쳣����ָ��·����δ�ҵ�Python�ű���ԭ���п�����Python�ű�·��������Ǵ���Pythonָ������������һ�£���ϸ������Ϣ:"+result);
            	}else{
            		luckyclient.publicclass.LogUtil.APP.error("����Python�ű������쳣��������Ϣ:"+result);
            	}        	
            }
           } 
         catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    	return result;
    }
    
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
