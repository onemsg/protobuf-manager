package com.onemsg.protobuf.manager.user;

import jakarta.validation.constraints.NotBlank;

/**
 * For web cookies: user_token, user_name
 */
public class UserModel {
    
    private UserModel() {}


    public record Info(
        int id,
        String name,
        String avatarUrl
    ) {

        public static Info create(Entity entity) {
            return new Info(entity.id, entity.name , entity.avatarUrl);
        }
    }

    public record Entity(
        int id,
        String name,
        String password,
        String avatarUrl
    ) {
    }

    public record Login(
        @NotBlank
        String name,
        @NotBlank
        String password
    ) {
    }

    record TokenUser(
            String token,
            UserModel.Info info) {
    }

}
