package luckyclient.caserun.publicdispose.actionkeyword;


import com.alibaba.fastjson.JSONObject;

import luckyclient.publicclass.LogUtil;

/**
 * 动作关键字的处理接口的实现类：从响应header中取出某个header值
 * @author: sunshaoyan
 * @date: Created on 2019/4/13
 */
@Action(name="header")
public class HeaderParser implements ActionKeyWordParser {


    /**
     * @param actionorder 动作关键字
     */
    @Override
    public String parse(String actionParams, String testResult) {
        String pre = "RESPONSE_HEAD:【";
        String headerStr = testResult.substring(testResult.indexOf(pre) + pre.length(), testResult.indexOf("】 RESPONSE_CODE"));
        String getHeader = JSONObject.parseObject(headerStr).getJSONArray(actionParams).getString(0);
        LogUtil.APP.info("Action(header):从响应header中取出指定header值是:{}",getHeader);
        return getHeader;

    }
}
