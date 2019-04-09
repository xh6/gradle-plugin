package com.xh6.mybatis.generator.gradle.plugin;

import java.util.Arrays;
import java.util.List;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.ibatis2.Ibatis2FormattingUtilities;

/**
 * number添加like查询
 * @author zhouxinghai
 * @date 2018/7/10
 */
public class NumberLikePlugin extends PluginAdapter {

    private static final List<String> SUPPORT_TYPES = Arrays.asList(Integer.class.getName(), Long.class.getName(), Short.class.getName());

    public NumberLikePlugin() {
        super();
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        InnerClass criteria = null;
        for (InnerClass innerClass : topLevelClass.getInnerClasses()) {
            if ("GeneratedCriteria".equals(innerClass.getType().getShortName())) {
                criteria = innerClass;
                break;
            }
        }

        if (criteria == null) {
            return true;
        }

        for (IntrospectedColumn introspectedColumn : introspectedTable.getNonBLOBColumns()) {

            if (introspectedColumn.isStringColumn() || introspectedColumn.isJDBCDateColumn() || introspectedColumn.isJDBCTimeColumn() ||
                    introspectedColumn.isBLOBColumn() || introspectedColumn.isJdbcCharacterColumn()) {
                continue;
            }

            String type = introspectedColumn.getFullyQualifiedJavaType().getFullyQualifiedName();
            if (!SUPPORT_TYPES.contains(type)) {
                continue;
            }

            addLike(introspectedColumn, criteria);
            addNotLike(introspectedColumn, criteria);
        }

        return true;
    }

    private void addLike(IntrospectedColumn introspectedColumn, InnerClass criteria) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "value"));

        StringBuilder sb = new StringBuilder();
        sb.append(introspectedColumn.getJavaProperty());
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        sb.insert(0, "and");
        sb.append("Like");
        method.setName(sb.toString());
        method.setReturnType(FullyQualifiedJavaType.getCriteriaInstance());

        sb.setLength(0);
        sb.append("addCriterion(\"");
        sb.append(Ibatis2FormattingUtilities.getAliasedActualColumnName(introspectedColumn));
        sb.append(" like\", value, \"");
        sb.append(introspectedColumn.getJavaProperty());
        sb.append("\");");
        method.addBodyLine(sb.toString());
        method.addBodyLine("return (Criteria) this;");

        criteria.addMethod(method);
    }

    private void addNotLike(IntrospectedColumn introspectedColumn, InnerClass criteria) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "value"));

        StringBuilder sb = new StringBuilder();
        sb.append(introspectedColumn.getJavaProperty());
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        sb.insert(0, "and");
        sb.append("NotLike");
        method.setName(sb.toString());
        method.setReturnType(FullyQualifiedJavaType.getCriteriaInstance());

        sb.setLength(0);
        sb.append("addCriterion(\"");
        sb.append(Ibatis2FormattingUtilities.getAliasedActualColumnName(introspectedColumn));
        sb.append(" not like\", value, \"");
        sb.append(introspectedColumn.getJavaProperty());
        sb.append("\");");
        method.addBodyLine(sb.toString());
        method.addBodyLine("return (Criteria) this;");

        criteria.addMethod(method);
    }
}
