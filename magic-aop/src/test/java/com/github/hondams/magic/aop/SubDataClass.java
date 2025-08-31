package com.github.hondams.magic.aop;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true, doNotUseGetters = true)
@EqualsAndHashCode(callSuper = true)
public class SubDataClass extends SuperDataClass {

    private String field1;
}
