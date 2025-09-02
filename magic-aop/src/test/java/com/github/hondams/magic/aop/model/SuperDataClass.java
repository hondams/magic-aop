package com.github.hondams.magic.aop.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode
public class SuperDataClass {

    private String field1;
}
