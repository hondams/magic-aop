package com.github.hondams.magic.aop.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataClassWithConstant {

    private static final String CONSTANT1 = "constant1";

    private String field1;
}
