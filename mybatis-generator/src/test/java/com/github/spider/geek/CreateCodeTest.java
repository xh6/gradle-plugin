package com.github.spider.geek;

import com.xh6.plugin.mybatis.generator.MybatisCodeGenerator;
import org.junit.Test;

public class CreateCodeTest {

    @Test
    public void createCode() {
        String configFile = "/project/gradle-plugin/mybatis-generator/src/test/resources/generatorConfig.xml";
        MybatisCodeGenerator.getInstance().createCode(null, configFile);
    }
}
