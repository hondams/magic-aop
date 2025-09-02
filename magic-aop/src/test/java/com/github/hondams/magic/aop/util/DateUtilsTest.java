package com.github.hondams.magic.aop.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DateUtilsTest {

    @Test
    void testToStringAndToDate() {
        String isoDateTime = "2024-06-15T12:34:56.789";
        String isoDate = "2024-06-15";
        String isoTime = "12:34:56";

        assertEquals(isoDateTime, DateUtils.toString(DateUtils.toDate(isoDateTime)));
        assertEquals(isoDateTime, DateUtils.toString(DateUtils.toSqlTimestamp(isoDateTime)));
        assertEquals(isoDate, DateUtils.toSqlDate(isoDate).toString());
        assertEquals(isoTime, DateUtils.toSqlTime(isoTime).toString());

        System.out.println(java.time.ZonedDateTime.of(2022, 4, 7, 14, 15, 16, 456,
            java.time.ZoneId.of("Asia/Tokyo")));
        System.out.println(java.time.OffsetDateTime.of(2022, 4, 7, 14, 15, 16, 456,
            java.time.ZoneOffset.ofHours(9)));
        ;
    }
}
