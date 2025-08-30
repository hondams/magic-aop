package com.github.hondams.magic.aop;


import com.github.hondams.magic.aop.config.MagicAopParameter;
import com.github.hondams.magic.aop.config.MagicAopParameterProvider;
import com.github.hondams.magic.aop.logger.MagicAopLogger;
import com.github.hondams.magic.aop.module.ByteBuddyAopModule;
import com.github.hondams.magic.aop.module.MagicAopModule;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class MagicAopAgent {

    public static void premain(String agentArgs, Instrumentation inst) {

        MagicAopParameterProvider.init(agentArgs);
        MagicAopParameter parameter = MagicAopParameterProvider.getParameter();
        MagicAopLogger.setLogLevel(parameter.getLogLevel());

        appendToSystemClassLoader(inst, parameter.getLibraryDirectory());

        MagicAopModule module = new ByteBuddyAopModule();
        module.install(inst);

        MagicAopLogger.info("installed: " + parameter);
    }

    private static void appendToSystemClassLoader(Instrumentation inst, Path libraryDirectory) {

        try (Stream<Path> filesStream = Files.walk(libraryDirectory)) {
            Iterable<Path> files = filesStream::iterator;
            for (Path file : files) {
                if (file.toString().endsWith(".jar")) {
                    inst.appendToSystemClassLoaderSearch(new JarFile(file.toFile()));
                    MagicAopLogger.debug("Add system classpath: " + file.toAbsolutePath());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
