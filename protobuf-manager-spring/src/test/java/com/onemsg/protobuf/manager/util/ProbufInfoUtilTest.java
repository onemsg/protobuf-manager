package com.onemsg.protobuf.manager.util;

import static com.onemsg.protobuf.manager.util.ProtobufInfoUtil.versionAsText;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ProbufInfoUtilTest {
    
    @Test
    public void testVersionAsText() {
        assertEquals("-1", versionAsText(-1));
        assertEquals("0.0.0", versionAsText(0));
        assertEquals("0.0.1", versionAsText(1));
        assertEquals("0.1.1", versionAsText(11));
        assertEquals("1.1.1", versionAsText(111));
        assertEquals("11.1.1", versionAsText(1111));

    }
}
