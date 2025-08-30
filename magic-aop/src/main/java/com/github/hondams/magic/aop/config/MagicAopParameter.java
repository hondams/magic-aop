package com.github.hondams.magic.aop.config;


import com.github.hondams.magic.aop.logger.MagicAopLogLevel;
import java.nio.file.Path;
import lombok.Data;

@Data
public class MagicAopParameter {

    private Path libraryDirectory;
    private Path configFile;
    private MagicAopLogLevel logLevel = MagicAopLogLevel.INFO;
}
