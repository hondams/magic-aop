package com.github.hondams.magic.aop;

import com.github.hondams.magic.aop.util.MagicAopSampleUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MagicAopSampleApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        MagicAopSampleUtils.getSamplePackage();
        log.info("MagicAopSampleApplicationRunner run()");
    }
}
