package luckyclient.caserun.publicdispose.actionkeyword;

/**
 * 动作关键字的处理接口
 * @author: sunshaoyan
 * @date: Created on 2019/4/13
 */
public interface ActionKeyWordParser {

    String parse(String actionParams, String testResult);
}
