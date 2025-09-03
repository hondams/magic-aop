package com.github.hondams.magic.aop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.hondams.magic.aop.model.ArrayDataClass;
import com.github.hondams.magic.aop.model.DataClass;
import com.github.hondams.magic.aop.model.DataClassWithConstant;
import com.github.hondams.magic.aop.model.ObjectFieldDataClass;
import com.github.hondams.magic.aop.model.SubDataClass;
import com.github.hondams.magic.aop.model.ValueClass;
import com.github.hondams.magic.aop.model.ValueTypeDataClass;
import com.github.hondams.magic.aop.model.ValueTypeValueClass;
import com.github.hondams.magic.aop.model.VolatileValueTypeDataClass;
import com.github.hondams.magic.aop.util.DateUtils;
import com.github.hondams.magic.aop.util.MagicAopJsonUtils;
import com.github.hondams.magic.aop.util.MagicAopObjectDeserializer;
import com.github.hondams.magic.aop.util.MagicAopObjectSerializer;
import com.github.hondams.magic.aop.util.UrlUtils;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.Value;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class MagicAopObjectSerializerTest {

    @ParameterizedTest
    @MethodSource("testCases")
    void test(TestCase testCase) {

        MagicAopObjectSerializer serializer = new MagicAopObjectSerializer();
        Object serialized = serializer.serialize(testCase.input);
        String json = MagicAopJsonUtils.toJson(serialized);
        System.out.println("toString=" + testCase.input);
        System.out.println("json=" + json);

        assertEquals(testCase.expectedJson, json);

        Map<String, Object> map = MagicAopJsonUtils.toMap(json);
        assertEquals(serialized, map);

        MagicAopObjectDeserializer deserializer = new MagicAopObjectDeserializer();
        Object deserialized = deserializer.deserialize(map);

        MagicAopObjectSerializer serializer2 = new MagicAopObjectSerializer();
        Object serialized2 = serializer2.serialize(deserialized);
        String json2 = MagicAopJsonUtils.toJson(serialized2);
        assertEquals(json, json2);
    }

    static Stream<TestCase> testCases() {

        return Stream.of(//
            // simple value
            new TestCase(//
                DataClass.builder().build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.DataClass\",\"@id\":1,\"field1\":null}"),
            new TestCase(//
                DataClass.builder().field1("value1").build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.DataClass\",\"@id\":1,\"field1\":\"value1\"}"),
            new TestCase(//
                DataClassWithConstant.builder().build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.DataClassWithConstant\",\"@id\":1,\"field1\":null}"),
            new TestCase(//
                DataClassWithConstant.builder().field1("value1").build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.DataClassWithConstant\",\"@id\":1,\"field1\":\"value1\"}"),
            new TestCase(//
                ValueClass.builder().build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ValueClass\",\"@id\":1,\"field1\":null}"),
            new TestCase(//
                ValueClass.builder().field1("value1").build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ValueClass\",\"@id\":1,\"field1\":\"value1\"}"),
            new TestCase(//
                SubDataClass.builder().build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.SubDataClass\",\"@id\":1,\"field1\":null,\"com.github.hondams.magic.aop.model.SuperDataClass:field1\":null}"),
            new TestCase(//
                SubDataClass.builder().field1("value1").build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.SubDataClass\",\"@id\":1,\"field1\":\"value1\",\"com.github.hondams.magic.aop.model.SuperDataClass:field1\":null}"),

            // collections
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(new ArrayList<>()).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@list\":\"java.util.ArrayList\",\"@items\":[]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(new ArrayList<>(List.of("string1"))).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@list\":\"java.util.ArrayList\",\"@items\":[\"string1\"]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(Collections.unmodifiableList(new ArrayList<>(List.of("string1"))))
                    .build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@list\":\"java.util.ArrayList\",\"@readonly\":\"java.util.Collections$UnmodifiableRandomAccessList\",\"@items\":[\"string1\"]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(new LinkedList<>()).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@list\":\"java.util.LinkedList\",\"@items\":[]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(new LinkedList<>(List.of("string1"))).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@list\":\"java.util.LinkedList\",\"@items\":[\"string1\"]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(Collections.unmodifiableList(new LinkedList<>(List.of("string1"))))
                    .build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@list\":\"java.util.LinkedList\",\"@readonly\":\"java.util.Collections$UnmodifiableList\",\"@items\":[\"string1\"]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(List.of()).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@list\":\"java.util.ImmutableCollections$ListN\",\"@items\":[]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(List.of("string1")).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@list\":\"java.util.ImmutableCollections$List12\",\"@items\":[\"string1\"]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(Collections.emptyList()).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@list\":\"java.util.Collections$EmptyList\",\"@items\":[]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(Collections.singletonList("string1")).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@list\":\"java.util.Collections$SingletonList\",\"@items\":[\"string1\"]}}"),

            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(new HashSet<>()).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@set\":\"java.util.HashSet\",\"@items\":[]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(new HashSet<>(Set.of("string1"))).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@set\":\"java.util.HashSet\",\"@items\":[\"string1\"]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(Collections.unmodifiableSet(//
                        new HashSet<>(Set.of("string1")))).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@set\":\"java.util.HashSet\",\"@readonly\":\"java.util.Collections$UnmodifiableSet\",\"@items\":[\"string1\"]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(new TreeSet<>()).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@set\":\"java.util.TreeSet\",\"@items\":[]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(new TreeSet<>(Set.of("string1"))).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@set\":\"java.util.TreeSet\",\"@items\":[\"string1\"]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(Collections.unmodifiableSet(//
                        new TreeSet<>(Set.of("string1")))).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@set\":\"java.util.TreeSet\",\"@readonly\":\"java.util.Collections$UnmodifiableSet\",\"@items\":[\"string1\"]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(Set.of()).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@set\":\"java.util.ImmutableCollections$SetN\",\"@items\":[]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(Set.of("string1")).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@set\":\"java.util.ImmutableCollections$Set12\",\"@items\":[\"string1\"]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(Collections.emptySet()).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@set\":\"java.util.Collections$EmptySet\",\"@items\":[]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(Collections.singleton("string1")).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@set\":\"java.util.Collections$SingletonSet\",\"@items\":[\"string1\"]}}"),

            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(new ArrayDeque<>()).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@collection\":\"java.util.ArrayDeque\",\"@items\":[]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(new ArrayDeque<>(List.of("string1"))).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@collection\":\"java.util.ArrayDeque\",\"@items\":[\"string1\"]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(Collections.unmodifiableCollection(//
                        new ArrayDeque<>(List.of("string1")))).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@collection\":\"java.util.ArrayDeque\",\"@readonly\":\"java.util.Collections$UnmodifiableCollection\",\"@items\":[\"string1\"]}}"),

            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(new HashMap<>()).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@map\":\"java.util.HashMap\",\"@entries\":[]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(new HashMap<>(Map.of("key1", "value1"))).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@map\":\"java.util.HashMap\",\"@entries\":[{\"@key\":\"key1\",\"@value\":\"value1\"}]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(Collections.unmodifiableMap(//
                        new HashMap<>(Map.of("key1", "value1")))).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@map\":\"java.util.HashMap\",\"@readonly\":\"java.util.Collections$UnmodifiableMap\",\"@entries\":[{\"@key\":\"key1\",\"@value\":\"value1\"}]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(new TreeMap<>()).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@map\":\"java.util.TreeMap\",\"@entries\":[]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(new TreeMap<>(Map.of("key1", "value1"))).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@map\":\"java.util.TreeMap\",\"@entries\":[{\"@key\":\"key1\",\"@value\":\"value1\"}]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(Collections.unmodifiableMap(//
                        new TreeMap<>(Map.of("key1", "value1")))).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@map\":\"java.util.TreeMap\",\"@readonly\":\"java.util.Collections$UnmodifiableMap\",\"@entries\":[{\"@key\":\"key1\",\"@value\":\"value1\"}]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(Map.of()).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@map\":\"java.util.ImmutableCollections$MapN\",\"@entries\":[]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(Map.of("key1", "value1")).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@map\":\"java.util.ImmutableCollections$Map1\",\"@entries\":[{\"@key\":\"key1\",\"@value\":\"value1\"}]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(Collections.emptyMap()).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@map\":\"java.util.Collections$EmptyMap\",\"@entries\":[]}}"),
            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(Collections.singletonMap("key1", "value1")).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@map\":\"java.util.Collections$SingletonMap\",\"@entries\":[{\"@key\":\"key1\",\"@value\":\"value1\"}]}}"),

            new TestCase(//
                ObjectFieldDataClass.builder()//
                    .field1(((Supplier<?>) () -> {
                        Object[] array = new Object[3];
                        Arrays.fill(array, new Object());
                        return array;
                    }).get()).build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ObjectFieldDataClass\",\"@id\":1,\"field1\":{\"@array\":\"java.lang.Object\",\"@length\":3,\"@items\":[{\"@class\":\"java.lang.Object\",\"@id\":2},{\"@class\":\"java.lang.Object\",\"@refid\":2},{\"@class\":\"java.lang.Object\",\"@refid\":2}]}}"),

            // all value type
            new TestCase(//
                ValueTypeDataClass.builder().build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ValueTypeDataClass\",\"@id\":1,\"bigDecimalField\":null,\"bigIntegerField\":null,\"booleanField\":false,\"booleanObjectField\":null,\"byteArrayField\":null,\"byteField\":{\"@value\":\"java.lang.Byte\",\"@text\":\"0\"},\"byteObjectField\":null,\"charField\":{\"@value\":\"java.lang.Character\",\"@text\":\"\\u0000\"},\"charObjectField\":null,\"dateField\":null,\"doubleField\":0.0,\"doubleObjectField\":null,\"enumField\":null,\"floatField\":{\"@value\":\"java.lang.Float\",\"@text\":\"0.0\"},\"floatObjectField\":null,\"intField\":0,\"intObjectField\":null,\"localDateField\":null,\"localDateTimeField\":null,\"localTimeField\":null,\"longField\":{\"@value\":\"java.lang.Long\",\"@text\":\"0\"},\"longObjectField\":null,\"objectField\":null,\"offsetDateTimeField\":null,\"shortField\":{\"@value\":\"java.lang.Short\",\"@text\":\"0\"},\"shortObjectField\":null,\"sqlDateField\":null,\"sqlTimeField\":null,\"sqlTimestampField\":null,\"stringField\":null,\"uriField\":null,\"urlField\":null,\"uuidField\":null,\"zonedDateTimeField\":null}"),
            new TestCase(//
                ValueTypeDataClass.builder()//
                    .booleanField(true)//
                    .byteField((byte) 1)//
                    .shortField((short) 2)//
                    .intField(3)//
                    .longField(4L)//
                    .floatField(5.0f)//
                    .doubleField(6.0)//
                    .booleanObjectField(Boolean.FALSE)//
                    .byteObjectField((byte) 7)//
                    .shortObjectField((short) 8)//
                    .intObjectField(9)//
                    .longObjectField(10L)//
                    .floatObjectField(11.0f)//
                    .doubleObjectField(12.0)//
                    .charField('a')//
                    .charObjectField('b')//
                    .stringField("string1")//
                    .objectField(new Object())//
                    .dateField(DateUtils.toDate("2024-06-30T23:59:59.123"))//
                    .sqlDateField(DateUtils.toSqlDate("2024-07-01"))//
                    .sqlTimeField(DateUtils.toSqlTime("59:59:12"))//
                    .sqlTimestampField(DateUtils.toSqlTimestamp("2024-09-01T23:59:59.123"))//
                    .localTimeField(DateUtils.toLocalTime("23:59:59.123"))
                    .localDateField(DateUtils.toLocalDate("2020-02-05"))//
                    .localDateTimeField(DateUtils.toLocalDateTime(//
                        "2019-06-30T23:59:59.123"))//
                    .zonedDateTimeField(DateUtils.toZonedDateTime(//
                        "2021-04-07T14:15:16.000000456+09:00[Asia/Tokyo]"))//
                    .offsetDateTimeField(DateUtils.toOffsetDateTime(//
                        "2020-04-07T14:15:16.000000456+09:00"))//
                    .bigIntegerField(new java.math.BigInteger("12345678901234567890"))
                    .bigDecimalField(new java.math.BigDecimal("12345.67890"))//
                    .uuidField(java.util.UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))//
                    .urlField(UrlUtils.toUrl("https://www2.example.com/sample?param3=value4"))//
                    .uriField(
                        java.net.URI.create("https://www3.example.com/sample?param4=value5"))//
                    .byteArrayField(new byte[]{1, 2, 3, 4, 5})//
                    .enumField(com.github.hondams.magic.aop.model.SampleEnum.VALUE1)//
                    .build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ValueTypeDataClass\",\"@id\":1,\"bigDecimalField\":{\"@value\":\"java.math.BigDecimal\",\"@text\":\"12345.67890\"},\"bigIntegerField\":{\"@value\":\"java.math.BigInteger\",\"@text\":\"12345678901234567890\"},\"booleanField\":true,\"booleanObjectField\":false,\"byteArrayField\":{\"@value\":\"[B\",\"@text\":\"0102030405\"},\"byteField\":{\"@value\":\"java.lang.Byte\",\"@text\":\"1\"},\"byteObjectField\":{\"@value\":\"java.lang.Byte\",\"@text\":\"7\"},\"charField\":{\"@value\":\"java.lang.Character\",\"@text\":\"a\"},\"charObjectField\":{\"@value\":\"java.lang.Character\",\"@text\":\"b\"},\"dateField\":{\"@value\":\"java.util.Date\",\"@text\":\"2024-06-30T23:59:59.123\"},\"doubleField\":6.0,\"doubleObjectField\":12.0,\"enumField\":{\"@value\":\"com.github.hondams.magic.aop.model.SampleEnum\",\"@text\":\"VALUE1\"},\"floatField\":{\"@value\":\"java.lang.Float\",\"@text\":\"5.0\"},\"floatObjectField\":{\"@value\":\"java.lang.Float\",\"@text\":\"11.0\"},\"intField\":3,\"intObjectField\":9,\"localDateField\":{\"@value\":\"java.time.LocalDate\",\"@text\":\"2020-02-05\"},\"localDateTimeField\":{\"@value\":\"java.time.LocalDateTime\",\"@text\":\"2019-06-30T23:59:59.123\"},\"localTimeField\":{\"@value\":\"java.time.LocalTime\",\"@text\":\"23:59:59.123\"},\"longField\":{\"@value\":\"java.lang.Long\",\"@text\":\"4\"},\"longObjectField\":{\"@value\":\"java.lang.Long\",\"@text\":\"10\"},\"objectField\":{\"@class\":\"java.lang.Object\",\"@id\":2},\"offsetDateTimeField\":{\"@value\":\"java.time.OffsetDateTime\",\"@text\":\"2020-04-07T14:15:16.000000456+09:00\"},\"shortField\":{\"@value\":\"java.lang.Short\",\"@text\":\"2\"},\"shortObjectField\":{\"@value\":\"java.lang.Short\",\"@text\":\"8\"},\"sqlDateField\":{\"@value\":\"java.sql.Date\",\"@text\":\"2024-07-01\"},\"sqlTimeField\":{\"@value\":\"java.sql.Time\",\"@text\":\"11:59:12\"},\"sqlTimestampField\":{\"@value\":\"java.sql.Timestamp\",\"@text\":\"2024-09-01T23:59:59.123\"},\"stringField\":\"string1\",\"uriField\":{\"@value\":\"java.net.URI\",\"@text\":\"https://www3.example.com/sample?param4=value5\"},\"urlField\":{\"@value\":\"java.net.URL\",\"@text\":\"https://www2.example.com/sample?param3=value4\"},\"uuidField\":{\"@value\":\"java.util.UUID\",\"@text\":\"123e4567-e89b-12d3-a456-426614174000\"},\"zonedDateTimeField\":{\"@value\":\"java.time.ZonedDateTime\",\"@text\":\"2021-04-07T14:15:16.000000456+09:00[Asia/Tokyo]\"}}"),
            new TestCase(//
                VolatileValueTypeDataClass.builder().build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.VolatileValueTypeDataClass\",\"@id\":1,\"bigDecimalField\":null,\"bigIntegerField\":null,\"booleanField\":false,\"booleanObjectField\":null,\"byteArrayField\":null,\"byteField\":{\"@value\":\"java.lang.Byte\",\"@text\":\"0\"},\"byteObjectField\":null,\"charField\":{\"@value\":\"java.lang.Character\",\"@text\":\"\\u0000\"},\"charObjectField\":null,\"dateField\":null,\"doubleField\":0.0,\"doubleObjectField\":null,\"enumField\":null,\"floatField\":{\"@value\":\"java.lang.Float\",\"@text\":\"0.0\"},\"floatObjectField\":null,\"intField\":0,\"intObjectField\":null,\"localDateField\":null,\"localDateTimeField\":null,\"localTimeField\":null,\"longField\":{\"@value\":\"java.lang.Long\",\"@text\":\"0\"},\"longObjectField\":null,\"objectField\":null,\"offsetDateTimeField\":null,\"shortField\":{\"@value\":\"java.lang.Short\",\"@text\":\"0\"},\"shortObjectField\":null,\"sqlDateField\":null,\"sqlTimeField\":null,\"sqlTimestampField\":null,\"stringField\":null,\"uriField\":null,\"urlField\":null,\"uuidField\":null,\"zonedDateTimeField\":null}"),
            new TestCase(//
                VolatileValueTypeDataClass.builder()//
                    .booleanField(true)//
                    .byteField((byte) 1)//
                    .shortField((short) 2)//
                    .intField(3)//
                    .longField(4L)//
                    .floatField(5.0f)//
                    .doubleField(6.0)//
                    .booleanObjectField(Boolean.FALSE)//
                    .byteObjectField((byte) 7)//
                    .shortObjectField((short) 8)//
                    .intObjectField(9)//
                    .longObjectField(10L)//
                    .floatObjectField(11.0f)//
                    .doubleObjectField(12.0)//
                    .charField('a')//
                    .charObjectField('b')//
                    .stringField("string1")//
                    .objectField(new Object())//
                    .dateField(DateUtils.toDate("2024-06-30T23:59:59.123"))//
                    .sqlDateField(DateUtils.toSqlDate("2024-07-01"))//
                    .sqlTimeField(DateUtils.toSqlTime("59:59:12"))//
                    .sqlTimestampField(DateUtils.toSqlTimestamp("2024-09-01T23:59:59.123"))//
                    .localTimeField(DateUtils.toLocalTime("23:59:59.123"))
                    .localDateField(DateUtils.toLocalDate("2020-02-05"))//
                    .localDateTimeField(DateUtils.toLocalDateTime(//
                        "2019-06-30T23:59:59.123"))//
                    .zonedDateTimeField(DateUtils.toZonedDateTime(//
                        "2021-04-07T14:15:16.000000456+09:00[Asia/Tokyo]"))//
                    .offsetDateTimeField(DateUtils.toOffsetDateTime(//
                        "2020-04-07T14:15:16.000000456+09:00"))//
                    .bigIntegerField(new java.math.BigInteger("12345678901234567890"))
                    .bigDecimalField(new java.math.BigDecimal("12345.67890"))//
                    .uuidField(java.util.UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))//
                    .urlField(UrlUtils.toUrl("https://www2.example.com/sample?param3=value4"))//
                    .uriField(
                        java.net.URI.create("https://www3.example.com/sample?param4=value5"))//
                    .byteArrayField(new byte[]{1, 2, 3, 4, 5})//
                    .enumField(com.github.hondams.magic.aop.model.SampleEnum.VALUE1)//
                    .build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.VolatileValueTypeDataClass\",\"@id\":1,\"bigDecimalField\":{\"@value\":\"java.math.BigDecimal\",\"@text\":\"12345.67890\"},\"bigIntegerField\":{\"@value\":\"java.math.BigInteger\",\"@text\":\"12345678901234567890\"},\"booleanField\":true,\"booleanObjectField\":false,\"byteArrayField\":{\"@value\":\"[B\",\"@text\":\"0102030405\"},\"byteField\":{\"@value\":\"java.lang.Byte\",\"@text\":\"1\"},\"byteObjectField\":{\"@value\":\"java.lang.Byte\",\"@text\":\"7\"},\"charField\":{\"@value\":\"java.lang.Character\",\"@text\":\"a\"},\"charObjectField\":{\"@value\":\"java.lang.Character\",\"@text\":\"b\"},\"dateField\":{\"@value\":\"java.util.Date\",\"@text\":\"2024-06-30T23:59:59.123\"},\"doubleField\":6.0,\"doubleObjectField\":12.0,\"enumField\":{\"@value\":\"com.github.hondams.magic.aop.model.SampleEnum\",\"@text\":\"VALUE1\"},\"floatField\":{\"@value\":\"java.lang.Float\",\"@text\":\"5.0\"},\"floatObjectField\":{\"@value\":\"java.lang.Float\",\"@text\":\"11.0\"},\"intField\":3,\"intObjectField\":9,\"localDateField\":{\"@value\":\"java.time.LocalDate\",\"@text\":\"2020-02-05\"},\"localDateTimeField\":{\"@value\":\"java.time.LocalDateTime\",\"@text\":\"2019-06-30T23:59:59.123\"},\"localTimeField\":{\"@value\":\"java.time.LocalTime\",\"@text\":\"23:59:59.123\"},\"longField\":{\"@value\":\"java.lang.Long\",\"@text\":\"4\"},\"longObjectField\":{\"@value\":\"java.lang.Long\",\"@text\":\"10\"},\"objectField\":{\"@class\":\"java.lang.Object\",\"@id\":2},\"offsetDateTimeField\":{\"@value\":\"java.time.OffsetDateTime\",\"@text\":\"2020-04-07T14:15:16.000000456+09:00\"},\"shortField\":{\"@value\":\"java.lang.Short\",\"@text\":\"2\"},\"shortObjectField\":{\"@value\":\"java.lang.Short\",\"@text\":\"8\"},\"sqlDateField\":{\"@value\":\"java.sql.Date\",\"@text\":\"2024-07-01\"},\"sqlTimeField\":{\"@value\":\"java.sql.Time\",\"@text\":\"11:59:12\"},\"sqlTimestampField\":{\"@value\":\"java.sql.Timestamp\",\"@text\":\"2024-09-01T23:59:59.123\"},\"stringField\":\"string1\",\"uriField\":{\"@value\":\"java.net.URI\",\"@text\":\"https://www3.example.com/sample?param4=value5\"},\"urlField\":{\"@value\":\"java.net.URL\",\"@text\":\"https://www2.example.com/sample?param3=value4\"},\"uuidField\":{\"@value\":\"java.util.UUID\",\"@text\":\"123e4567-e89b-12d3-a456-426614174000\"},\"zonedDateTimeField\":{\"@value\":\"java.time.ZonedDateTime\",\"@text\":\"2021-04-07T14:15:16.000000456+09:00[Asia/Tokyo]\"}}"),
            new TestCase(//
                ValueTypeValueClass.builder().build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ValueTypeValueClass\",\"@id\":1,\"bigDecimalField\":null,\"bigIntegerField\":null,\"booleanField\":false,\"booleanObjectField\":null,\"byteArrayField\":null,\"byteField\":{\"@value\":\"java.lang.Byte\",\"@text\":\"0\"},\"byteObjectField\":null,\"charField\":{\"@value\":\"java.lang.Character\",\"@text\":\"\\u0000\"},\"charObjectField\":null,\"dateField\":null,\"doubleField\":0.0,\"doubleObjectField\":null,\"enumField\":null,\"floatField\":{\"@value\":\"java.lang.Float\",\"@text\":\"0.0\"},\"floatObjectField\":null,\"intField\":0,\"intObjectField\":null,\"localDateField\":null,\"localDateTimeField\":null,\"localTimeField\":null,\"longField\":{\"@value\":\"java.lang.Long\",\"@text\":\"0\"},\"longObjectField\":null,\"objectField\":null,\"offsetDateTimeField\":null,\"shortField\":{\"@value\":\"java.lang.Short\",\"@text\":\"0\"},\"shortObjectField\":null,\"sqlDateField\":null,\"sqlTimeField\":null,\"sqlTimestampField\":null,\"stringField\":null,\"uriField\":null,\"urlField\":null,\"uuidField\":null,\"zonedDateTimeField\":null}"),
            new TestCase(//
                ValueTypeValueClass.builder()//
                    .booleanField(true)//
                    .byteField((byte) 1)//
                    .shortField((short) 2)//
                    .intField(3)//
                    .longField(4L)//
                    .floatField(5.0f)//
                    .doubleField(6.0)//
                    .booleanObjectField(Boolean.FALSE)//
                    .byteObjectField((byte) 7)//
                    .shortObjectField((short) 8)//
                    .intObjectField(9)//
                    .longObjectField(10L)//
                    .floatObjectField(11.0f)//
                    .doubleObjectField(12.0)//
                    .charField('a')//
                    .charObjectField('b')//
                    .stringField("string1")//
                    .objectField(new Object())//
                    .dateField(DateUtils.toDate("2024-06-30T23:59:59.123"))//
                    .sqlDateField(DateUtils.toSqlDate("2024-07-01"))//
                    .sqlTimeField(DateUtils.toSqlTime("59:59:12"))//
                    .sqlTimestampField(DateUtils.toSqlTimestamp("2024-09-01T23:59:59.123"))//
                    .localTimeField(DateUtils.toLocalTime("23:59:59.123"))
                    .localDateField(DateUtils.toLocalDate("2020-02-05"))//
                    .localDateTimeField(DateUtils.toLocalDateTime(//
                        "2019-06-30T23:59:59.123"))//
                    .zonedDateTimeField(DateUtils.toZonedDateTime(//
                        "2021-04-07T14:15:16.000000456+09:00[Asia/Tokyo]"))//
                    .offsetDateTimeField(DateUtils.toOffsetDateTime(//
                        "2020-04-07T14:15:16.000000456+09:00"))//
                    .bigIntegerField(new java.math.BigInteger("12345678901234567890"))
                    .bigDecimalField(new java.math.BigDecimal("12345.67890"))//
                    .uuidField(java.util.UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))//
                    .urlField(UrlUtils.toUrl("https://www2.example.com/sample?param3=value4"))//
                    .uriField(
                        java.net.URI.create("https://www3.example.com/sample?param4=value5"))//
                    .byteArrayField(new byte[]{1, 2, 3, 4, 5})//
                    .enumField(com.github.hondams.magic.aop.model.SampleEnum.VALUE1)//
                    .build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ValueTypeValueClass\",\"@id\":1,\"bigDecimalField\":{\"@value\":\"java.math.BigDecimal\",\"@text\":\"12345.67890\"},\"bigIntegerField\":{\"@value\":\"java.math.BigInteger\",\"@text\":\"12345678901234567890\"},\"booleanField\":true,\"booleanObjectField\":false,\"byteArrayField\":{\"@value\":\"[B\",\"@text\":\"0102030405\"},\"byteField\":{\"@value\":\"java.lang.Byte\",\"@text\":\"1\"},\"byteObjectField\":{\"@value\":\"java.lang.Byte\",\"@text\":\"7\"},\"charField\":{\"@value\":\"java.lang.Character\",\"@text\":\"a\"},\"charObjectField\":{\"@value\":\"java.lang.Character\",\"@text\":\"b\"},\"dateField\":{\"@value\":\"java.util.Date\",\"@text\":\"2024-06-30T23:59:59.123\"},\"doubleField\":6.0,\"doubleObjectField\":12.0,\"enumField\":{\"@value\":\"com.github.hondams.magic.aop.model.SampleEnum\",\"@text\":\"VALUE1\"},\"floatField\":{\"@value\":\"java.lang.Float\",\"@text\":\"5.0\"},\"floatObjectField\":{\"@value\":\"java.lang.Float\",\"@text\":\"11.0\"},\"intField\":3,\"intObjectField\":9,\"localDateField\":{\"@value\":\"java.time.LocalDate\",\"@text\":\"2020-02-05\"},\"localDateTimeField\":{\"@value\":\"java.time.LocalDateTime\",\"@text\":\"2019-06-30T23:59:59.123\"},\"localTimeField\":{\"@value\":\"java.time.LocalTime\",\"@text\":\"23:59:59.123\"},\"longField\":{\"@value\":\"java.lang.Long\",\"@text\":\"4\"},\"longObjectField\":{\"@value\":\"java.lang.Long\",\"@text\":\"10\"},\"objectField\":{\"@class\":\"java.lang.Object\",\"@id\":2},\"offsetDateTimeField\":{\"@value\":\"java.time.OffsetDateTime\",\"@text\":\"2020-04-07T14:15:16.000000456+09:00\"},\"shortField\":{\"@value\":\"java.lang.Short\",\"@text\":\"2\"},\"shortObjectField\":{\"@value\":\"java.lang.Short\",\"@text\":\"8\"},\"sqlDateField\":{\"@value\":\"java.sql.Date\",\"@text\":\"2024-07-01\"},\"sqlTimeField\":{\"@value\":\"java.sql.Time\",\"@text\":\"11:59:12\"},\"sqlTimestampField\":{\"@value\":\"java.sql.Timestamp\",\"@text\":\"2024-09-01T23:59:59.123\"},\"stringField\":\"string1\",\"uriField\":{\"@value\":\"java.net.URI\",\"@text\":\"https://www3.example.com/sample?param4=value5\"},\"urlField\":{\"@value\":\"java.net.URL\",\"@text\":\"https://www2.example.com/sample?param3=value4\"},\"uuidField\":{\"@value\":\"java.util.UUID\",\"@text\":\"123e4567-e89b-12d3-a456-426614174000\"},\"zonedDateTimeField\":{\"@value\":\"java.time.ZonedDateTime\",\"@text\":\"2021-04-07T14:15:16.000000456+09:00[Asia/Tokyo]\"}}"),

            // array
            new TestCase(//
                ArrayDataClass.builder()//
                    .build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ArrayDataClass\",\"@id\":1,\"booleanArrayArrayField\":null,\"booleanArrayField\":null,\"byteArrayArrayField\":null,\"byteArrayField\":null,\"charArrayArrayField\":null,\"charArrayField\":null,\"doubleArrayArrayField\":null,\"doubleArrayField\":null,\"floatArrayArrayField\":null,\"floatArrayField\":null,\"intArrayArrayField\":null,\"intArrayField\":null,\"longArrayArrayField\":null,\"longArrayField\":null,\"objectArrayArrayField\":null,\"objectArrayField\":null,\"shortArrayArrayField\":null,\"shortArrayField\":null,\"stringArrayArrayField\":null,\"stringArrayField\":null}"),
            new TestCase(//
                ArrayDataClass.builder()//
                    .booleanArrayField(new boolean[]{true, false, true})//
                    .byteArrayField(new byte[]{1, 2, 3})//
                    .charArrayField(new char[]{'a', 'b', 'c'})//
                    .shortArrayField(new short[]{4, 5, 6})//
                    .intArrayField(new int[]{7, 8, 9})//
                    .longArrayField(new long[]{10L, 11L, 12L})//
                    .floatArrayField(new float[]{13.0f, 14.0f, 15.0f})//
                    .doubleArrayField(new double[]{16.0, 17.0, 18.0})//
                    .stringArrayField(new String[]{"string1", null, "string2", "string3"})//
                    .objectArrayField(new Object[]{new Object(), null, new Object()})//
                    .booleanArrayArrayField(new boolean[][]{{true, false}, {false, true}})//
                    .byteArrayArrayField(new byte[][]{{1, 2}, null, {3, 4}})//
                    .charArrayArrayField(new char[][]{{'a', 'b'}, null, {'c', 'd'}})//
                    .shortArrayArrayField(new short[][]{{4, 5}, null, {6, 7}})//
                    .intArrayArrayField(new int[][]{{7, 8}, {9, 10}, null})
                    .longArrayArrayField(new long[][]{{10L, 11L}, null, {12L, 13L}})//
                    .floatArrayArrayField(new float[][]{{13.0f, 14.0f}, null, {15.0f, 16.0f}})//
                    .doubleArrayArrayField(new double[][]{null, {16.0, 17.0}, {18.0, 19.0}})//
                    .stringArrayArrayField(new String[][]{{"string1", "string2"}, null,
                        {"string3", null, "string4"}})//
                    .objectArrayArrayField(new Object[][]{{new Object()}, null,
                        {new Object(), "string5", 20, 21L, null, new Object()}})//
                    .build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ArrayDataClass\",\"@id\":1,\"booleanArrayArrayField\":{\"@array\":\"[Z\",\"@length\":2,\"@items\":[{\"@array\":\"boolean\",\"@length\":2,\"@items\":[true,false]},{\"@array\":\"boolean\",\"@length\":2,\"@items\":[false,true]}]},\"booleanArrayField\":{\"@array\":\"boolean\",\"@length\":3,\"@items\":[true,false,true]},\"byteArrayArrayField\":{\"@array\":\"[B\",\"@length\":3,\"@items\":[{\"@value\":\"[B\",\"@text\":\"0102\"},null,{\"@value\":\"[B\",\"@text\":\"0304\"}]},\"byteArrayField\":{\"@value\":\"[B\",\"@text\":\"010203\"},\"charArrayArrayField\":{\"@array\":\"[C\",\"@length\":3,\"@items\":[{\"@array\":\"char\",\"@length\":2,\"@items\":[{\"@value\":\"java.lang.Character\",\"@text\":\"a\"},{\"@value\":\"java.lang.Character\",\"@text\":\"b\"}]},null,{\"@array\":\"char\",\"@length\":2,\"@items\":[{\"@value\":\"java.lang.Character\",\"@text\":\"c\"},{\"@value\":\"java.lang.Character\",\"@text\":\"d\"}]}]},\"charArrayField\":{\"@array\":\"char\",\"@length\":3,\"@items\":[{\"@value\":\"java.lang.Character\",\"@text\":\"a\"},{\"@value\":\"java.lang.Character\",\"@text\":\"b\"},{\"@value\":\"java.lang.Character\",\"@text\":\"c\"}]},\"doubleArrayArrayField\":{\"@array\":\"[D\",\"@length\":3,\"@items\":[null,{\"@array\":\"double\",\"@length\":2,\"@items\":[16.0,17.0]},{\"@array\":\"double\",\"@length\":2,\"@items\":[18.0,19.0]}]},\"doubleArrayField\":{\"@array\":\"double\",\"@length\":3,\"@items\":[16.0,17.0,18.0]},\"floatArrayArrayField\":{\"@array\":\"[F\",\"@length\":3,\"@items\":[{\"@array\":\"float\",\"@length\":2,\"@items\":[{\"@value\":\"java.lang.Float\",\"@text\":\"13.0\"},{\"@value\":\"java.lang.Float\",\"@text\":\"14.0\"}]},null,{\"@array\":\"float\",\"@length\":2,\"@items\":[{\"@value\":\"java.lang.Float\",\"@text\":\"15.0\"},{\"@value\":\"java.lang.Float\",\"@text\":\"16.0\"}]}]},\"floatArrayField\":{\"@array\":\"float\",\"@length\":3,\"@items\":[{\"@value\":\"java.lang.Float\",\"@text\":\"13.0\"},{\"@value\":\"java.lang.Float\",\"@text\":\"14.0\"},{\"@value\":\"java.lang.Float\",\"@text\":\"15.0\"}]},\"intArrayArrayField\":{\"@array\":\"[I\",\"@length\":3,\"@items\":[{\"@array\":\"int\",\"@length\":2,\"@items\":[7,8]},{\"@array\":\"int\",\"@length\":2,\"@items\":[9,10]},null]},\"intArrayField\":{\"@array\":\"int\",\"@length\":3,\"@items\":[7,8,9]},\"longArrayArrayField\":{\"@array\":\"[J\",\"@length\":3,\"@items\":[{\"@array\":\"long\",\"@length\":2,\"@items\":[{\"@value\":\"java.lang.Long\",\"@text\":\"10\"},{\"@value\":\"java.lang.Long\",\"@text\":\"11\"}]},null,{\"@array\":\"long\",\"@length\":2,\"@items\":[{\"@value\":\"java.lang.Long\",\"@text\":\"12\"},{\"@value\":\"java.lang.Long\",\"@text\":\"13\"}]}]},\"longArrayField\":{\"@array\":\"long\",\"@length\":3,\"@items\":[{\"@value\":\"java.lang.Long\",\"@text\":\"10\"},{\"@value\":\"java.lang.Long\",\"@text\":\"11\"},{\"@value\":\"java.lang.Long\",\"@text\":\"12\"}]},\"objectArrayArrayField\":{\"@array\":\"[Ljava.lang.Object;\",\"@length\":3,\"@items\":[{\"@array\":\"java.lang.Object\",\"@length\":1,\"@items\":[{\"@class\":\"java.lang.Object\",\"@id\":2}]},null,{\"@array\":\"java.lang.Object\",\"@length\":6,\"@items\":[{\"@class\":\"java.lang.Object\",\"@id\":3},\"string5\",20,{\"@value\":\"java.lang.Long\",\"@text\":\"21\"},null,{\"@class\":\"java.lang.Object\",\"@id\":4}]}]},\"objectArrayField\":{\"@array\":\"java.lang.Object\",\"@length\":3,\"@items\":[{\"@class\":\"java.lang.Object\",\"@id\":5},null,{\"@class\":\"java.lang.Object\",\"@id\":6}]},\"shortArrayArrayField\":{\"@array\":\"[S\",\"@length\":3,\"@items\":[{\"@array\":\"short\",\"@length\":2,\"@items\":[{\"@value\":\"java.lang.Short\",\"@text\":\"4\"},{\"@value\":\"java.lang.Short\",\"@text\":\"5\"}]},null,{\"@array\":\"short\",\"@length\":2,\"@items\":[{\"@value\":\"java.lang.Short\",\"@text\":\"6\"},{\"@value\":\"java.lang.Short\",\"@text\":\"7\"}]}]},\"shortArrayField\":{\"@array\":\"short\",\"@length\":3,\"@items\":[{\"@value\":\"java.lang.Short\",\"@text\":\"4\"},{\"@value\":\"java.lang.Short\",\"@text\":\"5\"},{\"@value\":\"java.lang.Short\",\"@text\":\"6\"}]},\"stringArrayArrayField\":{\"@array\":\"[Ljava.lang.String;\",\"@length\":3,\"@items\":[{\"@array\":\"java.lang.String\",\"@length\":2,\"@items\":[\"string1\",\"string2\"]},null,{\"@array\":\"java.lang.String\",\"@length\":3,\"@items\":[\"string3\",null,\"string4\"]}]},\"stringArrayField\":{\"@array\":\"java.lang.String\",\"@length\":4,\"@items\":[\"string1\",null,\"string2\",\"string3\"]}}"));
    }

    @Value
    static class TestCase {

        Object input;
        String expectedJson;
    }
}
