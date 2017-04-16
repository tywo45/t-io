call mvn clean install

call rd ..\..\..\..\dist\examples\showcase\client /s /q
call xcopy target\dist\tio-examples-showcase-client-1.6.9.v20170408-RELEASE ..\..\..\..\dist\examples\showcase\client\ /s /e /q /y

