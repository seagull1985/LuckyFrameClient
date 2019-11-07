title 用例批量执行【%1】 
set classpath=%CLASSPATH%;.\luckyclient;
@echo 批量执行用例
@echo 参数说明 依次为：项目名称 tastId 用例集 
java -Djava.ext.dirs=./lib;.%3 luckyclient.execution.BatchCaseExecute %1 %2
@echo 当前用例批量执行窗口将在90秒后退出
ping 127.0.0.1 -n 90 >nul
exit
