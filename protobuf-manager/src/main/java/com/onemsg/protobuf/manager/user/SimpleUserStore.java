package com.onemsg.protobuf.manager.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SimpleUserStore {
    
    private final List<SimpleUser> users = new ArrayList<>();
    
    public Optional<SimpleUser> findById(long id) {
        return users.stream().filter(u -> u.id() == id).findFirst();
    }

    public Optional<SimpleUser> findByName(String name) {
        return users.stream().filter(u -> Objects.equals(u.name(), name)).findFirst();
    }

    public void add(SimpleUser user) {
        users.add(user);
    }
}
