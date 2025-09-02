package com.github.hondams.magic.aop.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ValueTypeValueClass {

    boolean booleanField;
    byte byteField;
    char charField;
    short shortField;
    int intField;
    long longField;
    float floatField;
    double doubleField;
    Boolean booleanObjectField;
    Byte byteObjectField;
    Character charObjectField;
    Short shortObjectField;
    Integer intObjectField;
    Long longObjectField;
    Float floatObjectField;
    Double doubleObjectField;
    String stringField;
    Object objectField;
    java.util.Date dateField;
    java.sql.Date sqlDateField;
    java.sql.Time sqlTimeField;
    java.sql.Timestamp sqlTimestampField;
    java.time.LocalTime localTimeField;
    java.time.LocalDate localDateField;
    java.time.LocalDateTime localDateTimeField;
    java.time.ZonedDateTime zonedDateTimeField;
    java.time.OffsetDateTime offsetDateTimeField;
    java.math.BigInteger bigIntegerField;
    java.math.BigDecimal bigDecimalField;
    java.util.UUID uuidField;
    java.net.URL urlField;
    java.net.URI uriField;
    byte[] byteArrayField;
    SampleEnum enumField;
}
