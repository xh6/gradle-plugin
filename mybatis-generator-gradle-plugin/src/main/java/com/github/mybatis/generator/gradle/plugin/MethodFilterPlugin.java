package com.github.mybatis.generator.gradle.plugin;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * @author zhouxinghai
 * 过滤方法
 */
public class MethodFilterPlugin extends PluginAdapter {

    private static final List<String>              xmlNodeNames = Arrays.asList("select", "delete", "insert", "update");

    private              Map<String, List<String>> excludes     = new ConcurrentHashMap<>();

    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        String excludeString = properties.getProperty("exclude");
        excludes = JSON.parseObject(excludeString, new TypeReference<Map<String, List<String>>>() {});
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getTableConfiguration().getTableName();
        XmlElement parentElement = document.getRootElement();
        Iterator<Element> iterator = parentElement.getElements().iterator();
        while (iterator.hasNext()) {
            XmlElement xmlElement = (XmlElement) iterator.next();
            if (xmlNodeNames.contains(xmlElement.getName())) {
                for (Attribute attribute : xmlElement.getAttributes()) {
                    if (attribute.getName().equals("id")) {
                        if (null != excludes && excludes.containsKey(tableName) && excludes.get(tableName).contains(attribute.getValue())) {
                            iterator.remove();
                        }
                        break;
                    }
                }
            }
        }
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getTableConfiguration().getTableName();
        Iterator<Method> iterator = interfaze.getMethods().iterator();
        boolean haseExample = false;
        while (iterator.hasNext()) {
            Method method = iterator.next();
            //executeParamter(method, introspectedTable);
            if (null != excludes && excludes.containsKey(tableName) && excludes.get(tableName).contains(method.getName())) {
                iterator.remove();
                continue;
            }
            if (!haseExample && method.getName().startsWith("updateByExample")) {
                haseExample = true;
            }
        }
        if (!haseExample) {
            try {
                Field importedTypesField = interfaze.getClass().getDeclaredField("importedTypes");
                importedTypesField.setAccessible(true);
                Set<FullyQualifiedJavaType> importedTypes = (Set<FullyQualifiedJavaType>) importedTypesField.get(interfaze);
                Iterator<FullyQualifiedJavaType> typeIterator = importedTypes.iterator();
                while (typeIterator.hasNext()) {
                    FullyQualifiedJavaType importedType = typeIterator.next();
                    if (importedType.getFullyQualifiedName().equals("org.apache.ibatis.annotations.Param")) {
                        typeIterator.remove();
                    }
                }
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    // 转换method参数
    private void executeParamter(Method method, IntrospectedTable introspectedTable) {
        List<Parameter> parameters = method.getParameters();
        List<Parameter> newParameters = new ArrayList<>();
        for (Parameter parameter : parameters) {
            FullyQualifiedJavaType type = parameter.getType();
            String shortName = getShortName(type);
            List<String> annotations = parameter.getAnnotations();
            Parameter newParameter = new Parameter(type, shortName);
            if (null != annotations && !annotations.isEmpty()) {
                for (String annotation : annotations) {
                    newParameter.addAnnotation(annotation);
                }
            }
            newParameters.add(newParameter);
        }
        method.getParameters().clear();
        if (null != newParameters && !newParameters.isEmpty()) {
            for (Parameter parameter : newParameters) {
                method.getParameters().add(parameter);
            }
        }
    }

    // 获取object类型
    private String getShortName(FullyQualifiedJavaType parameterType) {
        StringBuilder sb = new StringBuilder(parameterType.getShortName());
        sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        return sb.toString();
    }

}
