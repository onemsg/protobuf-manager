package com.onemsg.protobuf.manager.protobuf;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.onemsg.protobuf.manager.model.Pageable;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufCodeCreation;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufCodeCreationRequest;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufInfoCreation;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufInfoCreationRequest;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufInfoUpdateIntroRequest;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufInfoUpdateVersionRequest;
import com.onemsg.protobuf.manager.web.DataModel;
import com.onemsg.protobuf.manager.web.WebContext;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/protobuf")
public class ProtobufController {

    @Autowired
    private final ProtobufInfoService protobufService;
    
    @Autowired
    private final Validator validator;

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

    private <T> void validate(T data) {
        var errors = validator.validate(data);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }
    }
}
