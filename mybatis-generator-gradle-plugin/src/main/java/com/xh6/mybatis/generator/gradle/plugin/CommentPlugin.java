package com.xh6.mybatis.generator.gradle.plugin;

import java.sql.*;
import java.text.MessageFormat;
import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.Context;

/**
 * Created by zhouxinghai on 2015-10-26 .
 */
public class CommentPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String comment = queryTableComment(introspectedTable.getTableConfiguration().getTableName(), context);
        topLevelClass.addJavaDocLine(comment);
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String comment = queryTableComment(introspectedTable.getTableConfiguration().getTableName(), context);
        interfaze.addJavaDocLine(comment);
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    /**
     * 查询表备注
     */
    private static final String sql = "select TABLE_COMMENT from information_schema.TABLES where TABLE_SCHEMA=? and TABLE_NAME = ? ";

    public static String queryTableComment(String tableName, Context context) {
        String comment = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            String url = context.getJdbcConnectionConfiguration().getConnectionURL();
            String username = context.getJdbcConnectionConfiguration().getUserId();
            String password = context.getJdbcConnectionConfiguration().getPassword();
            String database = url.substring(0, url.indexOf("?"));
            database = database.substring(database.lastIndexOf("/") + 1);
            con = DriverManager.getConnection(url, username, password);
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, database);
            pstmt.setString(2, tableName);
            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                comment = result.getString(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (null != pstmt) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (null != con) {
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        // 备注
        comment = MessageFormat.format(" /** create by system from table {0}({1})  */", tableName, comment);
        return comment;
    }
}
