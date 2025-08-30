package com.github.hondams.magic.aop.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingInterceptor {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @RuntimeType
    public static Object intercept(@This(optional = true) Object instance, @Origin Method method,
        @SuperCall Callable<?> methodInvoker, @AllArguments Object[] args) throws Exception {

        Class<?> methodClass = getMethodClass(instance, method);
        List<String> parameterTypeNames = getParameterTypeNames(method);
        String methodSignature =
            methodClass.getTypeName() + ":" + method.getName() + "(" + String.join(", ",
                parameterTypeNames) + ")";

        Logger logger = LoggerFactory.getLogger(methodClass);

        long startTime = System.currentTimeMillis();
        try {
            logger.info("[AOP START] {} args={}",//
                methodSignature, toText(method, args));
            Object result = methodInvoker.call();
            logger.info("[AOP END  ] {} result={} elapsedTime={}ms",//
                methodSignature,//
                toText(result),//
                TimeUnit.NANOSECONDS.toMicros(System.currentTimeMillis() - startTime));
            return result;
        } catch (Exception e) {
            logger.error("[AOP ERROR] {} elapsedTime={}ms",//
                methodSignature,//
                TimeUnit.NANOSECONDS.toMicros(System.currentTimeMillis() - startTime), e);
            throw e;
        }
    }

    private static Class<?> getMethodClass(Object instance, Method method) {
        if (instance != null) {
            return instance.getClass();
        } else {
            return method.getDeclaringClass();
        }
    }

    private static List<String> getParameterTypeNames(Method method) {
        List<String> parameterTypeNames = new java.util.ArrayList<>();
        for (Class<?> paramType : method.getParameterTypes()) {
            parameterTypeNames.add(paramType.getTypeName());
        }
        return parameterTypeNames;
    }

    private static String toText(Method method, Object[] args) {
        Map<String, Object> map = new LinkedHashMap<>();
        int index = 0;
        for (Parameter parameter : method.getParameters()) {
            map.put(parameter.getName(), args[index]);
            index++;
        }
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            return String.valueOf(map);
        }
    }

    private static String toText(Object obj) {

        if (obj == null) {
            return "<null>";
        } else if (obj instanceof String) {
            return "\"" + StringEscapeUtils.escapeJson((String) obj) + "\"";
        } else if (obj instanceof Number || obj instanceof Boolean) {
            return String.valueOf(obj);
        } else {
            try {
                return objectMapper.writeValueAsString(obj);
            } catch (Exception e) {
                return String.valueOf(obj);
            }
        }
    }
}
