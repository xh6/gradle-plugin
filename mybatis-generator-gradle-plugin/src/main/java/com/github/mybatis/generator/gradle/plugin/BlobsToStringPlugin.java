package com.github.mybatis.generator.gradle.plugin;

import java.sql.JDBCType;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouxinghai on 2016-02-17 .
 */
public class BlobsToStringPlugin extends PluginAdapter {

    private static final Logger logger = LoggerFactory.getLogger(BlobsToStringPlugin.class);

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> blobColumns = introspectedTable.getBLOBColumns();
        if (null != blobColumns && !blobColumns.isEmpty()) {
            Iterator<IntrospectedColumn> it = blobColumns.iterator();
            while (it.hasNext()) {
                IntrospectedColumn c = it.next();
                c.setJdbcType(JDBCType.VARCHAR.getVendorTypeNumber());
                c.setJdbcTypeName(JDBCType.VARCHAR.getName());
                introspectedTable.getBaseColumns().add(c);
                it.remove();
            }
        }
        super.initialized(introspectedTable);
    }

}
