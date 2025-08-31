package com.github.hondams.magic.aop;

import com.github.hondams.magic.aop.util.MagicAopObjectSerializer;
import org.junit.jupiter.api.Test;

public class MagicAopObjectSerializerTest {

    @Test
    void test() {
        MagicAopObjectSerializer serializer = new MagicAopObjectSerializer();
        DataClass dataClass = new DataClass();
        dataClass.setField1("value1");
        System.out.println(serializer.serialize(dataClass));
        System.out.println(serializer.serialize(new DataClass()));
    }
}
