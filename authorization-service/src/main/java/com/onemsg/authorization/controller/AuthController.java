package com.onemsg.authorization.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authorization")
public class AuthController {
    
    @GetMapping("roles")
    public ResponseEntity<Object> getRoles() {
        return null;
    }

    @GetMapping("perms")
    public ResponseEntity<Object> getPerms() {
        return null;
    }
}
