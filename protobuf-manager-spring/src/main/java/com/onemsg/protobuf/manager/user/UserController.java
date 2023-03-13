package com.onemsg.protobuf.manager.user;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onemsg.protobuf.manager.exception.DataModelResponseException;
import com.onemsg.protobuf.manager.user.UserModel.TokenUser;
import com.onemsg.protobuf.manager.web.DataModel;
import com.onemsg.protobuf.manager.web.UserHeader;
import com.onemsg.protobuf.manager.web.UserHeader.UserToken;
import com.onemsg.protobuf.manager.web.WebContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@RequestMapping("/api")
@RestController
public class UserController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private Validator validator;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserModel.Login user, HttpServletRequest request) {
        UserToken userToken = UserHeader.getUserToken(request);
        if (userService.authenticate(userToken.name(), userToken.token()))  {
            throw new DataModelResponseException(200, 1, "User has logged in");   
        }

        var errors = validator.validate(user);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }

        TokenUser tokenUser = userService.login(user.name(), user.password());

        ResponseCookie nameCookie = ResponseCookie.from(UserHeader.COOKIE_NAME, tokenUser.info().name())
            .maxAge(Duration.ofDays(1))
            .sameSite("Strict")
            .build();

        ResponseCookie tokenCookie = ResponseCookie.from(UserHeader.COOKIE_TOKEN, tokenUser.token())
                .maxAge(Duration.ofDays(1))
                .sameSite("Strict")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, nameCookie.toString() );
        headers.add(HttpHeaders.SET_COOKIE, tokenCookie.toString());

        return ResponseEntity.ok().headers(headers).body(DataModel.ok(tokenUser));
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request) {
        UserToken userToken = UserHeader.getUserToken(request);
        userService.logout(userToken.name(), userToken.token());
        return ResponseEntity.ok(DataModel.ok("Logged out"));
    }

    @GetMapping("/user/info")
    public ResponseEntity<Object> info() {
        var userInfo = WebContext.currentWebContext().user;
        return ResponseEntity.ok(DataModel.ok(userInfo));
    }

}
