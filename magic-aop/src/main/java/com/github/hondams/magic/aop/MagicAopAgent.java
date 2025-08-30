package com.github.hondams.magic.aop;


import com.github.hondams.magic.aop.interceptor.LoggingInterceptor;
import java.lang.instrument.Instrumentation;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

public class MagicAopAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        AgentBuilder agentBuilder = new AgentBuilder.Default();

        agentBuilder.type(
                ElementMatchers.named("com.github.hondams.magic.aop.MagicAopSampleApplicationRunner"))
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
