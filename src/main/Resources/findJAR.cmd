set classpath=%CLASSPATH%;.\lib;.\bin;
@echo 查找重复jar包，输入包路径或是直接类名
@echo path="D:\\web_task\\TestFrame\\lib\\"
java -D luckyclient.publicclass.JarClassFind "D:\\web_task\\TestFrame\\lib\\" "TestLinkAPI"
pause