echo off
echo -
echo #下载源代码
echo mvn dependency:sources
echo #下载源代码jar。 -DdownloadJavadocs=true 下载javadoc包
echo -DdownloadSources=true

echo -
echo #将jar解压出来
echo mvn dependency:unpack-dependencies
echo #将jar拷贝到某一目录中(所有jar在同一目录中)
echo mvn dependency:copy-dependencies -Dmdep.useRepositoryLayout=false
echo #将jar按仓库目录拷贝出来()
echo mvn dependency:copy-dependencies -Dmdep.useRepositoryLayout=true -Dmdep.copyPom=true

echo -
echo #Checking for new versions of dependencies
echo mvn versions:display-dependency-updates
echo mvn versions:set -DnewVersion=4.0.0-talent-999

cmd
