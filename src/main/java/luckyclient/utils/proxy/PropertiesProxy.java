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
        LogUtil.APP.info("get config from service");
        //获取参数
        //通过接口获取服务端配置
        try{
            String result = HttpRequest.loadJSON("/system/clientConfig/config/"+ ClientHandler.clientId+"/"+key);
            if(StringUtils.isNotEmpty(result))
            {
                JSONObject res=JSONObject.parseObject(result);
                if(res.get("code")!=null&&res.getInteger("code")==200)
                {
                    String value= EncryptionUtils.decrypt(res.get("value").toString());
                    LogUtil.APP.info("get config from server:"+res.toJSONString()+";value="+value);
                    return value;
                }

            }
        }catch (Exception e)
        {
            LogUtil.APP.error("服务器没有配置:"+key);
        }
        return wapper.getProperty(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        LogUtil.APP.info("get config from service");
        //获取参数
        //通过接口获取服务端配置
        try{
            String result = HttpRequest.loadJSON("/system/clientConfig/config/"+ ClientHandler.clientId+"/"+key);
            if(StringUtils.isNotEmpty(result))
            {
                return result;
            }
        }catch (Exception e)
        {
            LogUtil.APP.error("服务器没有配置:"+key);
        }
        return wapper.getProperty(key, defaultValue);
    }
}
