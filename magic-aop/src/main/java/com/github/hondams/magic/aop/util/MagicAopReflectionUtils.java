package com.github.hondams.magic.aop.util;

import com.github.hondams.magic.aop.logger.MagicAopLogger;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.TreeMap;
import lombok.experimental.UtilityClass;
import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

@UtilityClass
public class MagicAopReflectionUtils {

    private final Unsafe unsafe = extractUnsafe();

    @SuppressWarnings("removal")
    private Unsafe extractUnsafe() {
        return java.security.AccessController.doPrivileged(
            (java.security.PrivilegedAction<Unsafe>) () -> {
                try {
                    Field f = Unsafe.class.getDeclaredField("theUnsafe");
                    f.setAccessible(true);
                    return (Unsafe) f.get(null);
                } catch (Exception e) {
                    MagicAopLogger.error("Failed to get Unsafe instance: " + e);
                    return null;
                }
            });
    }

    private Unsafe getUnsafe() {
        if (unsafe == null) {
            throw new IllegalStateException("Unsafe is not available");
        }
        return unsafe;
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstanceForSerialization(Class<T> type) {

        Constructor<?> defaultConstructor;
        try {
            defaultConstructor = Object.class.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }

        ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
        Constructor<?> customConstructor = reflectionFactory.newConstructorForSerialization(//
            type, defaultConstructor);
        customConstructor.setAccessible(true);

        try {
            return (T) customConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setInstanceFieldValue(Object instance, Field field, Object value) {
        if (instance == null) {
            throw new IllegalArgumentException("Instance is null for non-static field: " + field);
        }
        if (Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("Field is static: " + field);
        }
        if (!field.getDeclaringClass().isInstance(instance)) {
            throw new IllegalArgumentException(
                "Instance is not of the declaring class: " + field + ", instance=" + instance);
        }
        Unsafe unsafe = getUnsafe();
        long offset = unsafe.objectFieldOffset(field);
        setFieldValue(field, instance, offset, value);
    }

    public void setClassFieldValue(Field field, Object value) {
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("Field is not static: " + field);
        }
        Unsafe unsafe = getUnsafe();
        Object base = unsafe.staticFieldBase(field);
        long offset = unsafe.staticFieldOffset(field);
        setFieldValue(field, base, offset, value);
    }

    private void setFieldValue(Field field, Object base, long offset, Object value) {
        Unsafe unsafe = getUnsafe();
        Class<?> fieldType = field.getType();
        if (fieldType == int.class) {
            if (Modifier.isVolatile(field.getModifiers())) {
                unsafe.putIntVolatile(base, offset, (Integer) value);
            } else {
                unsafe.putInt(base, offset, (Integer) value);
            }
        } else if (fieldType == long.class) {
            if (Modifier.isVolatile(field.getModifiers())) {
                unsafe.putLongVolatile(base, offset, (Long) value);
            } else {
                unsafe.putLong(base, offset, (Long) value);
            }
        } else if (fieldType == boolean.class) {
            if (Modifier.isVolatile(field.getModifiers())) {
                unsafe.putBooleanVolatile(base, offset, (Boolean) value);
            } else {
                unsafe.putBoolean(base, offset, (Boolean) value);
            }
        } else if (fieldType == byte.class) {
            if (Modifier.isVolatile(field.getModifiers())) {
                unsafe.putByteVolatile(base, offset, (Byte) value);
            } else {
                unsafe.putByte(base, offset, (Byte) value);
            }
        } else if (fieldType == char.class) {
            if (Modifier.isVolatile(field.getModifiers())) {
                unsafe.putCharVolatile(base, offset, (Character) value);
            } else {
                unsafe.putChar(base, offset, (Character) value);
            }
        } else if (fieldType == short.class) {
            if (Modifier.isVolatile(field.getModifiers())) {
                unsafe.putShortVolatile(base, offset, (Short) value);
            } else {
                unsafe.putShort(base, offset, (Short) value);
            }
        } else if (fieldType == float.class) {
            if (Modifier.isVolatile(field.getModifiers())) {
                unsafe.putFloatVolatile(base, offset, (Float) value);
            } else {
                unsafe.putFloat(base, offset, (Float) value);
            }
        } else if (fieldType == double.class) {
            if (Modifier.isVolatile(field.getModifiers())) {
                unsafe.putDoubleVolatile(base, offset, (Double) value);
            } else {
                unsafe.putDouble(base, offset, (Double) value);
            }
        } else {
            if (Modifier.isVolatile(field.getModifiers())) {
                unsafe.putObjectVolatile(base, offset, value);
            } else {
                unsafe.putObject(base, offset, value);
            }
        }
    }

    public Object getInstanceFieldValue(Object instance, Field field) {
        if (instance == null) {
            throw new IllegalArgumentException("Instance is null for non-static field: " + field);
        }
        if (Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("Field is static: " + field);
        }
        if (!field.getDeclaringClass().isInstance(instance)) {
            throw new IllegalArgumentException(
                "Instance is not of the declaring class: " + field + ", instance=" + instance);
        }
        Unsafe unsafe = getUnsafe();
        long offset = unsafe.objectFieldOffset(field);
        return getFieldValue(field, instance, offset);
    }

    public Object getClassFieldValue(Field field) {
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("Field is not static: " + field);
        }
        Unsafe unsafe = getUnsafe();
        Object base = unsafe.staticFieldBase(field);
        long offset = unsafe.staticFieldOffset(field);
        return getFieldValue(field, base, offset);
    }

    private Object getFieldValue(Field field, Object base, long offset) {
        Unsafe unsafe = getUnsafe();
        Class<?> fieldType = field.getType();
        if (fieldType == int.class) {
            if (Modifier.isVolatile(field.getModifiers())) {
                return unsafe.getIntVolatile(base, offset);
            } else {
                return unsafe.getInt(base, offset);
            }
        } else if (fieldType == long.class) {
            if (Modifier.isVolatile(field.getModifiers())) {
                return unsafe.getLongVolatile(base, offset);
            } else {
                return unsafe.getLong(base, offset);
            }
        } else if (fieldType == boolean.class) {
            if (Modifier.isVolatile(field.getModifiers())) {
                return unsafe.getBooleanVolatile(base, offset);
            } else {
                return unsafe.getBoolean(base, offset);
            }
        } else if (fieldType == byte.class) {
            if (Modifier.isVolatile(field.getModifiers())) {
                return unsafe.getByteVolatile(base, offset);
            } else {
                return unsafe.getByte(base, offset);
            }
        } else if (fieldType == char.class) {
            if (Modifier.isVolatile(field.getModifiers())) {
                return unsafe.getCharVolatile(base, offset);
            } else {
                return unsafe.getChar(base, offset);
            }
        } else if (fieldType == short.class) {
            if (Modifier.isVolatile(field.getModifiers())) {
                return unsafe.getShortVolatile(base, offset);
            } else {
                return unsafe.getShort(base, offset);
            }
        } else if (fieldType == float.class) {
            if (Modifier.isVolatile(field.getModifiers())) {
                return unsafe.getFloatVolatile(base, offset);
            } else {
                return unsafe.getFloat(base, offset);
            }
        } else if (fieldType == double.class) {
            if (Modifier.isVolatile(field.getModifiers())) {
                return unsafe.getDoubleVolatile(base, offset);
            } else {
                return unsafe.getDouble(base, offset);
            }
        } else {
            if (Modifier.isVolatile(field.getModifiers())) {
                return unsafe.getObjectVolatile(base, offset);
            } else {
                return unsafe.getObject(base, offset);
            }
        }
    }

    public Map<String, Field> getFieldMap(Class<?> type) {
        Map<String, Field> fieldMap = new TreeMap<>();
        Class<?> currentType = type;
        while (currentType != null && currentType != Object.class) {
            for (Field field : currentType.getDeclaredFields()) {
                String name = field.getName();
                if (fieldMap.containsKey(name)) {
                    String duplicatedName = field.getDeclaringClass().getName() + ":" + name;
                    fieldMap.put(duplicatedName, field);
                } else {
                    fieldMap.put(name, field);
                }
                fieldMap.put(name, field);
            }
            currentType = currentType.getSuperclass();
        }
        return fieldMap;
    }

    public Map<String, Field> getClassFieldMap(Class<?> type) {
        Map<String, Field> fieldMap = new TreeMap<>();
        Class<?> currentType = type;
        while (currentType != null && currentType != Object.class) {
            for (Field field : currentType.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    String name = field.getName();
                    if (fieldMap.containsKey(name)) {
                        String duplicatedName = field.getDeclaringClass().getName() + ":" + name;
                        fieldMap.put(duplicatedName, field);
                    } else {
                        fieldMap.put(name, field);
                    }
                    fieldMap.put(name, field);
                }
            }
            currentType = currentType.getSuperclass();
        }
        return fieldMap;
    }

    public Map<String, Field> getInstanceFieldMap(Class<?> type) {
        Map<String, Field> fieldMap = new TreeMap<>();
        Class<?> currentType = type;
        while (currentType != null && currentType != Object.class) {
            for (Field field : currentType.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    String name = field.getName();
                    if (fieldMap.containsKey(name)) {
                        String duplicatedName = field.getDeclaringClass().getName() + ":" + name;
                        fieldMap.put(duplicatedName, field);
                    } else {
                        fieldMap.put(name, field);
                    }
                    fieldMap.put(name, field);
                }
            }
            currentType = currentType.getSuperclass();
        }
        return fieldMap;
    }


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
}
