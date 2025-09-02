package com.github.hondams.magic.aop.util;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtils {

    private final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private final DateTimeFormatter DATE_TIME_FORMATTER =//
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private final DateTimeFormatter TIME_FORMATTER =//
        DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public String toString(java.util.Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_FORMAT);
        return sdf.format(date);
    }

    public java.util.Date toDate(String text) {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_FORMAT);
        try {
            return sdf.parse(text);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + text, e);
        }
    }

    public java.sql.Date toSqlDate(String text) {
        return Date.valueOf(text);
    }

    public java.sql.Timestamp toSqlTimestamp(String text) {
        return new java.sql.Timestamp(toDate(text).getTime());
    }

    public java.sql.Time toSqlTime(String text) {
        return Time.valueOf(text);
    }

    public LocalDateTime toLocalDateTime(String text) {
        return LocalDateTime.parse(text, DATE_TIME_FORMATTER);
    }

    public LocalDate toLocalDate(String text) {
        return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public LocalTime toLocalTime(String text) {
        return LocalTime.parse(text, TIME_FORMATTER);
    }

    public ZonedDateTime toZonedDateTime(String text) {
        return ZonedDateTime.parse(text, DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    public OffsetDateTime toOffsetDateTime(String text) {
        return OffsetDateTime.parse(text, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
