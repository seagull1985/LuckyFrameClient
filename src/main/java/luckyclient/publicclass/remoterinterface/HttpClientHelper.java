package luckyclient.publicclass.remoterinterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.bind.DatatypeConverter;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import luckyclient.publicclass.Constants;
import luckyclient.publicclass.LogUtil;
import luckyclient.serverapi.entity.ProjectProtocolTemplate;

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
public class HttpClientHelper {
	/**
	 * 使用HttpURLConnection发送post请求
	 * @param urlParam
	 * @param params
	 * @param charset
	 * @param timeout
	 * @param headmsg
	 * @return
	 */
	public static String sendHttpURLPost(String urlParam, Map<String, Object> params, Map<String, String> headmsg,ProjectProtocolTemplate ppt) {
		String charset=ppt.getEncoding().toLowerCase();
		int timeout=ppt.getTimeout();
		int responsehead=ppt.getIsResponseHead();
		int responsecode=ppt.getIsResponseCode();
		
		StringBuffer resultBuffer = null;
		LogUtil.APP.info("设置HTTP请求地址:【{}】",urlParam);
		// 构建请求参数
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			if(1==params.size()&&params.containsKey("_forTextJson")){
				LogUtil.APP.warn("协议模板是纯文本模式(仅限httpClientPostJson以及httpClientPutJson请求)，无法使用sendHttpURLPost请求(必须为key-value)...");
				return "协议模板是纯文本，无法使用sendHttpURLPost请求(必须为key-value)...";
			}else{
				for (Entry<String, Object> e : params.entrySet()) {
					sbParams.append(e.getKey());
					sbParams.append("=");
					sbParams.append(e.getValue());
					sbParams.append("&");
					LogUtil.APP.info("设置HTTPURLPost参数信息...key:【{}】    value:【{}】",e.getKey(),e.getValue());
				}
			}
		}
		HttpURLConnection con = null;
		OutputStreamWriter osw = null;
		BufferedReader br = null;
		// 发送请求
		try {
			URL url = new URL(urlParam);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setConnectTimeout(timeout*1000);
			//替换头域信息
		    for (Map.Entry<String, String> m :headmsg.entrySet())  {
		    	String key=m.getKey();
		    	String value=m.getValue();
		    	LogUtil.APP.info("开始设置|替换HTTPURLPost头域信息...key:【{}】    value:【{}】",key,value);
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
		    		LogUtil.APP.info("将头域【{}】的值【{}】FORMAT成BASE64格式...",key,value);
		    		con.setRequestProperty(key, value);
		    	}else{
		    		con.setRequestProperty(key, value);
		    	}
	        }
			if (sbParams != null && sbParams.length() > 0) {
				osw = new OutputStreamWriter(con.getOutputStream(), charset);
				osw.write(sbParams.substring(0, sbParams.length() - 1));
				osw.flush();
			}
			// 读取返回内容
			resultBuffer = new StringBuffer();
			int contentLength =0;
			if(null!=con.getHeaderField("Content-Length")){
				contentLength = Integer.parseInt(con.getHeaderField("Content-Length"));
			}
			
			if(1==responsehead){
				Map<String, List<String>> headmsgstr=con.getHeaderFields();
				JSONObject itemJSONObj = JSONObject.parseObject(JSON.toJSONString(headmsgstr));
				resultBuffer.append(Constants.RESPONSE_HEAD+itemJSONObj+Constants.RESPONSE_END);
			}
			if(1==responsecode){
				resultBuffer.append(Constants.RESPONSE_CODE+con.getResponseCode()+Constants.RESPONSE_END);
			}
			if (contentLength > 0||"chunked".equals(con.getHeaderField("Transfer-Encoding"))) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
				String temp;
				while ((temp = br.readLine()) != null) {
					resultBuffer.append(temp);
				}
			}else{
				resultBuffer.append("Content-Length=0");
			}
		} catch (Exception e) {
			LogUtil.APP.error("使用HttpURLConnection发送post请求出现异常，请检查！", e);
			throw new RuntimeException(e);
		} finally {
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					LogUtil.APP.error("使用HttpURLConnection发送post请求后关闭流出现异常，请检查！", e);
					osw = null;
					throw new RuntimeException(e);
				} finally {
					if (con != null) {
						con.disconnect();
						con = null;
					}
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LogUtil.APP.error("使用HttpURLConnection发送post请求后关闭流出现异常，请检查！", e);
					br = null;
					throw new RuntimeException(e);
				} finally {
					if (con != null) {
						con.disconnect();
						con = null;
					}
				}
			}
		}

		return resultBuffer.toString();
	}

	/**
	 * 使用URLConnection发送post
	 * @param urlParam
	 * @param params
	 * @param charset
	 * @param timeout
	 * @param headmsg
	 * @return
	 */
	public static String sendURLPost(String urlParam, Map<String, Object> params, Map<String, String> headmsg,ProjectProtocolTemplate ppt) {		
		String charset=ppt.getEncoding().toLowerCase();
		int timeout=ppt.getTimeout();
		int responsehead=ppt.getIsResponseHead();
		int responsecode=ppt.getIsResponseCode();
		
		StringBuffer resultBuffer = null;
		// 构建请求参数
		LogUtil.APP.info("设置HTTP请求地址:【{}】",urlParam);
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			if(1==params.size()&&params.containsKey("_forTextJson")){
				LogUtil.APP.warn("协议模板是纯文本模式(仅限httpClientPostJson以及httpClientPutJson请求)，无法使用sendURLPost请求(必须为key-value)...");
				return "协议模板是纯文本，无法使用sendURLPost请求(必须为key-value)...";
			}else{
				for (Entry<String, Object> e : params.entrySet()) {
					sbParams.append(e.getKey());
					sbParams.append("=");
					sbParams.append(e.getValue());
					sbParams.append("&");
					LogUtil.APP.info("设置URLPost参数信息...key:【{}】    value:【{}】",e.getKey(),e.getValue());
				}
			}
		}
		URLConnection con = null;
		OutputStreamWriter osw = null;
		BufferedReader br = null;
		try {
			URL realUrl = new URL(urlParam);
			// 打开和URL之间的连接
			con = realUrl.openConnection();
			// 设置通用的请求属性
			con.setRequestProperty("accept", "*/*");
			con.setRequestProperty("connection", "Keep-Alive");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			//替换头域信息
		    for (Map.Entry<String, String> m :headmsg.entrySet())  {
		    	String key=m.getKey();
		    	String value=m.getValue();
		    	LogUtil.APP.info("开始设置|替换URLPost头域信息...key:【{}】    value:【{}】",key,value);
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
		    		LogUtil.APP.info("将头域【{}】的值【{}】FORMAT成BASE64格式...",key,value);
		    		con.setRequestProperty(key, value);
		    	}else{
		    		con.setRequestProperty(key, value);
		    	}
	        }
		    
			con.setConnectTimeout(timeout*1000);
			// 发送POST请求必须设置如下两行
			con.setDoOutput(true);
			con.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			osw = new OutputStreamWriter(con.getOutputStream(), charset);
			if (sbParams != null && sbParams.length() > 0) {
				// 发送请求参数
				osw.write(sbParams.substring(0, sbParams.length() - 1));
				// flush输出流的缓冲
				osw.flush();
			}
			// 定义BufferedReader输入流来读取URL的响应
			resultBuffer = new StringBuffer();
			int contentLength =0;
			if(null!=con.getHeaderField("Content-Length")){
				contentLength = Integer.parseInt(con.getHeaderField("Content-Length"));
			}
			if(1==responsehead){
				Map<String, List<String>> headmsgstr=con.getHeaderFields();
				JSONObject itemJSONObj = JSONObject.parseObject(JSON.toJSONString(headmsgstr));
				resultBuffer.append(Constants.RESPONSE_HEAD+itemJSONObj+Constants.RESPONSE_END);
			}
			if(1==responsecode){
				resultBuffer.append(Constants.RESPONSE_CODE+con.getHeaderField(null)+Constants.RESPONSE_END);
			}
			if (contentLength >= 0||"chunked".equals(con.getHeaderField("Transfer-Encoding"))) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
				String temp;
				while ((temp = br.readLine()) != null) {
					resultBuffer.append(temp);
				}
			}else{
					resultBuffer.append("Content-Length=0");
			}
		} catch (Exception e) {
			LogUtil.APP.error("使用URLConnection发送post请求出现异常，请检查！", e);
			throw new RuntimeException(e);
		} finally {
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					LogUtil.APP.error("使用URLConnection发送post请求后关闭osw流出现异常，请检查！", e);
					osw = null;
					throw new RuntimeException(e);
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LogUtil.APP.error("使用URLConnection发送post请求后关闭br流出现异常，请检查！", e);
					br = null;
					throw new RuntimeException(e);
				}
			}
		}
		return resultBuffer.toString();
	}

	/**
	 * 发送get请求保存下载文件
	 * @param urlParam
	 * @param params
	 * @param fileSavePath
	 * @param timeout
	 * @param headmsg
	 */
	public static String sendGetAndSaveFile(String urlParam, Map<String, Object> params, String fileSavePath, Map<String, String> headmsg,ProjectProtocolTemplate ppt) {
		// 构建请求参数
		LogUtil.APP.info("设置HTTP请求地址:【{}】",urlParam);
		int timeout=ppt.getTimeout();
		int responsehead=ppt.getIsResponseHead();
		int responsecode=ppt.getIsResponseCode();
		
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			if(1==params.size()&&params.containsKey("_forTextJson")){
				LogUtil.APP.warn("协议模板是纯文本模式(仅限httpClientPostJson以及httpClientPutJson请求)，无法使用sendGetAndSaveFile请求(必须为key-value)...");
			}else{
				for (Entry<String, Object> entry : params.entrySet()) {
					sbParams.append(entry.getKey());
					sbParams.append("=");
					sbParams.append(entry.getValue());
					sbParams.append("&");
					LogUtil.APP.info("设置HTTPSaveFile参数信息...key:【{}】    value:【{}】",entry.getKey(),entry.getValue());
				}
			}
		}
		HttpURLConnection con = null;
		BufferedReader br = null;
		FileOutputStream os = null;
		try {
			URL url = null;
			if (sbParams != null && sbParams.length() > 0) {
				url = new URL(urlParam + "?" + sbParams.substring(0, sbParams.length() - 1));
			} else {
				url = new URL(urlParam);
			}
			con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			//替换头域信息
		    for (Map.Entry<String, String> m :headmsg.entrySet())  {
		    	String key=m.getKey();
		    	String value=m.getValue();
		    	LogUtil.APP.info("开始设置|替换HTTPSaveFile头域信息...key:【{}】    value:【{}】",key,value);
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
		    		LogUtil.APP.info("将头域【{}】的值【{}】FORMAT成BASE64格式...",key,value);
		    		con.setRequestProperty(key, value);
		    	}else{
		    		con.setRequestProperty(key, value);
		    	}
	        }
			con.setConnectTimeout(timeout*1000);
			con.connect();
			// 定义BufferedReader输入流来读取URL的响应
			StringBuffer resultBuffer = new StringBuffer();
			if(1==responsehead){
				Map<String, List<String>> headmsgstr=con.getHeaderFields();
				JSONObject itemJSONObj = JSONObject.parseObject(JSON.toJSONString(headmsgstr));
				resultBuffer.append(Constants.RESPONSE_HEAD+itemJSONObj+Constants.RESPONSE_END);
			}
			if(1==responsecode){
				resultBuffer.append(Constants.RESPONSE_CODE+con.getResponseCode()+Constants.RESPONSE_END);
			}
			InputStream is = con.getInputStream();
			os = new FileOutputStream(fileSavePath);
			byte buf[] = new byte[1024];
			int count = 0;
			while ((count = is.read(buf)) != -1) {
				os.write(buf, 0, count);
			}
			os.flush();
			return resultBuffer.toString()+"下载文件成功，请前往客户端路径:" + fileSavePath + " 查看附件。";
		} catch (Exception e) {
			LogUtil.APP.error("发送get请求保存下载文件出现异常，请检查！", e);
			throw new RuntimeException(e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					LogUtil.APP.error("发送get请求保存下载文件后关闭OS流出现异常，请检查！", e);
					os = null;
					throw new RuntimeException(e);
				} finally {
					if (con != null) {
						con.disconnect();
						con = null;
					}
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LogUtil.APP.error("发送get请求保存下载文件后关闭br流出现异常，请检查！", e);
					br = null;
					throw new RuntimeException(e);
				} finally {
					if (con != null) {
						con.disconnect();
						con = null;
					}
				}
			}
		}
	}

	/**
	 * 使用HttpURLConnection发送get请求
	 * @param urlParam
	 * @param params
	 * @param charset
	 * @param timeout
	 * @param headmsg
	 * @return
	 */
	public static String sendHttpURLGet(String urlParam, Map<String, Object> params, Map<String, String> headmsg,ProjectProtocolTemplate ppt) {
		String charset=ppt.getEncoding().toLowerCase();
		int timeout=ppt.getTimeout();
		int responsehead=ppt.getIsResponseHead();
		int responsecode=ppt.getIsResponseCode();
		
		StringBuffer resultBuffer = null;
		// 构建请求参数
		LogUtil.APP.info("设置HTTP请求地址:【{}】",urlParam);
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			if(1==params.size()&&params.containsKey("_forTextJson")){
				LogUtil.APP.warn("协议模板是纯文本模式(仅限httpClientPostJson以及httpClientPutJson请求)，无法使用sendHttpURLGet请求(必须为key-value)...");
				return "协议模板是纯文本模式(仅限httpClientPostJson以及httpClientPutJson请求)，无法使用sendHttpURLGet请求(必须为key-value)...";
			}else{
				for (Entry<String, Object> entry : params.entrySet()) {
					sbParams.append(entry.getKey());
					sbParams.append("=");
					sbParams.append(entry.getValue());
					sbParams.append("&");
					LogUtil.APP.info("设置HTTPURLGet参数信息...key:【{}】    value:【{}】",entry.getKey(),entry.getValue());
				}
			}
		}
		HttpURLConnection con = null;
		BufferedReader br = null;
		try {
			URL url = null;
			if (sbParams != null && sbParams.length() > 0) {
				url = new URL(urlParam + "?" + sbParams.substring(0, sbParams.length() - 1));
			} else {
				url = new URL(urlParam);
			}
			con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			//替换头域信息
		    for (Map.Entry<String, String> m :headmsg.entrySet())  {
		    	String key=m.getKey();
		    	String value=m.getValue();
		    	LogUtil.APP.info("开始设置|替换HTTPURLGet头域信息...key:【{}】    value:【{}】",key,value);
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
		    		LogUtil.APP.info("将头域【{}】的值【{}】FORMAT成BASE64格式...",key,value);
		    		con.setRequestProperty(key, value);
		    	}else{
		    		con.setRequestProperty(key, value);
		    	}
	        }
			con.setConnectTimeout(timeout*1000);
			con.connect();
			resultBuffer = new StringBuffer();
			if(1==responsehead){
				Map<String, List<String>> headmsgstr=con.getHeaderFields();
				JSONObject itemJSONObj = JSONObject.parseObject(JSON.toJSONString(headmsgstr));
				resultBuffer.append(Constants.RESPONSE_HEAD+itemJSONObj+Constants.RESPONSE_END);
			}
			if(1==responsecode){
				resultBuffer.append(Constants.RESPONSE_CODE+con.getResponseCode()+Constants.RESPONSE_END);
			}
			br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
			String temp;
			while ((temp = br.readLine()) != null) {
				resultBuffer.append(temp);
			}
			if(resultBuffer.length()==0){
				resultBuffer.append("读取服务器响应数据异常!响应码："+con.getResponseCode());
			}
		} catch (Exception e) {
			LogUtil.APP.error("使用HttpURLConnection发送get请求出现异常，请检查！", e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LogUtil.APP.error("使用HttpURLConnection发送get请求后关闭br流出现异常，请检查！", e);
					br = null;
					throw new RuntimeException(e);
				} finally {
					if (con != null) {
						con.disconnect();
						con = null;
					}
				}
			}
		}
		return resultBuffer.toString();
	}

	/**
	 * 使用URLConnection发送get请求
	 * @param urlParam
	 * @param params
	 * @param charset
	 * @param timeout
	 * @param headmsg
	 * @return
	 */
	public static String sendURLGet(String urlParam, Map<String, Object> params, Map<String, String> headmsg,ProjectProtocolTemplate ppt) {
		String charset=ppt.getEncoding().toLowerCase();
		int timeout=ppt.getTimeout();
		int responsehead=ppt.getIsResponseHead();
		int responsecode=ppt.getIsResponseCode();
		
		StringBuffer resultBuffer = null;
		// 构建请求参数
		LogUtil.APP.info("设置HTTP请求地址:【{}】",urlParam);
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			if(1==params.size()&&params.containsKey("_forTextJson")){
				LogUtil.APP.warn("协议模板是纯文本模式(仅限httpClientPostJson以及httpClientPutJson请求)，无法使用sendURLGet请求(必须为key-value)...");
				return "协议模板是纯文本模式(仅限httpClientPostJson以及httpClientPutJson请求)，无法使用sendURLGet请求(必须为key-value)...";
			}else{
				for (Entry<String, Object> entry : params.entrySet()) {
					sbParams.append(entry.getKey());
					sbParams.append("=");
					sbParams.append(entry.getValue());
					sbParams.append("&");
					LogUtil.APP.info("设置URLGet参数信息...key:【{}】    value:【{}】",entry.getKey(),entry.getValue());
				}
			}

		}
		BufferedReader br = null;
		try {
			URL url = null;
			if (sbParams != null && sbParams.length() > 0) {
				url = new URL(urlParam + "?" + sbParams.substring(0, sbParams.length() - 1));
			} else {
				url = new URL(urlParam);
			}
			URLConnection con = url.openConnection();
			// 设置请求属性
			con.setRequestProperty("accept", "*/*");
			con.setRequestProperty("connection", "Keep-Alive");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			//替换头域信息
		    for (Map.Entry<String, String> m :headmsg.entrySet())  {
		    	String key=m.getKey();
		    	String value=m.getValue();
		    	LogUtil.APP.info("开始设置|替换URLGet头域信息...key:【{}】    value:【{}】",key,value);
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
		    		LogUtil.APP.info("将头域【{}】的值【{}】FORMAT成BASE64格式...",key,value);
		    		con.setRequestProperty(key, value);
		    	}else{
		    		con.setRequestProperty(key, value);
		    	}
	        }
			con.setConnectTimeout(timeout*1000);
			// 建立连接
			con.connect();
			resultBuffer = new StringBuffer();
			if(1==responsehead){
				Map<String, List<String>> headmsgstr=con.getHeaderFields();
				JSONObject itemJSONObj = JSONObject.parseObject(JSON.toJSONString(headmsgstr));
				resultBuffer.append(Constants.RESPONSE_HEAD+itemJSONObj+Constants.RESPONSE_END);
			}
			if(1==responsecode){
				resultBuffer.append(Constants.RESPONSE_CODE+con.getHeaderField(null)+Constants.RESPONSE_END);
			}
			br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
			String temp;
			while ((temp = br.readLine()) != null) {
				resultBuffer.append(temp);
			}
			if(resultBuffer.length()==0){
				resultBuffer.append("读取服务器响应数据异常!");
			}
		} catch (Exception e) {
			LogUtil.APP.error("使用URLConnection发送get请求出现异常，请检查！", e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LogUtil.APP.error("使用URLConnection发送get请求后关闭br流出现异常，请检查！", e);
					br = null;
					throw new RuntimeException(e);
				}
			}
		}
		return resultBuffer.toString();
	}

	/**
	 * 使用HttpClient以JSON格式发送post请求
	 * @param urlParam
	 * @param params
	 * @param charset
	 * @param headmsg
	 * @param cerpath
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public static String httpClientPostJson(String urlParam, Map<String, Object> params, Map<String, String> headmsg,ProjectProtocolTemplate ppt) throws NoSuchAlgorithmException, KeyManagementException {
		String cerpath=ppt.getCerificatePath();
		String charset=ppt.getEncoding().toLowerCase();
		int timeout=ppt.getTimeout()*1000;
		int responsehead=ppt.getIsResponseHead();
		int responsecode=ppt.getIsResponseCode();
		
		StringBuffer resultBuffer = null;
		LogUtil.APP.info("设置HTTP请求地址:【{}】",urlParam);
		CloseableHttpClient httpclient=iniHttpClient(urlParam,cerpath);
		HttpPost httpPost = new HttpPost(urlParam);
	    httpPost.setHeader("Content-Type", "application/json");
	    RequestConfig requestConfig = RequestConfig.custom()  
	            .setConnectTimeout(timeout)
	            .setConnectionRequestTimeout(timeout)  
	             //设置请求和传输超时时间
	            .setSocketTimeout(timeout).build();  
	    httpPost.setConfig(requestConfig);
		//替换头域信息
	    for (Map.Entry<String, String> m :headmsg.entrySet())  {
	    	String key=m.getKey();
	    	String value=m.getValue();
	    	LogUtil.APP.info("开始设置|替换HTTPPostJson头域信息...key:【{}】    value:【{}】",key,value);
	    	if(null!=value&&value.indexOf("Base64(")==0){
	    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
	    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
	    		LogUtil.APP.info("将头域【{}】的值【{}】FORMAT成BASE64格式...",key,value);
	    		httpPost.setHeader(key, value);
	    	}else{
	    		httpPost.setHeader(key, value);
	    	}
        }
		// 构建请求参数
		BufferedReader br = null;
		try {
		if(params.size()>0){
			if(1==params.size()&&params.containsKey("_forTextJson")){
				LogUtil.APP.info("参数类型：TEXT,设置HTTPPostJson参数信息...【{}】",params.get("_forTextJson").toString());
				StringEntity entity = new StringEntity(params.get("_forTextJson").toString(),charset);
				httpPost.setEntity(entity);
			}else{
			    String jsonString = JSON.toJSONString(params);
				LogUtil.APP.info("参数类型：FORM,设置HTTPPostJson参数信息...【{}】",jsonString);
				StringEntity entity = new StringEntity(jsonString,charset);
				httpPost.setEntity(entity);
			}
		}
       
		 CloseableHttpResponse response = httpclient.execute(httpPost);

		// 读取服务器响应数据
		resultBuffer = new StringBuffer();
		if(1==responsehead){
			Header[] headmsgstr=response.getAllHeaders();
			resultBuffer.append("RESPONSE_HEAD:【{");
			for(Header header:headmsgstr){
				resultBuffer.append("\""+header.getName()+"\":\""+header.getValue()+"\",");
			}
			resultBuffer.delete(resultBuffer.length()-1, resultBuffer.length()).append("}】 ");
		}
		if(1==responsecode){
			resultBuffer.append(Constants.RESPONSE_CODE+response.getStatusLine().getStatusCode()+Constants.RESPONSE_END);
		}
		if(null!=response.getEntity()){
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
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
					br = null;
					throw new RuntimeException(e);
				}
			}
		}		
		return resultBuffer.toString();
	}
	
	/**
	 * 使用HttpClient发送post请求
	 * @param urlParam
	 * @param params
	 * @param charset
	 * @param headmsg
	 * @param cerpath
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public static String httpClientPost(String urlParam, Map<String, Object> params, Map<String, String> headmsg,ProjectProtocolTemplate ppt) throws NoSuchAlgorithmException, KeyManagementException {
		String cerpath=ppt.getCerificatePath();
		String charset=ppt.getEncoding().toLowerCase();
		int timeout=ppt.getTimeout()*1000;
		int responsehead=ppt.getIsResponseHead();
		int responsecode=ppt.getIsResponseCode();
		
		StringBuffer resultBuffer = null;
		CloseableHttpClient httpclient=iniHttpClient(urlParam,cerpath);
		LogUtil.APP.info("设置HTTP请求地址:【{}】",urlParam);
		HttpPost httpPost = new HttpPost(urlParam);
	    RequestConfig requestConfig = RequestConfig.custom()  
	            .setConnectTimeout(timeout)
	            .setConnectionRequestTimeout(timeout) 
	            //设置请求和传输超时时间
	            .setSocketTimeout(timeout).build(); 
	    httpPost.setConfig(requestConfig);
		//替换头域信息
	    for (Map.Entry<String, String> m :headmsg.entrySet())  {
	    	String key=m.getKey();
	    	String value=m.getValue();
	    	LogUtil.APP.info("开始设置|替换HTTPClientPost头域信息...key:【{}】    value:【{}】",key,value);
	    	if(null!=value&&value.indexOf("Base64(")==0){
	    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
	    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
	    		LogUtil.APP.info("将头域【{}】的值【{}】FORMAT成BASE64格式...",key,value);
	    		httpPost.setHeader(key, value);
	    	}else{
	    		httpPost.setHeader(key, value);
	    	}
        }
		// 构建请求参数
		BufferedReader br = null;
		try {
			if(params.size()>0){
				if(1==params.size()&&params.containsKey("_forTextJson")){
					LogUtil.APP.warn("协议模板是纯文本模式(仅限httpClientPostJson以及httpClientPutJson请求)，无法使用httpClientPost请求(必须为key-value)...");
					return "协议模板是纯文本模式(仅限httpClientPostJson以及httpClientPutJson请求)，无法使用httpClientPost请求(必须为key-value)...";
				}else{
					//拼接参数
				    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
				    for (Map.Entry<String, Object> m :params.entrySet())  { 
			            nvps.add(new BasicNameValuePair(m.getKey(), m.getValue().toString()));
			            LogUtil.APP.info("设置HTTPClientPost参数信息...key:【{}】    value:【{}】",m.getKey(),m.getValue());
			        }
				    httpPost.setEntity(new UrlEncodedFormEntity(nvps,charset));
				}
			}

			 CloseableHttpResponse response = httpclient.execute(httpPost);
			// 读取服务器响应数据
			resultBuffer = new StringBuffer();
			if(1==responsehead){
				Header[] headmsgstr=response.getAllHeaders();
				resultBuffer.append("RESPONSE_HEAD:【{");
				for(Header header:headmsgstr){
					resultBuffer.append("\""+header.getName()+"\":\""+header.getValue()+"\",");
				}
				resultBuffer.delete(resultBuffer.length()-1, resultBuffer.length()).append("}】 ");
			}
			if(1==responsecode){
				resultBuffer.append(Constants.RESPONSE_CODE+response.getStatusLine().getStatusCode()+Constants.RESPONSE_END);
			}
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
			String temp;
			while ((temp = br.readLine()) != null) {
				resultBuffer.append(temp);
			}
			if(resultBuffer.length()==0){
				resultBuffer.append("读取服务器响应数据异常，响应码："+response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			LogUtil.APP.error("使用HttpClient发送post请求出现异常，请检查！", e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LogUtil.APP.error("使用HttpClient发送post请求后关闭br流出现异常，请检查！", e);
					br = null;
					throw new RuntimeException(e);
				}
			}
		}
		
		return resultBuffer.toString();
	}

	/**
	 * 使用HttpClient上传文件
	 * @param urlParam
	 * @param params
	 * @param charset
	 * @param headmsg
	 * @param cerpath
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public static String httpClientUploadFile(String urlParam, Map<String, Object> params, Map<String, String> headmsg,ProjectProtocolTemplate ppt) throws NoSuchAlgorithmException, KeyManagementException {
		String cerpath=ppt.getCerificatePath();
		String charset=ppt.getEncoding().toLowerCase();
		int timeout=ppt.getTimeout()*1000;
		int responsehead=ppt.getIsResponseHead();
		int responsecode=ppt.getIsResponseCode();
		
		StringBuffer resultBuffer = null;
		LogUtil.APP.info("设置HTTP请求地址:【{}】",urlParam);
		CloseableHttpClient httpclient=iniHttpClient(urlParam,cerpath);
		HttpPost httpPost = new HttpPost(urlParam);
	    RequestConfig requestConfig = RequestConfig.custom()  
	            .setConnectTimeout(timeout)
	            .setConnectionRequestTimeout(timeout)  
	             //设置请求和传输超时时间
	            .setSocketTimeout(timeout).build();  
	    httpPost.setConfig(requestConfig);
		//替换头域信息
	    for (Map.Entry<String, String> m :headmsg.entrySet())  {
	    	String key=m.getKey();
	    	String value=m.getValue();
	    	LogUtil.APP.info("开始设置|替换httpClientUploadFile头域信息...key:【{}】    value:【{}】",key,value);
	    	if(null!=value&&value.indexOf("Base64(")==0){
	    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
	    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
	    		LogUtil.APP.info("将头域【{}】的值【{}】FORMAT成BASE64格式...",key,value);
	    		httpPost.setHeader(key, value);
	    	}else{
	    		httpPost.setHeader(key, value);
	    	}
        }
		// 构建请求参数
		BufferedReader br = null;
		try {
			if(params.size()>0){
				if(1==params.size()&&params.containsKey("_forTextJson")){
					LogUtil.APP.warn("协议模板是纯文本模式(仅限httpClientPostJson以及httpClientPutJson请求)，无法使用httpClientUploadFile请求(必须为key-value)...");
					return "协议模板是纯文本模式(仅限httpClientPostJson以及httpClientPutJson请求)，无法使用httpClientUploadFile请求(必须为key-value)...";
				}else{
					//拼接参数
					MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
					//设置请求的编码格式  
					entityBuilder.setCharset(Charset.forName(charset));
					
				    for (Map.Entry<String, Object> m :params.entrySet())  {
				    	if (m.getValue() instanceof File) {
				    		entityBuilder.addBinaryBody(m.getKey(), (File)m.getValue());
				    		LogUtil.APP.info("设置httpClientUploadFile 上传文件参数信息...key:【{}】    value:【{}】",m.getKey(),m.getValue());
				    	}else{
				    		entityBuilder.addTextBody(m.getKey(), m.getValue().toString());
				    		LogUtil.APP.info("设置httpClientUploadFile参数信息...key:【{}】    value:【{}】",m.getKey(),m.getValue());
				    	}
			        }
				    HttpEntity reqEntity =entityBuilder.build();
				    httpPost.setEntity(reqEntity);
				}
			}

			 CloseableHttpResponse response = httpclient.execute(httpPost);
			// 读取服务器响应数据
			resultBuffer = new StringBuffer();
			if(1==responsehead){
				Header[] headmsgstr=response.getAllHeaders();
				resultBuffer.append("RESPONSE_HEAD:【{");
				for(Header header:headmsgstr){
					resultBuffer.append("\""+header.getName()+"\":\""+header.getValue()+"\",");
				}
				resultBuffer.delete(resultBuffer.length()-1, resultBuffer.length()).append("}】 ");
			}
			if(1==responsecode){
				resultBuffer.append(Constants.RESPONSE_CODE+response.getStatusLine().getStatusCode()+Constants.RESPONSE_END);
			}
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
			String temp;
			while ((temp = br.readLine()) != null) {
				resultBuffer.append(temp);
			}
			if(resultBuffer.length()==0){
				resultBuffer.append("读取服务器响应数据异常，响应码："+response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {
			LogUtil.APP.error("使用HttpClient上传文件出现异常，请检查！", e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LogUtil.APP.error("使用HttpClient上传文件后关闭br流出现异常，请检查！", e);
					br = null;
					throw new RuntimeException(e);
				}
			}
		}
		return resultBuffer.toString();
	}
	
	/**
	 * 使用HttpClient发送get请求
	 * @param urlParam
	 * @param params
	 * @param charset
	 * @param headmsg
	 * @param cerpath
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public static String httpClientGet(String urlParam, Map<String, Object> params, Map<String, String> headmsg,ProjectProtocolTemplate ppt) throws NoSuchAlgorithmException, KeyManagementException {
		String cerpath=ppt.getCerificatePath();
		String charset=ppt.getEncoding().toLowerCase();
		int timeout=ppt.getTimeout()*1000;
		int responsehead=ppt.getIsResponseHead();
		int responsecode=ppt.getIsResponseCode();
		
		StringBuffer resultBuffer = null;
		LogUtil.APP.info("设置HTTP请求地址:【{}】",urlParam);	
		CloseableHttpClient httpclient=iniHttpClient(urlParam,cerpath);
		BufferedReader br = null;
		// 构建请求参数
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			if(1==params.size()&&params.containsKey("_forTextJson")){
				LogUtil.APP.warn("协议模板是纯文本模式(仅限httpClientPostJson以及httpClientPutJson请求)，无法使用httpClientGet请求(必须为key-value)...");
				return "协议模板是纯文本模式(仅限httpClientPostJson以及httpClientPutJson请求)，无法使用httpClientGet请求(必须为key-value)...";
			}else{
				for (Entry<String, Object> entry : params.entrySet()) {
					sbParams.append(entry.getKey());
					sbParams.append("=");
					try {
						sbParams.append(URLEncoder.encode(String.valueOf(entry.getValue()), charset));
					} catch (UnsupportedEncodingException e) {
						LogUtil.APP.error("使用HttpClient发送get请求拼接URL时出现异常，请检查！", e);
						throw new RuntimeException(e);
					}
					sbParams.append("&");
					LogUtil.APP.info("设置HTTPClientGet参数信息...key:【{}】    value:【{}】",entry.getKey(),entry.getValue());
				}
			}
			
		}
		if (sbParams != null && sbParams.length() > 0) {
			urlParam = urlParam + "?" + sbParams.substring(0, sbParams.length() - 1);
		}
		HttpGet httpGet = new HttpGet(urlParam);
	    RequestConfig requestConfig = RequestConfig.custom()  
	            .setConnectTimeout(timeout)
	            .setConnectionRequestTimeout(timeout) 
	            //设置请求和传输超时时间
	            .setSocketTimeout(timeout).build(); 
	    httpGet.setConfig(requestConfig);
		//替换头域信息
	    for (Map.Entry<String, String> m :headmsg.entrySet())  {
	    	String key=m.getKey();
	    	String value=m.getValue();
	    	LogUtil.APP.info("开始设置|替换HTTPClientGet头域信息...key:【{}】    value:【{}】",key,value);
	    	if(null!=value&&value.indexOf("Base64(")==0){
	    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
	    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
	    		LogUtil.APP.info("将头域【{}】的值【{}】FORMAT成BASE64格式...",key,value);
	    		httpGet.setHeader(key, value);
	    	}else{
	    		httpGet.setHeader(key, value);
	    	}
        }
		try {
			CloseableHttpResponse response = httpclient.execute(httpGet);
			
			// 读取服务器响应数据
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
			String temp;
			resultBuffer = new StringBuffer();
			if(1==responsehead){
				Header[] headmsgstr=response.getAllHeaders();
				resultBuffer.append("RESPONSE_HEAD:【{");
				for(Header header:headmsgstr){
					resultBuffer.append("\""+header.getName()+"\":\""+header.getValue()+"\",");
				}
				resultBuffer.delete(resultBuffer.length()-1, resultBuffer.length()).append("}】 ");
			}
			if(1==responsecode){
				resultBuffer.append(Constants.RESPONSE_CODE+response.getStatusLine().getStatusCode()+Constants.RESPONSE_END);
			}
			while ((temp = br.readLine()) != null) {
				resultBuffer.append(temp);
			}
		} catch (Exception e) {
			LogUtil.APP.error("使用HttpClient发送get请求出现异常，请检查！", e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LogUtil.APP.error("使用HttpClient发送get请求后关闭br流出现异常，请检查！", e);
					br = null;
					throw new RuntimeException(e);
				}
			}
		}
		return resultBuffer.toString();
	}

	/**
	 * 使用socket发送post请求
	 * @param urlParam
	 * @param params
	 * @param charset
	 * @param headmsg
	 * @return
	 */
	public static String sendSocketPost(String urlParam, Map<String, Object> params, String charset,
			Map<String, String> headmsg) {
		String result = "";
		LogUtil.APP.info("设置Socket请求地址:【{}】",urlParam);
		// 构建请求参数
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			if(1==params.size()&&params.containsKey("_forTextJson")){
				LogUtil.APP.warn("协议模板是纯文本模式(仅限httpClientPostJson以及httpClientPutJson请求)，无法使用sendSocketPost请求(必须为key-value)...");
				return "协议模板是纯文本模式(仅限httpClientPostJson以及httpClientPutJson请求)，无法使用sendSocketPost请求(必须为key-value)...";
			}else{
				for (Entry<String, Object> entry : params.entrySet()) {
					sbParams.append(entry.getKey());
					sbParams.append("=");
					sbParams.append(entry.getValue());
					sbParams.append("&");
					LogUtil.APP.info("设置SocketPost参数信息...key:【{}】    value:【{}】",entry.getKey(),entry.getValue());
				}
			}
		}
		Socket socket = null;
		OutputStreamWriter osw = null;
		InputStream is = null;
		try {
			URL url = new URL(urlParam);
			String host = url.getHost();
			int port = url.getPort();
			if (-1 == port) {
				port = 80;
			}
			String path = url.getPath();
			socket = new Socket(host, port);
			StringBuffer sb = new StringBuffer();
			sb.append("POST " + path + " HTTP/1.1\r\n");
			sb.append("Host: " + host + "\r\n");
			sb.append("Connection: Keep-Alive\r\n");
			sb.append("Content-Type: application/x-www-form-urlencoded; charset=utf-8 \r\n");
			//替换头域信息
		    for (Map.Entry<String, String> m :headmsg.entrySet())  {
		    	String key=m.getKey();
		    	String value=m.getValue();
		    	LogUtil.APP.info("开始设置|替换Socket头域信息...key:【{}】    value:【{}】",key,value);
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
		    		LogUtil.APP.info("将头域【{}】的值【{}】FORMAT成BASE64格式...",key,value);
		    		sb.append(key+": "+value+" \r\n");
		    	}else{
		    		sb.append(key+": "+value+" \r\n");
		    	}
	        }
			sb.append("Content-Length: ").append(sb.toString().getBytes().length).append("\r\n");
			// 这里一个回车换行，表示消息头写完，不然服务器会继续等待
			sb.append("\r\n");
			if (sbParams != null && sbParams.length() > 0) {
				sb.append(sbParams.substring(0, sbParams.length() - 1));
			}
			osw = new OutputStreamWriter(socket.getOutputStream());
			osw.write(sb.toString());
			osw.flush();
			is = socket.getInputStream();
			String line = null;
			// 服务器响应体数据长度
			int contentLength = 0;
			// 读取http响应头部信息
			do {
				line = readLine(is, 0, charset);
				if (line.startsWith("Content-Length")) {
					// 拿到响应体内容长度
					contentLength = Integer.parseInt(line.split(":")[1].trim());
				}
				// 如果遇到了一个单独的回车换行，则表示请求头结束
			} while (!"\r\n".equals(line));
			// 读取出响应体数据（就是你要的数据）
			result = readLine(is, contentLength, charset);
		} catch (Exception e) {
			LogUtil.APP.error("使用socket发送post请求出现异常，请检查！", e);
			throw new RuntimeException(e);
		} finally {
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					LogUtil.APP.error("使用socket发送post请求后关闭osw流出现异常，请检查！", e);
					osw = null;
					throw new RuntimeException(e);
				} finally {
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							LogUtil.APP.error("使用socket发送post请求后关闭socket出现异常，请检查！", e);
							socket = null;
							throw new RuntimeException(e);
						}
					}
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					LogUtil.APP.error("使用socket发送post请求后关闭socket is对象出现异常，请检查！", e);
					is = null;
					throw new RuntimeException(e);
				} finally {
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							socket = null;
							LogUtil.APP.error("使用socket发送post请求后关闭socket出现异常，请检查！", e);
							throw new RuntimeException(e);
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * 使用socket发送get请求
	 * @param urlParam
	 * @param params
	 * @param charset
	 * @param headmsg
	 * @return
	 */
	public static String sendSocketGet(String urlParam, Map<String, Object> params, String charset,Map<String, String> headmsg) {
		String result = "";
		LogUtil.APP.info("设置Socket请求地址:【{}】",urlParam);
		// 构建请求参数
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			if(1==params.size()&&params.containsKey("_forTextJson")){
				LogUtil.APP.warn("协议模板是纯文本模式(仅限httpClientPostJson以及httpClientPutJson请求)，无法使用sendSocketGet请求(必须为key-value)...");
				return "协议模板是纯文本模式(仅限httpClientPostJson以及httpClientPutJson请求)，无法使用sendSocketGet请求(必须为key-value)...";
			}else{
				for (Entry<String, Object> entry : params.entrySet()) {
					sbParams.append(entry.getKey());
					sbParams.append("=");
					sbParams.append(entry.getValue());
					sbParams.append("&");
					LogUtil.APP.info("设置SocketPost参数信息...key:【{}】    value:【{}】",entry.getKey(),entry.getValue());
				}
			}

		}
		Socket socket = null;
		OutputStreamWriter osw = null;
		InputStream is = null;
		try {
			URL url = new URL(urlParam);
			String host = url.getHost();
			int port = url.getPort();
			if (-1 == port) {
				port = 80;
			}
			String path = url.getPath();
			socket = new Socket(host, port);
			StringBuffer sb = new StringBuffer();
			sb.append("GET " + path + " HTTP/1.1\r\n");
			sb.append("Host: " + host + "\r\n");
			sb.append("Connection: Keep-Alive\r\n");
			sb.append("Content-Type: application/x-www-form-urlencoded; charset=utf-8 \r\n");
			//替换头域信息
		    for (Map.Entry<String, String> m :headmsg.entrySet())  {
		    	String key=m.getKey();
		    	String value=m.getValue();
		    	LogUtil.APP.info("开始设置|替换Socket头域信息...key:【{}】    value:【{}】",key,value);
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
		    		LogUtil.APP.info("将头域【{}】的值【{}】FORMAT成BASE64格式...",key,value);
		    		sb.append(key+": "+value+" \r\n");
		    	}else{
		    		sb.append(key+": "+value+" \r\n");
		    	}
	        }
			sb.append("Content-Length: ").append(sb.toString().getBytes().length).append("\r\n");
			// 这里一个回车换行，表示消息头写完，不然服务器会继续等待
			sb.append("\r\n");
			if (sbParams != null && sbParams.length() > 0) {
				sb.append(sbParams.substring(0, sbParams.length() - 1));
			}
			osw = new OutputStreamWriter(socket.getOutputStream());
			osw.write(sb.toString());
			osw.flush();
			is = socket.getInputStream();
			String line = null;
			// 服务器响应体数据长度
			int contentLength = 0;
			// 读取http响应头部信息
			do {
				line = readLine(is, 0, charset);
				if (line.startsWith("Content-Length")) {
					// 拿到响应体内容长度
					contentLength = Integer.parseInt(line.split(":")[1].trim());
				}
				// 如果遇到了一个单独的回车换行，则表示请求头结束
			} while (!"\r\n".equals(line));
			// 读取出响应体数据（就是你要的数据）
			result = readLine(is, contentLength, charset);
		} catch (Exception e) {
			LogUtil.APP.error("使用socket发送get请求出现异常，请检查！", e);
			throw new RuntimeException(e);
		} finally {
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					LogUtil.APP.error("使用socket发送get请求后关闭osw流出现异常，请检查！", e);
					osw = null;
					throw new RuntimeException(e);
				} finally {
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							LogUtil.APP.error("使用socket发送get请求后关闭socket出现异常，请检查！", e);
							socket = null;
							throw new RuntimeException(e);
						}
					}
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					LogUtil.APP.error("使用socket发送get请求后关闭socket is对象出现异常，请检查！", e);
					is = null;
					throw new RuntimeException(e);
				} finally {
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							LogUtil.APP.error("使用socket发送get请求后关闭socket对象出现异常，请检查！", e);
							socket = null;
							throw new RuntimeException(e);
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * 读取一行数据，contentLe内容长度为0时，读取响应头信息，不为0时读正文
	 * @param is
	 * @param contentLength
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	private static String readLine(InputStream is, int contentLength, String charset) throws IOException {
		List<Byte> lineByte = new ArrayList<Byte>();
		byte tempByte;
		int cumsum = 0;
		if (contentLength != 0) {
			do {
				tempByte = (byte) is.read();
				lineByte.add(Byte.valueOf(tempByte));
				cumsum++;
				// cumsum等于contentLength表示已读完
			} while (cumsum < contentLength);
		} else {
			do {
				tempByte = (byte) is.read();
				lineByte.add(Byte.valueOf(tempByte));
				// 换行符的ascii码值为10
			} while (tempByte != 10);
		}

		byte[] resutlBytes = new byte[lineByte.size()];
		for (int i = 0; i < lineByte.size(); i++) {
			resutlBytes[i] = (lineByte.get(i)).byteValue();
		}
		return new String(resutlBytes, charset);
	}

	/**
	 * 使用HttpURLConnection发送delete请求
	 * @param urlParam
	 * @param params
	 * @param charset
	 * @param timeout
	 * @param headmsg
	 * @return
	 */
	public static String sendHttpURLDel(String urlParam, Map<String, Object> params, Map<String, String> headmsg,ProjectProtocolTemplate ppt) {
		String charset=ppt.getEncoding().toLowerCase();
		int timeout=ppt.getTimeout();
		int responsehead=ppt.getIsResponseHead();
		int responsecode=ppt.getIsResponseCode();
		
		StringBuffer resultBuffer = null;
		// 构建请求参数
		LogUtil.APP.info("设置HTTP请求地址:【{}】",urlParam);
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			if(1==params.size()&&params.containsKey("_forTextJson")){
				LogUtil.APP.warn("协议模板是纯文本模式(仅限httpClientPostJson以及httpClientPutJson请求)，无法使用sendHttpURLDel请求(必须为key-value)...");
				return "协议模板是纯文本模式(仅限httpClientPostJson以及httpClientPutJson请求)，无法使用sendHttpURLDel请求(必须为key-value)...";
			}else{
				for (Entry<String, Object> e : params.entrySet()) {
					sbParams.append(e.getKey());
					sbParams.append("=");
					sbParams.append(e.getValue());
					sbParams.append("&");
					LogUtil.APP.info("设置HttpURLDel参数信息...key:【{}】    value:【{}】",e.getKey(),e.getValue());
				}
			}
		}
		HttpURLConnection con = null;
		OutputStreamWriter osw = null;
		BufferedReader br = null;
		// 发送请求
		try {
			URL url = new URL(urlParam);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("DELETE");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type", "application/json");
			//替换头域信息
		    for (Map.Entry<String, String> m :headmsg.entrySet())  {
		    	String key=m.getKey();
		    	String value=m.getValue();
		    	LogUtil.APP.info("开始设置|替换HTTP头域信息...key:【{}】    value:【{}】",key,value);
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
		    		LogUtil.APP.info("将头域【{}】的值【{}】FORMAT成BASE64格式...",key,value);
		    		con.setRequestProperty(key,value);
		    	}else{
		    		con.setRequestProperty(key,value);
		    	}
	        }
			con.setConnectTimeout(timeout*1000);
			if (sbParams != null && sbParams.length() > 0) {
				osw = new OutputStreamWriter(con.getOutputStream(), charset);
				osw.write(sbParams.substring(0, sbParams.length() - 1));
				osw.flush();
			}
			// 读取返回内容
			resultBuffer = new StringBuffer();
			if(1==responsehead){
				Map<String, List<String>> headmsgstr=con.getHeaderFields();
				JSONObject itemJSONObj = JSONObject.parseObject(JSON.toJSONString(headmsgstr));
				resultBuffer.append(Constants.RESPONSE_HEAD+itemJSONObj+Constants.RESPONSE_END);
			}
			if(1==responsecode){
				resultBuffer.append(Constants.RESPONSE_CODE+con.getResponseCode()+Constants.RESPONSE_END);
			}
			if (null != con.getHeaderField("Content-Length") || null != con.getHeaderField("Transfer-Encoding")) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
				String temp;
				while ((temp = br.readLine()) != null) {
					resultBuffer.append(temp);
				}
			}
		} catch (Exception e) {
			LogUtil.APP.error("使用HttpURLConnection发送delete请求出现异常，请检查！", e);
			throw new RuntimeException(e);
		} finally {
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					LogUtil.APP.error("使用HttpURLConnection发送delete请求后关闭osw流出现异常，请检查！", e);
					osw = null;
					throw new RuntimeException(e);
				} finally {
					if (con != null) {
						con.disconnect();
						con = null;
					}
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LogUtil.APP.error("使用HttpURLConnection发送delete请求后关闭br流出现异常，请检查！", e);
					br = null;
					throw new RuntimeException(e);
				} finally {
					if (con != null) {
						con.disconnect();
						con = null;
					}
				}
			}
		}

		return resultBuffer.toString();
	}


	/**
	 * 使用HttpClient发送put请求  参数JSON格式
	 * @param urlParam
	 * @param params
	 * @param charset
	 * @param headmsg
	 * @param cerpath
	 * @return
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	public static String httpClientPutJson(String urlParam, Map<String, Object> params, Map<String, String> headmsg,ProjectProtocolTemplate ppt) throws KeyManagementException, NoSuchAlgorithmException {
		String cerpath=ppt.getCerificatePath();
		String charset=ppt.getEncoding().toLowerCase();
		int timeout=ppt.getTimeout()*1000;
		int responsehead=ppt.getIsResponseHead();
		int responsecode=ppt.getIsResponseCode();
		
		StringBuffer resultBuffer = null;
		LogUtil.APP.info("设置HTTP请求地址:【{}】",urlParam);
		CloseableHttpClient httpclient=iniHttpClient(urlParam,cerpath);
		HttpPut httpput = new HttpPut(urlParam);
	    httpput.setHeader("Content-Type", "application/json");
	    RequestConfig requestConfig = RequestConfig.custom()  
	            .setConnectTimeout(timeout)
	            .setConnectionRequestTimeout(timeout)  
	            //设置请求和传输超时时间
	            .setSocketTimeout(timeout).build();  
	    httpput.setConfig(requestConfig);
		//替换头域信息
	    for (Map.Entry<String, String> m :headmsg.entrySet())  {
	    	String key=m.getKey();
	    	String value=m.getValue();
	    	LogUtil.APP.info("开始设置|替换HTTP头域信息...key:【{}】    value:【{}】",key,value);
	    	if(null!=value&&value.indexOf("Base64(")==0){
	    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
	    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
	    		LogUtil.APP.info("将头域【{}】的值【{}】FORMAT成BASE64格式...",key,value);
	    		httpput.setHeader(key,value);
	    	}else{
	    		httpput.setHeader(key,value);
	    	}
        }
		// 构建请求参数
		BufferedReader br = null;
		try {
		if(params.size()>0){
			if(1==params.size()&&params.containsKey("_forTextJson")){
				LogUtil.APP.info("参数类型：TEXT,设置HTTPClientPutJson参数信息...【{}】",params.get("_forTextJson").toString());
				StringEntity entity = new StringEntity(params.get("_forTextJson").toString(),charset);
				httpput.setEntity(entity);
			}else{
			    String jsonString = JSON.toJSONString(params);
				LogUtil.APP.info("参数类型：FORM,设置HTTPClientPutJson参数信息...【{}】",jsonString);
				StringEntity entity = new StringEntity(jsonString,charset);
				httpput.setEntity(entity);
			}

		}
       
		 CloseableHttpResponse response = httpclient.execute(httpput);

			// 读取服务器响应数据
			resultBuffer = new StringBuffer();
			if(1==responsehead){
				Header[] headmsgstr=response.getAllHeaders();
				resultBuffer.append("RESPONSE_HEAD:【{");
				for(Header header:headmsgstr){
					resultBuffer.append("\""+header.getName()+"\":\""+header.getValue()+"\",");
				}
				resultBuffer.delete(resultBuffer.length()-1, resultBuffer.length()).append("}】 ");
			}
			if(1==responsecode){
				resultBuffer.append(Constants.RESPONSE_CODE+response.getStatusLine().getStatusCode()+Constants.RESPONSE_END);
			}
	        if(null!=response.getEntity()){
	        	br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
				String temp;
				while ((temp = br.readLine()) != null) {
					resultBuffer.append(temp);
				}
	        }
		} catch (Exception e) {
			LogUtil.APP.error("使用HttpClient发送put请求(参数JSON格式)出现异常，请检查！", e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LogUtil.APP.error("使用HttpClient发送put请求(参数JSON格式)后关闭br流出现异常，请检查！", e);
					br = null;
					throw new RuntimeException(e);
				}
			}
		}		
		return resultBuffer.toString();
	}

	/**
	 * 使用HttpClient发送put请求
	 * @param urlParam
	 * @param params
	 * @param charset
	 * @param headmsg
	 * @param cerpath
	 * @return
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	public static String httpClientPut(String urlParam, Map<String, Object> params, Map<String, String> headmsg,ProjectProtocolTemplate ppt) throws KeyManagementException, NoSuchAlgorithmException {
		String cerpath=ppt.getCerificatePath();
		String charset=ppt.getEncoding().toLowerCase();
		int timeout=ppt.getTimeout()*1000;
		int responsehead=ppt.getIsResponseHead();
		int responsecode=ppt.getIsResponseCode();
		
		StringBuffer resultBuffer = null;
		LogUtil.APP.info("设置HTTP请求地址:【{}】",urlParam);
		CloseableHttpClient httpclient=iniHttpClient(urlParam,cerpath);
		HttpPut httpput = new HttpPut(urlParam);
	    RequestConfig requestConfig = RequestConfig.custom()  
	            .setConnectTimeout(timeout)
	            .setConnectionRequestTimeout(timeout)  
	            //设置请求和传输超时时间
	            .setSocketTimeout(timeout).build();  
	    httpput.setConfig(requestConfig);
		//替换头域信息
	    for (Map.Entry<String, String> m :headmsg.entrySet())  {
	    	String key=m.getKey();
	    	String value=m.getValue();
	    	LogUtil.APP.info("开始设置|替换HTTP头域信息...key:【{}】    value:【{}】",key,value);
	    	if(null!=value&&value.indexOf("Base64(")==0){
	    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
	    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
	    		LogUtil.APP.info("将头域【{}】的值【{}】FORMAT成BASE64格式...",key,value);
	    		httpput.setHeader(key,value);
	    	}else{
	    		httpput.setHeader(key,value);
	    	}
        }
		// 构建请求参数
		BufferedReader br = null;
		try {
			if(params.size()>0){
				if(1==params.size()&&params.containsKey("_forTextJson")){
					LogUtil.APP.warn("协议模板是纯文本模式(仅限httpClientPostJson以及httpClientPutJson请求)，无法使用httpClientPut请求(必须为key-value)...");
					return "协议模板是纯文本模式(仅限httpClientPostJson以及httpClientPutJson请求)，无法使用httpClientPut请求(必须为key-value)...";
				}else{
					//拼接参数
				    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
				    for (Map.Entry<String, Object> m :params.entrySet())  { 
			            nvps.add(new BasicNameValuePair(m.getKey(), m.getValue().toString()));
			            LogUtil.APP.info("开始设置HTTPClientPut参数信息...key:【{}】    value:【{}】",m.getKey(),m.getValue());
			        }
				    httpput.setEntity(new UrlEncodedFormEntity(nvps,charset));
				}

			}
       
		 CloseableHttpResponse response = httpclient.execute(httpput);

			// 读取服务器响应数据
			resultBuffer = new StringBuffer();
			if(1==responsehead){
				Header[] headmsgstr=response.getAllHeaders();
				resultBuffer.append("RESPONSE_HEAD:【{");
				for(Header header:headmsgstr){
					resultBuffer.append("\""+header.getName()+"\":\""+header.getValue()+"\",");
				}
				resultBuffer.delete(resultBuffer.length()-1, resultBuffer.length()).append("}】 ");
			}
			if(1==responsecode){
				resultBuffer.append(Constants.RESPONSE_CODE+response.getStatusLine().getStatusCode()+Constants.RESPONSE_END);
			}
	        if(null!=response.getEntity()){
	        	br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
				String temp;
				while ((temp = br.readLine()) != null) {
					resultBuffer.append(temp);
				}
	        }		
		} catch (Exception e) {
			LogUtil.APP.error("使用HttpClient发送put请求出现异常，请检查！", e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LogUtil.APP.error("使用HttpClient发送put请求后关闭br流出现异常，请检查！", e);
					br = null;
					throw new RuntimeException(e);
				}
			}
		}		
		return resultBuffer.toString();
	}

    /**
     * 设置信任自签名证书
     * @param keyStorePath
     * @param keyStorepass
     * @return
     */
    private static SSLContext sslContextKeyStore(String keyStorePath, String keyStorepass) {
        SSLContext sslContext = null;
        FileInputStream instream = null;
        KeyStore trustStore = null;
        LogUtil.APP.info("证书路径:{}  密钥:{}",keyStorePath,keyStorepass);
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            LogUtil.APP.info("开始读取证书文件流...");
            instream = new FileInputStream(new File(keyStorePath));
            LogUtil.APP.info("开始设置证书以及密钥...");
            trustStore.load(instream, keyStorepass.toCharArray());
            // 相信自己的CA和所有自签名的证书
            sslContext = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
            // 构造 javax.net.ssl.TrustManager 对象
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
            tmf.init(trustStore);
            TrustManager[] tms = tmf.getTrustManagers();
            // 使用构造好的 TrustManager 访问相应的 https 站点
            sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tms, new java.security.SecureRandom());
        } catch (Exception e) {
        	LogUtil.APP.error("设置信任自签名证书出现异常，请检查！", e);
        } finally {
            try {
                instream.close();
            } catch (IOException e) {
            	LogUtil.APP.error("设置信任自签名证书后关闭instream流出现异常，请检查！", e);
            }
        }
        return sslContext;
    }

    /**
     * httpclient方式 HTTP/HTTPS初始化
     * @param urlParam
     * @param cerpath
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private static CloseableHttpClient iniHttpClient(String urlParam,String cerpath) throws NoSuchAlgorithmException, KeyManagementException{
    	CloseableHttpClient httpclient=null;
    	urlParam=urlParam.trim();
    	if(urlParam.startsWith("http://")){
    		httpclient = HttpClients.createDefault();
    	}else if(urlParam.startsWith("https://")){
    		//采用绕过验证的方式处理https请求
    		SSLContext sslContext=null;
    		if(null==cerpath||"".equals(cerpath.trim())){
    			LogUtil.APP.info("开始构建HTTPS单向认证请求...");
    	        TrustManager[] trustManagers = {new MyX509TrustManager()};  
    	        sslContext = SSLContext.getInstance("TLS");
    	        sslContext.init(null, trustManagers, new SecureRandom());
    		}else{
    			LogUtil.APP.info("开始构建HTTPS双向认证请求...");
    			String[] strcerpath=cerpath.split(";",-1);
    			sslContext = sslContextKeyStore(strcerpath[0], strcerpath[1]);
    		}
            
            // 设置协议http和https对应的处理socket链接工厂的对象
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                // 正常的SSL连接会验证所有证书信息
                // .register("https", new SSLConnectionSocketFactory(sslContext)).build();
                // 忽略域名验证
                .register("https", new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
                .build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            connManager.setDefaultMaxPerRoute(1);
            //创建自定义的httpclient对象
            httpclient = HttpClients.custom().setConnectionManager(connManager).build();
    	}else{
    		httpclient = HttpClients.createDefault();
    	}
    	return httpclient;
    }
	
    /**
	 * 使用HttpClient以XML发送post请求
	 * @param urlParam
	 * @param params
	 * @param charset
	 * @param headmsg
	 * @param cerpath
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public static String httpClientPostXml(String urlParam, Map<String, Object> params, Map<String, String> headmsg,ProjectProtocolTemplate ppt) throws NoSuchAlgorithmException, KeyManagementException {
		String cerpath=ppt.getCerificatePath();
		String charset=ppt.getEncoding().toLowerCase();
		int timeout=ppt.getTimeout()*1000;
		int responsehead=ppt.getIsResponseHead();
		int responsecode=ppt.getIsResponseCode();
		
		StringBuffer resultBuffer = null;
		LogUtil.APP.info("设置HTTP请求地址:【{}】",urlParam);
		CloseableHttpClient httpclient=iniHttpClient(urlParam,cerpath);
		HttpPost httpPost = new HttpPost(urlParam);
	    httpPost.setHeader("Content-Type", "text/xml");
	    RequestConfig requestConfig = RequestConfig.custom()  
	            .setConnectTimeout(timeout)
	            .setConnectionRequestTimeout(timeout)  
	             //设置请求和传输超时时间
	            .setSocketTimeout(timeout).build();  
	    httpPost.setConfig(requestConfig);
		//替换头域信息
	    for (Map.Entry<String, String> m :headmsg.entrySet())  {
	    	String key=m.getKey();
	    	String value=m.getValue();
	    	LogUtil.APP.info("开始设置|替换httpClientPostXml头域信息...key:【{}】    value:【{}】",key,value);
	    	if(null!=value&&value.indexOf("Base64(")==0){
	    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
	    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
	    		LogUtil.APP.info("将头域【{}】的值【{}】FORMAT成BASE64格式...",key,value);
	    		httpPost.setHeader(key, value);
	    	}else{
	    		httpPost.setHeader(key, value);
	    	}
        }

		try {
		if(params.size()>0){
			if(1==params.size()&&params.containsKey("_forTextXml")){
				LogUtil.APP.info("参数类型：XML,设置httpClientPostXml参数信息...【{}】",params.get("_forTextXml").toString());
				String xmlStr=getXmlString(params.get("_forTextXml").toString());
				StringEntity entity = new StringEntity(xmlStr,charset);
				entity.setContentType("text/xml");
				httpPost.setEntity(entity);
			}else{
			    String jsonString = JSON.toJSONString(params);
				LogUtil.APP.info("参数类型：FORM,设置httpClientPostXml参数信息...【{}】",jsonString);
				StringEntity entity = new StringEntity(jsonString,charset);
				httpPost.setEntity(entity);
			}
		}
       
		 CloseableHttpResponse response = httpclient.execute(httpPost);

		// 读取服务器响应数据
		resultBuffer = new StringBuffer();
		if(1==responsehead){
			Header[] headmsgstr=response.getAllHeaders();
			resultBuffer.append("RESPONSE_HEAD:【{");
			for(Header header:headmsgstr){
				resultBuffer.append("\""+header.getName()+"\":\""+header.getValue()+"\",");
			}
			resultBuffer.delete(resultBuffer.length()-1, resultBuffer.length()).append("}】 ");
		}
		if(1==responsecode){
			resultBuffer.append(Constants.RESPONSE_CODE+response.getStatusLine().getStatusCode()+Constants.RESPONSE_END);
		}
		if(null!=response.getEntity()){
            HttpEntity entity =  response.getEntity();
            resultBuffer.append(EntityUtils.toString(entity));
		}
		} catch (Exception e) {
			LogUtil.APP.error("使用HttpClient以XML发送post请求出现异常，请检查！", e);
			throw new RuntimeException(e);
		}	
		return resultBuffer.toString();
	}
	
	/**
	 * 读取xml内容,将请求的xml保存成字符串 进行post发送
	 * @param path
	 * @return
	 * @author Seagull
	 * @date 2019年9月17日
	 */
	private static String getXmlString(String path) {
		StringBuilder sb = new StringBuilder();
		// 构建请求参数
	    BufferedReader br = null;
		try {
			InputStream inputStream = new FileInputStream(path);
			br = new BufferedReader(new InputStreamReader(inputStream));
			String line = "";
			for (line = br.readLine(); line != null; line = br.readLine()) {
				sb.append(line + "\n");
			}
		} catch (FileNotFoundException e) {
			LogUtil.APP.error("此路径【"+path+"】下没有找到对应的XML文件", e);
		} catch (IOException e) {
			LogUtil.APP.error("读取文件失败，出现异常！", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LogUtil.APP.error("读取xml文件后关闭br流出现异常，请检查！", e);
					br = null;
					throw new RuntimeException(e);
				}
			}
		}	
		return sb.toString();
	}

}
