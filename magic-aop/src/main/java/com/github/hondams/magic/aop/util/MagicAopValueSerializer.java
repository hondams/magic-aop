package com.github.hondams.magic.aop.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@FunctionalInterface
public interface MagicAopValueSerializer {

    MagicAopValueSerializer DEFAULT = create(Object::toString);
    MagicAopValueSerializer ENUM = create((Enum<?> obj) -> obj.name());

    @SuppressWarnings("unchecked")
    static <T> MagicAopValueSerializer create(Function<T, String> textSelector) {
        return obj -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("@value", obj.getClass().getName());
            map.put("text", textSelector.apply((T) obj));
            return map;
        };
    }

    Map<String, Object> toValueMap(Object obj);
}
