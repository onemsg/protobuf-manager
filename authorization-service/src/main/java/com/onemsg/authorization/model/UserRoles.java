package com.onemsg.authorization.model;

import java.util.List;

public record UserRoles (
    int id,
    String user,
    List<Role> roles
){
        
}

