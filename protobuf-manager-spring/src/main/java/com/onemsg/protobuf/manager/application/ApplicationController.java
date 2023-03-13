package com.onemsg.protobuf.manager.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.onemsg.protobuf.manager.application.model.Application;
import com.onemsg.protobuf.manager.exception.DataModelResponseException;
import com.onemsg.protobuf.manager.exception.NotExistedException;
import com.onemsg.protobuf.manager.user.UserModel;
import com.onemsg.protobuf.manager.web.DataModel;
import com.onemsg.protobuf.manager.web.WebContext;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/application")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private Validator validator;

    @GetMapping("/name")
    public ResponseEntity<DataModel> getNamesByGroupId(@RequestParam int groupId) {
        try {
            var data = applicationService.getNamesByGroupId(groupId);
            return ResponseEntity.ok(DataModel.ok(data));
        } catch (NotExistedException e) {
            throw new DataModelResponseException(e);
        }
    }

    @GetMapping("/groups")
    public ResponseEntity<DataModel> getAllGroupNames() {
        var data = applicationService.getAllGroupNames();
        return ResponseEntity.ok(DataModel.ok(data));
    }

    @PostMapping
    public ResponseEntity<DataModel> create(@RequestBody Application.Creation model) {

        var errors = validator.validate(model);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }

        UserModel.Info user = WebContext.currentWebContext().getUser();

        try {
            int id = applicationService.create(model, user.name());
            return ResponseEntity.status(201).body(DataModel.createdOK(id));
        } catch (NotExistedException e) {
            throw new DataModelResponseException(e);
        }

    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<DataModel> deleteApplication(@PathVariable int id) {
        
        if (id < 0) {
            throw new DataModelResponseException(400, 400, "Bad request");
        }

        applicationService.remove(id);
        return ResponseEntity.ok(DataModel.deletedOK(id));
    }

}
