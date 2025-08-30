package com.github.hondams.magic.aop.module;

import com.github.hondams.magic.aop.interceptor.LoggingInterceptor;
import java.lang.instrument.Instrumentation;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

public class ByteBuddyAopModule implements MagicAopModule {

    @Override
    public void install(Instrumentation inst) {
        AgentBuilder agentBuilder = new AgentBuilder.Default()//
            .type(ElementMatchers.nameStartsWith("com.github.hondams.magic.aop"))
            .transform((builder, typeDescription, classLoader, module, protectionDomain) ->//
                builder.method(ElementMatchers.any())
                    .intercept(MethodDelegation.to(LoggingInterceptor.class)));
        //			.type(ElementMatchers.nameStartsWith("com.example.service"))
        //			.transform((builder, typeDescription, classLoader, module) ->
        //				builder.method(ElementMatchers.any())
        //					.intercept(MethodDelegation.to(LoggingInterceptor.class))
        //			)
        agentBuilder.installOn(inst);
    }
}
