call mvn clean install

call rd ..\..\..\..\dist\examples\helloworld\server /s /q
call xcopy target\dist\tio-examples-helloworld-server-1.7.0.v20170501-RELEASE ..\..\..\..\dist\examples\helloworld\server\ /s /e /q /y

