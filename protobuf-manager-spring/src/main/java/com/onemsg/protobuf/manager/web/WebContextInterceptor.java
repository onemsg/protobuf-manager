package com.onemsg.protobuf.manager.web;


import java.util.Objects;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.onemsg.protobuf.manager.exception.DataModelResponseException;
import com.onemsg.protobuf.manager.user.UserService;
import com.onemsg.protobuf.manager.web.UserHeader.UserToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Web context 拦截器
 * 
 * @author mashuguang
 * @since 2022-05
 */
public class WebContextInterceptor implements HandlerInterceptor {

    private final UserService userService;

    public WebContextInterceptor(UserService userService) {
        this.userService = Objects.requireNonNull(userService);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        UserToken userToken = UserHeader.getUserToken(request);

        var user = userService.getInfo(userToken.name(), userToken.token());
        if (user == null) {
            throw DataModelResponseException.AUTHENTICATION_FAILURE;
        }

        // 存放到 ThreadLocal 中，便于在此请求处理过程中获取
        WebContext.setWebContext(new WebContext(user));
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable ModelAndView modelAndView) throws Exception {
        WebContext.resetWebContext();
    }

}
