cd ..\src\parent
call mvn clean
cd ..\..\bin
dir

cd ..\src\zoo\http\client
call mvn clean
cd ..\..\..\..\bin
dir

pause