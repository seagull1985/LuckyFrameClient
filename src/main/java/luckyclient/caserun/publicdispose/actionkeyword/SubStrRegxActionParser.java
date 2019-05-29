package luckyclient.caserun.publicdispose.actionkeyword;

import luckyclient.driven.SubString;
import luckyclient.publicclass.LogUtil;

/**
 * 动作关键字的处理接口的实现类：获取JSON字符串指定Key的值，采用正则匹配
 * @author: sunshaoyan
 * @date: Created on 2019/4/13
 */
@Action(name="subStrRgex")
public class SubStrRegxActionParser implements ActionKeyWordParser {


    /**
     * 获取JSON字符串指定Key的值，采用正则匹配
     * @param actionKeyWord 动作关键字
     * @param testResult 测试结果
     */
    @Override
    public String parse(String actionKeyWord, String testResult) {
        String actionparams=actionKeyWord.substring(0, actionKeyWord.lastIndexOf("#subStrRgex"));
        String key="";
        String index="1";
        if(actionparams.endsWith("]")&&actionparams.contains("[")){
            key=actionparams.substring(0,actionparams.lastIndexOf("["));
            index=actionparams.substring(actionparams.lastIndexOf("[")+1, actionparams.lastIndexOf("]"));
            testResult= SubString.subStrRgex(testResult, key, index);
        }else{
            key=actionparams;
            testResult= SubString.subStrRgex(testResult, key, index);
        }
        LogUtil.APP.info("Action(subStrRgex):获取JSON字符串指定Key的值是："+testResult);
        return testResult;
    }
}
