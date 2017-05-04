call mvn clean install

call rd ..\..\..\..\dist\examples\showcase\server /s /q
call xcopy target\dist\tio-examples-showcase-server-1.7.0.v20170501-RELEASE ..\..\..\..\dist\examples\showcase\server\ /s /e /q /y

