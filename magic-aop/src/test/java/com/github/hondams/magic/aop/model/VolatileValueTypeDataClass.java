package com.github.hondams.magic.aop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolatileValueTypeDataClass {

    private volatile boolean booleanField;
    private volatile byte byteField;
    private volatile char charField;
    private volatile short shortField;
    private volatile int intField;
    private volatile long longField;
    private volatile float floatField;
    private volatile double doubleField;
    private volatile Boolean booleanObjectField;
    private volatile Byte byteObjectField;
    private volatile Character charObjectField;
    private volatile Short shortObjectField;
    private volatile Integer intObjectField;
    private volatile Long longObjectField;
    private volatile Float floatObjectField;
    private volatile Double doubleObjectField;
    private volatile String stringField;
    private volatile Object objectField;
    private volatile java.util.Date dateField;
    private volatile java.sql.Date sqlDateField;
    private volatile java.sql.Time sqlTimeField;
    private volatile java.sql.Timestamp sqlTimestampField;
    private volatile java.time.LocalTime localTimeField;
    private volatile java.time.LocalDate localDateField;
    private volatile java.time.LocalDateTime localDateTimeField;
    private volatile java.time.ZonedDateTime zonedDateTimeField;
    private volatile java.time.OffsetDateTime offsetDateTimeField;
    private volatile java.math.BigInteger bigIntegerField;
    private volatile java.math.BigDecimal bigDecimalField;
    private volatile java.util.UUID uuidField;
    private volatile java.net.URL urlField;
    private volatile java.net.URI uriField;
    private volatile byte[] byteArrayField;
    private volatile SampleEnum enumField;
}
