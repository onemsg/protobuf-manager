package com.onemsg.protobuf.manager.gen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GrpcClientMavenGenerator {

    private final Configuration cfg = new Configuration(Configuration.VERSION_2_3_33);

    @Autowired
    private LocalProtocRunner protocRunner;

    public static final String TEMPLATES_CLASSPATH = "templates";
    public static final String POM_TEMPLATE_NAME = "grpc-client-pom.ftl";

    public GrpcClientMavenGenerator() throws Exception {
        
        cfg.setDirectoryForTemplateLoading(new File(ClassLoader.getSystemResource(TEMPLATES_CLASSPATH).toURI()));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
    }

    public Path generateClientProject(MavenBaseInfo mavenBaseInfo, String protobufName, String protobufCode) throws Exception {

        var tempDir = Files.createTempDirectory("grpc-client-");

        var projectDir = Files.createDirectory(tempDir.resolve(mavenBaseInfo.artifactId));
        var javaDir = Files.createDirectories(projectDir.resolve("src/main/java"));
        var resourceProtoDir = Files.createDirectories(projectDir.resolve("src/main/resources/proto"));

        String protoFileName = protobufName.endsWith(".proto") ? protobufName : protobufName + ".proto";
        var protoSourceFile = Files.createFile(resourceProtoDir.resolve(protoFileName));
        Files.writeString(protoSourceFile, protobufCode);
        var pomXmlFile = Files.createFile(projectDir.resolve("pom.xml"));
        var template = cfg.getTemplate("grpc-client-pom.ftl");
        try (FileWriter out = new FileWriter(pomXmlFile.toFile())) {
            template.process(mavenBaseInfo, out);
        }

        protocRunner.generateGrpcJavaCodes(protoSourceFile.toAbsolutePath(), javaDir);
        
        var zipFile = Files.createFile(tempDir.resolve(mavenBaseInfo.artifactId + ".zip"));

        pack(projectDir, zipFile);
        return zipFile;
    }

    private static void pack(Path sourceDirPath, Path zipFilePath) throws IOException {
        if (!zipFilePath.toFile().exists()) {
            Files.createFile(zipFilePath);
        }
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
            Files.walk(sourceDirPath)
                .filter(path -> !Files.isDirectory(path))
                .forEach(path -> {
                    ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString());
                    try {
                        zs.putNextEntry(zipEntry);
                        Files.copy(path, zs);
                        zs.closeEntry();
                    } catch (IOException e) {
                        log.error("Zip pack failed {} {}",sourceDirPath, zipFilePath, e);
                    }
                });
        }
    }

}
