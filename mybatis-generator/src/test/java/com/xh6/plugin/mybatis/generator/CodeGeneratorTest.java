package com.xh6.plugin.mybatis.generator;

import org.junit.Test;

public class CodeGeneratorTest {

    @Test
    public void test() {
        String configFile = "/project/gradle-plugin/mybatis-generator/src/test/resources/generatorConfig.xml";
        CodeGenerator.getInstance().createCode(null, configFile);
    }
}
