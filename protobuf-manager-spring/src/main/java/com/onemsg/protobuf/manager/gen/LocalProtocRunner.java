package com.onemsg.protobuf.manager.gen;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * 本地 Protoc 执行器
 */
@Slf4j
@Service
public class LocalProtocRunner {
    
    private final String protocPath;
    private final String grpcPluginPath;

    public LocalProtocRunner(@Value("${protoc.local.path}") String protocPath, 
            @Value("${protoc.local.grp-plugin.path}") String grpcPluginPath ) {
        
        if (!Paths.get(protocPath).toFile().canExecute()) {
            throw new IllegalArgumentException("protoc.local.path=" + protocPath + " 文件无效");
        }

        if (!Paths.get(grpcPluginPath).toFile().canExecute()) {
            throw new IllegalArgumentException("protoc.local.grp-plugin.path=" + grpcPluginPath + " 文件无效");
        }      
        this.protocPath = protocPath;
        this.grpcPluginPath = grpcPluginPath;
    }
    
    /**
     * 生成 grpc java 代码
     * @param sourcePath
     * @param outPath
     * @throws ProtocExecException
     */
    public void generateGrpcJavaCodes(Path sourcePath, Path outPath) throws ProtocExecException {
        String[] command = {protocPath, 
                "--plugin=protoc-gen-grpc-java=" + grpcPluginPath, 
                "--grpc-java_out=" + outPath,
                "--java_out=" + outPath,
                "--proto_path=" + sourcePath.getParent(),
                sourcePath.getFileName().toString()};
        try {
            Process runner = Runtime.getRuntime().exec(command);
            int exitCode = runner.waitFor();
            if (exitCode != 0) {
                var error = runner.inputReader().lines().collect(Collectors.joining("\n"));
                error += runner.errorReader().lines().collect(Collectors.joining("\n"));
                throw new ProtocExecException("protoc generate grpc code failed", exitCode, error);
            }
        } catch (Exception e) {
            throw new ProtocExecException("protoc 程序执行错误: " + e);
        }
    }


}
