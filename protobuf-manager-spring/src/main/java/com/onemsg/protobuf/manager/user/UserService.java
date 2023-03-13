package com.onemsg.protobuf.manager.user;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.onemsg.protobuf.manager.exception.DataModelResponseException;
import com.onemsg.protobuf.manager.user.UserModel.TokenUser;

import jakarta.annotation.Nullable;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final Cache<String, TokenUser> session = Caffeine.newBuilder()
        .expireAfterWrite(3, TimeUnit.DAYS)
        .expireAfterAccess(1, TimeUnit.DAYS)
        .build();

    public TokenUser login(String name, String password) throws DataModelResponseException {
        
        var userOptainal = userRepository.findByName(name);

        if (userOptainal.isPresent() && Objects.equals(userOptainal.get().password(), password)) {
            var user = UserModel.Info.create(userOptainal.get());
            String newToken = genToken();
            TokenUser userToken = new TokenUser(newToken, user);
            session.put(name, userToken);
            return userToken;
        } else {
            throw DataModelResponseException.AUTHENTICATION_FAILURE;
        }
    }
    
    public void logout(String name, String token) throws DataModelResponseException {
        if (authenticate(name, token)) {
            session.invalidate(name);
        } else {
            throw new DataModelResponseException(401, 401, "User not logged in");
        }
    }

    public boolean authenticate(String name, String token) {
        if (name == null || name.isBlank()) return false;
        if (token == null || token.isBlank()) return false;
        var userToken = session.getIfPresent(name);
        return userToken != null && Objects.equals(userToken.token(), token);
    }

    @Nullable
    public UserModel.Info getInfo(String name, String token) {
        if (authenticate(name, token)) {
            return session.getIfPresent(name).info();
        } else {
            return null;
        }
    }

    public TokenUser refreshToken(String name, String token) throws DataModelResponseException{
        if (authenticate(name, token)) {
            TokenUser oldUserToken = session.getIfPresent(name);
            String newToken = genToken();
            TokenUser newUserToken = new TokenUser(newToken, oldUserToken.info());
            session.put(name, newUserToken);
            return newUserToken;
        } else {
            throw DataModelResponseException.AUTHENTICATION_FAILURE;
        }
    }

    static String genToken() {
        return ThreadLocalRandom.current().ints(1, 256)
            .limit(10)
            .mapToObj(n -> Integer.toString(n, 36))
            .collect(Collectors.joining());
    }
 
}
