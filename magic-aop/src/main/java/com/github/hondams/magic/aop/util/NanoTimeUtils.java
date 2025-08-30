package com.github.hondams.magic.aop.util;

import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NanoTimeUtils {

    public double toMillis(long nanoTime) {
        return TimeUnit.NANOSECONDS.toMicros(nanoTime) / 1_000.0d;
    }
}
