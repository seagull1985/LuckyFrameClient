#!/bin/sh
BASE_DIR="."
LIB="${BASE_DIR}/lib"
JAVA_OPTS=" -Xmx2048m -XX:PermSize=64m -XX:MaxPermSize=512m -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:logs/gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=1 -XX:GCLogFileSize=1024k -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=logs/mtdperf.hprof -server -Dfile.encoding=UTF-8"
START_CLASS="springboot.RunService"

echo ${LIB}

for libfile in ${LIB}/*.jar ; do
if [ -f $libfile ] ; then
    CLASSPATH=$libfile:${CLASSPATH}
fi
done

CLASSPATH=${BASE_DIR}:${CLASSPATH}
echo .:${CLASSPATH} ${START_CLASS}
java -cp .:${CLASSPATH} ${START_CLASS} &
