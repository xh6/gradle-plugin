/**
 * Copyright 2006-2017 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xh6.plugin.mybatis.generator;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

public class SelectFirstByExamplePlugin extends PluginAdapter {

    private static final String METHOD_NAME = "selectFirstByExample";

    /**
     * 修改Mapper类
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        addBatchInsertMethod(interfaze, introspectedTable);
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    /**
     * 修改Mapper.xml
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        addBatchInsertSelectiveXml(document, introspectedTable);
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    private void addBatchInsertMethod(Interface interfaze, IntrospectedTable introspectedTable) {
        // 设置需要导入的类
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        importedTypes.add(FullyQualifiedJavaType.getNewListInstance());
        importedTypes.add(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));

        Method method = new Method();
        // 1.设置方法可见性
        method.setVisibility(JavaVisibility.PUBLIC);
        // 2.设置返回值类型

        FullyQualifiedJavaType returnType = introspectedTable.getRules().calculateAllFieldsClass();
        method.setReturnType(returnType);
        importedTypes.add(returnType);

        //设置参数
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getExampleType());
        importedTypes.add(type);
        method.addParameter(new Parameter(type, "example"));

        // 3.设置方法名
        method.setName(METHOD_NAME);
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
    }

    public void addBatchInsertSelectiveXml(Document document, IntrospectedTable introspectedTable) {

        String fqjt = introspectedTable.getExampleType();

        XmlElement answer = new XmlElement("select");
        answer.addAttribute(new Attribute("id", METHOD_NAME));
        answer.addAttribute(new Attribute("resultMap", introspectedTable.getBaseResultMapId()));
        answer.addAttribute(new Attribute("parameterType", fqjt));

        answer.addElement(new TextElement("select"));
        XmlElement ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", "distinct"));
        ifElement.addElement(new TextElement("distinct"));
        answer.addElement(ifElement);

        StringBuilder sb = new StringBuilder();
        if (stringHasValue(introspectedTable.getSelectByExampleQueryId())) {
            sb.append('\'');
            sb.append(introspectedTable.getSelectByExampleQueryId());
            sb.append("' as QUERYID,");
            answer.addElement(new TextElement(sb.toString()));
        }

        answer.addElement(getBaseColumnListElement(introspectedTable));

        sb.setLength(0);
        sb.append(" from ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));
        answer.addElement(getExampleIncludeElement(introspectedTable));

        ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", "orderByClause != null"));
        ifElement.addElement(new TextElement("order by ${orderByClause} "));
        answer.addElement(ifElement);
        answer.addElement(new TextElement(" limit 1"));

        context.getCommentGenerator().addComment(answer);
        document.getRootElement().addElement(answer);

    }

    protected XmlElement getBaseColumnListElement(IntrospectedTable introspectedTable) {
        XmlElement answer = new XmlElement("include");
        answer.addAttribute(new Attribute("refid", introspectedTable.getBaseColumnListId()));
        return answer;
    }

    protected XmlElement getExampleIncludeElement(IntrospectedTable introspectedTable) {
        XmlElement ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", "_parameter != null"));

        XmlElement includeElement = new XmlElement("include");
        includeElement.addAttribute(new Attribute("refid", introspectedTable.getExampleWhereClauseId()));
        ifElement.addElement(includeElement);

        return ifElement;
    }

}
