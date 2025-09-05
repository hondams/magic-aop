package com.github.hondams.magic.aop.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ClassUtils {

    private final Map<String, Class<?>> PRIMITIVE_TYPE_MAP = Map.ofEntries(
        Map.entry("byte", byte.class), Map.entry("short", short.class), Map.entry("int", int.class),
        Map.entry("long", long.class), Map.entry("float", float.class),
        Map.entry("double", double.class), Map.entry("boolean", boolean.class),
        Map.entry("char", char.class));

    public Class<?> findSuperClass(Class<?> type, String superClassName) {
        Class<?> superClass = type;
        while (superClass != null) {
            if (superClass.getName().equals(superClassName)) {
                return superClass;
            }
            superClass = superClass.getSuperclass();
        }
        return null;
    }

    public Class<?> getClass(String className) {
        if (PRIMITIVE_TYPE_MAP.containsKey(className)) {
            return PRIMITIVE_TYPE_MAP.get(className);
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }


    public Object newInstance(Class<?> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    public Object tryNewInstance(Class<?> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            return null;
        }
    }
}
