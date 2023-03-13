package com.onemsg.protobuf.manager.user;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

@Repository
public class UserRepository {
    

    private final List<UserModel.Entity> store;

    private static final String fileName = "/user_store.json";

    @SuppressWarnings("unchecked")    
    public UserRepository(ObjectMapper objectMapper) throws Exception{
        CollectionType type = objectMapper.getTypeFactory().constructCollectionType(List.class, UserModel.Entity.class);
        var in = UserRepository.class.getResourceAsStream(fileName);
        store = (List<UserModel.Entity>) objectMapper.readValue(in, type);
        in.close();
    }

    public Optional<UserModel.Entity> findByName(String name) {
        return store.stream()
            .filter(u -> u.name().equals(name))
            .findFirst();
    }

}
