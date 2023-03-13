package com.onemsg.protobuf.manager.user;

import org.junit.jupiter.api.Test;

public class UserServiceTest {
    

    @Test
    public void testGenToken() {
        String token = UserService.genToken();

        System.out.println(token);
    }
}
