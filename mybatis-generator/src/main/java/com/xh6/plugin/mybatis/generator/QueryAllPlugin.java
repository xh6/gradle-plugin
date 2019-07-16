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

import static org.mybatis.generator.internal.util.messages.Messages.getString;

public class QueryAllPlugin extends PluginAdapter {

    private static final String METHOD_NAME = "queryAll";

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

        FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getNewListInstance();
        FullyQualifiedJavaType listType;
        if (introspectedTable.getRules().generateBaseRecordClass()) {
            listType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        } else if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            listType = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
        } else {
            throw new RuntimeException(getString("RuntimeError.12")); //$NON-NLS-1$
        }

        importedTypes.add(listType);
        returnType.addTypeArgument(listType);

        method.setReturnType(returnType);
        // 3.设置方法名
        method.setName(METHOD_NAME);
        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
    }

    public void addBatchInsertSelectiveXml(Document document, IntrospectedTable introspectedTable) {

        StringBuilder sb = new StringBuilder();
        sb.append("select * from  ");
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());

        XmlElement answer = new XmlElement("select");
        answer.addAttribute(new Attribute("id", METHOD_NAME));
        answer.addElement(new TextElement(sb.toString()));
        context.getCommentGenerator().addComment(answer);
        document.getRootElement().addElement(answer);

    }

}
