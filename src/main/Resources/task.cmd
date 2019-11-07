title 测试任务执行【%1】 
set classpath=%CLASSPATH%;.\luckyclient;
@echo 指定任务执行
@echo 项目名称 任务ID
java -Djava.ext.dirs=./lib;.%2 luckyclient.execution.RunAutomationTest %1
@echo 当前测试任务执行窗口将在90秒后退出
ping 127.0.0.1 -n 90 >nul
exit