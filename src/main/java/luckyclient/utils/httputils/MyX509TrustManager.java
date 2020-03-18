package luckyclient.utils.httputils;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * 
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019年8月8日
 */
public class MyX509TrustManager implements X509TrustManager  
{  
    @Override  
    public void checkClientTrusted(X509Certificate[] ax509certificate, String s) {
        //TODO nothing  
    }  
  
    @Override  
    public void checkServerTrusted(X509Certificate[] ax509certificate, String s) {
        //TODO nothing  
    }  
  
    @Override  
    public X509Certificate[] getAcceptedIssuers()  
    {  
        return new X509Certificate[]{};  
    }  
}  
