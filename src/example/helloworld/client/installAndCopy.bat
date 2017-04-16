call mvn clean install

call rd ..\..\..\..\dist\examples\helloworld\client /s /q
call xcopy target\dist\tio-examples-helloworld-client-1.6.9.v20170408-RELEASE ..\..\..\..\dist\examples\helloworld\client\ /s /e /q /y

