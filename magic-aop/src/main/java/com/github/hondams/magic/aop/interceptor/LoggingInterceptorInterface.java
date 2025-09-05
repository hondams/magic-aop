package com.github.hondams.magic.aop.interceptor;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public interface LoggingInterceptorInterface {

    Object intercept(Object instance, Method method, Callable<?> methodInvoker, Object[] args)
        throws Exception;
}
