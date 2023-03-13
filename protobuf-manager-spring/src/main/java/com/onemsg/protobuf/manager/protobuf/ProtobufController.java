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
import com.onemsg.protobuf.manager.model.Totalable;
import com.onemsg.protobuf.manager.protobuf.model.Protobuf;
import com.onemsg.protobuf.manager.protobuf.model.Protobuf.SearchVo;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufCode;
import com.onemsg.protobuf.manager.web.DataModel;
import com.onemsg.protobuf.manager.web.WebContext;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@RestController
@RequestMapping("/api/protobuf")
public class ProtobufController {

    @Autowired
    private ProtobufService protobufService;
    
    @Autowired
    private Validator validator;

    /**
     * 
     * @param search 搜索词，应用名|protobuf名|author名
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GetMapping
    public ResponseEntity<DataModel> search(@RequestParam String search, 
        @RequestParam(defaultValue = "0") int pageIndex, 
        @RequestParam(defaultValue = "20") int pageSize) {

        Pageable.valide(pageIndex, pageSize);
        Totalable<SearchVo> totol = protobufService.search(search, pageIndex, pageSize);
        Pageable<SearchVo> pageable = Pageable.create(totol, pageIndex, pageSize);
        return ResponseEntity.ok(DataModel.ok(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataModel> getById(@PathVariable int id) {

        try {
            Protobuf.DetailVo detailVo = protobufService.getDetailById(id);
            return ResponseEntity.ok(DataModel.ok(detailVo));
        } catch (NotExistedException e) {
            throw new DataModelResponseException(e);
        }
    }

    @PostMapping
    public ResponseEntity<DataModel> create(@RequestBody Protobuf.Creation creation) {

        var errors = validator.validate(creation);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }

        var user = WebContext.currentWebContext().getUser();

        try {
            int id = protobufService.create(creation, user.name());
            URI uri = URI.create("/api/protobuf/" + id);
            return ResponseEntity.created(uri).body(DataModel.createdOK(id));
        } catch (NotExistedException e) {
            throw new DataModelResponseException(e);
        }
    }

    @PutMapping("/{id}/intro")
    public ResponseEntity<DataModel> updateIntro(@PathVariable int id, @RequestBody Protobuf.UpdateIntro updateIntro ) {

        var errors = validator.validate(updateIntro);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }
        
        try {
            protobufService.updateIntro(id, updateIntro.intro);
            return ResponseEntity.ok(DataModel.updatedOK(id));
        } catch (NotExistedException e) {
            throw new DataModelResponseException(e);
        }
    }

    @PutMapping("/{id}/current-version")
    public ResponseEntity<DataModel> updateCurrentVersion(@PathVariable int id, 
            @RequestBody Protobuf.UpdateVersion updateVersion ) {

        var errors = validator.validate(updateVersion);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }
        
        try {
            protobufService.updateCurrentVersion(id, updateVersion.version);
            return ResponseEntity.ok(DataModel.updatedOK(id));
        } catch (NotExistedException e) {
            throw new DataModelResponseException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable int id) {
        protobufService.delete(id);
        return ResponseEntity.ok(DataModel.deletedOK(id));
    }

    @PostMapping("/code")
    public ResponseEntity<DataModel> createProtobufCode(@RequestBody ProtobufCode.Creation creation) {

        var errors = validator.validate(creation);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }

        var user = WebContext.currentWebContext().getUser();

        try {
            int id = protobufService.createProtobufCode(creation, user.name());
            return ResponseEntity.ok(DataModel.createdOK(id));
        } catch (NotExistedException e) {
            throw new DataModelResponseException(e);
        }
    }

    @GetMapping("/code/{id}")
    public ResponseEntity<DataModel> getProtobufCode(@PathVariable int id) {
        try {
            var data = protobufService.getProtobufCodeById(id);
            return ResponseEntity.ok(DataModel.ok(data));
        } catch (NotExistedException e) {
            throw new DataModelResponseException(e);
        }
    }

    @GetMapping("/{protobufId}/code")
    public ResponseEntity<DataModel> getProtobufCodes(@PathVariable int protobufId) {
        try {
            var data = protobufService.getProtobufCodesByProtobufId(protobufId);
            return ResponseEntity.ok(DataModel.ok(data));
        } catch (NotExistedException e) {
            throw new DataModelResponseException(e);
        }
    }

}
