cd ..\src\parent
call mvn clean install
cd ..\..


cd .\src\example\im\server
call dir
call installAndCopy.bat
cd ..\..\..\..

cd .\src\example\im\client
call installAndCopy.bat
cd ..\..\..\..



cd .\src\example\im-simple\server
call dir
call installAndCopy.bat
cd ..\..\..\..

cd .\src\example\im-simple\client
call installAndCopy.bat
cd ..\..\..\..



cd .\src\example\showcase\server
call installAndCopy.bat
cd ..\..\..\..

cd .\src\example\showcase\client
call installAndCopy.bat
cd ..\..\..\..



cd .\src\example\helloworld\server
call installAndCopy.bat
cd ..\..\..\..

cd .\src\example\helloworld\client
call installAndCopy.bat
cd ..\..\..\..
