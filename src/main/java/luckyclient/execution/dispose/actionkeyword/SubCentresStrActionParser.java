package luckyclient.execution.dispose.actionkeyword;

import luckyclient.driven.SubString;
import luckyclient.utils.LogUtil;

/**
 * 动作关键字的处理接口的实现类：截取测试结果指定开始及结束位置字符串
 * @author: sunshaoyan
 * @date: Created on 2019/4/13
 */
@Action(name="subcentrestr")
public class SubCentresStrActionParser implements ActionKeyWordParser {


    /**
     * 截取测试结果指定开始及结束位置字符串
     * @param actionParams 动作关键字
     * @param testResult 测试结果
     */
    @Override
    public String parse(String actionParams, String testResult) {
        String startstr;
        String endstr;
        if(actionParams.startsWith("[")&&actionParams.endsWith("]")){
            startstr=actionParams.substring(actionParams.indexOf("[")+1, actionParams.indexOf("]"));
            endstr=actionParams.substring(actionParams.lastIndexOf("[")+1, actionParams.lastIndexOf("]"));
            testResult= SubString.subCentreStr(testResult, startstr, endstr);
            LogUtil.APP.info("Action(subCentreStr):截取测试结果指定开始及结束位置字符串:{}",testResult);
        }else{
            testResult="步骤动作：subCentreStr 必须是[\"开始字符\"][\"结束字符\"]#subCentreStr 格式，请检查您的步骤动作参数:"+actionParams;
            LogUtil.APP.warn("步骤动作：subCentreStr 必须是[\"开始字符\"][\"结束字符\"]#subCentreStr 格式，请检查您的步骤动作参数:{}",actionParams);
        }
        return testResult;
    }
}
