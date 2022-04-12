title Web UI界面用例调试-【%1】
set classpath=%CLASSPATH%;.\luckyclient;
@echo Web UI界面调试用例接口
@echo 参数说明 依次为：用例编号 执行者 用例类型 浏览器类型
java -Djava.ext.dirs=./lib;.%4 luckyclient.execution.WebDebugExecuteWeb %1 %2 %3 %5
@echo 当前用例调试窗口将在90秒后退出
ping 127.0.0.1 -n 90 >nul
exit