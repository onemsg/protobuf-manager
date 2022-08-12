package com.onemsg.protobuf.manager.util;

import java.util.stream.Stream;

public class StringUtil {
    
    private StringUtil() {}

    public static boolean isBlank(String s){
        return s == null || s.isBlank();
    }

    public static boolean isBlank(String...s) {
        return Stream.of(s).anyMatch(StringUtil::isBlank);
    }

    public static boolean anyContains(String search, String... s) {
        return Stream.of(s).anyMatch( str -> str.contains(search));
    }

    public static String toString(Object s){
        return s == null ? null : s.toString();
    }
}
