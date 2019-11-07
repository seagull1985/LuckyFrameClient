title 用例单条执行【%2】 
set classpath=%CLASSPATH%;.\luckyclient;
@echo 项目单条用例执行
@echo 参数说明 依次为：项目名称 tastId 用例编号 用例版本号 
java -Djava.ext.dirs=./lib;.%4 luckyclient.execution.OneCaseExecute %1 %2 %3
@echo 当前用例单条执行窗口将在90秒后退出
ping 127.0.0.1 -n 90 >nul
exit
