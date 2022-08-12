package com.onemsg.protobuf.manager.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 用户信息存储
 */
public class UserStore {
    
    private static final List<UserInfo> store = new ArrayList<>();

    static {
        store.add(new UserInfo(1, "xiaoming", "xiaoming@qq.com", "xiaoming123", null, 0));
        store.add(new UserInfo(2, "liuxing", "liuxing@qq.com", "liuxing123", null, 0));
        store.add(new UserInfo(3, "lihua", "lihua@qq.com", "lihua123", null, 0));
        store.add(new UserInfo(4, "wukong", "wukong@qq.com", "wukong123", null, 0));
        store.add(new UserInfo(5, "lidehua", "lidehua@qq.com", "lidehua123", null, 0));
    }

    public UserInfo findUserWithId(long id){
        return store.stream().filter(user -> user.id() == id).findFirst().orElse(null);
    }

    public UserInfo findUserWithEmail(String email){
        return store.stream().filter(user -> Objects.equals(user.email(), email)).findFirst().orElse(null);
    }    
}
