package com.github.hondams.magic.aop.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MagicAopInterceptorConfigFactory {

    public List<MagicAopInterceptorConfig> createList(Path configFile) {
        try {
            List<MagicAopInterceptorConfig> interceptors = new ArrayList<>();
            MagicAopInterceptorConfig interceptor = null;

            List<String> lines = Files.readAllLines(configFile);
            int lineNumber = 1;
            for (String line : lines) {
                if (!line.startsWith("#") && !line.isBlank()) {
                    String[] values = line.split("\t");
                    if (interceptor == null) {
                        if (values[0].isBlank()) {
                            throw new IllegalStateException(
                                "The first column must be the interceptor class name. (line: "
                                    + lineNumber + ")");
                        } else {
                            interceptor = createInterceptor(values);
                            interceptors.add(interceptor);
                        }
                    } else {
                        if (values[0].isBlank()) {
                            interceptor.getInterceptionMethods()
                                .add(createInterceptionMethod(lineNumber, values));
                        } else {
                            interceptor = createInterceptor(values);
                            interceptors.add(interceptor);
                        }
                    }
                }
                lineNumber++;
            }
            return interceptors;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private MagicAopInterceptorConfig createInterceptor(String[] values) {
        MagicAopInterceptorConfig interceptor = new MagicAopInterceptorConfig();
        interceptor.setInterceptorClassName(values[0]);
        for (int i = 1; i < values.length; i++) {
            if (values[i].startsWith("#")) {
                break;
            }
            interceptor.getOptions().add(values[i]);
        }
        return interceptor;
    }

    private MagicAopInterceptionMethodConfig createInterceptionMethod(int lineNumber,
        String[] values) {
        MagicAopInterceptionMethodConfig interceptionMethod = new MagicAopInterceptionMethodConfig();
        if (values.length < 2 || values[1].isBlank()) {
            throw new IllegalStateException(
                "The first column must be the type pattern. (line: " + lineNumber + ")");
        }
        if (values.length < 3 || values[2].isBlank()) {
            throw new IllegalStateException(
                "The second column must be the method pattern. (line: " + lineNumber + ")");
        }
        interceptionMethod.setTypePattern(values[1]);
        interceptionMethod.setMethodPattern(values[2]);
        for (int i = 3; i < values.length; i++) {
            if (values[i].startsWith("#")) {
                break;
            }
            interceptionMethod.getOptions().add(values[i]);
        }
        return interceptionMethod;
    }
}
