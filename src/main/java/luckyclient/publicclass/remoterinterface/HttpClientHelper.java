package luckyclient.publicclass.remoterinterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;

import net.sf.json.JSONObject;

/**
 * =================================================================
 * ����һ�������Ƶ�������������������κ�δ�������ǰ���¶Գ����������޸ĺ�������ҵ��;��Ҳ������Գ�������޸ĺ����κ���ʽ�κ�Ŀ�ĵ��ٷ�����
 * Ϊ���������ߵ��Ͷ��ɹ���LuckyFrame�ؼ���Ȩ��Ϣ�Ͻ��۸�
 * ���κ����ʻ�ӭ��ϵ�������ۡ� QQ:1573584944  seagull1985
 * =================================================================
 * 
 * @author�� seagull
 * @date 2017��12��1�� ����9:29:40
 * 
 */
public class HttpClientHelper {
	/**
	 * @Description:ʹ��HttpURLConnection����post����
	 */
	public static String sendHttpURLPost(String urlParam, Map<String, Object> params, String charset, int timeout,Map<String, String> headmsg) {
		StringBuffer resultBuffer = null;
		// �����������
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (Entry<String, Object> e : params.entrySet()) {
				sbParams.append(e.getKey());
				sbParams.append("=");
				sbParams.append(e.getValue());
				sbParams.append("&");
				luckyclient.publicclass.LogUtil.APP.info("����HTTPURLPost������Ϣ...key:��"+e.getKey()+"��    value:��"+e.getValue()+"��");
			}
		}
		HttpURLConnection con = null;
		OutputStreamWriter osw = null;
		BufferedReader br = null;
		// ��������
		try {
			URL url = new URL(urlParam);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setConnectTimeout(timeout*1000);
			//�滻ͷ����Ϣ
		    for (Map.Entry<String, String> m :headmsg.entrySet())  {
		    	String key=m.getKey();
		    	String value=m.getValue();
		    	luckyclient.publicclass.LogUtil.APP.info("��ʼ����|�滻HTTPURLPostͷ����Ϣ...key:��"+key+"��    value:��"+value+"��");
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
		    		luckyclient.publicclass.LogUtil.APP.info("��ͷ��"+key+"����ֵ��"+value+"��FORMAT��BASE64��ʽ...");
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
			// ��ȡ��������
			resultBuffer = new StringBuffer();
			int contentLength =0;
			if(null!=con.getHeaderField("Content-Length")){
				contentLength = Integer.parseInt(con.getHeaderField("Content-Length"));
			}
			
			if (contentLength > 0) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
				String temp;
				while ((temp = br.readLine()) != null) {
					resultBuffer.append(temp);
				}
			}else{
				resultBuffer.append("Content-Length=0 ��Ӧ�룺"+con.getResponseCode());
			}
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
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
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
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
	 * @Description:ʹ��URLConnection����post
	 */
	public static String sendURLPost(String urlParam, Map<String, Object> params, String charset, int timeout,Map<String, String> headmsg) {
		StringBuffer resultBuffer = null;
		// �����������
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (Entry<String, Object> e : params.entrySet()) {
				sbParams.append(e.getKey());
				sbParams.append("=");
				sbParams.append(e.getValue());
				sbParams.append("&");
				luckyclient.publicclass.LogUtil.APP.info("����URLPost������Ϣ...key:��"+e.getKey()+"��    value:��"+e.getValue()+"��");
			}
		}
		URLConnection con = null;
		OutputStreamWriter osw = null;
		BufferedReader br = null;
		try {
			URL realUrl = new URL(urlParam);
			// �򿪺�URL֮�������
			con = realUrl.openConnection();
			// ����ͨ�õ���������
			con.setRequestProperty("accept", "*/*");
			con.setRequestProperty("connection", "Keep-Alive");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			//�滻ͷ����Ϣ
		    for (Map.Entry<String, String> m :headmsg.entrySet())  {
		    	String key=m.getKey();
		    	String value=m.getValue();
		    	luckyclient.publicclass.LogUtil.APP.info("��ʼ����|�滻URLPostͷ����Ϣ...key:��"+key+"��    value:��"+value+"��");
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
		    		luckyclient.publicclass.LogUtil.APP.info("��ͷ��"+key+"����ֵ��"+value+"��FORMAT��BASE64��ʽ...");
		    		con.setRequestProperty(key, value);
		    	}else{
		    		con.setRequestProperty(key, value);
		    	}
	        }
		    
			con.setConnectTimeout(timeout*1000);
			// ����POST�������������������
			con.setDoOutput(true);
			con.setDoInput(true);
			// ��ȡURLConnection�����Ӧ�������
			osw = new OutputStreamWriter(con.getOutputStream(), charset);
			if (sbParams != null && sbParams.length() > 0) {
				// �����������
				osw.write(sbParams.substring(0, sbParams.length() - 1));
				// flush������Ļ���
				osw.flush();
			}
			// ����BufferedReader����������ȡURL����Ӧ
			resultBuffer = new StringBuffer();
			int contentLength =0;
			if(null!=con.getHeaderField("Content-Length")){
				contentLength = Integer.parseInt(con.getHeaderField("Content-Length"));
			}
			if (contentLength > 0) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
				String temp;
				while ((temp = br.readLine()) != null) {
					resultBuffer.append(temp);
				}
			}else{
					resultBuffer.append("Content-Length=0");
			}
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
					osw = null;
					throw new RuntimeException(e);
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
					br = null;
					throw new RuntimeException(e);
				}
			}
		}
		return resultBuffer.toString();
	}

	/**
	 * @Description:����get���󱣴������ļ�
	 */
	public static void sendGetAndSaveFile(String urlParam, Map<String, Object> params, String fileSavePath, int timeout,Map<String, String> headmsg) {
		// �����������
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (Entry<String, Object> entry : params.entrySet()) {
				sbParams.append(entry.getKey());
				sbParams.append("=");
				sbParams.append(entry.getValue());
				sbParams.append("&");
				luckyclient.publicclass.LogUtil.APP.info("����HTTPSaveFile������Ϣ...key:��"+entry.getKey()+"��    value:��"+entry.getValue()+"��");
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
			//�滻ͷ����Ϣ
		    for (Map.Entry<String, String> m :headmsg.entrySet())  {
		    	String key=m.getKey();
		    	String value=m.getValue();
		    	luckyclient.publicclass.LogUtil.APP.info("��ʼ����|�滻HTTPSaveFileͷ����Ϣ...key:��"+key+"��    value:��"+value+"��");
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
		    		luckyclient.publicclass.LogUtil.APP.info("��ͷ��"+key+"����ֵ��"+value+"��FORMAT��BASE64��ʽ...");
		    		con.setRequestProperty(key, value);
		    	}else{
		    		con.setRequestProperty(key, value);
		    	}
	        }
			con.setConnectTimeout(timeout*1000);
			con.connect();
			InputStream is = con.getInputStream();
			os = new FileOutputStream(fileSavePath);
			byte buf[] = new byte[1024];
			int count = 0;
			while ((count = is.read(buf)) != -1) {
				os.write(buf, 0, count);
			}
			os.flush();
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
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
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
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
	 * @Description:ʹ��HttpURLConnection����get����
	 */
	public static String sendHttpURLGet(String urlParam, Map<String, Object> params, String charset, int timeout,Map<String, String> headmsg) {
		StringBuffer resultBuffer = null;
		// �����������
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (Entry<String, Object> entry : params.entrySet()) {
				sbParams.append(entry.getKey());
				sbParams.append("=");
				sbParams.append(entry.getValue());
				sbParams.append("&");
				luckyclient.publicclass.LogUtil.APP.info("����HTTPURLGet������Ϣ...key:��"+entry.getKey()+"��    value:��"+entry.getValue()+"��");
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
			//�滻ͷ����Ϣ
		    for (Map.Entry<String, String> m :headmsg.entrySet())  {
		    	String key=m.getKey();
		    	String value=m.getValue();
		    	luckyclient.publicclass.LogUtil.APP.info("��ʼ����|�滻HTTPURLGetͷ����Ϣ...key:��"+key+"��    value:��"+value+"��");
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
		    		luckyclient.publicclass.LogUtil.APP.info("��ͷ��"+key+"����ֵ��"+value+"��FORMAT��BASE64��ʽ...");
		    		con.setRequestProperty(key, value);
		    	}else{
		    		con.setRequestProperty(key, value);
		    	}
	        }
			con.setConnectTimeout(timeout*1000);
			con.connect();
			resultBuffer = new StringBuffer();
			br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
			String temp;
			while ((temp = br.readLine()) != null) {
				resultBuffer.append(temp);
			}
			if(resultBuffer.length()==0){
				resultBuffer.append("��ȡ��������Ӧ�����쳣!��Ӧ�룺"+con.getResponseCode());
			}
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
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
	 * @Description:ʹ��URLConnection����get����
	 */
	public static String sendURLGet(String urlParam, Map<String, Object> params, String charset, int timeout,Map<String, String> headmsg) {
		StringBuffer resultBuffer = null;
		// �����������
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (Entry<String, Object> entry : params.entrySet()) {
				sbParams.append(entry.getKey());
				sbParams.append("=");
				sbParams.append(entry.getValue());
				sbParams.append("&");
				luckyclient.publicclass.LogUtil.APP.info("����URLGet������Ϣ...key:��"+entry.getKey()+"��    value:��"+entry.getValue()+"��");
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
			// ������������
			con.setRequestProperty("accept", "*/*");
			con.setRequestProperty("connection", "Keep-Alive");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			//�滻ͷ����Ϣ
		    for (Map.Entry<String, String> m :headmsg.entrySet())  {
		    	String key=m.getKey();
		    	String value=m.getValue();
		    	luckyclient.publicclass.LogUtil.APP.info("��ʼ����|�滻URLGetͷ����Ϣ...key:��"+key+"��    value:��"+value+"��");
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
		    		luckyclient.publicclass.LogUtil.APP.info("��ͷ��"+key+"����ֵ��"+value+"��FORMAT��BASE64��ʽ...");
		    		con.setRequestProperty(key, value);
		    	}else{
		    		con.setRequestProperty(key, value);
		    	}
	        }
			con.setConnectTimeout(timeout*1000);
			// ��������
			con.connect();
			resultBuffer = new StringBuffer();
			br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
			String temp;
			while ((temp = br.readLine()) != null) {
				resultBuffer.append(temp);
			}
			if(resultBuffer.length()==0){
				resultBuffer.append("��ȡ��������Ӧ�����쳣!");
			}
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
					br = null;
					throw new RuntimeException(e);
				}
			}
		}
		return resultBuffer.toString();
	}

	/**
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 * @Description:ʹ��HttpClient��JSON��ʽ����post����
	 */
	public static String httpClientPostJson(String urlParam, Map<String, Object> params, String charset,Map<String, String> headmsg,String cerpath) throws NoSuchAlgorithmException, KeyManagementException {
		StringBuffer resultBuffer = null;
		CloseableHttpClient httpclient=iniHttpClient(urlParam,cerpath);
		HttpPost httpPost = new HttpPost(urlParam);
	    httpPost.setHeader("Content-Type", "application/json");
		//�滻ͷ����Ϣ
	    for (Map.Entry<String, String> m :headmsg.entrySet())  {
	    	String key=m.getKey();
	    	String value=m.getValue();
	    	luckyclient.publicclass.LogUtil.APP.info("��ʼ����|�滻HTTPPostJsonͷ����Ϣ...key:��"+key+"��    value:��"+value+"��");
	    	if(null!=value&&value.indexOf("Base64(")==0){
	    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
	    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
	    		luckyclient.publicclass.LogUtil.APP.info("��ͷ��"+key+"����ֵ��"+value+"��FORMAT��BASE64��ʽ...");
	    		httpPost.setHeader(key, value);
	    	}else{
	    		httpPost.setHeader(key, value);
	    	}
        }
		// �����������
		BufferedReader br = null;
		try {
		if(params.size()>0){
				JSONObject jsonObject = JSONObject.fromObject(params); 
				luckyclient.publicclass.LogUtil.APP.info("����HTTPPostJson������Ϣ...��"+jsonObject.toString()+"��");
				StringEntity entity = new StringEntity(jsonObject.toString(),charset);
				httpPost.setEntity(entity);
			}
       
		 CloseableHttpResponse response = httpclient.execute(httpPost);
		 //��ȡ��������е���Ӧ�ж���  
		 org.apache.http.StatusLine statusLine = response.getStatusLine();
		 //��״̬���л�ȡ״̬��  
	     String responsecode = String.valueOf(statusLine.getStatusCode());
		// ��ȡ��������Ӧ����
		resultBuffer = new StringBuffer();
		resultBuffer.append("��Ӧ�룺"+responsecode+" ");
		if(null!=response.getEntity()){
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
			String temp;
			while ((temp = br.readLine()) != null) {
				resultBuffer.append(temp);
			}	
		}
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
					br = null;
					throw new RuntimeException(e);
				}
			}
		}		
		return resultBuffer.toString();
	}
	
	/**
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 * @Description:ʹ��HttpClient����post����
	 */
	public static String httpClientPost(String urlParam, Map<String, Object> params, String charset,Map<String, String> headmsg,String cerpath) throws NoSuchAlgorithmException, KeyManagementException {
		StringBuffer resultBuffer = null;
		CloseableHttpClient httpclient=iniHttpClient(urlParam,cerpath);
		HttpPost httpPost = new HttpPost(urlParam);
		//�滻ͷ����Ϣ
	    for (Map.Entry<String, String> m :headmsg.entrySet())  {
	    	String key=m.getKey();
	    	String value=m.getValue();
	    	luckyclient.publicclass.LogUtil.APP.info("��ʼ����|�滻HTTPClientPostͷ����Ϣ...key:��"+key+"��    value:��"+value+"��");
	    	if(null!=value&&value.indexOf("Base64(")==0){
	    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
	    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
	    		luckyclient.publicclass.LogUtil.APP.info("��ͷ��"+key+"����ֵ��"+value+"��FORMAT��BASE64��ʽ...");
	    		httpPost.setHeader(key, value);
	    	}else{
	    		httpPost.setHeader(key, value);
	    	}
        }
		// �����������
		BufferedReader br = null;
		try {
			if(params.size()>0){
				//ƴ�Ӳ���
			    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			    for (Map.Entry<String, Object> m :params.entrySet())  { 
		            nvps.add(new BasicNameValuePair(m.getKey(), m.getValue().toString()));
		            luckyclient.publicclass.LogUtil.APP.info("����HTTPClientPost������Ϣ...key:��"+m.getKey()+"��    value:��"+m.getValue()+"��");
		        }
			    httpPost.setEntity(new UrlEncodedFormEntity(nvps,charset));
			}

			 CloseableHttpResponse response = httpclient.execute(httpPost);
			 //��ȡ��������е���Ӧ�ж���  
			 org.apache.http.StatusLine statusLine = response.getStatusLine();
			 //��״̬���л�ȡ״̬��  
		     String responsecode = String.valueOf(statusLine.getStatusCode());
			// ��ȡ��������Ӧ����
			resultBuffer = new StringBuffer();

			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
			String temp;
			while ((temp = br.readLine()) != null) {
				resultBuffer.append(temp);
			}
			if(resultBuffer.length()==0){
				resultBuffer.append("��ȡ��������Ӧ�����쳣����Ӧ�룺"+responsecode);
			}
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
					br = null;
					throw new RuntimeException(e);
				}
			}
		}
		
		return resultBuffer.toString();
	}

	/**
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 * @Description:ʹ��HttpClient�ϴ��ļ�
	 */
	public static String httpClientUploadFile(String urlParam, Map<String, Object> params, String charset,Map<String, String> headmsg,String cerpath) throws NoSuchAlgorithmException, KeyManagementException {
		StringBuffer resultBuffer = null;
		CloseableHttpClient httpclient=iniHttpClient(urlParam,cerpath);
		HttpPost httpPost = new HttpPost(urlParam);
		//�滻ͷ����Ϣ
	    for (Map.Entry<String, String> m :headmsg.entrySet())  {
	    	String key=m.getKey();
	    	String value=m.getValue();
	    	luckyclient.publicclass.LogUtil.APP.info("��ʼ����|�滻httpClientUploadFileͷ����Ϣ...key:��"+key+"��    value:��"+value+"��");
	    	if(null!=value&&value.indexOf("Base64(")==0){
	    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
	    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
	    		luckyclient.publicclass.LogUtil.APP.info("��ͷ��"+key+"����ֵ��"+value+"��FORMAT��BASE64��ʽ...");
	    		httpPost.setHeader(key, value);
	    	}else{
	    		httpPost.setHeader(key, value);
	    	}
        }
		// �����������
		BufferedReader br = null;
		try {
			if(params.size()>0){
				//ƴ�Ӳ���
				MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
				//��������ı����ʽ  
				entityBuilder.setCharset(Charset.forName(charset));
				
			    for (Map.Entry<String, Object> m :params.entrySet())  {
			    	if (m.getValue() instanceof File) {
			    		entityBuilder.addBinaryBody(m.getKey(), (File)m.getValue());
			    		luckyclient.publicclass.LogUtil.APP.info("����httpClientUploadFile �ϴ��ļ�������Ϣ...key:��"+m.getKey()+"��    value:��"+m.getValue()+"��");
			    	}else{
			    		entityBuilder.addTextBody(m.getKey(), m.getValue().toString());
			    		luckyclient.publicclass.LogUtil.APP.info("����httpClientUploadFile������Ϣ...key:��"+m.getKey()+"��    value:��"+m.getValue()+"��");
			    	}
		        }
			    HttpEntity reqEntity =entityBuilder.build();
			    httpPost.setEntity(reqEntity);
			}

			 CloseableHttpResponse response = httpclient.execute(httpPost);
			 //��ȡ��������е���Ӧ�ж���  
			 org.apache.http.StatusLine statusLine = response.getStatusLine();
			 //��״̬���л�ȡ״̬��  
		     String responsecode = String.valueOf(statusLine.getStatusCode());
			// ��ȡ��������Ӧ����
			resultBuffer = new StringBuffer();

			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
			String temp;
			while ((temp = br.readLine()) != null) {
				resultBuffer.append(temp);
			}
			if(resultBuffer.length()==0){
				resultBuffer.append("��ȡ��������Ӧ�����쳣����Ӧ�룺"+responsecode);
			}
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
					br = null;
					throw new RuntimeException(e);
				}
			}
		}
		return resultBuffer.toString();
	}
	
	/**
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 * @Description:ʹ��HttpClient����get����
	 */
	public static String httpClientGet(String urlParam, Map<String, Object> params, String charset,Map<String, String> headmsg,String cerpath) throws NoSuchAlgorithmException, KeyManagementException {
		StringBuffer resultBuffer = null;
		CloseableHttpClient httpclient=iniHttpClient(urlParam,cerpath);
		BufferedReader br = null;
		// �����������
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (Entry<String, Object> entry : params.entrySet()) {
				sbParams.append(entry.getKey());
				sbParams.append("=");
				try {
					sbParams.append(URLEncoder.encode(String.valueOf(entry.getValue()), charset));
				} catch (UnsupportedEncodingException e) {
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
					throw new RuntimeException(e);
				}
				sbParams.append("&");
				luckyclient.publicclass.LogUtil.APP.info("����HTTPClientGet������Ϣ...key:��"+entry.getKey()+"��    value:��"+entry.getValue()+"��");
			}
		}
		if (sbParams != null && sbParams.length() > 0) {
			urlParam = urlParam + "?" + sbParams.substring(0, sbParams.length() - 1);
		}
		HttpGet httpGet = new HttpGet(urlParam);
		//�滻ͷ����Ϣ
	    for (Map.Entry<String, String> m :headmsg.entrySet())  {
	    	String key=m.getKey();
	    	String value=m.getValue();
	    	luckyclient.publicclass.LogUtil.APP.info("��ʼ����|�滻HTTPClientGetͷ����Ϣ...key:��"+key+"��    value:��"+value+"��");
	    	if(null!=value&&value.indexOf("Base64(")==0){
	    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
	    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
	    		luckyclient.publicclass.LogUtil.APP.info("��ͷ��"+key+"����ֵ��"+value+"��FORMAT��BASE64��ʽ...");
	    		httpGet.setHeader(key, value);
	    	}else{
	    		httpGet.setHeader(key, value);
	    	}
        }
		try {
			 HttpResponse response = httpclient.execute(httpGet);
			// ��ȡ��������Ӧ����
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
			String temp;
			resultBuffer = new StringBuffer();
			while ((temp = br.readLine()) != null) {
				resultBuffer.append(temp);
			}
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
					br = null;
					throw new RuntimeException(e);
				}
			}
		}
		return resultBuffer.toString();
	}

	/**
	 * @Description:ʹ��socket����post����
	 */
	public static String sendSocketPost(String urlParam, Map<String, Object> params, String charset,Map<String, String> headmsg) {
		String result = "";
		// �����������
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (Entry<String, Object> entry : params.entrySet()) {
				sbParams.append(entry.getKey());
				sbParams.append("=");
				sbParams.append(entry.getValue());
				sbParams.append("&");
				luckyclient.publicclass.LogUtil.APP.info("����SocketPost������Ϣ...key:��"+entry.getKey()+"��    value:��"+entry.getValue()+"��");
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
			//�滻ͷ����Ϣ
		    for (Map.Entry<String, String> m :headmsg.entrySet())  {
		    	String key=m.getKey();
		    	String value=m.getValue();
		    	luckyclient.publicclass.LogUtil.APP.info("��ʼ����|�滻Socketͷ����Ϣ...key:��"+key+"��    value:��"+value+"��");
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
		    		luckyclient.publicclass.LogUtil.APP.info("��ͷ��"+key+"����ֵ��"+value+"��FORMAT��BASE64��ʽ...");
		    		sb.append(key+": "+value+" \r\n");
		    	}else{
		    		sb.append(key+": "+value+" \r\n");
		    	}
	        }
			sb.append("Content-Length: ").append(sb.toString().getBytes().length).append("\r\n");
			// ����һ���س����У���ʾ��Ϣͷд�꣬��Ȼ������������ȴ�
			sb.append("\r\n");
			if (sbParams != null && sbParams.length() > 0) {
				sb.append(sbParams.substring(0, sbParams.length() - 1));
			}
			osw = new OutputStreamWriter(socket.getOutputStream());
			osw.write(sb.toString());
			osw.flush();
			is = socket.getInputStream();
			String line = null;
			// ��������Ӧ�����ݳ���
			int contentLength = 0;
			// ��ȡhttp��Ӧͷ����Ϣ
			do {
				line = readLine(is, 0, charset);
				if (line.startsWith("Content-Length")) {
					// �õ���Ӧ�����ݳ���
					contentLength = Integer.parseInt(line.split(":")[1].trim());
				}
				// ���������һ�������Ļس����У����ʾ����ͷ����
			} while (!"\r\n".equals(line));
			// ��ȡ����Ӧ�����ݣ�������Ҫ�����ݣ�
			result = readLine(is, contentLength, charset);
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
					osw = null;
					throw new RuntimeException(e);
				} finally {
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
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
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
					is = null;
					throw new RuntimeException(e);
				} finally {
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							socket = null;
							luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
							throw new RuntimeException(e);
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * @Description:ʹ��socket����get����
	 */
	public static String sendSocketGet(String urlParam, Map<String, Object> params, String charset,Map<String, String> headmsg) {
		String result = "";
		// �����������
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (Entry<String, Object> entry : params.entrySet()) {
				sbParams.append(entry.getKey());
				sbParams.append("=");
				sbParams.append(entry.getValue());
				sbParams.append("&");
				luckyclient.publicclass.LogUtil.APP.info("����SocketPost������Ϣ...key:��"+entry.getKey()+"��    value:��"+entry.getValue()+"��");
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
			//�滻ͷ����Ϣ
		    for (Map.Entry<String, String> m :headmsg.entrySet())  {
		    	String key=m.getKey();
		    	String value=m.getValue();
		    	luckyclient.publicclass.LogUtil.APP.info("��ʼ����|�滻Socketͷ����Ϣ...key:��"+key+"��    value:��"+value+"��");
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
		    		luckyclient.publicclass.LogUtil.APP.info("��ͷ��"+key+"����ֵ��"+value+"��FORMAT��BASE64��ʽ...");
		    		sb.append(key+": "+value+" \r\n");
		    	}else{
		    		sb.append(key+": "+value+" \r\n");
		    	}
	        }
			sb.append("Content-Length: ").append(sb.toString().getBytes().length).append("\r\n");
			// ����һ���س����У���ʾ��Ϣͷд�꣬��Ȼ������������ȴ�
			sb.append("\r\n");
			if (sbParams != null && sbParams.length() > 0) {
				sb.append(sbParams.substring(0, sbParams.length() - 1));
			}
			osw = new OutputStreamWriter(socket.getOutputStream());
			osw.write(sb.toString());
			osw.flush();
			is = socket.getInputStream();
			String line = null;
			// ��������Ӧ�����ݳ���
			int contentLength = 0;
			// ��ȡhttp��Ӧͷ����Ϣ
			do {
				line = readLine(is, 0, charset);
				if (line.startsWith("Content-Length")) {
					// �õ���Ӧ�����ݳ���
					contentLength = Integer.parseInt(line.split(":")[1].trim());
				}
				// ���������һ�������Ļس����У����ʾ����ͷ����
			} while (!"\r\n".equals(line));
			// ��ȡ����Ӧ�����ݣ�������Ҫ�����ݣ�
			result = readLine(is, contentLength, charset);
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
					osw = null;
					throw new RuntimeException(e);
				} finally {
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
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
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
					is = null;
					throw new RuntimeException(e);
				} finally {
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
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
	 * @Description:��ȡһ�����ݣ�contentLe���ݳ���Ϊ0ʱ����ȡ��Ӧͷ��Ϣ����Ϊ0ʱ������
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
				// cumsum����contentLength��ʾ�Ѷ���
			} while (cumsum < contentLength);
		} else {
			do {
				tempByte = (byte) is.read();
				lineByte.add(Byte.valueOf(tempByte));
				// ���з���ascii��ֵΪ10
			} while (tempByte != 10);
		}

		byte[] resutlBytes = new byte[lineByte.size()];
		for (int i = 0; i < lineByte.size(); i++) {
			resutlBytes[i] = (lineByte.get(i)).byteValue();
		}
		return new String(resutlBytes, charset);
	}

/**
	 * @Description:ʹ��HttpURLConnection����delete����
	 */
	public static String sendHttpURLDel(String urlParam, Map<String, Object> params, String charset, int timeout,Map<String, String> headmsg) {
		StringBuffer resultBuffer = null;
		String responsecode = null;
		// �����������
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (Entry<String, Object> e : params.entrySet()) {
				sbParams.append(e.getKey());
				sbParams.append("=");
				sbParams.append(e.getValue());
				sbParams.append("&");
				luckyclient.publicclass.LogUtil.APP.info("����HttpURLDel������Ϣ...key:��"+e.getKey()+"��    value:��"+e.getValue()+"��");
			}
		}
		HttpURLConnection con = null;
		OutputStreamWriter osw = null;
		BufferedReader br = null;
		// ��������
		try {
			URL url = new URL(urlParam);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("DELETE");
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type", "application/json");
			//�滻ͷ����Ϣ
		    for (Map.Entry<String, String> m :headmsg.entrySet())  {
		    	String key=m.getKey();
		    	String value=m.getValue();
		    	luckyclient.publicclass.LogUtil.APP.info("��ʼ����|�滻HTTPͷ����Ϣ...key:��"+key+"��    value:��"+value+"��");
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
		    		luckyclient.publicclass.LogUtil.APP.info("��ͷ��"+key+"����ֵ��"+value+"��FORMAT��BASE64��ʽ...");
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
			// ��ȡ��������
			resultBuffer = new StringBuffer();
		    responsecode = String.valueOf(con.getResponseCode());
		    if(null!=con.getHeaderField("Content-Length")){
				int contentLength = Integer.parseInt(con.getHeaderField("Content-Length"));
				if (contentLength > 0) {
					br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
					String temp;
					while ((temp = br.readLine()) != null) {
						resultBuffer.append(temp);
					}
				}
		    }
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
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
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
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

		return responsecode + resultBuffer.toString();
	}


/**
	 * @throws NoSuchAlgorithmException 
 * @throws KeyManagementException 
 * @Description:ʹ��HttpClient����put����  ����JSON��ʽ
	 */
	public static String httpClientPutJson(String urlParam, Map<String, Object> params, String charset,Map<String, String> headmsg,String cerpath) throws KeyManagementException, NoSuchAlgorithmException {
		StringBuffer resultBuffer = null;
		String responsecode = null;
		CloseableHttpClient httpclient=iniHttpClient(urlParam,cerpath);
		HttpPut httpput = new HttpPut(urlParam);
	    httpput.setHeader("Content-Type", "application/json");
		//�滻ͷ����Ϣ
	    for (Map.Entry<String, String> m :headmsg.entrySet())  {
	    	String key=m.getKey();
	    	String value=m.getValue();
	    	luckyclient.publicclass.LogUtil.APP.info("��ʼ����|�滻HTTPͷ����Ϣ...key:��"+key+"��    value:��"+value+"��");
	    	if(null!=value&&value.indexOf("Base64(")==0){
	    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
	    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
	    		luckyclient.publicclass.LogUtil.APP.info("��ͷ��"+key+"����ֵ��"+value+"��FORMAT��BASE64��ʽ...");
	    		httpput.setHeader(key,value);
	    	}else{
	    		httpput.setHeader(key,value);
	    	}
        }
		// �����������
		BufferedReader br = null;
		try {
		if(params.size()>0){
				JSONObject jsonObject = JSONObject.fromObject(params);
				luckyclient.publicclass.LogUtil.APP.info("����HTTPClientPutJson������Ϣ...��"+jsonObject.toString()+"��");
				StringEntity entity = new StringEntity(jsonObject.toString(),charset);
				httpput.setEntity(entity);
			}
       
		 CloseableHttpResponse response = httpclient.execute(httpput);

			// ��ȡ��������Ӧ����
			resultBuffer = new StringBuffer();
			//��ȡ��������е���Ӧ�ж���  
			org.apache.http.StatusLine statusLine = response.getStatusLine();
			//��״̬���л�ȡ״̬��  
	        responsecode = String.valueOf(statusLine.getStatusCode());
	        if(null!=response.getEntity()){
	        	br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
				String temp;
				while ((temp = br.readLine()) != null) {
					resultBuffer.append(temp);
				}
	        }
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
					br = null;
					throw new RuntimeException(e);
				}
			}
		}		
		return responsecode + resultBuffer.toString();
	}

	/**
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 * @Description:ʹ��HttpClient����put����
	 */
	public static String httpClientPut(String urlParam, Map<String, Object> params, String charset,Map<String, String> headmsg,String cerpath) throws KeyManagementException, NoSuchAlgorithmException {
		StringBuffer resultBuffer = null;
		String responsecode = null;
		CloseableHttpClient httpclient=iniHttpClient(urlParam,cerpath);
		HttpPut httpput = new HttpPut(urlParam);
		//�滻ͷ����Ϣ
	    for (Map.Entry<String, String> m :headmsg.entrySet())  {
	    	String key=m.getKey();
	    	String value=m.getValue();
	    	luckyclient.publicclass.LogUtil.APP.info("��ʼ����|�滻HTTPͷ����Ϣ...key:��"+key+"��    value:��"+value+"��");
	    	if(null!=value&&value.indexOf("Base64(")==0){
	    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
	    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
	    		luckyclient.publicclass.LogUtil.APP.info("��ͷ��"+key+"����ֵ��"+value+"��FORMAT��BASE64��ʽ...");
	    		httpput.setHeader(key,value);
	    	}else{
	    		httpput.setHeader(key,value);
	    	}
        }
		// �����������
		BufferedReader br = null;
		try {
			if(params.size()>0){
				//ƴ�Ӳ���
			    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			    for (Map.Entry<String, Object> m :params.entrySet())  { 
		            nvps.add(new BasicNameValuePair(m.getKey(), m.getValue().toString()));
		            luckyclient.publicclass.LogUtil.APP.info("��ʼ����HTTPClientPut������Ϣ...key:��"+m.getKey()+"��    value:��"+m.getValue()+"��");
		        }
			    httpput.setEntity(new UrlEncodedFormEntity(nvps,charset));
			}
       
		 CloseableHttpResponse response = httpclient.execute(httpput);

			// ��ȡ��������Ӧ����
			resultBuffer = new StringBuffer();
			//��ȡ��������е���Ӧ�ж���  
			org.apache.http.StatusLine statusLine = response.getStatusLine();
			//��״̬���л�ȡ״̬��  
	        responsecode = String.valueOf(statusLine.getStatusCode());
	        if(null!=response.getEntity()){
	        	br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
				String temp;
				while ((temp = br.readLine()) != null) {
					resultBuffer.append(temp);
				}
	        }		
		} catch (Exception e) {
			luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
					br = null;
					throw new RuntimeException(e);
				}
			}
		}		
		return responsecode + resultBuffer.toString();
	}

    /**

     * ����������ǩ��֤��

     * 

     * @param keyStorePath ��Կ��·��

     * @param keyStorepass ��Կ������

     * @return

     */

    private static SSLContext sslContextKeyStore(String keyStorePath, String keyStorepass) {
        SSLContext sslContext = null;
        FileInputStream instream = null;
        KeyStore trustStore = null;
        luckyclient.publicclass.LogUtil.APP.info("֤��·����"+keyStorePath+"  ��Կ��"+keyStorepass);
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            luckyclient.publicclass.LogUtil.APP.info("��ʼ��ȡ֤���ļ���...");
            instream = new FileInputStream(new File(keyStorePath));
            luckyclient.publicclass.LogUtil.APP.info("��ʼ����֤���Լ���Կ...");
            trustStore.load(instream, keyStorepass.toCharArray());
            // �����Լ���CA��������ǩ����֤��
            sslContext = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
            // ���� javax.net.ssl.TrustManager ����
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
            tmf.init(trustStore);
            TrustManager tms [] = tmf.getTrustManagers();
            // ʹ�ù���õ� TrustManager ������Ӧ�� https վ��
            sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tms, new java.security.SecureRandom());
        } catch (Exception e) {
        	luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
        } finally {
            try {
                instream.close();
            } catch (IOException e) {
            	luckyclient.publicclass.LogUtil.APP.error(e.getMessage(), e);
            }
        }
        return sslContext;
    }

    /**
     * httpclient��ʽ HTTP/HTTPS��ʼ��
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
    		//�����ƹ���֤�ķ�ʽ����https����
    		SSLContext sslContext=null;
    		if(null==cerpath||"".equals(cerpath.trim())){
    			luckyclient.publicclass.LogUtil.APP.info("��ʼ����HTTPS������֤����...");
    	        TrustManager[] trustManagers = {new MyX509TrustManager()};  
    	        sslContext = SSLContext.getInstance("TLS");   
    	        sslContext.init(null, trustManagers, new SecureRandom());
    		}else{
    			luckyclient.publicclass.LogUtil.APP.info("��ʼ����HTTPS˫����֤����...");
    			String strcerpath[]=cerpath.split(";");
    			sslContext = sslContextKeyStore(strcerpath[0], strcerpath[1]);
    		}
            
            // ����Э��http��https��Ӧ�Ĵ���socket���ӹ����Ķ���
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslContext))
                .build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            connManager.setDefaultMaxPerRoute(1);
            //�����Զ����httpclient����
            httpclient = HttpClients.custom().setConnectionManager(connManager).build();
    	}else{
    		httpclient = HttpClients.createDefault();
    	}
    	return httpclient;
    }
	
	public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException {
		
	}
}
