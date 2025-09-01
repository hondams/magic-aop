package com.github.hondams.magic.aop.logger;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MagicAopLogger {

    @Getter
    @Setter
    private MagicAopLogLevel logLevel = MagicAopLogLevel.INFO;

    public void info(String message) {
        if (0 < MagicAopLogLevel.INFO.compareTo(logLevel)) {
            return;
        }
        System.out.println("[MAGIC_AOP] [INFO ] " + message);
    }

    public void debug(String message) {
        if (0 < MagicAopLogLevel.DEBUG.compareTo(logLevel)) {
            return;
        }
        System.out.println("[MAGIC_AOP] [DEBUG] " + message);
    }

    public void warn(String message) {
        if (0 < MagicAopLogLevel.WARN.compareTo(logLevel)) {
            return;
        }
        System.out.println("[MAGIC_AOP] [WARN ] " + message);
    }

    public void error(String message) {
        if (0 < MagicAopLogLevel.ERROR.compareTo(logLevel)) {
            return;
        }
        System.err.println("[MAGIC_AOP] [ERROR] " + message);
    }
}
