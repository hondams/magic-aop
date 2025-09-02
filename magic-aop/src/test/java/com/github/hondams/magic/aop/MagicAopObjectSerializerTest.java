package com.github.hondams.magic.aop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.hondams.magic.aop.model.ArrayDataClass;
import com.github.hondams.magic.aop.model.DataClass;
import com.github.hondams.magic.aop.model.DataClassWithConstant;
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
import java.util.Map;
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
            new TestCase(//
                ValueTypeDataClass.builder().build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ValueTypeDataClass\",\"@id\":1,\"bigDecimalField\":null,\"bigIntegerField\":null,\"booleanField\":false,\"booleanObjectField\":null,\"byteArrayField\":null,\"byteField\":{\"@value\":\"java.lang.Byte\",\"text\":\"0\"},\"byteObjectField\":null,\"charField\":{\"@value\":\"java.lang.Character\",\"text\":\"\\u0000\"},\"charObjectField\":null,\"dateField\":null,\"doubleField\":0.0,\"doubleObjectField\":null,\"enumField\":null,\"floatField\":{\"@value\":\"java.lang.Float\",\"text\":\"0.0\"},\"floatObjectField\":null,\"intField\":0,\"intObjectField\":null,\"localDateField\":null,\"localDateTimeField\":null,\"localTimeField\":null,\"longField\":{\"@value\":\"java.lang.Long\",\"text\":\"0\"},\"longObjectField\":null,\"objectField\":null,\"offsetDateTimeField\":null,\"shortField\":{\"@value\":\"java.lang.Short\",\"text\":\"0\"},\"shortObjectField\":null,\"sqlDateField\":null,\"sqlTimeField\":null,\"sqlTimestampField\":null,\"stringField\":null,\"uriField\":null,\"urlField\":null,\"uuidField\":null,\"zonedDateTimeField\":null}"),
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
                "{\"@class\":\"com.github.hondams.magic.aop.model.ValueTypeDataClass\",\"@id\":1,\"bigDecimalField\":{\"@value\":\"java.math.BigDecimal\",\"text\":\"12345.67890\"},\"bigIntegerField\":{\"@value\":\"java.math.BigInteger\",\"text\":\"12345678901234567890\"},\"booleanField\":true,\"booleanObjectField\":false,\"byteArrayField\":{\"@value\":\"[B\",\"text\":\"0102030405\"},\"byteField\":{\"@value\":\"java.lang.Byte\",\"text\":\"1\"},\"byteObjectField\":{\"@value\":\"java.lang.Byte\",\"text\":\"7\"},\"charField\":{\"@value\":\"java.lang.Character\",\"text\":\"a\"},\"charObjectField\":{\"@value\":\"java.lang.Character\",\"text\":\"b\"},\"dateField\":{\"@value\":\"java.util.Date\",\"text\":\"2024-06-30T23:59:59.123\"},\"doubleField\":6.0,\"doubleObjectField\":12.0,\"enumField\":{\"@value\":\"com.github.hondams.magic.aop.model.SampleEnum\",\"text\":\"VALUE1\"},\"floatField\":{\"@value\":\"java.lang.Float\",\"text\":\"5.0\"},\"floatObjectField\":{\"@value\":\"java.lang.Float\",\"text\":\"11.0\"},\"intField\":3,\"intObjectField\":9,\"localDateField\":{\"@value\":\"java.time.LocalDate\",\"text\":\"2020-02-05\"},\"localDateTimeField\":{\"@value\":\"java.time.LocalDateTime\",\"text\":\"2019-06-30T23:59:59.123\"},\"localTimeField\":{\"@value\":\"java.time.LocalTime\",\"text\":\"23:59:59.123\"},\"longField\":{\"@value\":\"java.lang.Long\",\"text\":\"4\"},\"longObjectField\":{\"@value\":\"java.lang.Long\",\"text\":\"10\"},\"objectField\":{\"@class\":\"java.lang.Object\",\"@id\":2},\"offsetDateTimeField\":{\"@value\":\"java.time.OffsetDateTime\",\"text\":\"2020-04-07T14:15:16.000000456+09:00\"},\"shortField\":{\"@value\":\"java.lang.Short\",\"text\":\"2\"},\"shortObjectField\":{\"@value\":\"java.lang.Short\",\"text\":\"8\"},\"sqlDateField\":{\"@value\":\"java.sql.Date\",\"text\":\"2024-07-01\"},\"sqlTimeField\":{\"@value\":\"java.sql.Time\",\"text\":\"11:59:12\"},\"sqlTimestampField\":{\"@value\":\"java.sql.Timestamp\",\"text\":\"2024-09-01T23:59:59.123\"},\"stringField\":\"string1\",\"uriField\":{\"@value\":\"java.net.URI\",\"text\":\"https://www3.example.com/sample?param4=value5\"},\"urlField\":{\"@value\":\"java.net.URL\",\"text\":\"https://www2.example.com/sample?param3=value4\"},\"uuidField\":{\"@value\":\"java.util.UUID\",\"text\":\"123e4567-e89b-12d3-a456-426614174000\"},\"zonedDateTimeField\":{\"@value\":\"java.time.ZonedDateTime\",\"text\":\"2021-04-07T14:15:16.000000456+09:00[Asia/Tokyo]\"}}"),
            new TestCase(//
                VolatileValueTypeDataClass.builder().build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.VolatileValueTypeDataClass\",\"@id\":1,\"bigDecimalField\":null,\"bigIntegerField\":null,\"booleanField\":false,\"booleanObjectField\":null,\"byteArrayField\":null,\"byteField\":{\"@value\":\"java.lang.Byte\",\"text\":\"0\"},\"byteObjectField\":null,\"charField\":{\"@value\":\"java.lang.Character\",\"text\":\"\\u0000\"},\"charObjectField\":null,\"dateField\":null,\"doubleField\":0.0,\"doubleObjectField\":null,\"enumField\":null,\"floatField\":{\"@value\":\"java.lang.Float\",\"text\":\"0.0\"},\"floatObjectField\":null,\"intField\":0,\"intObjectField\":null,\"localDateField\":null,\"localDateTimeField\":null,\"localTimeField\":null,\"longField\":{\"@value\":\"java.lang.Long\",\"text\":\"0\"},\"longObjectField\":null,\"objectField\":null,\"offsetDateTimeField\":null,\"shortField\":{\"@value\":\"java.lang.Short\",\"text\":\"0\"},\"shortObjectField\":null,\"sqlDateField\":null,\"sqlTimeField\":null,\"sqlTimestampField\":null,\"stringField\":null,\"uriField\":null,\"urlField\":null,\"uuidField\":null,\"zonedDateTimeField\":null}"),
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
                "{\"@class\":\"com.github.hondams.magic.aop.model.VolatileValueTypeDataClass\",\"@id\":1,\"bigDecimalField\":{\"@value\":\"java.math.BigDecimal\",\"text\":\"12345.67890\"},\"bigIntegerField\":{\"@value\":\"java.math.BigInteger\",\"text\":\"12345678901234567890\"},\"booleanField\":true,\"booleanObjectField\":false,\"byteArrayField\":{\"@value\":\"[B\",\"text\":\"0102030405\"},\"byteField\":{\"@value\":\"java.lang.Byte\",\"text\":\"1\"},\"byteObjectField\":{\"@value\":\"java.lang.Byte\",\"text\":\"7\"},\"charField\":{\"@value\":\"java.lang.Character\",\"text\":\"a\"},\"charObjectField\":{\"@value\":\"java.lang.Character\",\"text\":\"b\"},\"dateField\":{\"@value\":\"java.util.Date\",\"text\":\"2024-06-30T23:59:59.123\"},\"doubleField\":6.0,\"doubleObjectField\":12.0,\"enumField\":{\"@value\":\"com.github.hondams.magic.aop.model.SampleEnum\",\"text\":\"VALUE1\"},\"floatField\":{\"@value\":\"java.lang.Float\",\"text\":\"5.0\"},\"floatObjectField\":{\"@value\":\"java.lang.Float\",\"text\":\"11.0\"},\"intField\":3,\"intObjectField\":9,\"localDateField\":{\"@value\":\"java.time.LocalDate\",\"text\":\"2020-02-05\"},\"localDateTimeField\":{\"@value\":\"java.time.LocalDateTime\",\"text\":\"2019-06-30T23:59:59.123\"},\"localTimeField\":{\"@value\":\"java.time.LocalTime\",\"text\":\"23:59:59.123\"},\"longField\":{\"@value\":\"java.lang.Long\",\"text\":\"4\"},\"longObjectField\":{\"@value\":\"java.lang.Long\",\"text\":\"10\"},\"objectField\":{\"@class\":\"java.lang.Object\",\"@id\":2},\"offsetDateTimeField\":{\"@value\":\"java.time.OffsetDateTime\",\"text\":\"2020-04-07T14:15:16.000000456+09:00\"},\"shortField\":{\"@value\":\"java.lang.Short\",\"text\":\"2\"},\"shortObjectField\":{\"@value\":\"java.lang.Short\",\"text\":\"8\"},\"sqlDateField\":{\"@value\":\"java.sql.Date\",\"text\":\"2024-07-01\"},\"sqlTimeField\":{\"@value\":\"java.sql.Time\",\"text\":\"11:59:12\"},\"sqlTimestampField\":{\"@value\":\"java.sql.Timestamp\",\"text\":\"2024-09-01T23:59:59.123\"},\"stringField\":\"string1\",\"uriField\":{\"@value\":\"java.net.URI\",\"text\":\"https://www3.example.com/sample?param4=value5\"},\"urlField\":{\"@value\":\"java.net.URL\",\"text\":\"https://www2.example.com/sample?param3=value4\"},\"uuidField\":{\"@value\":\"java.util.UUID\",\"text\":\"123e4567-e89b-12d3-a456-426614174000\"},\"zonedDateTimeField\":{\"@value\":\"java.time.ZonedDateTime\",\"text\":\"2021-04-07T14:15:16.000000456+09:00[Asia/Tokyo]\"}}"),
            new TestCase(//
                ValueTypeValueClass.builder().build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ValueTypeValueClass\",\"@id\":1,\"bigDecimalField\":null,\"bigIntegerField\":null,\"booleanField\":false,\"booleanObjectField\":null,\"byteArrayField\":null,\"byteField\":{\"@value\":\"java.lang.Byte\",\"text\":\"0\"},\"byteObjectField\":null,\"charField\":{\"@value\":\"java.lang.Character\",\"text\":\"\\u0000\"},\"charObjectField\":null,\"dateField\":null,\"doubleField\":0.0,\"doubleObjectField\":null,\"enumField\":null,\"floatField\":{\"@value\":\"java.lang.Float\",\"text\":\"0.0\"},\"floatObjectField\":null,\"intField\":0,\"intObjectField\":null,\"localDateField\":null,\"localDateTimeField\":null,\"localTimeField\":null,\"longField\":{\"@value\":\"java.lang.Long\",\"text\":\"0\"},\"longObjectField\":null,\"objectField\":null,\"offsetDateTimeField\":null,\"shortField\":{\"@value\":\"java.lang.Short\",\"text\":\"0\"},\"shortObjectField\":null,\"sqlDateField\":null,\"sqlTimeField\":null,\"sqlTimestampField\":null,\"stringField\":null,\"uriField\":null,\"urlField\":null,\"uuidField\":null,\"zonedDateTimeField\":null}"),
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
                "{\"@class\":\"com.github.hondams.magic.aop.model.ValueTypeValueClass\",\"@id\":1,\"bigDecimalField\":{\"@value\":\"java.math.BigDecimal\",\"text\":\"12345.67890\"},\"bigIntegerField\":{\"@value\":\"java.math.BigInteger\",\"text\":\"12345678901234567890\"},\"booleanField\":true,\"booleanObjectField\":false,\"byteArrayField\":{\"@value\":\"[B\",\"text\":\"0102030405\"},\"byteField\":{\"@value\":\"java.lang.Byte\",\"text\":\"1\"},\"byteObjectField\":{\"@value\":\"java.lang.Byte\",\"text\":\"7\"},\"charField\":{\"@value\":\"java.lang.Character\",\"text\":\"a\"},\"charObjectField\":{\"@value\":\"java.lang.Character\",\"text\":\"b\"},\"dateField\":{\"@value\":\"java.util.Date\",\"text\":\"2024-06-30T23:59:59.123\"},\"doubleField\":6.0,\"doubleObjectField\":12.0,\"enumField\":{\"@value\":\"com.github.hondams.magic.aop.model.SampleEnum\",\"text\":\"VALUE1\"},\"floatField\":{\"@value\":\"java.lang.Float\",\"text\":\"5.0\"},\"floatObjectField\":{\"@value\":\"java.lang.Float\",\"text\":\"11.0\"},\"intField\":3,\"intObjectField\":9,\"localDateField\":{\"@value\":\"java.time.LocalDate\",\"text\":\"2020-02-05\"},\"localDateTimeField\":{\"@value\":\"java.time.LocalDateTime\",\"text\":\"2019-06-30T23:59:59.123\"},\"localTimeField\":{\"@value\":\"java.time.LocalTime\",\"text\":\"23:59:59.123\"},\"longField\":{\"@value\":\"java.lang.Long\",\"text\":\"4\"},\"longObjectField\":{\"@value\":\"java.lang.Long\",\"text\":\"10\"},\"objectField\":{\"@class\":\"java.lang.Object\",\"@id\":2},\"offsetDateTimeField\":{\"@value\":\"java.time.OffsetDateTime\",\"text\":\"2020-04-07T14:15:16.000000456+09:00\"},\"shortField\":{\"@value\":\"java.lang.Short\",\"text\":\"2\"},\"shortObjectField\":{\"@value\":\"java.lang.Short\",\"text\":\"8\"},\"sqlDateField\":{\"@value\":\"java.sql.Date\",\"text\":\"2024-07-01\"},\"sqlTimeField\":{\"@value\":\"java.sql.Time\",\"text\":\"11:59:12\"},\"sqlTimestampField\":{\"@value\":\"java.sql.Timestamp\",\"text\":\"2024-09-01T23:59:59.123\"},\"stringField\":\"string1\",\"uriField\":{\"@value\":\"java.net.URI\",\"text\":\"https://www3.example.com/sample?param4=value5\"},\"urlField\":{\"@value\":\"java.net.URL\",\"text\":\"https://www2.example.com/sample?param3=value4\"},\"uuidField\":{\"@value\":\"java.util.UUID\",\"text\":\"123e4567-e89b-12d3-a456-426614174000\"},\"zonedDateTimeField\":{\"@value\":\"java.time.ZonedDateTime\",\"text\":\"2021-04-07T14:15:16.000000456+09:00[Asia/Tokyo]\"}}"),
            new TestCase(//
                ArrayDataClass.builder()//
                    .build(),//
                "{\"@class\":\"com.github.hondams.magic.aop.model.ArrayDataClass\",\"@id\":1,\"booleanArrayArrayField\":null,\"booleanArrayField\":null,\"byteArrayArrayField\":null,\"byteArrayField\":null,\"charArrayArrayField\":null,\"charArrayField\":null,\"doubleArrayArrayField\":null,\"doubleArrayField\":null,\"floatArrayArrayField\":null,\"floatArrayField\":null,\"intArrayArrayField\":null,\"intArrayField\":null,\"longArrayArrayField\":null,\"longArrayField\":null,\"objectArrayArrayField\":null,\"objectArrayField\":null,\"shortArrayArrayField\":null,\"shortArrayField\":null,\"stringArrayArrayField\":null,\"stringArrayField\":null}"));
    }

    @Value
    static class TestCase {

        Object input;
        String expectedJson;
    }
}
