package com.onemsg.protobuf.manager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.onemsg.protobuf.manager.user.UserService;
import com.onemsg.protobuf.manager.web.WebContextInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private UserService userService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new WebContextInterceptor(userService))
            .addPathPatterns("/api/**")
            .excludePathPatterns("/api/login", "/api/logout");
    }
}
