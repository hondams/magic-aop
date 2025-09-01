package com.github.hondams.magic.aop.util;

import java.lang.reflect.InvocationTargetException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ClassUtils {

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
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
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
