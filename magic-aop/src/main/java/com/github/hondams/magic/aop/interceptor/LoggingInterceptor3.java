package com.github.hondams.magic.aop.interceptor;

import com.github.hondams.magic.aop.util.ClassUtils;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

@SuppressWarnings("unused")
public class LoggingInterceptor3 {

    private static final String INTERCEPTOR_CLASS_NAME = "com.github.hondams.magic.aop.interceptor.LoggingInterceptorImpl";

    private static final AtomicReference<LoggingInterceptorInterface> interceptorHolder = new AtomicReference<>();

    @RuntimeType
    public static Object intercept(@This(optional = true) Object instance, @Origin Method method,
        @SuperCall Callable<?> methodInvoker, @AllArguments Object[] args) throws Exception {

        LoggingInterceptorInterface interceptor = getInterceptor();
        return interceptor.intercept(instance, method, methodInvoker, args);
    }

    private static LoggingInterceptorInterface getInterceptor() {
        LoggingInterceptorInterface interceptor = interceptorHolder.get();
        if (interceptor == null) {
            try {
                Class<?> interceptorClass = Thread.currentThread().getContextClassLoader()
                    .loadClass(INTERCEPTOR_CLASS_NAME);
                interceptor = (LoggingInterceptorInterface) ClassUtils.newInstance(
                    interceptorClass);
                boolean updated = interceptorHolder.compareAndSet(null, interceptor);
                if (!updated) {
                    interceptor = interceptorHolder.get();
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }
        return interceptor;
    }
}
