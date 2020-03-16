package luckyclient.netty;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
    /**
     * 使用HttpClient以JSON格式发送post请求
     * @param urlParam 请求地址
     * @param jsonparams 请求参数
     * @param socketTimeout 超时时间
     * @return 返回请求结果
     * @author Seagull
     * @date 2019年5月14日
     */
    public static String httpClientPost(String urlParam,String jsonparams,Integer socketTimeout) throws IOException{
        StringBuffer resultBuffer;
        CloseableHttpClient httpclient= HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(urlParam);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(3000).setConnectionRequestTimeout(1500)
                .setSocketTimeout(socketTimeout).build();
        httpPost.setConfig(requestConfig);
        httpPost.setHeader("Content-Type", "application/json");
        // 构建请求参数
        BufferedReader br = null;
        try {
            if(null!=jsonparams&&jsonparams.length()>0){
                StringEntity entity = new StringEntity(jsonparams,"utf-8");
                httpPost.setEntity(entity);
            }

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
        } catch (RuntimeException e) {
            log.error("网络链接出现异常，请检查...",e);
            throw new RuntimeException(e);
        } catch (ConnectException e) {
            log.error("客户端网络无法链接，请检查...",e);
            throw new ConnectException();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("关闭流出现异常...",e);
                }
            }
        }
        return resultBuffer.toString();
    }

    /**
     * 使用HttpClient以JSON格式发送get请求
     * @param urlParam 请求地址
     * @param params 请求参数
     * @param socketTimeout 超时时间
     * @return 返回请求结果
     * @author Seagull
     * @date 2019年5月14日
     */
    public static String httpClientGet(String urlParam, Map<String, Object> params,Integer socketTimeout) {
        StringBuffer resultBuffer;
        CloseableHttpClient httpclient= HttpClients.createDefault();
        BufferedReader br = null;
        // 构建请求参数
        StringBuilder sbParams = new StringBuilder();
        if (params != null && params.size() > 0) {
            for (Entry<String, Object> entry : params.entrySet()) {
                sbParams.append(entry.getKey());
                sbParams.append("=");
                try {
                    sbParams.append(URLEncoder.encode(String.valueOf(entry.getValue()), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                sbParams.append("&");
            }
        }
        if (sbParams.length() > 0) {
            urlParam = urlParam + "?" + sbParams.substring(0, sbParams.length() - 1);
        }
        HttpGet httpGet = new HttpGet(urlParam);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(3000).setConnectionRequestTimeout(1500)
                .setSocketTimeout(socketTimeout).build();
        httpGet.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = httpclient.execute(httpGet);

            // 读取服务器响应数据
            br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
            String temp;
            resultBuffer = new StringBuffer();
            while ((temp = br.readLine()) != null) {
                resultBuffer.append(temp);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("关闭流出现异常...",e);
                }
            }
        }
        return resultBuffer.toString();
    }


    /**
     * 上传文件
     * @param urlParam 请求地址
     * @param loadpath 文件加载路径
     * @param file 文件对象
     * @return 返回上传结果
     * @author Seagull
     * @date 2019年3月15日
     */
    public static String httpClientUploadFile(String urlParam, String loadpath, File file) {
        StringBuffer resultBuffer;
        CloseableHttpClient httpclient= HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(urlParam);
        // 构建请求参数
        BufferedReader br = null;
        try {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            //设置请求的编码格式
            entityBuilder.setCharset(StandardCharsets.UTF_8);
            entityBuilder.addBinaryBody("jarfile", file);
            entityBuilder.addTextBody("loadpath", loadpath);
            HttpEntity reqEntity =entityBuilder.build();
            httpPost.setEntity(reqEntity);

            CloseableHttpResponse response = httpclient.execute(httpPost);
            //从状态行中获取状态码
            String responsecode = String.valueOf(response.getStatusLine().getStatusCode());
            // 读取服务器响应数据
            resultBuffer = new StringBuffer();

            br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
            String temp;
            while ((temp = br.readLine()) != null) {
                resultBuffer.append(temp);
            }
            if(resultBuffer.length()==0){
                resultBuffer.append("上传文件异常，响应码：").append(responsecode);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("关闭流出现异常...",e);
                }
            }
        }
        return resultBuffer.toString();
    }

    /**
     * 获取文件流
     * @param urlParam 请求地址
     * @param params 请求参数
     * @return 返回获取到的文件流
     * @author Seagull
     * @date 2019年3月15日
     */
    public static byte[] getFile(String urlParam, Map<String, Object> params) throws IOException {
        // 构建请求参数
        StringBuilder sbParams = new StringBuilder();
        if (params != null && params.size() > 0) {
            for (Entry<String, Object> entry : params.entrySet()) {
                sbParams.append(entry.getKey());
                sbParams.append("=");
                try {
                    sbParams.append(URLEncoder.encode(String.valueOf(entry.getValue()), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                sbParams.append("&");
            }
        }
        if (sbParams.length() > 0) {
            urlParam = urlParam + "?" + sbParams.substring(0, sbParams.length() - 1);
        }
        URL urlConet = new URL(urlParam);
        HttpURLConnection con = (HttpURLConnection)urlConet.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(4 * 1000);
        InputStream inStream = con .getInputStream();    //通过输入流获取图片数据
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int len;
        while( (len=inStream.read(buffer)) != -1 ){
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

    /**
     * 从网络Url中下载文件
     * @param urlStr 请求地址
     * @param fileName 文件名
     * @param savePath 保存路径
     */
    public static void downLoadFromUrl(String urlStr, String fileName, String savePath) {
        try
        {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3*1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            //得到输入流
            InputStream inputStream = conn.getInputStream();
            //获取自己数组
            byte[] getData = readInputStream(inputStream);

            //文件保存位置
            File saveDir = new File(savePath);
            if(!saveDir.exists()){
                saveDir.mkdir();
            }
            File file = new File(saveDir+File.separator+fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(getData);
            fos.close();
            inputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 从输入流中获取字节数组
     * @param inputStream 字节流
     * @return 返回字节
     */
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }


}
