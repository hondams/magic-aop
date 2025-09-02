package com.github.hondams.magic.aop.util;

import com.github.hondams.magic.aop.logger.MagicAopLogger;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MagicAopObjectDeserializer {

    private static final Map<String, MagicAopValueDeserializer> valueDeserializerMap = new ConcurrentHashMap<>();

    static {
        valueDeserializerMap.put(Character.class.getName(),//
            MagicAopValueDeserializer.create(text -> text.charAt(0)));
        valueDeserializerMap.put(Byte.class.getName(),//
            MagicAopValueDeserializer.create(Byte::valueOf));
        valueDeserializerMap.put(Short.class.getName(),//
            MagicAopValueDeserializer.create(Short::valueOf));
        valueDeserializerMap.put(Long.class.getName(),//
            MagicAopValueDeserializer.create(Long::valueOf));
        valueDeserializerMap.put(Float.class.getName(),//
            MagicAopValueDeserializer.create(Float::valueOf));
        valueDeserializerMap.put(BigDecimal.class.getName(),//
            MagicAopValueDeserializer.create(BigDecimal::new));
        valueDeserializerMap.put(BigInteger.class.getName(),//
            MagicAopValueDeserializer.create(BigInteger::new));
        valueDeserializerMap.put(Locale.class.getName(),//
            MagicAopValueDeserializer.create(LocaleUtils::create));
        valueDeserializerMap.put(LocalDateTime.class.getName(),//
            MagicAopValueDeserializer.create(DateUtils::toLocalDateTime));
        valueDeserializerMap.put(LocalTime.class.getName(),//
            MagicAopValueDeserializer.create(DateUtils::toLocalTime));
        valueDeserializerMap.put(LocalDate.class.getName(),//
            MagicAopValueDeserializer.create(DateUtils::toLocalDate));
        valueDeserializerMap.put(ZonedDateTime.class.getName(),//
            MagicAopValueDeserializer.create(DateUtils::toZonedDateTime));
        valueDeserializerMap.put(OffsetDateTime.class.getName(),//
            MagicAopValueDeserializer.create(DateUtils::toOffsetDateTime));
        valueDeserializerMap.put(java.util.Date.class.getName(),//
            MagicAopValueDeserializer.create(DateUtils::toDate));
        valueDeserializerMap.put(java.sql.Date.class.getName(),//
            MagicAopValueDeserializer.create(DateUtils::toSqlDate));
        valueDeserializerMap.put(java.sql.Time.class.getName(),//
            MagicAopValueDeserializer.create(DateUtils::toSqlTime));
        valueDeserializerMap.put(java.sql.Timestamp.class.getName(),//
            MagicAopValueDeserializer.create(DateUtils::toSqlTimestamp));
        valueDeserializerMap.put(UUID.class.getName(),//
            MagicAopValueDeserializer.create(UUID::fromString));
        valueDeserializerMap.put(URL.class.getName(),//
            MagicAopValueDeserializer.create(UrlUtils::toUrl));
        valueDeserializerMap.put(URI.class.getName(),//
            MagicAopValueDeserializer.create(URI::create));
        valueDeserializerMap.put(byte[].class.getName(),//
            MagicAopValueDeserializer.create(ByteArrayUtils::fromHex));
    }

    public static void registerValueSerializer(Class<?> valueType,
        MagicAopValueDeserializer valueDeserializer) {
        valueDeserializerMap.put(valueType.getName(), valueDeserializer);
    }

    private final Map<Integer, Object> idObjectMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public Object deserialize(Object obj) {
        if (obj == null) {
            return null;
        }
        Object rawValue = toRawValue(obj);
        if (rawValue != null) {
            // raw value
            return rawValue;
        }
        if (obj instanceof Map<?, ?>) {
            Map<String, Object> map = (Map<String, Object>) obj;
            if (map.containsKey("@value")) {
                // @value=, text=
                return toValue(map);
            } else if (map.containsKey("@array")) {
                // @array=, items=
                return toArray(map);
            } else if (map.containsKey("@list")) {
                // @list=, items=
                return toList(map);
            } else if (map.containsKey("@set")) {
                // @set=, items=
                return toSet(map);
            } else if (map.containsKey("@map")) {
                // @map=, entries={key=,value=}
                return toMap(map);
            } else if (map.containsKey("@collection")) {
                // @collection=, items=
                return toCollection(map);
            } else if (map.containsKey("@class")) {
                // @class=, @id=, @refid=, fieldName=
                return toObject(map);
            } else {
                throw new IllegalArgumentException("Invalid map: " + map);
            }
        } else {
            throw new IllegalArgumentException("Invalid object: " + obj);
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object toValue(Map<String, Object> map) {
        String valueTypeName = (String) map.get("@value");
        String text = (String) map.get("text");

        MagicAopValueDeserializer valueDeserializer = valueDeserializerMap.get(valueTypeName);
        if (valueDeserializer != null) {
            return valueDeserializer.toValue(map);
        }

        Class<?> valueType = ClassUtils.getClass(valueTypeName);
        if (valueType.isEnum()) {
            return Enum.valueOf((Class<? extends Enum>) valueType, text);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Object toArray(Map<String, Object> map) {
        String arrayTypeName = (String) map.get("@array");
        int length = (int) map.get("@length");
        List<Object> items = (List<Object>) map.get("@items");

        Class<?> arrayType = ClassUtils.getClass(arrayTypeName);
        Object obj = Array.newInstance(arrayType, length);

        int index = 0;
        for (Object item : items) {
            Object value = deserialize(item);
            Array.set(obj, index, value);
            index++;
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    private List<?> toList(Map<String, Object> map) {
        String listTypeName = (String) map.get("@list");
        String readonly = (String) map.get("@readonly");
        List<Object> items = (List<Object>) map.get("@items");

        Class<?> listType = ClassUtils.getClass(listTypeName);
        List<Object> obj = (List<Object>) ClassUtils.tryNewInstance(listType);
        if (obj == null) {
            obj = new ArrayList<>();
            MagicAopLogger.warn(
                "Failed to create instance of list: " + listTypeName + ", use ArrayList instead.");
        }
        String errorMessage = null;
        for (Object item : items) {
            Object value = deserialize(item);
            try {
                obj.add(value);
            } catch (Exception e) {
                errorMessage = e.getMessage();
                break;
            }
        }
        if (errorMessage != null) {
            MagicAopLogger.warn("Failed to to add item to list: " + listTypeName
                + ", use ArrayList instead. error: " + errorMessage);
            obj = new ArrayList<>();
            for (Object item : items) {
                Object value = deserialize(item);
                obj.add(value);
            }
        }
        if (readonly != null) {
            obj = Collections.unmodifiableList(obj);
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    private Set<?> toSet(Map<String, Object> map) {
        String setTypeName = (String) map.get("@set");
        String readonly = (String) map.get("@readonly");
        List<Object> items = (List<Object>) map.get("@items");

        Class<?> setType = ClassUtils.getClass(setTypeName);
        Set<Object> obj = (Set<Object>) ClassUtils.tryNewInstance(setType);
        if (obj == null) {
            obj = new LinkedHashSet<>();
            MagicAopLogger.warn("Failed to create instance of set: " + setTypeName
                + ", use LinkedHashSet instead.");
        }
        String errorMessage = null;
        for (Object item : items) {
            Object value = deserialize(item);
            try {
                obj.add(value);
            } catch (Exception e) {
                errorMessage = e.getMessage();
                break;
            }
        }
        if (errorMessage != null) {
            MagicAopLogger.warn("Failed to to add item to set: " + setTypeName
                + ", use LinkedHashSet instead. error: " + errorMessage);
            obj = new LinkedHashSet<>();
            for (Object item : items) {
                Object value = deserialize(item);
                obj.add(value);
            }
        }
        if (readonly != null) {
            obj = Collections.unmodifiableSet(obj);
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    private Collection<?> toCollection(Map<String, Object> map) {
        String collectionTypeName = (String) map.get("@collection");
        String readonly = (String) map.get("@readonly");
        List<Object> items = (List<Object>) map.get("@items");

        Class<?> collectionType = ClassUtils.getClass(collectionTypeName);
        Collection<Object> obj = (Collection<Object>) ClassUtils.tryNewInstance(collectionType);
        if (obj == null) {
            obj = new ArrayList<>();
            MagicAopLogger.warn("Failed to create instance of collection: " + collectionTypeName
                + ", use ArrayList instead.");
        }
        String errorMessage = null;
        for (Object item : items) {
            Object value = deserialize(item);
            try {
                obj.add(value);
            } catch (Exception e) {
                errorMessage = e.getMessage();
                break;
            }
        }
        if (errorMessage != null) {
            MagicAopLogger.warn("Failed to to add item to collection: " + collectionTypeName
                + ", use ArrayList instead. error: " + errorMessage);
            obj = new ArrayList<>();
            for (Object item : items) {
                Object value = deserialize(item);
                obj.add(value);
            }
        }
        if (readonly != null) {
            obj = Collections.unmodifiableCollection(obj);
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    private Map<?, ?> toMap(Map<String, Object> map) {
        String mapTypeName = (String) map.get("@map");
        String readonly = (String) map.get("@readonly");
        List<Map<String, Object>> entries = (List<Map<String, Object>>) map.get("entries");

        Class<?> mapType = ClassUtils.getClass(mapTypeName);
        Map<Object, Object> obj = (Map<Object, Object>) ClassUtils.tryNewInstance(mapType);
        if (obj == null) {
            obj = new LinkedHashMap<>();
            MagicAopLogger.warn("Failed to create instance of map: " + mapTypeName
                + ", use LinkedHashMap instead.");
        }
        String errorMessage = null;
        for (Map<String, Object> entry : entries) {
            Object key = deserialize(entry.get("key"));
            Object value = deserialize(entry.get("value"));
            try {
                obj.put(key, value);
            } catch (Exception e) {
                errorMessage = e.getMessage();
                break;
            }
        }
        if (errorMessage != null) {
            MagicAopLogger.warn("Failed to to put entry to map: " + mapTypeName
                + ", use LinkedHashMap instead. error: " + errorMessage);
            obj = new LinkedHashMap<>();
            for (Map<String, Object> entry : entries) {
                Object key = deserialize(entry.get("key"));
                Object value = deserialize(entry.get("value"));
                obj.put(key, value);
            }
        }
        if (readonly != null) {
            obj = Collections.unmodifiableMap(obj);
        }
        return obj;
    }

    private Object toObject(Map<String, Object> map) {
        String className = (String) map.get("@class");
        Integer refId = (Integer) map.get("@refid");
        Integer id = (Integer) map.get("@id");

        if (refId != null) {
            if (!this.idObjectMap.containsKey(refId)) {
                throw new IllegalStateException("Invalid @refid: " + refId);
            }
            return this.idObjectMap.get(refId);
        }

        Class<?> type = ClassUtils.getClass(className);
        Object obj = MagicAopReflectionUtils.newInstanceForSerialization(type);
        if (id != null) {
            this.idObjectMap.put(id, obj);
        }

        Map<String, Field> fieldMap = MagicAopReflectionUtils.getInstanceFieldMap(obj.getClass());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String fieldName = entry.getKey();
            if (!fieldName.startsWith("@")) {
                Field field = fieldMap.get(fieldName);
                if (field == null) {
                    MagicAopLogger.warn(
                        "Field not found: " + fieldName + " in class: " + type.getName());
                } else {
                    Object fieldValue = deserialize(entry.getValue());
                    MagicAopReflectionUtils.setInstanceFieldValue(obj, field, fieldValue);
                }
            }
        }
        return obj;
    }
}
