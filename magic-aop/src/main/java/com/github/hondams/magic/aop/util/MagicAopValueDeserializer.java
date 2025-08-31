package com.github.hondams.magic.aop.util;

import java.util.Map;
import java.util.function.Function;

@FunctionalInterface
public interface MagicAopValueDeserializer {

    static <T> MagicAopValueDeserializer create(Function<String, T> valueSelector) {
        return map -> valueSelector.apply((String) map.get("text"));
    }

    Object toValue(Map<String, Object> map);
}
