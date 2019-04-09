package com.github.mybatis.generator.gradle.plugin;

import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouxinghai on 2016-02-17 .
 */
public class MySqlPagePlugin extends PluginAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MySqlPagePlugin.class);

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
        Field field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(FullyQualifiedJavaType.getIntInstance());
        field.setName("pageNum");
        field.addJavaDocLine("/** 当前页 */");
        topLevelClass.addField(field);

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("initPageInfo");
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Integer"), "pageNum"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Integer"), "pageSize"));
        method.addBodyLine("if (null == pageNum || pageNum <= 0) {\n" + "            this.pageNum = 1;\n" + "        } else {\n" +
                "            this.pageNum = pageNum.intValue();\n" + "        }\n" + "        if (null == pageSize || pageSize <= 0) {\n" +
                "            this.pageSize = 10;\n" + "        } else {\n" + "            this.pageSize = pageSize.intValue();\n" + "        }");
        topLevelClass.addMethod(method);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName("getPageNum");
        method.addBodyLine("return pageNum;");
        topLevelClass.addMethod(method);

        field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(FullyQualifiedJavaType.getIntInstance());
        field.setName("pageSize");
        field.addJavaDocLine("/** 每页数据条数 */");
        topLevelClass.addField(field);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName("getPageSize");
        method.addBodyLine("return pageSize;");
        topLevelClass.addMethod(method);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName("getLimitStart");
        method.addBodyLine(
                "if (pageNum > 0 && pageSize > 0) {\n" + "            return (pageNum - 1) * pageSize;\n" + "        }\n" + "        return 0;");
        topLevelClass.addMethod(method);

        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        addLimit(element);
        return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    private void addLimit(XmlElement element) {
        XmlElement limitElement = new XmlElement("if");
        limitElement.addAttribute(new Attribute("test", "pageNum > 0 and pageSize > 0"));
        limitElement.addElement(new TextElement("  limit ${limitStart} , ${pageSize}"));
        element.addElement(limitElement);
    }

}
