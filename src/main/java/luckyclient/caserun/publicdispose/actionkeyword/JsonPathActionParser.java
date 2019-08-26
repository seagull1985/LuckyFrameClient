package luckyclient.caserun.publicdispose.actionkeyword;

import luckyclient.driven.SubString;
import luckyclient.publicclass.LogUtil;

/**
 * 动作关键字的处理接口的实现类：使用jsonpath处理json字符串
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019年8月26日
 */
@Action(name="jsonpath")
public class JsonPathActionParser implements ActionKeyWordParser {


    /**
     * 获取JSON字符串指定Key的值是
     * @param actionKeyWord 动作关键字
     * @param testResult 测试结果
     */
    @Override
    public String parse(String actionParams, String testResult) {
        String key="";
        String index="1";
        if(actionParams.endsWith("]")&&actionParams.contains("[")){
            key=actionParams.substring(0,actionParams.lastIndexOf("["));
            index=actionParams.substring(actionParams.lastIndexOf("[")+1, actionParams.lastIndexOf("]"));
            testResult= SubString.getJsonValue(testResult, key, index);
        }else{
            key=actionParams;
            testResult=SubString.getJsonValue(testResult, key, index);
        }
        LogUtil.APP.info("Action(getJV):获取JSON字符串指定Key的值是:{}",testResult);
        return testResult;
    }
}
