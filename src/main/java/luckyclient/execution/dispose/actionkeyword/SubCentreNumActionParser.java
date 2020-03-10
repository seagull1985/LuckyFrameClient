package luckyclient.execution.dispose.actionkeyword;


import luckyclient.driven.SubString;
import luckyclient.utils.LogUtil;

/**
 * 动作关键字的处理接口的实现类：截取测试结果指定开始及结束位置字符串
 * @author: sunshaoyan
 * @date: Created on 2019/4/13
 */
@Action(name="subcentrenum")
public class SubCentreNumActionParser implements ActionKeyWordParser {


    /**
     * 截取测试结果指定开始及结束位置字符串
     * @param actionParams 动作关键字
     * @param testResult 测试结果
     */
    @Override
    public String parse(String actionParams, String testResult) {
        if(actionParams.startsWith("[")&&actionParams.endsWith("]")){
            String startnum=actionParams.substring(actionParams.indexOf("[")+1, actionParams.indexOf("]"));
            String endnum=actionParams.substring(actionParams.lastIndexOf("[")+1, actionParams.lastIndexOf("]"));
            testResult= SubString.subCentreNum(testResult, startnum, endnum);
            LogUtil.APP.info("Action(subCentreNum):截取测试结果指定开始及结束位置字符串:{}",testResult);
        }else{
            testResult="步骤动作：subCentreNum 必须是[\"开始字符\"][\"结束字符\"]#subCentreNum 格式，请检查您的步骤动作参数:"+actionParams;
            LogUtil.APP.warn("步骤动作：subCentreNum 必须是[\"开始位置(整数)\"][\"结束位置(整数)\"]#subCentreNum 格式，请检查您的步骤动作参数:{}",actionParams);
        }
        return testResult;
    }
}
