call mvn -Dmaven.test.skip=true clean install

call rd ..\..\..\..\dist\examples\im\server /s /q
call xcopy target\dist\tio-examples-im-server-3.2.2.v20181122-RELEASE ..\..\..\..\dist\examples\im\server\ /s /e /q /y

