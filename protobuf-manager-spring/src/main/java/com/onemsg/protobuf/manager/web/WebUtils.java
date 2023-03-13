package com.onemsg.protobuf.manager.web;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.Cookie;

public class WebUtils {
    

    private WebUtils() {}

    public static String toString(Cookie cookie) {
        if (cookie == null) return "";
        Map<String, String>  map = new HashMap<>(cookie.getAttributes());
        map.put(cookie.getName(), cookie.getValue());
        return map.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining(";"));
    }
}
