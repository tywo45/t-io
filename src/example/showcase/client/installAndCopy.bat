call mvn clean install

call rd ..\..\..\..\dist\examples\showcase\client /s /q
call xcopy target\dist\tio-examples-showcase-client-1.7.0.1.v20170601-RELEASE ..\..\..\..\dist\examples\showcase\client\ /s /e /q /y

