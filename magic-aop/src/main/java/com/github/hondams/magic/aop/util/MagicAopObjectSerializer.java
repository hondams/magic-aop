package com.github.hondams.magic.aop.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MagicAopObjectSerializer {

    private static final Map<Class<?>, MagicAopValueSerializer> valueSerializerMap = new ConcurrentHashMap<>();

    static {
        valueSerializerMap.put(Character.class, MagicAopValueSerializer.DEFAULT);
        valueSerializerMap.put(Byte.class, MagicAopValueSerializer.DEFAULT);
        valueSerializerMap.put(Short.class, MagicAopValueSerializer.DEFAULT);
        valueSerializerMap.put(Long.class, MagicAopValueSerializer.DEFAULT);
        valueSerializerMap.put(Float.class, MagicAopValueSerializer.DEFAULT);
        valueSerializerMap.put(BigDecimal.class, MagicAopValueSerializer.DEFAULT);
        valueSerializerMap.put(BigInteger.class, MagicAopValueSerializer.DEFAULT);
        valueSerializerMap.put(Locale.class, MagicAopValueSerializer.DEFAULT);
        valueSerializerMap.put(LocalDateTime.class, MagicAopValueSerializer.DEFAULT);
        valueSerializerMap.put(LocalTime.class, MagicAopValueSerializer.DEFAULT);
        valueSerializerMap.put(LocalDate.class, MagicAopValueSerializer.DEFAULT);
        valueSerializerMap.put(ZonedDateTime.class, MagicAopValueSerializer.DEFAULT);
        valueSerializerMap.put(OffsetDateTime.class, MagicAopValueSerializer.DEFAULT);
        valueSerializerMap.put(java.util.Date.class,
            MagicAopValueSerializer.create((java.util.Date obj) -> DateUtils.toString(obj)));
        valueSerializerMap.put(java.sql.Date.class, MagicAopValueSerializer.DEFAULT);
        valueSerializerMap.put(java.sql.Time.class, MagicAopValueSerializer.DEFAULT);
        valueSerializerMap.put(java.sql.Timestamp.class,
            MagicAopValueSerializer.create((java.sql.Timestamp obj) -> DateUtils.toString(obj)));
        valueSerializerMap.put(UUID.class, MagicAopValueSerializer.DEFAULT);
        valueSerializerMap.put(URL.class, MagicAopValueSerializer.DEFAULT);
        valueSerializerMap.put(URI.class, MagicAopValueSerializer.DEFAULT);
        valueSerializerMap.put(byte[].class,//
            MagicAopValueSerializer.create(ByteArrayUtils::toHex));
    }

    public static void registerValueSerializer(Class<?> valueType,
        MagicAopValueSerializer valueSerializer) {
        valueSerializerMap.put(valueType, valueSerializer);
    }

    private final Map<Object, Integer> objectIdMap = new IdentityHashMap<>();

    public Object serialize(Object obj) {
        if (obj == null) {
            return null;
        }
        Object rawValue = toRawValue(obj);
        if (rawValue != null) {
            // raw value
            return rawValue;
        }
        Map<String, Object> valueMap = toValueMap(obj);
        if (valueMap != null) {
            // @value=, text=
            return valueMap;
        }
        if (obj.getClass().isArray()) {
            // @array=, items=
            return toArrayMap(obj);
        } else if (obj instanceof List<?>) {
            // @list=, items=
            return toListMap((List<?>) obj);
        } else if (obj instanceof Set<?>) {
            // @set=, items=
            return toSetMap((Set<?>) obj);
        } else if (obj instanceof Map<?, ?>) {
            // @map=, entries={key=,value=}
            return toMapMap((Map<?, ?>) obj);
        } else if (obj instanceof Collection<?>) {
            // @collection=, items=
            return toCollectionMap((Collection<?>) obj);
        } else {
            // @class=, @id=, @refid=, fieldName=
            return toObjectMap(obj);
        }
    }

    private Object toRawValue(Object obj) {
        if (obj instanceof String) {
            return obj;
        } else if (obj instanceof Boolean) {
            return obj;
        } else if (obj instanceof Integer) {
            return obj;
        } else if (obj instanceof Double) {
            return obj;
        }
        return null;
    }

    private Map<String, Object> toValueMap(Object obj) {
        MagicAopValueSerializer valueSerializer = valueSerializerMap.get(obj.getClass());
        if (valueSerializer != null) {
            return valueSerializer.toValueMap(obj);
        }
        if (obj instanceof Enum<?>) {
            return MagicAopValueSerializer.ENUM.toValueMap(obj);
        }
        return null;
    }

    private Map<String, Object> toArrayMap(Object obj) {
        Map<String, Object> map = new LinkedHashMap<>();
        int length = Array.getLength(obj);
        map.put("@array", obj.getClass().getComponentType().getName());
        map.put("@length", length);
        List<Object> items = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Object item = Array.get(obj, i);
            items.add(serialize(item));
        }
        map.put("items", items);
        return map;
    }

    private Map<String, Object> toListMap(List<?> obj) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (isUnmodifiable(obj)) {
            map.put("@list", getOriginalCollection(obj).getClass().getName());
            map.put("@readonly", obj.getClass().getName());
        } else {
            map.put("@list", obj.getClass().getName());
        }

        List<Object> items = new ArrayList<>();
        for (Object item : obj) {
            items.add(serialize(item));
        }
        map.put("items", items);
        return map;
    }

    private Map<String, Object> toSetMap(Set<?> obj) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (isUnmodifiable(obj)) {
            map.put("@set", getOriginalCollection(obj).getClass().getName());
            map.put("@readonly", obj.getClass().getName());
        } else {
            map.put("@set", obj.getClass().getName());
        }

        List<Object> items = new ArrayList<>();
        for (Object item : obj) {
            items.add(serialize(item));
        }
        map.put("items", items);
        return map;
    }

    private Map<String, Object> toCollectionMap(Collection<?> obj) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (isUnmodifiable(obj)) {
            map.put("@collection", getOriginalCollection(obj).getClass().getName());
            map.put("@readonly", obj.getClass().getName());
        } else {
            map.put("@collection", obj.getClass().getName());
        }

        List<Object> items = new ArrayList<>();
        for (Object item : obj) {
            items.add(serialize(item));
        }
        map.put("items", items);
        return map;
    }

    private Map<String, Object> toMapMap(Map<?, ?> obj) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (isUnmodifiable(obj)) {
            map.put("@map", getOriginalMap(obj).getClass().getName());
            map.put("@readonly", obj.getClass().getName());
        } else {
            map.put("@map", obj.getClass().getName());
        }

        List<Map<String, Object>> entries = new ArrayList<>();
        for (Map.Entry<?, ?> entry : obj.entrySet()) {
            Map<String, Object> entryMap = new LinkedHashMap<>();
            entryMap.put("key", serialize(entry.getKey()));
            entryMap.put("value", serialize(entry.getValue()));
            entries.add(entryMap);
        }
        map.put("entries", entries);
        return map;
    }

    private Map<String, Object> toObjectMap(Object obj) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("@class", obj.getClass().getName());
        if (this.objectIdMap.containsKey(obj)) {
            map.put("@refid", this.objectIdMap.get(obj));
            return map;
        } else {
            int id = this.objectIdMap.size() + 1;
            this.objectIdMap.put(obj, id);
            map.put("@id", id);
            Map<String, Field> fieldMap = MagicAopReflectionUtils.getInstanceFieldMap(
                obj.getClass());
            for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
                String fieldName = entry.getKey();
                if (!fieldName.contains(":")) {
                    Field field = entry.getValue();
                    Object fieldValue = MagicAopReflectionUtils.getInstanceFieldValue(obj, field);
                    map.put(fieldName, serialize(fieldValue));
                }
            }
            for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
                String fieldName = entry.getKey();
                if (fieldName.contains(":")) {
                    Field field = entry.getValue();
                    Object fieldValue = MagicAopReflectionUtils.getInstanceFieldValue(obj, field);
                    map.put(fieldName, serialize(fieldValue));
                }
            }
        }
        return map;
    }

    private Map<?, ?> getOriginalMap(Map<?, ?> obj) {
        Class<?> unmodifiableClass = ClassUtils.findSuperClass(obj.getClass(),
            "java.util.Collections$UnmodifiableMap");
        if (unmodifiableClass == null) {
            throw new IllegalStateException("Not an unmodifiable map: " + obj.getClass().getName());
        }
        try {
            Field field = unmodifiableClass.getDeclaredField("m");
            return (Map<?, ?>) MagicAopReflectionUtils.getInstanceFieldValue(obj, field);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(
                "Failed to get original map from unmodifiable: " + obj.getClass().getName(), e);
        }
    }


    private Collection<?> getOriginalCollection(Collection<?> obj) {
        Class<?> unmodifiableClass = ClassUtils.findSuperClass(obj.getClass(),
            "java.util.Collections$UnmodifiableCollection");
        if (unmodifiableClass == null) {
            throw new IllegalStateException(
                "Not an unmodifiable collection: " + obj.getClass().getName());
        }
        try {
            Field field = unmodifiableClass.getDeclaredField("c");
            return (Collection<?>) MagicAopReflectionUtils.getInstanceFieldValue(obj, field);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(
                "Failed to get original collection from unmodifiable: " + obj.getClass().getName(),
                e);
        }
    }

    private boolean isUnmodifiable(Object obj) {
        String className = obj.getClass().getName();
        return "java.util.Collections$UnmodifiableRandomAccessList".equals(className)
            || "java.util.Collections$UnmodifiableList".equals(className)
            || "java.util.Collections$UnmodifiableMap".equals(className)
            || "java.util.Collections$UnmodifiableSet".equals(className)
            || "java.util.Collections$UnmodifiableSortedSet".equals(className)
            || "java.util.Collections$UnmodifiableNavigableSet".equals(className)
            || "java.util.Collections$UnmodifiableCollection".equals(className);
    }
}
