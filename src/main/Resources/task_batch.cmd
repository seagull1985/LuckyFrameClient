set classpath=%CLASSPATH%;.\luckyclient;
@echo 批量执行用例
@echo 参数说明 依次为：项目名称 tastId 用例集 
java -Djava.ext.dirs=./lib luckyclient.caserun.BatchCaseExecute %1 %2
exit
