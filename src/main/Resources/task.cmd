set classpath=%CLASSPATH%;.\luckyclient;
@echo 指定任务执行
@echo 项目名称 任务ID
java -Djava.ext.dirs=./lib;.%2 luckyclient.caserun.RunAutomationTest %1
exit