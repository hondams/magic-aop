package com.github.hondams.magic.aop.util;

import java.util.Locale;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LocaleUtils {

    public Locale create(String text) {
        String[] parts = text.split("_");
        if (parts.length == 1) {
            return new Locale(parts[0]);
        } else if (parts.length == 2) {
            return new Locale(parts[0], parts[1]);
        } else if (parts.length == 3) {
            return new Locale(parts[0], parts[1], parts[2]);
        } else {
            return Locale.ROOT;
        }
    }
}
