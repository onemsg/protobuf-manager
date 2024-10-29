$protoc = C:\Development\proto\bin\protoc.exe

$proto_file = .\proto\hello.proto

C:\Development\protoc-22.2\bin\protoc.exe --plugin=protoc-gen-grpc-java=C:\Development\proto\plugins\protoc-gen-grpc-java.exe --grpc-java_out="java" --java_out="java" -I=".\proto\" hello.proto