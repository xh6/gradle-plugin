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

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

public class BatchUpdateByPrimaryKeySelectivePlugin extends PluginAdapter {

    private static final String METHOD_NAME = "batchUpdateByPrimaryKeySelective";

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

        Method ibsmethod = new Method();
        // 1.设置方法可见性
        ibsmethod.setVisibility(JavaVisibility.PUBLIC);
        // 2.设置返回值类型
        FullyQualifiedJavaType ibsreturnType = FullyQualifiedJavaType.getIntInstance();// int型
        ibsmethod.setReturnType(ibsreturnType);
        // 3.设置方法名
        ibsmethod.setName(METHOD_NAME);
        // 4.设置参数列表
        FullyQualifiedJavaType paramType = FullyQualifiedJavaType.getNewListInstance();
        FullyQualifiedJavaType paramListType;
        if (introspectedTable.getRules().generateBaseRecordClass()) {
            paramListType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        } else if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            paramListType = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
        } else {
            throw new RuntimeException(getString("RuntimeError.12"));
        }
        paramType.addTypeArgument(paramListType);

        ibsmethod.addParameter(new Parameter(paramType, "records"));

        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(ibsmethod);
    }

    public void addBatchInsertSelectiveXml(Document document, IntrospectedTable introspectedTable) {

        XmlElement foreachElement = new XmlElement("foreach");
        foreachElement.addAttribute(new Attribute("collection", "list"));
        foreachElement.addAttribute(new Attribute("item", "record"));
        foreachElement.addAttribute(new Attribute("separator", ";"));

        StringBuilder sb = new StringBuilder();
        sb.append("update ");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        foreachElement.addElement(new TextElement(sb.toString()));

        XmlElement dynamicElement = new XmlElement("set"); //$NON-NLS-1$
        foreachElement.addElement(dynamicElement);

        for (IntrospectedColumn introspectedColumn : ListUtilities.removeGeneratedAlwaysColumns(introspectedTable.getNonPrimaryKeyColumns())) {
            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty("record."));
            sb.append(" != null");
            XmlElement isNotNullElement = new XmlElement("if");
            isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
            dynamicElement.addElement(isNotNullElement);

            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn,"record."));
            sb.append(',');
            isNotNullElement.addElement(new TextElement(sb.toString()));
        }

        boolean and = false;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            sb.setLength(0);
            if (and) {
                sb.append("  and ");
            } else {
                sb.append("where ");
                and = true;
            }
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn,"record."));
            foreachElement.addElement(new TextElement(sb.toString()));
        }

        XmlElement answer = new XmlElement("update");
        answer.addAttribute(new Attribute("id", METHOD_NAME));
        answer.addAttribute(new Attribute("parameterType", "java.util.List"));
        answer.addElement(foreachElement);
        context.getCommentGenerator().addComment(answer);
        document.getRootElement().addElement(answer);

    }

}
