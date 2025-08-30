package com.github.hondams.magic.aop.module;

import com.github.hondams.magic.aop.config.MagicAopInterceptionMethodConfig;
import com.github.hondams.magic.aop.config.MagicAopInterceptorConfig;
import com.github.hondams.magic.aop.config.MagicAopParameterProvider;
import com.github.hondams.magic.aop.logger.MagicAopLogger;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

public class ByteBuddyAopModule implements MagicAopModule {

    @Override
    public void install(Instrumentation inst) {
        AgentBuilder agentBuilder = new AgentBuilder.Default();

        for (MagicAopInterceptorConfig interceptorConfig ://
            MagicAopParameterProvider.getParameter().getInterceptors()) {
            Class<?> interceptorClass;
            try {
                interceptorClass = Class.forName(interceptorConfig.getInterceptorClassName());
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }

            Map<String, List<String>> methodToTypesMap = new TreeMap<>();
            for (MagicAopInterceptionMethodConfig interceptionMethod ://
                interceptorConfig.getInterceptionMethods()) {
                List<String> typePatterns = methodToTypesMap.computeIfAbsent(
                    interceptionMethod.getMethodPattern(), k -> new ArrayList<>());
                typePatterns.add(interceptionMethod.getTypePattern());
            }

            Map<String, List<String>> typeToMethodsMap = new TreeMap<>();
            for (Map.Entry<String, List<String>> entry : methodToTypesMap.entrySet()) {
                String methodPattern = entry.getKey();
                List<String> typePatterns = entry.getValue();
                if (typePatterns.size() == 1) {
                    List<String> methodPatterns = typeToMethodsMap.computeIfAbsent(
                        typePatterns.get(0), k -> new ArrayList<>());
                    methodPatterns.add(methodPattern);
                } else {

                    MagicAopLogger.debug(
                        "Register interceptor: interceptor=" + interceptorClass.getName()//
                            + ", methods=" + typePatterns + ": " + methodPattern);
                    agentBuilder = agentBuilder.type(typeMatcher(typePatterns))//
                        .transform(
                            (builder, typeDescription, classLoader, module, protectionDomain) ->//
                                builder.method(methodMatcher(methodPattern))
                                    .intercept(MethodDelegation.to(interceptorClass)));
                }
            }
            for (Map.Entry<String, List<String>> entry : typeToMethodsMap.entrySet()) {
                String typePattern = entry.getKey();
                List<String> methodPatterns = entry.getValue();

                MagicAopLogger.debug(
                    "Register interceptor: interceptor=" + interceptorClass.getName()//
                        + ", methods=" + typePattern + ": " + methodPatterns);

                agentBuilder = agentBuilder.type(typeMatcher(typePattern))//
                    .transform(
                        (builder, typeDescription, classLoader, module, protectionDomain) ->//
                            builder.method(methodMatcher(methodPatterns))
                                .intercept(MethodDelegation.to(interceptorClass)));
            }
        }

        agentBuilder.installOn(inst);
    }


    private <T extends NamedElement> ElementMatcher.Junction<T> typeMatcher(List<String> patterns) {

        ElementMatcher.Junction<T> matcher = null;
        for (String pattern : patterns) {
            if (matcher == null) {
                matcher = typeMatcher(pattern);
            } else {
                matcher = matcher.or(typeMatcher(pattern));
            }
        }
        return matcher;
    }

    private <T extends NamedElement> ElementMatcher.Junction<T> typeMatcher(String pattern) {

        if (pattern.trim().startsWith("^") && pattern.endsWith("$")) {
            return ElementMatchers.nameMatches(pattern.trim());
        }

        return nameMatcher(pattern);
    }

    private <T extends NamedElement> ElementMatcher.Junction<T> methodMatcher(
        List<String> patterns) {

        ElementMatcher.Junction<T> matcher = null;
        for (String pattern : patterns) {
            if (matcher == null) {
                matcher = methodMatcher(pattern);
            } else {
                matcher = matcher.or(methodMatcher(pattern));
            }
        }
        return matcher;
    }

    private <T extends NamedElement> ElementMatcher.Junction<T> methodMatcher(String pattern) {

        if (pattern.trim().equals("*")) {
            return ElementMatchers.any();
        }

        if (pattern.trim().startsWith("^") && pattern.endsWith("$")) {
            return ElementMatchers.nameMatches(pattern.trim());
        }

        ElementMatcher.Junction<T> matcher = null;
        String[] methodNames = pattern.split(",");
        for (String methodName : methodNames) {
            if (matcher == null) {
                matcher = nameMatcher(methodName);
            } else {
                matcher = matcher.or(nameMatcher(methodName));
            }
        }
        return matcher;
    }

    private <T extends NamedElement> ElementMatcher.Junction<T> nameMatcher(String pattern) {
        if (pattern.contains("*")) {
            String regex = "^" + pattern.trim().replace(".", "\\.")//
                .replace("*", ".[^\\.]+") + "$";
            return ElementMatchers.nameMatches(regex);
        } else {
            return ElementMatchers.named(pattern.trim());
        }
    }
}
