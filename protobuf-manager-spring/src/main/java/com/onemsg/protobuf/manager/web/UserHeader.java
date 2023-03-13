package com.onemsg.protobuf.manager.web;

import java.util.Objects;

import org.springframework.util.StringUtils;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * UserHeader
 */
public class UserHeader {

    private UserHeader() {}

    public static final String TOKEN = "X-Auth-Token";
    
    public static final String NAME = "X-Auth-Name";

    public static final String COOKIE_TOKEN = "auth_token";

    public static final String COOKIE_NAME = "auth_name";


    @Nullable
    public static UserToken getUserToken(HttpServletRequest request) {
        String userName = request.getHeader(UserHeader.NAME);
        String userToken = request.getHeader(UserHeader.TOKEN);

        if (userName == null || userToken == null) {
            userName = getCookieValue(request, UserHeader.COOKIE_NAME);
            userToken = getCookieValue(request, UserHeader.COOKIE_TOKEN);
        }
        return new UserToken(userName, userToken);
    }

    private static String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (Objects.equals(cookie.getName(), name)) {
                return cookie.getValue();
            }
        }
        return null;
    }
    
    public record UserToken(
            String name,
            String token) {
        public boolean isEmpty() {
            return !StringUtils.hasText(name) || !StringUtils.hasText(token);
        }
    }
}