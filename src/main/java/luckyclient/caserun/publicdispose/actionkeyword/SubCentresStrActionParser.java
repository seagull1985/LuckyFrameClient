package luckyclient.caserun.publicdispose.actionkeyword;

import luckyclient.driven.SubString;
import luckyclient.publicclass.LogUtil;

/**
 * 动作关键字的处理接口的实现类：截取测试结果指定开始及结束位置字符串
 * @author: sunshaoyan
 * @date: Created on 2019/4/13
 */
@Action(name="subCentreStr")
public class SubCentresStrActionParser implements ActionKeyWordParser {


    /**
     * 截取测试结果指定开始及结束位置字符串
     * @param actionKeyWord 动作关键字
     * @param testResult 测试结果
     */
    @Override
    public String parse(String actionKeyWord, String testResult) {
        String actionparams=actionKeyWord.substring(0, actionKeyWord.lastIndexOf("#subCentreStr"));
        String startstr="";
        String endstr="";
        if(actionparams.startsWith("[")&&actionparams.endsWith("]")){
            startstr=actionparams.substring(actionparams.indexOf("[")+1, actionparams.indexOf("]"));
            endstr=actionparams.substring(actionparams.lastIndexOf("[")+1, actionparams.lastIndexOf("]"));
            testResult= SubString.subCentreStr(testResult, startstr, endstr);
            LogUtil.APP.info("Action(subCentreStr):截取测试结果指定开始及结束位置字符串："+testResult);
        }else{
            testResult="步骤动作：subCentreStr 必须是[\"开始字符\"][\"结束字符\"]#subCentreStr 格式，请检查您的步骤动作关键字:"+actionKeyWord;
            LogUtil.APP.error("步骤动作：subCentreStr 必须是[\"开始字符\"][\"结束字符\"]#subCentreStr 格式，请检查您的步骤动作关键字:"+actionKeyWord);
        }
        return testResult;
    }
}
