call mvn clean install

call rd ..\..\..\..\dist\examples\im-simple\server /s /q
call xcopy target\dist\tio-examples-im-simple-server-1.6.9.v20170408-RELEASE ..\..\..\..\dist\examples\im-simple\server\ /s /e /q /y

