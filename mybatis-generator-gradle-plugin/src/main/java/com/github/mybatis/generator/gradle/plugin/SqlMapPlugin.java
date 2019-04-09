package com.github.mybatis.generator.gradle.plugin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouxinghai on 2016-02-17 .
 */
public class SqlMapPlugin extends PluginAdapter {

    private static final Logger  logger = LoggerFactory.getLogger(SqlMapPlugin.class);

    /**
     * 是否合并代码
     * 系统针对xml写死了,true
     */
    private              Boolean isMergeable;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        isMergeable = Boolean.valueOf(properties.getProperty("isMergeable"));
    }

    /**
     * 修复bug
     * mapper.xml默认合并
     * @param sqlMap
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        try {
            Field field = sqlMap.getClass().getDeclaredField("isMergeable");
            field.setAccessible(true);
            field.set(sqlMap, isMergeable);
        } catch (Exception e) {
            logger.error("SqlMapPlugin sqlMapGenerated error ", e);
            throw new RuntimeException(e);
        }
        return super.sqlMapGenerated(sqlMap, introspectedTable);
    }
}
