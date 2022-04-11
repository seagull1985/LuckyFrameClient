package luckyclient.execution;

import luckyclient.execution.httpinterface.TestControl;
import luckyclient.execution.httpinterface.WebTestCaseDebug;
import luckyclient.utils.LogUtil;
import org.apache.log4j.PropertyConfigurator;
import springboot.RunService;

import java.io.File;

public class WebDebugExecuteWeb extends TestControl {
    public static void main(String[] args) {
        try {
            PropertyConfigurator.configure(RunService.APPLICATION_HOME + File.separator + "log4j.conf");
            String caseIdStr = args[0];
            String userIdStr = args[1];
            //修改点
            String caseTypeStr =args[2];
            String browserTypeStr =args[3];
            WebTestCaseDebug.oneCaseDebug(caseIdStr, userIdStr,caseTypeStr,browserTypeStr);
        } catch (Exception e) {
            LogUtil.APP.error("启动用例调试主函数出现异常，请检查！",e);
        } finally{
            System.exit(0);
        }
    }
}
