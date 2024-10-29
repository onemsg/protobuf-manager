package com.onemsg.protobuf.manager.protobuf;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.onemsg.protobuf.manager.exception.DataModelResponseException;
import com.onemsg.protobuf.manager.exception.NotExistedException;
import com.onemsg.protobuf.manager.function.CheckedRunnable;
import com.onemsg.protobuf.manager.gen.GrpcClientMavenGenerator;
import com.onemsg.protobuf.manager.gen.LocalProtocRunner;
import com.onemsg.protobuf.manager.gen.MavenBaseInfo;
import com.onemsg.protobuf.manager.gen.ProtocExecException;
import com.onemsg.protobuf.manager.model.Pageable;
import com.onemsg.protobuf.manager.protobuf.model.GenerateClientJarRequest;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufCodeCreation;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufCodeCreationRequest;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufCodeRequest;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufInfoCreation;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufInfoCreationRequest;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufInfoUpdateIntroRequest;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufInfoUpdateVersionRequest;
import com.onemsg.protobuf.manager.web.DataModel;
import com.onemsg.protobuf.manager.web.WebContext;

import io.undertow.servlet.spec.HttpServletResponseImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/protobuf")
public class ProtobufController {

    @Autowired
    private ProtobufInfoService protobufService;

    @Autowired
    private LocalProtocRunner protocRunner;

    @Autowired
    private GrpcClientMavenGenerator clientGenerator;

    @Autowired
    private Validator validator;

    @Autowired
    @Qualifier("applicationTaskExecutor")
    private AsyncTaskExecutor executor;

    /**
     * 
     * @param search 搜索词，应用名|protobuf名|author名
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GetMapping()
    public ResponseEntity<DataModel> search(@RequestParam(required = false) String search, 
        @RequestParam(defaultValue = "0") int pageIndex, 
        @RequestParam(defaultValue = "20") int pageSize) {

        Pageable.valide(pageIndex, pageSize);
        var totol = protobufService.search(search, pageIndex, pageSize);
        var pageable = Pageable.create(totol, pageIndex, pageSize);
        return ResponseEntity.ok(DataModel.ok(pageable));
    }

    @GetMapping("/{id}")
    public DataModel getInfoById(@PathVariable int id) throws NotExistedException{
        var info = protobufService.getInfoById(id);
        return DataModel.ok(info);
    }

    @PostMapping("")
    public ResponseEntity<DataModel> create(@RequestBody ProtobufInfoCreationRequest request) {

        validate(request);
        var user = WebContext.currentWebContext().getUser();
        var creation = ProtobufInfoCreation.create(request, user.name());
        log.info("pojos: {} {}", request, creation);
        int id = protobufService.create(creation);
        URI uri = URI.create("/api/protobuf/" + id);
        return ResponseEntity.created(uri).body(DataModel.createdOK(id));
    }

    @PutMapping("/{id}/intro")
    public DataModel updateIntro(@PathVariable int id,
            @RequestBody ProtobufInfoUpdateIntroRequest request)
                throws NotExistedException {

        validate(request);
        protobufService.updateIntro(id, request.intro);
        return DataModel.updatedOK(id);
    }

    @PutMapping("/{id}/current-version")
    public DataModel updateCurrentVersion(@PathVariable int id, 
            @RequestBody ProtobufInfoUpdateVersionRequest request ) 
                throws NotExistedException {

        validate(request);
        protobufService.updateCurrentVersion(id, request.verison);
        return DataModel.updatedOK(id);
    }

    @GetMapping("/{id}/current-code")
    public DataModel getCurrentProtobufCode(@PathVariable int id) throws NotExistedException {
        var data = protobufService.getCurrentProtobufCodeByProtobufId(id);
        if (data == null) {
            throw new DataModelResponseException(400, 400, "Current ProtobufCode not found");
        }
        return DataModel.ok(data);
    }

    @GetMapping("/{protobufId}/code-version")
    public DataModel getProtobufCodes(@PathVariable int protobufId) 
            throws NotExistedException {
        var data = protobufService.getProtobufCodeVersionsByProtobufId(protobufId);
        return DataModel.ok(data);
    }

    @DeleteMapping("/{id}")
    public DataModel delete(@PathVariable int id) {
        protobufService.delete(id);
        return DataModel.deletedOK(id);
    }

    @PostMapping("/code")
    public DataModel createProtobufCode(@RequestBody ProtobufCodeCreationRequest request) 
            throws NotExistedException {

        validate(request);
        compileProtobufCode(request.code);

        var user = WebContext.currentWebContext().getUser();
        ProtobufCodeCreation creation = ProtobufCodeCreation.create(request, user.name());
        int id = protobufService.createProtobufCode(creation);
        return DataModel.createdOK(id);
    }

    @GetMapping("/code/{id}")
    public DataModel getProtobufCodeById(@PathVariable int id) 
            throws NotExistedException{
        var data = protobufService.getProtobufCodeById(id);
        return DataModel.ok(data);
    }

    @GetMapping("/code")
    public DataModel getProtobufCode(@RequestParam int protobufId, @RequestParam int version)
            throws NotExistedException{ 
        var data = protobufService.getProtobufCodeByProtobufIdAndVersion(protobufId, version);
        return DataModel.ok(data);
    }

    @GetMapping("/tools/compile-check")
    public DataModel compileCheck(@RequestBody ProtobufCodeRequest request) {
        if (!StringUtils.hasText(request.code())) {
            throw new DataModelResponseException(400, 400, "Protobuf code 不能为空" );
        }
        compileProtobufCode(request.code());
        return DataModel.ok("Protobuf 编译通过");
    }

    @SuppressWarnings("null")
    @GetMapping("/tools/generate-client-jar")
    public void generateClientProject(GenerateClientJarRequest request, HttpServletResponse servletResponse) throws NotExistedException {
        validate(request);

        var protobufInfo = protobufService.getInfoById(request.protobufId());
        var protobufCodeEntity = switch(request.version()) {
            case null -> protobufService.getCurrentProtobufCodeByProtobufId(request.protobufId());
            default -> protobufService.getProtobufCodeByProtobufIdAndVersion(request.protobufId(), request.version());
        };

        MavenBaseInfo mavenBaseInfo = new MavenBaseInfo();
        mavenBaseInfo.groupId = Objects.requireNonNullElse(request.groupId(), "com.onemsg.grpc.client");
        mavenBaseInfo.artifactId = Objects.requireNonNullElse(request.artifactId(), 
            protobufInfo.id + "-" + protobufInfo.name + "-client");
        mavenBaseInfo.version = protobufCodeEntity.getVersionText();
        mavenBaseInfo.name = protobufInfo.getFullName() + " client jar";

        try {
            var zipPath = clientGenerator.generateClientProject(mavenBaseInfo, protobufInfo.name, protobufCodeEntity.code);
            servletResponse.setContentType("application/octet-stream");
            servletResponse.setHeader("Content-Disposition", "attachment;filename=" + zipPath.getFileName());
            servletResponse.setStatus(HttpServletResponse.SC_OK);
            Files.copy(zipPath, servletResponse.getOutputStream());
            servletResponse.flushBuffer();
        } catch (Exception e) {
            throw new DataModelResponseException(500, 500, "生成客户端代码失败")
                .addProperty("errorDetail", e.toString());
        }
        
    }

    private void compileProtobufCode(String code) throws DataModelResponseException {

        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("protobuf-check-");
            var protoPath = Files.createTempFile(tempDir, "", ".proto");
            var outPath = Files.createDirectories(tempDir.resolve("out"));
            Files.writeString(protoPath, code);
            protocRunner.generateGrpcJavaCodes(protoPath, outPath);
        } catch (Exception e) {
            if (e instanceof ProtocExecException pe && pe.getErrorDetail() != null) {
                throw new DataModelResponseException(400, 400, "Protobuf 编译失败")
                    .addProperty("errorDetail", pe.getErrorDetail());
            }
            throw new DataModelResponseException(500, 500, "Protobuf 编译执行失败")
                .addProperty("errorDetail", e.toString());
        } finally {
            if (tempDir != null) {
                var dir = tempDir;
                submit("Delete tempDir " + dir, () -> FileSystemUtils.deleteRecursively(dir));
            }
        }
    }

    private void submit(String taskName, CheckedRunnable<?> task) {
        executor.submit(() -> {
            try {
                task.run();
            } catch (Exception e) {
               log.warn("Task run failed: {}", taskName, e);
            }
        });
    }


    private <T> void validate(T data) {
        var errors = validator.validate(data);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }
    }
}
