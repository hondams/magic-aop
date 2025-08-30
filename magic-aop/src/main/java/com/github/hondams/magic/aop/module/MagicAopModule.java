package com.github.hondams.magic.aop.module;

import java.lang.instrument.Instrumentation;

public interface MagicAopModule {

    void install(Instrumentation inst);
}
