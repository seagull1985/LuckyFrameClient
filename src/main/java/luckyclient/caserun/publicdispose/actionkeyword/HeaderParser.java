package luckyclient.caserun.publicdispose.actionkeyword;


import com.alibaba.fastjson.JSONObject;

/**
 * 动作关键字的处理接口的实现类：从响应header中取出某个header值
 * @author: sunshaoyan
 * @date: Created on 2019/4/13
 */
@Action(name="Header")
public class HeaderParser implements ActionKeyWordParser {


    /**
     * @param actionorder 动作关键字
     */
    @Override
    public String parse(String actionorder, String testResult) {


        // 获取步骤间等待时间
        String headerParam=actionorder.substring(0, actionorder.lastIndexOf("#Header"));
        String pre = "RESPONSE_HEAD:【";
        String headerStr = testResult.substring(testResult.indexOf(pre) + pre.length(), testResult.indexOf("】 RESPONSE_CODE"));
        return JSONObject.parseObject(headerStr).getJSONArray(headerParam).getString(0);

    }
}
