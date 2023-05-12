package com.onemsg.authorization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onemsg.authorization.service.RoleService;


@RestController
@RequestMapping("/api/management")
public class ManagementController {
    
    @Autowired
    private RoleService roleService;

    @GetMapping("/user-roles")
    public ResponseEntity<Object> queryUserRoles() {
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/user-roles")
    public ResponseEntity<Object> addUserRoles() {
        return ResponseEntity.ok("OK");
    }

    @DeleteMapping("/user-roles")
    public ResponseEntity<Object> removeUserRoles() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/roles")
    public ResponseEntity<Object> queryRoles() {
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/roles")
    public ResponseEntity<Object> createRole() {
        return ResponseEntity.ok("OK");
    }

    @DeleteMapping("/roles")
    public ResponseEntity<Object> removeRole() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/roles/perms")
    public ResponseEntity<Object> queryRolePerms() {
        return ResponseEntity.ok("OK");
    }

    @PutMapping("/roles/perms")
    public ResponseEntity<Object> addRolePerms() {
        return ResponseEntity.ok("OK");
    }

    @DeleteMapping("/roles/perms")
    public ResponseEntity<Object> removeRolePerms() {
        return ResponseEntity.ok("OK");
    }


    @GetMapping("/resources")
    public ResponseEntity<Object> queryResources() {
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/resources")
    public ResponseEntity<Object> createResource() {
        return ResponseEntity.ok("OK");
    }

    @DeleteMapping("/resources")
    public ResponseEntity<Object> removeResource() {
        return ResponseEntity.ok("OK");
    }

}
