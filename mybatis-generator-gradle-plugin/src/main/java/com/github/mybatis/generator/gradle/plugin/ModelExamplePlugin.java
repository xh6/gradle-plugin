package com.github.mybatis.generator.gradle.plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouxinghai on 2016-02-17 .
 */
public class ModelExamplePlugin extends PluginAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ModelExamplePlugin.class);

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        try {
            FullyQualifiedJavaType type = topLevelClass.getType();
            topLevelClass.addSuperInterface(new FullyQualifiedJavaType("Serializable"));
            topLevelClass.addImportedType("java.io.Serializable");

            org.mybatis.generator.api.dom.java.Field field = new org.mybatis.generator.api.dom.java.Field();
            field.setVisibility(JavaVisibility.PRIVATE);
            field.setStatic(true);
            field.setFinal(true);
            field.setType(new FullyQualifiedJavaType("long"));
            field.setName("serialVersionUID = 1L");

            List<org.mybatis.generator.api.dom.java.Field> fields = new ArrayList<>();
            fields.add(field);
            fields.addAll(topLevelClass.getFields());
            topLevelClass.getFields().clear();
            topLevelClass.getFields().addAll(fields);

            Field packageField = type.getClass().getDeclaredField("packageName");
            packageField.setAccessible(true);
            String packageName = packageField.get(type).toString();
            String newPackageName = packageName + ".example";
            packageField.set(type, newPackageName);

            Field baseQualifiedNameField = type.getClass().getDeclaredField("baseQualifiedName");
            baseQualifiedNameField.setAccessible(true);
            String baseQualifiedName = baseQualifiedNameField.get(type).toString();
            String newBaseQualifiedName = baseQualifiedName.replace(packageName, newPackageName);
            baseQualifiedNameField.set(type, newBaseQualifiedName);

            Field internalAttributesField = introspectedTable.getClass().getSuperclass().getDeclaredField("internalAttributes");
            internalAttributesField.setAccessible(true);
            Map<Object, String> internalAttributes = (Map<Object, String>) internalAttributesField.get(introspectedTable);
            for (Map.Entry<Object, String> entry : internalAttributes.entrySet()) {
                if (entry.getValue().equals(baseQualifiedName)) {
                    entry.setValue(newBaseQualifiedName);
                }
                //                logger.info("{} {}", entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }

}
