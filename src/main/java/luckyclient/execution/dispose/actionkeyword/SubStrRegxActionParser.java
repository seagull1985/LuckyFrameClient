package luckyclient.execution.dispose.actionkeyword;

import luckyclient.driven.SubString;
import luckyclient.utils.LogUtil;

/**
 * 动作关键字的处理接口的实现类：获取JSON字符串指定Key的值，采用正则匹配
 * @author: sunshaoyan
 * @date: Created on 2019/4/13
 */
@Action(name="substrrgex")
public class SubStrRegxActionParser implements ActionKeyWordParser {


    /**
     * 获取JSON字符串指定Key的值，采用正则匹配
     * @param actionParams 动作关键字
     * @param testResult 测试结果
     */
    @Override
    public String parse(String actionParams, String testResult) {
        String key;
        String index="1";
        if(actionParams.endsWith("]")&&actionParams.contains("[")){
            key=actionParams.substring(0,actionParams.lastIndexOf("["));
            index=actionParams.substring(actionParams.lastIndexOf("[")+1, actionParams.lastIndexOf("]"));
        }else{
            key=actionParams;
        }
        testResult= SubString.subStrRgex(testResult, key, index);
        LogUtil.APP.info("Action(subStrRgex):获取JSON字符串指定Key的值是:{}",testResult);
        return testResult;
    }
}
