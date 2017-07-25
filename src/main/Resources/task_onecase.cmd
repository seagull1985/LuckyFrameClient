set classpath=%CLASSPATH%;.\luckyclient;
@echo 项目单条用例执行
@echo 参数说明 依次为：项目名称 tastId 用例编号 用例版本号 
java -Djava.ext.dirs=./lib luckyclient.caserun.OneCaseExecute %1 %2 %3
exit
