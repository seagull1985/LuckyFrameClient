package luckyclient.utils.httputils;

import luckyclient.utils.LogUtil;
import luckyclient.utils.config.SysConfig;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @author： seagull
 * @date 2017年12月1日 上午9:29:40
 * 
 */
public class HttpRequest {
	final static Properties PROPERTIES = SysConfig.getConfiguration();
	private final static String WEB_URL = "http://" + PROPERTIES.getProperty("server.web.ip") + ":"
			+ PROPERTIES.getProperty("server.web.port")+ PROPERTIES.getProperty("server.web.path");

	/**
	 * 字符串参数
	 * @param repath 请求路径
	 * @return 返回请求结果
	 */
	public static String loadJSON(String repath) {
		String charset="GBK";
		StringBuffer resultBuffer;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		BufferedReader br = null;
		// 构建请求参数
		HttpGet httpGet = new HttpGet(WEB_URL+repath);
		try {
			HttpResponse response = httpclient.execute(httpGet);
			// 读取服务器响应数据
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
			String temp;
			resultBuffer = new StringBuffer();
			while ((temp = br.readLine()) != null) {
				resultBuffer.append(temp);
			}
		} catch (Exception e) {
			LogUtil.APP.error("loadJSON发送请求出现异常，请检查！", e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LogUtil.APP.error("loadJSON发送请求后关闭br流出现异常，请检查！", e);
				}
			}
		}
		return resultBuffer.toString();
	}

	 /**
     * 向指定 URL 发送POST方法的请求
     * @param param
     * 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String repath, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(WEB_URL+repath);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流，设置utf-8编码
            out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "GBK"));
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应,设置utf-8编码
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "GBK"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
        	LogUtil.APP.error("向指定URL发送POST方法的请求出现异常，请检查！", e);
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
            	LogUtil.APP.error("向指定URL发送POST方法的请求后关闭流出现异常，请检查！", ex);
            }
        }
        return result.toString();
    }

	/**
	 * 使用HttpClient以JSON格式发送post请求
	 * @param urlParam 请求路径
	 * @param params 请求参数
	 * @return 返回请求结果
	 */
	public static String httpClientPostJson(String urlParam, String params){		
		StringBuffer resultBuffer;
		CloseableHttpClient httpclient=HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(WEB_URL+urlParam);
	    httpPost.setHeader("Content-Type", "application/json");
	    RequestConfig requestConfig = RequestConfig.custom()  
	            .setConnectTimeout(60*1000)
	            .setConnectionRequestTimeout(60*1000)  
	            //设置请求和传输超时时间
	            .setSocketTimeout(60*1000).build(); 
	    httpPost.setConfig(requestConfig);
		// 构建请求参数
		BufferedReader br = null;
		try {
		StringEntity entity = new StringEntity(params,"utf-8");
		httpPost.setEntity(entity);
       
		 CloseableHttpResponse response = httpclient.execute(httpPost);

		// 读取服务器响应数据
		resultBuffer = new StringBuffer();
		if(null!=response.getEntity()){
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
			String temp;
			while ((temp = br.readLine()) != null) {
				resultBuffer.append(temp);
			}	
		}
		} catch (Exception e) {
			LogUtil.APP.error("使用HttpClient以JSON格式发送post请求出现异常，请检查！", e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LogUtil.APP.error("使用HttpClient以JSON格式发送post请求后关闭br流出现异常，请检查！", e);
				}
			}
		}		
		return resultBuffer.toString();
	}

}
