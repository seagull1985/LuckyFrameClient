package luckyclient.execution.dispose.actionkeyword;

/**
 * 动作关键字的处理接口
 * @author: sunshaoyan
 * @date: Created on 2019/4/13
 */
public interface ActionKeyWordParser {

	/**
	 * 针对关键字的抽象方法
	 * @param actionParams
	 * @param testResult
	 * @return
	 * @author Seagull
	 * @date 2019年8月8日
	 */
    String parse(String actionParams, String testResult);
}
