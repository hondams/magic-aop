package com.github.hondams.magic.aop.config;

import com.github.hondams.magic.aop.logger.MagicAopLogLevel;
import com.github.hondams.magic.aop.logger.MagicAopLogger;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MagicAopParameterProvider {

    @Getter
    private static MagicAopParameter parameter;

    public void init(String agentArgs) {

        parameter = new MagicAopParameter();

        String[] keyValues = agentArgs.split(";");
        for (String keyValue : keyValues) {
            int index = keyValue.indexOf("=");
            if (index < 0) {
                MagicAopLogger.error("Invalid agent arguments: " + keyValue);
            } else {
                String key = keyValue.substring(0, index);
                String value = keyValue.substring(index + 1);
                switch (key) {
                    case "libdir" -> //
                        parameter.setLibraryDirectory(Path.of(value));
                    case "config" -> //
                        parameter.setConfigFile(Path.of(value));
                    case "loglevel" -> //
                        parameter.setLogLevel(MagicAopLogLevel.valueOf(value));
                    default -> throw new IllegalArgumentException("Unknown key: " + key);
                }
            }
        }

        validate(parameter);
    }

    private void validate(MagicAopParameter parameter) {
        boolean error = false;

        if (parameter.getLibraryDirectory() == null) {
            MagicAopLogger.error("Missing library directory (libdir)");
            error = true;
        } else if (!Files.isDirectory(parameter.getLibraryDirectory())) {
            MagicAopLogger.error("Library directory (libdir) is not a directory: "
                + parameter.getLibraryDirectory());
            error = true;
        }

        if (parameter.getConfigFile() == null) {
            MagicAopLogger.error("Missing config file (config)");
            error = true;
        } else if (!Files.isRegularFile(parameter.getConfigFile())) {
            MagicAopLogger.error(
                "Config file (config) is not a file: " + parameter.getConfigFile());
            error = true;
        }

        if (error) {
            throw new IllegalArgumentException("Invalid agent arguments");
        }
    }
}
