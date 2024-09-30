package com.onemsg.protobuf.manager.application;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.onemsg.protobuf.manager.application.model.ApplicationCreation;
import com.onemsg.protobuf.manager.application.model.ApplicationCreationRequest;
import com.onemsg.protobuf.manager.application.model.ApplicationUpdateRequest;
import com.onemsg.protobuf.manager.application.model.GroupCreationRequest;
import com.onemsg.protobuf.manager.exception.DataModelResponseException;
import com.onemsg.protobuf.manager.exception.NotExistedException;
import com.onemsg.protobuf.manager.model.RemoveIdRequest;
import com.onemsg.protobuf.manager.user.UserModel;
import com.onemsg.protobuf.manager.web.DataModel;
import com.onemsg.protobuf.manager.web.WebContext;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@RestController
@RequestMapping("/api/application")
public class ApplicationController {

    private ApplicationService applicationService;
    private GroupService groupService;

    private Validator validator;

    @GetMapping("groupNameList")
    public DataModel getGroupNameList() {
        var names = groupService.getNameList();
        return DataModel.ok(names);
    }

    @GetMapping("applicationNameList")
    public DataModel getApplicationNameList(@RequestParam int groupId) {
        var names = applicationService.getNameListByGroupId(groupId);
        return DataModel.ok(names);
    }


    @GetMapping("groupList")
    public DataModel getGroupList() {
        var list = groupService.getAll();
        return DataModel.ok(list);
    }

    @GetMapping("applicationList")
    public DataModel getApplicationList() {
        var list = applicationService.getAll();
        return DataModel.ok(list);
    }

    @GetMapping("applicationDetail")
    public DataModel getApplication(@RequestParam int applicationId) throws NotExistedException {
        var app = applicationService.getById(applicationId);
        if (app == null) {
            throw new NotExistedException("Application", applicationId);
        }
        return DataModel.ok(app);
    }

    @PostMapping("/createGroup")
    public ResponseEntity<DataModel> createGroup(@RequestBody GroupCreationRequest request) {
        validate(request);
        UserModel.Info user = WebContext.currentWebContext().getUser();
        int newId = groupService.create(request.name, user.name());
        return ResponseEntity.status(201).body(DataModel.createdOK(newId));
    }

    @PostMapping("/createApplication")
    public ResponseEntity<DataModel> createApplication(@RequestBody ApplicationCreationRequest request) {

        validate(request);
        UserModel.Info user = WebContext.currentWebContext().getUser();
        ApplicationCreation data = ApplicationCreation.create(request);
        data.creator = user.name();
        try {
            int id = applicationService.create(data);
            return ResponseEntity.status(201).body(DataModel.createdOK(id));
        } catch (NotExistedException e) {
            throw new DataModelResponseException(e);
        }

    }

    @PatchMapping("/updateApplication")
    public DataModel updateApplication(@RequestBody ApplicationUpdateRequest request) {

        validate(request);
        try {
            applicationService.updateIntro(request.applicationId, request.zhName, request.intro);
            return DataModel.updatedOK(request.applicationId);
        } catch (NotExistedException e) {
            throw new DataModelResponseException(e);
        }
    }

    @DeleteMapping("/removeGroup")
    public ResponseEntity<DataModel> removeGroup(@RequestBody RemoveIdRequest removeIdRequest) {
        validate(removeIdRequest);

        int id = removeIdRequest.id;
        groupService.remove(id);
        return ResponseEntity.ok(DataModel.deletedOK(id));
    }

    @DeleteMapping("/removeApplication")
    public ResponseEntity<DataModel> removeApplication(@RequestBody RemoveIdRequest removeIdRequest) {
        
        validate(removeIdRequest);

        int id = removeIdRequest.id;
        applicationService.remove(id);
        return ResponseEntity.ok(DataModel.deletedOK(id));
    }

    private <T> void validate(T data) {
        var errors = validator.validate(data);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }
    }
}
