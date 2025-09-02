package com.github.hondams.magic.aop.util;

import java.net.MalformedURLException;
import java.net.URL;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UrlUtils {

    public URL toUrl(String text) {
        try {
            return new URL(text);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + text, e);
        }
    }
}
