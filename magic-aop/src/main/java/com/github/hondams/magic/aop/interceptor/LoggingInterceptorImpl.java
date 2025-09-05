package com.github.hondams.magic.aop.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.hondams.magic.aop.util.NanoTimeUtils;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class LoggingInterceptorImpl implements LoggingInterceptorInterface {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object intercept(Object instance, Method method, Callable<?> methodInvoker,
        Object[] args) throws Exception {

        Class<?> methodClass = getMethodClass(instance, method);
        List<String> parameterTypeNames = getParameterTypeNames(method);
        String methodSignature =
            methodClass.getTypeName() + ":" + method.getName() + "(" + String.join(", ",
                parameterTypeNames) + ")";

        Logger logger = LoggerFactory.getLogger(methodClass);

        long startTime = System.nanoTime();
        try {
            logger.info("[MAGIC_AOP_TRACE START] {} args={}",//
                methodSignature, toText(method, args));
            Object result = methodInvoker.call();
            if (method.getReturnType() == void.class) {
                logger.info("[MAGIC_AOP_TRACE END  ] {} elapsedTime={}ms",//
                    methodSignature,//
                    NanoTimeUtils.toMillis(System.nanoTime() - startTime));
            } else {
                logger.info("[MAGIC_AOP_TRACE END  ] {} result={} elapsedTime={}ms",//
                    methodSignature,//
                    toText(result),//
                    NanoTimeUtils.toMillis(System.nanoTime() - startTime));
            }

            return result;
        } catch (Exception e) {
            logger.error("[MAGIC_AOP_TRACE ERROR] {} elapsedTime={}ms",//
                methodSignature,//
                NanoTimeUtils.toMillis(System.nanoTime() - startTime), e);
            throw e;
        }
    }

    private Class<?> getMethodClass(Object instance, Method method) {
        if (instance != null) {
            return instance.getClass();
        } else {
            return method.getDeclaringClass();
        }
    }

    private List<String> getParameterTypeNames(Method method) {
        List<String> parameterTypeNames = new java.util.ArrayList<>();
        for (Class<?> paramType : method.getParameterTypes()) {
            parameterTypeNames.add(paramType.getTypeName());
        }
        return parameterTypeNames;
    }

    private String toText(Method method, Object[] args) {
        Map<String, Object> map = new LinkedHashMap<>();
        int index = 0;
        for (Parameter parameter : method.getParameters()) {
            map.put(parameter.getName(), args[index]);
            index++;
        }
        try {
            return this.objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            return String.valueOf(map);
        }
    }

    private String toText(Object obj) {

        if (obj == null) {
            return "<null>";
        } else if (obj instanceof String) {
            return "\"" + StringEscapeUtils.escapeJson((String) obj) + "\"";
        } else if (obj instanceof Number || obj instanceof Boolean) {
            return String.valueOf(obj);
        } else {
            try {
                return this.objectMapper.writeValueAsString(obj);
            } catch (Exception e) {
                return String.valueOf(obj);
            }
        }
    }
}
