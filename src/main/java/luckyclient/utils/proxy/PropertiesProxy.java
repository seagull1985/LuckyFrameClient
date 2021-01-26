package luckyclient.utils.proxy;

import com.alibaba.fastjson.JSONObject;
import luckyclient.netty.ClientHandler;
import luckyclient.utils.EncryptionUtils;
import luckyclient.utils.LogUtil;
import luckyclient.utils.httputils.HttpRequest;
import org.apache.commons.lang.StringUtils;

import java.util.Properties;

/**
 * @author fengjian
 * @date 2020/6/22 21:26
 */
public class PropertiesProxy extends Properties {

    private  Properties wapper = new Properties();

    public Properties getWapper() {
        return wapper;
    }

    public void setWapper(Properties wapper) {
        this.wapper = wapper;
    }

    @Override
    public String getProperty(String key) {
        //获取参数
        //通过接口获取服务端配置
        try{
            if(key.startsWith("server.web")||key.startsWith("client.")||key.startsWith("netty.")){
                //LogUtil.APP.info("key is "+key+"，skip config from service");
                return wapper.getProperty(key);
            }
            if(!ClientHandler.clientId.equals(-1)){
                //String result = HttpRequest.loadJSON("/system/clientConfig/config/"+ ClientHandler.clientId+"/"+key);
                String url = "/system/clientConfig/config/"+ ClientHandler.clientId+ "/"+key;
                String result = HttpRequest.loadJSON(url);
                if(StringUtils.isNotEmpty(result))
                {
                    JSONObject res=JSONObject.parseObject(result);
                    if(res.get("code")!=null&&res.getInteger("code")==200)
                    {
                        String value= EncryptionUtils.decrypt(res.get("value").toString());
                        LogUtil.APP.info("get config from server succeed:"+res.toJSONString()+";");
                        //LogUtil.APP.info("get config from server:"+res.toJSONString()+";value="+value);
                        return value;
                    }

                }
            }
        }catch (Exception e)
        {
            LogUtil.APP.error("服务器没有配置:"+key, e);
        }
        return wapper.getProperty(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        //获取参数
        //通过接口获取服务端配置
        try{
            if(key.startsWith("server.web")||key.startsWith("client.")||key.startsWith("netty.")){
                LogUtil.APP.info("key is "+key+"，skip config from service");
                return wapper.getProperty(key);
            }
            if(!ClientHandler.clientId.equals(-1)){
                LogUtil.APP.info("get config from service");
                String url = "/system/clientConfig/config/"+ ClientHandler.clientId+ "/"+key;
                LogUtil.APP.info("getProperty---url-{}", url);
                //String result = HttpRequest.loadJSON("/system/clientConfig/config/"+ ClientHandler.clientId+"/"+key);
                String result = HttpRequest.loadJSON(url);
                if(StringUtils.isNotEmpty(result))
                {
                    return result;
                }
            }
        }catch (Exception e)
        {
            LogUtil.APP.error("服务器没有配置:"+key, e);
        }
        return wapper.getProperty(key, defaultValue);
    }

}
