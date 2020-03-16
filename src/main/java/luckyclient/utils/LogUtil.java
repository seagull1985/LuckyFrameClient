package luckyclient.utils;

import java.lang.reflect.Field;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 系统日志记录
 *
 * @author Seagull
 */
public class LogUtil {

    /**
     * 主要使用三种日志级别，info,warn,error
     * info 记录客户端系统日志，监控客户端运行情况
     * warn 记录客户端业务上的告警日志
     * error 记录客户端在执行过程中抛出的异常以及严重错误
     */
    public static final Logger APP = LoggerFactory.getLogger("info");

    public static StringBuffer getFieldValue(Object bean) {
        StringBuffer sb = new StringBuffer();

        try {
            if (bean == null) {
                return null;
            }
            Field[] fieldArray = bean.getClass().getDeclaredFields();
            int indexId = 0;
            Object obj;
            for (Field field : fieldArray) {
                field.setAccessible(true);
                obj = field.get(bean);
                if (!(obj instanceof List) && !"serialVersionUID".equals(field.getName())) {
                    if (indexId > 0) {
                        sb.append(",");
                    }
                    if (obj != null) {
                        sb.append(field.getName()).append("=").append(obj.toString());
                    } else {
                        sb.append(field.getName()).append("=");
                    }
                    indexId += 1;
                }
            }
        } catch (Exception ex) {
            LogUtil.APP.error("日志异常", ex);
        }
        return sb;
    }
}
