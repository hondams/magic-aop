package com.github.hondams.magic.aop.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class MagicAopInterceptionMethodConfig {

    private String typePattern;
    private String methodPattern;
    private List<String> options = new ArrayList<>();
}
