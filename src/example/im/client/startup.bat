rem -Xms64m -Xmx2048m

@echo off
setlocal & pushd
set APP_ENTRY=org.tio.examples.im.client.ImClientStarter
set BASE=%~dp0
set CP=%BASE%\config;%BASE%\lib\*
java -XX:+HeapDumpOnOutOfMemoryError -Dtio.default.read.buffer.size=4096 -XX:HeapDumpPath=c:/java-t-io-im-client-pid.hprof -cp "%CP%" %APP_ENTRY%
endlocal & popd
