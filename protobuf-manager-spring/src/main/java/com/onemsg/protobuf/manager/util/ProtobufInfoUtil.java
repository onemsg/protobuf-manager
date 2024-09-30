package com.onemsg.protobuf.manager.util;

public class ProtobufInfoUtil {
    
    private ProtobufInfoUtil() {}

    public static String versionAsText(int version) {
        if (version < 0)
            return String.valueOf(version);

        var sb = new StringBuilder();
        sb.append(version);
        while (sb.length() < 3) {
            sb.insert(0, '0');
        }
        sb.insert(sb.length() - 2, ".");
        sb.insert(sb.length() - 1, ".");
        return sb.toString();
    }
}
