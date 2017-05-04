call mvn clean install

call rd ..\..\..\..\dist\examples\helloworld\client /s /q
call xcopy target\dist\tio-examples-helloworld-client-1.7.0.v20170501-RELEASE ..\..\..\..\dist\examples\helloworld\client\ /s /e /q /y

