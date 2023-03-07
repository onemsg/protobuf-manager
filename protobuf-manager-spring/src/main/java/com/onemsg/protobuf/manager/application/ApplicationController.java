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
import com.onemsg.protobuf.manager.web.DataModel;

@RestController
@RequestMapping("/api/application")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

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
        try {
            int id = applicationService.create(model);
            return ResponseEntity.status(201).body(DataModel.createdOK(id));
        } catch (NotExistedException e) {
            throw new DataModelResponseException(e);
        }

    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<DataModel> deleteApplication(@PathVariable int id) {
        applicationService.remove(id);
        return ResponseEntity.ok(DataModel.deletedOK(id));
    }

}
