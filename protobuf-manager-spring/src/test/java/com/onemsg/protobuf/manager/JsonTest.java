package com.onemsg.protobuf.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onemsg.protobuf.manager.protobuf.model.ProtobufCode;

public class JsonTest {
    
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testJson() throws JsonProcessingException {
        ProtobufCode.ItemVo vo = new ProtobufCode.ItemVo();
        vo.id = 2;
        vo.version = 103;
        String s = objectMapper.writeValueAsString(vo);
        System.out.println(s);
    }
}
