call mvn clean install

call rd ..\..\..\..\dist\examples\im-simple\server /s /q
call xcopy target\dist\tio-examples-im-simple-server-1.7.0.1.v20170601-RELEASE ..\..\..\..\dist\examples\im-simple\server\ /s /e /q /y

