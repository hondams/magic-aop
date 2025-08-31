package com.github.hondams.magic.aop.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MagicAopJsonUtils {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public Map<String, Object> toMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
