package com.onemsg.protobuf.manager.application;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onemsg.protobuf.manager.application.model.Group;
import com.onemsg.protobuf.manager.exception.DataModelResponseException;
import com.onemsg.protobuf.manager.exception.NotExistedException;
import com.onemsg.protobuf.manager.user.UserModel;
import com.onemsg.protobuf.manager.web.DataModel;
import com.onemsg.protobuf.manager.web.WebContext;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/group")
public class GroupController {

    private final GroupService groupService;

    private final Validator validator;

    @GetMapping
    public ResponseEntity<DataModel> queryAll() {
        var data = groupService.getAll();
        return ResponseEntity.ok(DataModel.ok(data));
    }

    @GetMapping("/name")
    public ResponseEntity<DataModel> getAllNames() {
        var data = groupService.getAllNames();
        return ResponseEntity.ok(DataModel.ok(data));
    }

    @PostMapping
    public ResponseEntity<DataModel> create(@RequestBody Group.CreationRequest request) {
        var errors = validator.validate(request);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }

        UserModel.Info user = WebContext.currentWebContext().getUser();
        int id = groupService.create(request.name(), request.intro(), user.name());
        return ResponseEntity.status(201).body(DataModel.createdOK(id));
    }

    @PatchMapping("{id}")
    public void updateIntro(@PathVariable int id, @RequestBody Group.UpdateIntroRequest request) {
        var errors = validator.validate(request);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }

        try {
            groupService.updateIntro(id, request.intro());
        } catch (NotExistedException e) {
            throw new DataModelResponseException(e);
        }

    }
}
