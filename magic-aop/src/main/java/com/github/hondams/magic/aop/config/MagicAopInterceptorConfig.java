package com.github.hondams.magic.aop.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class MagicAopInterceptorConfig {

    private String interceptorClassName;
    private List<String> options = new ArrayList<>();
    private List<MagicAopInterceptionMethodConfig> interceptionMethods = new ArrayList<>();
}
