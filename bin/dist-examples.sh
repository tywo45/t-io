#!/bin/sh
#######################################
# 如果文件夹不存在 创建文件夹
#######################################
function createDir(){
 if [ ! -d $1 ]
 then
  /bin/mkdir -p $1 >/dev/null 2>&1 && echo "Directory $1 created." ||  echo "Error: Failed to create $1 directory."
 else
  echo "Error: $1 directory exits!"
 fi
}
#######################################
# 当前脚本所在路径
#######################################
CURRENT_PATH=$(pwd)
#echo ${CURRENT_PATH}

cd ../src/parent && mvn clean install
cd ${CURRENT_PATH}

#######################################
# example和end配置 TODO:如果新增了example 直接在EXAMPLE_NAMES里增加example的名字即可 用空格分开
#######################################
EXAMPLE_NAMES=(im im-simple helloworld showcase)
ENDS=(server client)

#######################################
# 循环拷贝所有的example的客户端和服务端
#######################################
for i in "${!EXAMPLE_NAMES[@]}"; do
    for j in "${!ENDS[@]}"; do
        EXAMPLE_NAME=${EXAMPLE_NAMES[$i]}
        END=${ENDS[$j]}
#        echo ${EXAMPLE_NAME} ${END}
        # 源文件目录
        SOURCE_DIR=../src/example/${EXAMPLE_NAME}/${END}/target/dist/
#        echo ${SOURCE_DIR}
        # dist目录下example路径
        DIST_EXAMPLE_DIR=../dist/examples/${EXAMPLE_NAME}/${END}/
#        echo ${DIST_EXAMPLE_DIR}
        if [ ! -d "${DIST_EXAMPLE_DIR}" ]; then
          createDir "${DIST_EXAMPLE_DIR}"
        fi

#        echo `ls ${SOURCE_DIR}`
        for dir in `ls ${SOURCE_DIR}`
        do
                # 筛选文件夹 如果是文件夹就将文件夹下的内容拷贝到dist
                if [ -d "${SOURCE_DIR}${dir}" ];
                then
                    cd "${SOURCE_DIR}${dir}"
                    cp -r . ../../../../../../${DIST_EXAMPLE_DIR}
                    cd ${CURRENT_PATH}
                fi
        done
    done
done