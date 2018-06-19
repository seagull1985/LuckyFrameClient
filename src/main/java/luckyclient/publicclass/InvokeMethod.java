package luckyclient.publicclass;

import java.io.File;
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
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 *
 * @ClassName: InvokeMethod
 * @Description: 动态调用方法
 * @author： seagull
 * @date 2017年9月24日 上午9:29:40
 */
public class InvokeMethod {

    /**
     * @throws Throwable
     */
    public static String callCase(String packagename, String functionname, Object[] getParameterValues, int steptype, String action) {
        String result = "调用异常，请查看错误日志！";
        try {
            if (steptype == 0) {
                // 调用非静态方法用到
                Object server = Class.forName(packagename).newInstance();
                @SuppressWarnings("rawtypes")
                Class[] getParameterTypes = null;
                if (getParameterValues != null) {
                    int paramscount = getParameterValues.length;
                    // 赋值数组，定义类型
                    getParameterTypes = new Class[paramscount];
                    for (int i = 0; i < paramscount; i++) {
                        getParameterTypes[i] = String.class;
                    }
                }
                Method method = getMethod(server.getClass().getMethods(), functionname, getParameterTypes);
                if (method == null) {
                    throw new Exception("客户端本地驱动目录下没有在包名为【" + packagename + "】中找到被调用的方法【" + functionname + "】,请检查方法名称以及参数个数是否一致！");
                }
                Object str = method.invoke(server, getParameterValues);
                if (str == null) {
                    result = "调用异常，返回结果是null";
                } else {
                    result = str.toString();
                }
            } else if (steptype == 2) {
                String templateidstr = action.substring(1, action.indexOf("】"));
                String templatenamestr = action.substring(action.indexOf("】") + 1);
                luckyclient.publicclass.LogUtil.APP.info("即将使用模板【" + templatenamestr + "】，ID【" + templateidstr + "】发送HTTP请求！");

                String httpppt = HttpRequest.loadJSON("/projectprotocolTemplate/cgetPTemplateById.do?templateid=" + templateidstr);
                ProjectProtocolTemplate ppt = JSONObject.parseObject(httpppt, ProjectProtocolTemplate.class);
                if (null == ppt) {
                    luckyclient.publicclass.LogUtil.APP.error("协议模板为空，请检查用例使用的协议模板是否已经删除！");
                    return "协议模板为空，请确认用例使用的模板是否已经删除！";
                }

                String httpptp = HttpRequest.loadJSON("/projectTemplateParams/cgetParamsByTemplate.do?templateid=" + templateidstr);
                JSONObject jsonptpObject = JSONObject.parseObject(httpptp);
                List<ProjectTemplateParams> paramslist = new ArrayList<ProjectTemplateParams>();
                paramslist = JSONObject.parseArray(jsonptpObject.getString("params"), ProjectTemplateParams.class);  

                //处理头域
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

                //处理更换参数
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
                //处理参数
                Map<String, Object> params = new HashMap<String, Object>(0);
                for (ProjectTemplateParams ptp : paramslist) {
                    //处理参数对象
                    if (ptp.getParamtype() == 1) {
                        String tempparam = ptp.getParam().replace("&quot;", "\"");
                        JSONObject json = JSONObject.parseObject(tempparam);
                        params.put(ptp.getParamname().replace("&quot;", "\""), json);
                        luckyclient.publicclass.LogUtil.APP.info("模板参数【" + ptp.getParamname() + "】  JSONObject类型参数值:【" + json.toString() + "】");
                    } else if (ptp.getParamtype() == 2) {
                        String tempparam = ptp.getParam().replace("&quot;", "\"");
                        JSONArray jarr = JSONArray.parseArray(tempparam);
                        params.put(ptp.getParamname().replace("&quot;", "\""), jarr);
                        luckyclient.publicclass.LogUtil.APP.info("模板参数【" + ptp.getParamname() + "】  JSONArray类型参数值:【" + jarr.toString() + "】");
                    } else if (ptp.getParamtype() == 3) {
                        String tempparam = ptp.getParam().replace("&quot;", "\"");
                        File file = new File(tempparam);
                        params.put(ptp.getParamname().replace("&quot;", "\""), file);
                        luckyclient.publicclass.LogUtil.APP.info("模板参数【" + ptp.getParamname() + "】  File类型参数值:【" + file.getAbsolutePath() + "】");
                    } else if (ptp.getParamtype() == 4) {
                        String tempparam = ptp.getParam().replace("&quot;", "\"");
                        Double dp = Double.valueOf(tempparam);
                        params.put(ptp.getParamname().replace("&quot;", "\""), dp);
                        luckyclient.publicclass.LogUtil.APP.info("模板参数【" + ptp.getParamname() + "】  数字类型参数值:【" + tempparam + "】");
                    } else if (ptp.getParamtype() == 5) {
                        String tempparam = ptp.getParam().replace("&quot;", "\"");
                        Boolean bn = Boolean.valueOf(tempparam);
                        params.put(ptp.getParamname().replace("&quot;", "\""), bn);
                        luckyclient.publicclass.LogUtil.APP.info("模板参数【" + ptp.getParamname() + "】  Boolean类型参数值:【" + bn + "】");
                    } else {
                        params.put(ptp.getParamname().replace("&quot;", "\""), ptp.getParam().replace("&quot;", "\""));
                        luckyclient.publicclass.LogUtil.APP.info("模板参数【" + ptp.getParamname() + "】  String类型参数值:【" + ptp.getParam().replace("&quot;", "\"") + "】");
                    }
                }

                if (functionname.toLowerCase().equals("httpurlpost")) {
                    result = HttpClientHelper.sendHttpURLPost(packagename, params, ppt.getContentencoding().toLowerCase(), ppt.getConnecttimeout(), headmsg);
                } else if (functionname.toLowerCase().equals("urlpost")) {
                    result = HttpClientHelper.sendURLPost(packagename, params, ppt.getContentencoding().toLowerCase(), ppt.getConnecttimeout(), headmsg);
                } else if (functionname.toLowerCase().equals("getandsavefile")) {
                    String fileSavePath = System.getProperty("user.dir") + "\\HTTPSaveFile\\";
                    HttpClientHelper.sendGetAndSaveFile(packagename, params, fileSavePath, ppt.getConnecttimeout(), headmsg);
                    result = "下载文件成功，请前往客户端路径:" + fileSavePath + " 查看附件。";
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
                    luckyclient.publicclass.LogUtil.APP.error("您的HTTP操作方法异常，检测到的操作方法是：" + functionname);
                    result = "调用异常，请查看错误日志！";
                }
            } else if (steptype == 3) {
                String templateidstr = action.substring(1, action.indexOf("】"));
                String templatenamestr = action.substring(action.indexOf("】") + 1);
                luckyclient.publicclass.LogUtil.APP.info("即将使用模板【" + templatenamestr + "】  ID:【" + templateidstr + "】 发送SOCKET请求！");

                String httpppt = HttpRequest.loadJSON("/projectprotocolTemplate/cgetPTemplateById.do?templateid=" + templateidstr);
                ProjectProtocolTemplate ppt = JSONObject.parseObject(httpppt,ProjectProtocolTemplate.class);

                String httpptp = HttpRequest.loadJSON("/projectTemplateParams/cgetParamsByTemplate.do?templateid=" + templateidstr);
                JSONObject jsonptpObject = JSONObject.parseObject(httpptp);
                List<ProjectTemplateParams> paramslist = new ArrayList<ProjectTemplateParams>();
                paramslist = JSONObject.parseArray(jsonptpObject.getString("params"), ProjectTemplateParams.class);  
                
                //处理头域
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

                //处理更换参数
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
                    //处理参数对象
                    if (ptp.getParamtype() == 1) {
                        String tempparam = ptp.getParam().replace("&quot;", "\"");
                        JSONObject json = JSONObject.parseObject(tempparam);
                        params.put(ptp.getParamname().replace("&quot;", "\""), json);
                    } else if (ptp.getParamtype() == 2) {
                        String tempparam = ptp.getParam().replace("&quot;", "\"");
                        JSONArray jarr = JSONArray.parseArray(tempparam);
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
                    luckyclient.publicclass.LogUtil.APP.error("您的SOCKET操作方法异常，检测到的操作方法是：" + functionname);
                    result = "调用异常，请查看错误日志！";
                }
            }
        } catch (Throwable e) {
            luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
            return "调用异常，请查看错误日志！";
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
        // parameterTypes 里面，int->Integer
        // orgParameterTypes是原始参数类型
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
