call mvn clean install

call rd ..\..\..\..\dist\examples\im\server /s /q
call xcopy target\dist\tio-examples-im-server-1.6.9.v20170408-RELEASE ..\..\..\..\dist\examples\im\server\ /s /e /q /y

