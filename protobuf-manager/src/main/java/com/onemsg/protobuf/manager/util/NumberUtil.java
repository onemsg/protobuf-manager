package com.onemsg.protobuf.manager.util;

public class NumberUtil {
    
    private NumberUtil() {}

    public static int parseInt(String s, int defaultValue){

        if (s == null) return defaultValue;

        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
