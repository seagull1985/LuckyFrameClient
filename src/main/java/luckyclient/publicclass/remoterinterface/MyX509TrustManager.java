package luckyclient.publicclass.remoterinterface;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class MyX509TrustManager implements X509TrustManager  
{  
    @Override  
    public void checkClientTrusted(X509Certificate ax509certificate[], String s) throws CertificateException  
    {  
        //TODO nothing  
    }  
  
    @Override  
    public void checkServerTrusted(X509Certificate ax509certificate[], String s) throws CertificateException  
    {  
        //TODO nothing  
    }  
  
    @Override  
    public X509Certificate[] getAcceptedIssuers()  
    {  
        return new X509Certificate[]{};  
    }  
}  
