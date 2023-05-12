package com.onemsg.authorization.model;

public class UserRole {

    public record Entity(
        int id,
        String user,
        int roleId
    ) {}

}
