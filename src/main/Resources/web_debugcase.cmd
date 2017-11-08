set classpath=%CLASSPATH%;.\luckyclient;
@echo Web界面调试用例接口
@echo 参数说明 依次为：用例编号 执行者
java -Djava.ext.dirs=./lib;.%3 luckyclient.caserun.WebDebugExecute %1 %2
exit
