package luckyclient.jenkinsapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class JenkinsBuilding {
	/**
     * 向指定URL发送GET方法的请求
     * 发起构建请求
     * @param buildname
     *            jenkins中的构建名称
     * @param param
     *            延迟多少秒进行构建
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendBuilding(String buildname, int param) {
        String result = "";
        BufferedReader in = null;
        try {
        	final String jenkinsurl = "http://10.211.19.19:18080/jenkins/job/";
        	
            String urlString = jenkinsurl+buildname + "/build?delay="+param+"sec";
                      
            URL realUrl = new URL(urlString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                luckyclient.publicclass.LogUtil.APP.info(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            luckyclient.publicclass.LogUtil.APP.error("发送构建请求(GET)时出现异常！", e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    
	/**
     * 向指定URL发送GET方法的请求
     * 判断最后一次构建有没有成功
     * @param buildname
     *            jenkins中的构建名称
     * @return URL 所代表远程资源的响应结果
     * 
     * alt="Success"  alt="In progress"  alt="Failed"
     */
    public static String BuildingResult(String buildname) {
        String result = "";
        BufferedReader in = null;
        try {
        	final String jenkinsurl = "http://10.211.19.19:18080/jenkins/job/";
        	
            String urlString = jenkinsurl+buildname + "/lastBuild/";
                      
            URL realUrl = new URL(urlString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                luckyclient.publicclass.LogUtil.APP.info(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            luckyclient.publicclass.LogUtil.APP.error("发送构建请求(GET)时出现异常！", e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//发送 GET 请求
		//http://10.211.19.19:18080/jenkins/job/72_deploy_settle_check_server/lastBuild/
        String s=JenkinsBuilding.BuildingResult("deploy-abc-b2cApi");
        System.out.println(s);
	}

}
