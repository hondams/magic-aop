package com.github.hondams.magic.aop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.hondams.magic.aop.util.MagicAopJsonUtils;
import com.github.hondams.magic.aop.util.MagicAopObjectDeserializer;
import com.github.hondams.magic.aop.util.MagicAopObjectSerializer;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class MagicAopObjectSerializerTest {

    @Test
    void test() {

        DataClass input = new DataClass();
        input.setField1("value1");

        String expected = "{\"@class\":\"com.github.hondams.magic.aop.DataClass\",\"@id\":1,\"field1\":\"value1\"}";

        MagicAopObjectSerializer serializer = new MagicAopObjectSerializer();
        Object serialized = serializer.serialize(input);
        String json = MagicAopJsonUtils.toJson(serialized);
        System.out.println(json);

        assertEquals(expected, json);

        Map<String, Object> map = MagicAopJsonUtils.toMap(json);
        assertEquals(serialized, map);

        MagicAopObjectDeserializer deserializer = new MagicAopObjectDeserializer();
        Object deserialized = deserializer.deserialize(map);

        MagicAopObjectSerializer serializer2 = new MagicAopObjectSerializer();
        Object serialized2 = serializer2.serialize(deserialized);
        String json2 = MagicAopJsonUtils.toJson(serialized2);
        assertEquals(json, json2);
    }
}
