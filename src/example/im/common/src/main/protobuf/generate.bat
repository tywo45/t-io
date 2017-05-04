md .\src\c
md .\src\java
md .\src\python

protoc.exe --cpp_out=.\src\c chat.proto

protoc.exe --java_out=.\src\java chat.proto

protoc.exe --python_out=.\src\python chat.proto

pause