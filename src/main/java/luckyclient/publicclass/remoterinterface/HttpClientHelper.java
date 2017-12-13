package luckyclient.publicclass.remoterinterface;

import java.io.BufferedReader;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import net.sf.json.JSONObject;

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
	 * @Description:使用HttpURLConnection发送post请求
	 */
	public static String sendHttpURLPost(String urlParam, Map<String, Object> params, String charset, int timeout,Map<String, String> headmsg) {
		StringBuffer resultBuffer = null;
		// 构建请求参数
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (Entry<String, Object> e : params.entrySet()) {
				sbParams.append(e.getKey());
				sbParams.append("=");
				sbParams.append(e.getValue());
				sbParams.append("&");
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
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		//value="Basic  " + Base64.encode((valuesub).getBytes());
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
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
			int contentLength = Integer.parseInt(con.getHeaderField("Content-Length"));
			if (contentLength > 0) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
				String temp;
				while ((temp = br.readLine()) != null) {
					resultBuffer.append(temp);
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

		return resultBuffer.toString();
	}

	/**
	 * @Description:使用URLConnection发送post
	 */
	public static String sendURLPost(String urlParam, Map<String, Object> params, String charset, int timeout,Map<String, String> headmsg) {
		StringBuffer resultBuffer = null;
		// 构建请求参数
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (Entry<String, Object> e : params.entrySet()) {
				sbParams.append(e.getKey());
				sbParams.append("=");
				sbParams.append(e.getValue());
				sbParams.append("&");
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
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
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
			int contentLength = Integer.parseInt(con.getHeaderField("Content-Length"));
			if (contentLength > 0) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
				String temp;
				while ((temp = br.readLine()) != null) {
					resultBuffer.append(temp);
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
	 * @Description:发送get请求保存下载文件
	 */
	public static void sendGetAndSaveFile(String urlParam, Map<String, Object> params, String fileSavePath, int timeout,Map<String, String> headmsg) {
		// 构建请求参数
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (Entry<String, Object> entry : params.entrySet()) {
				sbParams.append(entry.getKey());
				sbParams.append("=");
				sbParams.append(entry.getValue());
				sbParams.append("&");
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
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
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
	 * @Description:使用HttpURLConnection发送get请求
	 */
	public static String sendHttpURLGet(String urlParam, Map<String, Object> params, String charset, int timeout,Map<String, String> headmsg) {
		StringBuffer resultBuffer = null;
		// 构建请求参数
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (Entry<String, Object> entry : params.entrySet()) {
				sbParams.append(entry.getKey());
				sbParams.append("=");
				sbParams.append(entry.getValue());
				sbParams.append("&");
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
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
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
	 * @Description:使用URLConnection发送get请求
	 */
	public static String sendURLGet(String urlParam, Map<String, Object> params, String charset, int timeout,Map<String, String> headmsg) {
		StringBuffer resultBuffer = null;
		// 构建请求参数
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (Entry<String, Object> entry : params.entrySet()) {
				sbParams.append(entry.getKey());
				sbParams.append("=");
				sbParams.append(entry.getValue());
				sbParams.append("&");
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
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
		    		con.setRequestProperty(key, value);
		    	}else{
		    		con.setRequestProperty(key, value);
		    	}
	        }
			con.setConnectTimeout(timeout*1000);
			// 建立连接
			con.connect();
			resultBuffer = new StringBuffer();
			br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
			String temp;
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
	 * @Description:使用HttpClient以JSON格式发送post请求
	 */
	public static String httpClientPostJson(String urlParam, Map<String, Object> params, String charset,Map<String, String> headmsg) {
		StringBuffer resultBuffer = null;
		//HttpClient client = new HttpClient();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(urlParam);
	    httpPost.setHeader("Content-Type", "application/json");
		//替换头域信息
	    for (Map.Entry<String, String> m :headmsg.entrySet())  {
	    	String key=m.getKey();
	    	String value=m.getValue();
	    	if(null!=value&&value.indexOf("Base64(")==0){
	    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
	    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
	    		httpPost.setHeader(key, value);
	    	}else{
	    		httpPost.setHeader(key, value);
	    	}
        }
		// 构建请求参数
		BufferedReader br = null;
		try {
		if(params.size()>0){
				JSONObject jsonObject = JSONObject.fromObject(params); 
				StringEntity entity = new StringEntity(jsonObject.toString(),charset);
				httpPost.setEntity(entity);
			}
       
		 CloseableHttpResponse response = httpclient.execute(httpPost);

			// 读取服务器响应数据
			resultBuffer = new StringBuffer();
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
			String temp;
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
	 * @Description:使用HttpClient发送post请求
	 */
	public static String httpClientPost(String urlParam, Map<String, Object> params, String charset,Map<String, String> headmsg) {
		StringBuffer resultBuffer = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(urlParam);
		//替换头域信息
	    for (Map.Entry<String, String> m :headmsg.entrySet())  {
	    	String key=m.getKey();
	    	String value=m.getValue();
	    	if(null!=value&&value.indexOf("Base64(")==0){
	    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
	    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
	    		httpPost.setHeader(key, value);
	    	}else{
	    		httpPost.setHeader(key, value);
	    	}
        }
		// 构建请求参数
		BufferedReader br = null;
		try {
			if(params.size()>0){
				//拼接参数
			    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			    for (Map.Entry<String, Object> m :params.entrySet())  { 
		            nvps.add(new BasicNameValuePair(m.getKey(), m.getValue().toString()));
		        }
			    httpPost.setEntity(new UrlEncodedFormEntity(nvps,charset));
			}

			 CloseableHttpResponse response = httpclient.execute(httpPost);
			// 读取服务器响应数据
			resultBuffer = new StringBuffer();

			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
			String temp;
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
	 * @Description:使用HttpClient发送get请求
	 */
	public static String httpClientGet(String urlParam, Map<String, Object> params, String charset,Map<String, String> headmsg) {
		StringBuffer resultBuffer = null;
		//@SuppressWarnings({ "deprecation", "resource" })
		//HttpClient client = new HttpClient();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		BufferedReader br = null;
		// 构建请求参数
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
			}
		}
		if (sbParams != null && sbParams.length() > 0) {
			urlParam = urlParam + "?" + sbParams.substring(0, sbParams.length() - 1);
		}
		HttpGet httpGet = new HttpGet(urlParam);
		//替换头域信息
	    for (Map.Entry<String, String> m :headmsg.entrySet())  {
	    	String key=m.getKey();
	    	String value=m.getValue();
	    	if(null!=value&&value.indexOf("Base64(")==0){
	    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
	    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
	    		httpGet.setHeader(key, value);
	    	}else{
	    		httpGet.setHeader(key, value);
	    	}
        }
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
	 * @Description:使用socket发送post请求
	 */
	public static String sendSocketPost(String urlParam, Map<String, Object> params, String charset,Map<String, String> headmsg) {
		String result = "";
		// 构建请求参数
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (Entry<String, Object> entry : params.entrySet()) {
				sbParams.append(entry.getKey());
				sbParams.append("=");
				sbParams.append(entry.getValue());
				sbParams.append("&");
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
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
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
	 * @Description:使用socket发送get请求
	 */
	public static String sendSocketGet(String urlParam, Map<String, Object> params, String charset,Map<String, String> headmsg) {
		String result = "";
		// 构建请求参数
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (Entry<String, Object> entry : params.entrySet()) {
				sbParams.append(entry.getKey());
				sbParams.append("=");
				sbParams.append(entry.getValue());
				sbParams.append("&");
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
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
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
	 * @Description:读取一行数据，contentLe内容长度为0时，读取响应头信息，不为0时读正文
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
	 * @Description:使用HttpURLConnection发送delete请求
	 */
	public static String sendHttpURLDel(String urlParam, Map<String, Object> params, String charset, int timeout,Map<String, String> headmsg) {
		StringBuffer resultBuffer = null;
		String responsecode = null;
		// 构建请求参数
		StringBuffer sbParams = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (Entry<String, Object> e : params.entrySet()) {
				sbParams.append(e.getKey());
				sbParams.append("=");
				sbParams.append(e.getValue());
				sbParams.append("&");
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
		    	if(null!=value&&value.indexOf("Base64(")==0){
		    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
		    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
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
	 * @Description:使用HttpClient发送put请求  参数JSON格式
	 */
	public static String httpClientPutJson(String urlParam, Map<String, Object> params, String charset,Map<String, String> headmsg) {
		StringBuffer resultBuffer = null;
		String responsecode = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPut httpput = new HttpPut(urlParam);
	    httpput.setHeader("Content-Type", "application/json");
		//替换头域信息
	    for (Map.Entry<String, String> m :headmsg.entrySet())  {
	    	String key=m.getKey();
	    	String value=m.getValue();
	    	if(null!=value&&value.indexOf("Base64(")==0){
	    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
	    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
	    		httpput.setHeader(key,value);
	    	}else{
	    		httpput.setHeader(key,value);
	    	}
        }
		// 构建请求参数
		BufferedReader br = null;
		try {
		if(params.size()>0){
				JSONObject jsonObject = JSONObject.fromObject(params);
				StringEntity entity = new StringEntity(jsonObject.toString(),charset);
				httpput.setEntity(entity);
			}
       
		 CloseableHttpResponse response = httpclient.execute(httpput);

			// 读取服务器响应数据
			resultBuffer = new StringBuffer();
			//获取请求对象中的响应行对象  
			org.apache.http.StatusLine statusLine = response.getStatusLine();
			//从状态行中获取状态码  
	        responsecode = String.valueOf(statusLine.getStatusCode());
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
			String temp;
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
		return responsecode + resultBuffer.toString();
	}

	/**
	 * @Description:使用HttpClient发送put请求
	 */
	public static String httpClientPut(String urlParam, Map<String, Object> params, String charset,Map<String, String> headmsg) {
		StringBuffer resultBuffer = null;
		String responsecode = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPut httpput = new HttpPut(urlParam);
		//替换头域信息
	    for (Map.Entry<String, String> m :headmsg.entrySet())  {
	    	String key=m.getKey();
	    	String value=m.getValue();
	    	if(null!=value&&value.indexOf("Base64(")==0){
	    		String valuesub=value.substring(value.indexOf("Base64(")+7,value.lastIndexOf(")"));
	    		value="Basic " + DatatypeConverter.printBase64Binary((valuesub).getBytes());
	    		httpput.setHeader(key,value);
	    	}else{
	    		httpput.setHeader(key,value);
	    	}
        }
		// 构建请求参数
		BufferedReader br = null;
		try {
			if(params.size()>0){
				//拼接参数
			    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			    for (Map.Entry<String, Object> m :params.entrySet())  { 
		            nvps.add(new BasicNameValuePair(m.getKey(), m.getValue().toString()));
		        }
			    httpput.setEntity(new UrlEncodedFormEntity(nvps,charset));
			}
       
		 CloseableHttpResponse response = httpclient.execute(httpput);

			// 读取服务器响应数据
			resultBuffer = new StringBuffer();
			//获取请求对象中的响应行对象  
			org.apache.http.StatusLine statusLine = response.getStatusLine();
			//从状态行中获取状态码  
	        responsecode = String.valueOf(statusLine.getStatusCode());
			br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
			String temp;
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
		return responsecode + resultBuffer.toString();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
