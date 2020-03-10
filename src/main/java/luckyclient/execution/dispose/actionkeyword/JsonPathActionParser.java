package luckyclient.execution.dispose.actionkeyword;

import luckyclient.driven.SubString;
import luckyclient.utils.LogUtil;

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
     * 通过jsonPath表达式获取JSON字符串指定值
     * 仅支持返回值是String类型，不支持List,如果jsonPath表达式返回的是List将抛出异常
     * @param actionParams 动作关键字
     * @param testResult 测试结果
     */
	@Override
    public String parse(String actionParams, String testResult) {
    	LogUtil.APP.info("Action(jsonPath):开始处理jsonPath动作...参数：【{}】   待处理json字符串：【{}】",actionParams,testResult);
    	testResult = SubString.jsonPathGetParams(actionParams, testResult);
        LogUtil.APP.info("Action(jsonPath):处理jsonPath动作完成...处理结果【{}】",testResult);
        return testResult;
    }
}
