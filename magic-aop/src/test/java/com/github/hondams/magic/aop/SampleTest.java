package com.github.hondams.magic.aop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.hondams.magic.aop.model.DataClass;
import com.github.hondams.magic.aop.model.SubDataClass;
import com.github.hondams.magic.aop.model.ValueClass;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

public class SampleTest {

    @Test
    void testValueClass() throws Exception {
        //
        // 同一参照を同じキーとみなす
        // IdentityHashMap<>

        Constructor<?> defaultConstructor = Object.class.getDeclaredConstructor();
        ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
        Constructor<?> customConstructor = reflectionFactory.newConstructorForSerialization(
            ValueClass.class, defaultConstructor);
        customConstructor.setAccessible(true);
        ValueClass instance = (ValueClass) customConstructor.newInstance();

        Unsafe unsafe = getUnsafe();
        Field f = ValueClass.class.getDeclaredField("field1");
        System.out.println("f=" + Modifier.toString(f.getModifiers()) + " " + f.getName() + " ("
            + f.getDeclaringClass().getName() + ")");
        assertTrue(Modifier.isFinal(f.getModifiers()));
        long offset = unsafe.objectFieldOffset(f);
        unsafe.putObject(instance, offset, "abc");

        // int フィールドは、putIntを使わないと、適切な値を設定できない。

        assertEquals("ValueClass(field1=abc)", instance.toString());

        for (Field field : getAllFields(ValueClass.class)) {
            System.out.println(
                Modifier.toString(field.getModifiers()) + " " + field.getName() + " ("
                    + field.getDeclaringClass().getName() + ")");
        }
    }

    @Test
    void testDataClass() throws Exception {
        //
        // 同一参照を同じキーとみなす
        // IdentityHashMap<>

        Constructor<?> defaultConstructor = Object.class.getDeclaredConstructor();
        ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
        Constructor<?> customConstructor = reflectionFactory.newConstructorForSerialization(
            DataClass.class, defaultConstructor);
        customConstructor.setAccessible(true);
        DataClass instance = (DataClass) customConstructor.newInstance();

        Unsafe unsafe = getUnsafe();
        Field f = DataClass.class.getDeclaredField("field1");
        System.out.println("f=" + Modifier.toString(f.getModifiers()) + " " + f.getName() + " ("
            + f.getDeclaringClass().getName() + ")");
        //assertTrue(Modifier.isFinal(f.getModifiers()));
        long offset = unsafe.objectFieldOffset(f);
        unsafe.putObject(instance, offset, "abc");

        assertEquals("DataClass(field1=abc)", instance.toString());

        for (Field field : getAllFields(DataClass.class)) {
            System.out.println(
                Modifier.toString(field.getModifiers()) + " " + field.getName() + " ("
                    + field.getDeclaringClass().getName() + ")");
        }
    }

    @Test
    void testSubDataClass() throws Exception {
        //
        // 同一参照を同じキーとみなす
        // IdentityHashMap<>

        Constructor<?> defaultConstructor = Object.class.getDeclaredConstructor();
        ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
        Constructor<?> customConstructor = reflectionFactory.newConstructorForSerialization(
            SubDataClass.class, defaultConstructor);
        customConstructor.setAccessible(true);
        SubDataClass instance = (SubDataClass) customConstructor.newInstance();

        Unsafe unsafe = getUnsafe();
        Field f = SubDataClass.class.getDeclaredField("field1");
        System.out.println("f=" + Modifier.toString(f.getModifiers()) + " " + f.getName() + " ("
            + f.getDeclaringClass().getName() + ")");
        //assertTrue(Modifier.isFinal(f.getModifiers()));
        long offset = unsafe.objectFieldOffset(f);
        unsafe.putObject(instance, offset, "abc");

        assertEquals("SubDataClass(super=SuperDataClass(field1=null), field1=abc)",
            instance.toString());

        SubDataClass instance2 = new SubDataClass();
        instance2.setField1("abc2");
        assertEquals("SubDataClass(super=SuperDataClass(field1=null), field1=abc2)",
            instance2.toString());

        for (Field field : getAllFields(SubDataClass.class)) {
            System.out.println(
                Modifier.toString(field.getModifiers()) + " " + field.getName() + " ("
                    + field.getDeclaringClass().getName() + ")");
        }
    }

    @SuppressWarnings("removal")
    public static Unsafe getUnsafe() {
        return AccessController.doPrivileged((PrivilegedAction<Unsafe>) () -> {
            try {
                Field f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                return (Unsafe) f.get(null);
            } catch (Exception e) {
                throw new RuntimeException("Unable to access Unsafe", e);
            }
        });
    }

    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }

        // 同名フィールドは、親側のフィールドを、クラス名＋フィールド名にする

        // @class
        // @instance

        // @class
        // @ref
        return fields;
    }

    @Test
    void test() throws Exception {
        System.out.println(Collections.unmodifiableList(new ArrayList<>()).getClass().getName());
        System.out.println(Collections.unmodifiableList(new LinkedList<>()).getClass().getName());
        System.out.println(Collections.unmodifiableMap(new HashMap<>()).getClass().getName());
        System.out.println(Collections.unmodifiableSet(new HashSet<>()).getClass().getName());
        System.out.println(
            Collections.unmodifiableCollection(new ArrayList<>()).getClass().getName());
        System.out.println(new int[0].getClass().getName());
        System.out.println(new Object[0].getClass().getName());
        System.out.println(Class.forName(new Object[0].getClass().getName()));
        System.out.println(int.class.getName());
        System.out.println(Locale.GERMANY);

    }
}
