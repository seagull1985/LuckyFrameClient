package luckyclient.execution.dispose.actionkeyword;


import luckyclient.execution.dispose.ChangString;
import luckyclient.utils.LogUtil;

/**
 * 动作关键字的处理接口的实现类：线程等待时间
 * @author: sunshaoyan
 * @date: Created on 2019/4/13
 */
@Action(name="wait")
public class ThreadWaitAction implements ActionKeyWordParser {


    /**
     * 动作关键字
     * @param actionParams 关键字参数
     * @param testResult 待处理测试结果
     * @return 返回处理后结果
     */
    @Override
    public String parse(String actionParams, String testResult) {
        if(ChangString.isInteger(actionParams)){
            try {
                // 获取步骤间等待时间
                int time=Integer.parseInt(actionParams);
                if (time > 0) {
                    LogUtil.APP.info("Action(Wait):线程等待【{}】秒...",time);
                    Thread.sleep(time * 1000);
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            LogUtil.APP.error("使用等待关键字的参数不是整数，直接跳过此动作，请检查！");
        }
        return testResult;
    }
}
