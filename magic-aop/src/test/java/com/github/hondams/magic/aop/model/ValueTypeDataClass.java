package com.github.hondams.magic.aop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValueTypeDataClass {

    private boolean booleanField;
    private byte byteField;
    private char charField;
    private short shortField;
    private int intField;
    private long longField;
    private float floatField;
    private double doubleField;
    private Boolean booleanObjectField;
    private Byte byteObjectField;
    private Character charObjectField;
    private Short shortObjectField;
    private Integer intObjectField;
    private Long longObjectField;
    private Float floatObjectField;
    private Double doubleObjectField;
    private String stringField;
    private Object objectField;
    private java.util.Date dateField;
    private java.sql.Date sqlDateField;
    private java.sql.Time sqlTimeField;
    private java.sql.Timestamp sqlTimestampField;
    private java.time.LocalTime localTimeField;
    private java.time.LocalDate localDateField;
    private java.time.LocalDateTime localDateTimeField;
    private java.time.ZonedDateTime zonedDateTimeField;
    private java.time.OffsetDateTime offsetDateTimeField;
    private java.math.BigInteger bigIntegerField;
    private java.math.BigDecimal bigDecimalField;
    private java.util.UUID uuidField;
    private java.net.URL urlField;
    private java.net.URI uriField;
    private byte[] byteArrayField;
    private SampleEnum enumField;
}
