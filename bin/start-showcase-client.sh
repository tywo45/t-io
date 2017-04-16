#!/bin/sh
#######################################
# 变量声明
#######################################
EXAMPLE_NAME=showcase
END=client
MAIN_CLASS=ShowcaseClientStarter
APP_ENTRY=org.tio.examples.${EXAMPLE_NAME}.${END}.${MAIN_CLASS}
#echo ${APP_ENTRY}

# 进入目标路径
cd ../dist/examples/${EXAMPLE_NAME}/${END}
# 基础路径
BASE_PATH=$(pwd)
#echo ${BASE}

# classpath
CP="${BASE_PATH}"/config:"${BASE_PATH}"/lib/*:"${BASE_PATH}"/class
#echo ${CP}

# 启动java程序
java -XX:+HeapDumpOnOutOfMemoryError \
-Dtio.default.read.buffer.size=512 \
-XX:HeapDumpPath=./java-t-io-${EXAMPLE_NAME}-${END}_pid.hprof \
-cp ${CP} ${APP_ENTRY}