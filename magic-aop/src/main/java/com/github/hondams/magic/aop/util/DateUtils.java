package com.github.hondams.magic.aop.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtils {

    private final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

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

    public java.sql.Date toJavaSqlDate(String text) {
        return new java.sql.Date(toDate(text).getTime());
    }

    public java.sql.Timestamp toJavaSqlTimestamp(String text) {
        return new java.sql.Timestamp(toDate(text).getTime());
    }

    public java.sql.Time toJavaSqlTime(String text) {
        return new java.sql.Time(toDate(text).getTime());
    }
}
