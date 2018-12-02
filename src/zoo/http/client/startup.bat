rem -Xms64m -Xmx2048m

@echo off
setlocal & pushd
set APP_ENTRY=org.tio.http.client.HttpClientStarter
set BASE=%~dp0
set CP=%BASE%\config;%BASE%\lib\*
java -server -Xverify:none -Xms1G -Xmx1G -cp "%CP%" %APP_ENTRY%
endlocal & popd



