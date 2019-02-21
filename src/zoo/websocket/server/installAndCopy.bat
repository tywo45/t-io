call mvn -Dmaven.test.skip=true clean install

call rd ..\..\..\..\dist\examples\im\server /s /q
call xcopy target\dist\tio-core-showcase-im-server-3.2.7.v20190222-RELEASE ..\..\..\..\dist\examples\im\server\ /s /e /q /y

