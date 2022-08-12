package com.onemsg.protobuf.manager.user;

/**
 * 用户
 */
public record UserInfo (
    long id,
    String name,
    String email,
    String password,
    String avatarUrl,
    int roleId
){}
