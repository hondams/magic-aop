package com.github.hondams.magic.aop.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MagicAopSampleUtils {

    public static String getSamplePackage() {
        testAbc();
        return MagicAopSampleUtils.class.getPackageName().replace(".util", "");
    }

    private static void testAbc() {
        System.out.println("testAbc");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
