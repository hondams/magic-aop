package com.github.hondams.magic.aop;


import com.github.hondams.magic.aop.module.ByteBuddyAopModule;
import com.github.hondams.magic.aop.module.MagicAopModule;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class MagicAopAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        Path libraryDirectory = Path.of(
            "C:\\projects\\github\\hondams\\magic-aop\\magic-aop\\target\\libs");
        appendToSystemClassLoader(inst, libraryDirectory);
        //ClassLoader classLoader = createClassLoader(libraryDirectory);
        MagicAopModule module = new ByteBuddyAopModule();
        module.install(inst);
    }

    private static void appendToSystemClassLoader(Instrumentation inst, Path libraryDirectory) {

        try (Stream<Path> filesStream = Files.walk(libraryDirectory)) {
            Iterable<Path> files = filesStream::iterator;
            for (Path file : files) {
                if (file.toString().endsWith(".jar")) {
                    inst.appendToSystemClassLoaderSearch(new JarFile(file.toFile()));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static MagicAopModule loadModule(ClassLoader classLoader, String className) {
        try {
            Class<?> clazz = classLoader.loadClass(className);
            return (MagicAopModule) clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }

    }

    private static ClassLoader createClassLoader(Path libraryDirectory) {
        List<URL> urlList = new ArrayList<>();
        try (Stream<Path> filesStream = Files.walk(libraryDirectory)) {
            Iterable<Path> files = filesStream::iterator;
            for (Path file : files) {
                if (file.toString().endsWith(".jar")) {
                    urlList.add(file.toUri().toURL());
                }
            }
            URL[] urls = urlList.toArray(new URL[0]);
            for (URL url : urls) {
                System.out.println("Load library: " + url);
            }
            return new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
