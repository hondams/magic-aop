package com.github.hondams.magic.aop.config;


import com.github.hondams.magic.aop.logger.MagicAopLogLevel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class MagicAopParameter {

    private Path libraryDirectory;
    private Path configFile;
    private List<MagicAopInterceptorConfig> interceptors = new ArrayList<>();
    private MagicAopLogLevel logLevel = MagicAopLogLevel.INFO;
}
