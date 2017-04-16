call mvn clean install

call rd ..\..\..\..\dist\examples\im\client /s /q
call xcopy target\dist\tio-examples-im-client-1.6.9.v20170408-RELEASE ..\..\..\..\dist\examples\im\client\ /s /e /q /y

