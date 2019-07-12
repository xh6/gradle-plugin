package com.xh6.plugin.mybatis.generator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xh6.plugin.mybatis.generator.parse.ParserEntityResolver2;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Project;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.config.xml.MyBatisGeneratorConfigurationParser;
import org.mybatis.generator.config.xml.ParserErrorHandler;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.mybatis.generator.plugins.SerializablePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * 代码生成入口
 * Created by zhouxinghai on 16/8/24.
 */
public class MybatisCodeGenerator {

    private static final Logger       logger      = LoggerFactory.getLogger(MybatisCodeGenerator.class);

    private              List<String> warnings    = new ArrayList<>();

    private              List<String> parseErrors = new ArrayList<>();

    private MybatisCodeGenerator() {

    }

    public static MybatisCodeGenerator getInstance() {
        return new MybatisCodeGenerator();
    }

    public void createCode(Project project) {
        String searchPath = project.getProjectDir().getAbsolutePath();
        String configFile = searchPath + "/src/main/resources/generatorConfig.xml";
        if (new File(configFile).exists()) {
            System.out.println("find mybatis generator config :" + configFile);
        } else {
            System.out.println("mybatis generator config not found :" + configFile);
            configFile = searchPath + "/src/test/resources/generatorConfig.xml";
            if (new File(configFile).exists()) {
                System.out.println("find mybatis generator config :" + configFile);
            } else {
                System.out.println("mybatis generator config not found " + configFile);
                throw new RuntimeException("mybatis generator config not found");
            }
        }

        createCode(project.getRootDir().getAbsolutePath(), configFile);
    }

    public void createCode(String defaultProjectRoot, String configFile) {
        try (InputStream inputStream = new FileInputStream(configFile)) {
            Configuration config = parseConfiguration(inputStream);
            initConfiguration(config, defaultProjectRoot);
            DefaultShellCallback callback = new DefaultShellCallback(true);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            myBatisGenerator.generate(null);
            warnings.forEach(System.out::println);
        } catch (Exception e) {
            logger.error("mybatis generator error", e);
        }

    }

    private void initConfiguration(Configuration configuration, String defaultProjectRoot) throws IOException {

        for (Context context : configuration.getContexts()) {

            if (StringUtils.isBlank(context.getTargetRuntime())) {
                context.setTargetRuntime("MyBatis3");
            }

            context.getProperties().putIfAbsent("useActualColumnNames", "false");
            context.getProperties().putIfAbsent("suppressAllComments", "true");

            String projectRoot = context.getProperty(GeneratorConstant.PROJECT_ROOT);
            if (StringUtils.isBlank(projectRoot)) {
                projectRoot = defaultProjectRoot;
            }

            if (null == context.getJdbcConnectionConfiguration()) {
                String dbHost = getAndRemove(context, GeneratorConstant.DB_HOST);
                String dbPort = getAndRemove(context, GeneratorConstant.DB_PORT);
                String username = getAndRemove(context, GeneratorConstant.DB_USERNAME);
                String database = getAndRemove(context, GeneratorConstant.DB_DATABASE);
                String passowrd = getAndRemove(context, GeneratorConstant.DB_PASSOWRD);
                String jdbcUrl = String
                        .format("jdbc:mysql://%s:%s/%s?useUnicode=true&amp;characterEncoding=utf8&amp;autoReconnect=true&amp;failOverReadOnly=false",
                                dbHost, dbPort, database);
                JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
                jdbcConnectionConfiguration.setDriverClass("com.mysql.jdbc.Driver");
                jdbcConnectionConfiguration.setUserId(username);
                jdbcConnectionConfiguration.setPassword(passowrd);
                jdbcConnectionConfiguration.setConnectionURL(jdbcUrl);
                context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);
            }

            if (null == context.getSqlMapGeneratorConfiguration()) {
                String sqlmapPath = getAndRemove(context, GeneratorConstant.SQLMAP_PATH);
                if (!StringUtils.startsWith(sqlmapPath, "/")) {
                    sqlmapPath = projectRoot + "/" + sqlmapPath;
                }
                String sqlmapPackage = context.getProperty(GeneratorConstant.SQLMAP_PACKAGE);
                SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
                sqlMapGeneratorConfiguration.setTargetProject(sqlmapPath);
                sqlMapGeneratorConfiguration.setTargetPackage(sqlmapPackage);
                sqlMapGeneratorConfiguration.addProperty("enableSubPackages", "true");
                context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);
            }

            if (null == context.getJavaTypeResolverConfiguration()) {
                JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
                javaTypeResolverConfiguration.addProperty("forceBigDecimals", "true");
                context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);
            }

            if (null == context.getJavaModelGeneratorConfiguration()) {
                String modelPath = getAndRemove(context, GeneratorConstant.MODEL_PATH);
                if (!StringUtils.startsWith(modelPath, "/")) {
                    modelPath = projectRoot + "/" + modelPath;
                }
                String modelPackage = getAndRemove(context, GeneratorConstant.MODEL_PACKAGE);
                JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
                javaModelGeneratorConfiguration.setTargetProject(modelPath);
                javaModelGeneratorConfiguration.setTargetPackage(modelPackage);
                javaModelGeneratorConfiguration.addProperty("enableSubPackages", "true");
                javaModelGeneratorConfiguration.addProperty("immutable", "false");
                javaModelGeneratorConfiguration.addProperty("trimStrings", "true");
                javaModelGeneratorConfiguration.addProperty("constructorBased", "false");
                context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);
            }

            if (null == context.getJavaClientGeneratorConfiguration()) {
                String mapperPath = getAndRemove(context, GeneratorConstant.MAPPER_PATH);
                if (!StringUtils.startsWith(mapperPath, "/")) {
                    mapperPath = projectRoot + "/" + mapperPath;
                }
                String mapperPackage = getAndRemove(context, GeneratorConstant.MAPPER_PACKAGE);
                JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
                javaClientGeneratorConfiguration.setTargetProject(mapperPath);
                javaClientGeneratorConfiguration.setTargetPackage(mapperPackage);
                //javaClientGeneratorConfiguration.setImplementationPackage("");
                javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
                javaClientGeneratorConfiguration.addProperty("enableSubPackages", "true");
                javaClientGeneratorConfiguration.addProperty("methodNameCalculator", "extended");
                context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);
            }

            if (null == context.getCommentGeneratorConfiguration()) {
                CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
                commentGeneratorConfiguration.setConfigurationType(MyCommentGenerator.class.getName());
                commentGeneratorConfiguration.addProperty("suppressAllComments", "false");
                commentGeneratorConfiguration.addProperty("suppressDate", "true");
                context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);
            }

            context.addPluginConfiguration(PluginConfigurationBuilder.getInstance(BlobsToStringPlugin.class).build());
            //context.addPluginConfiguration(PluginConfigurationBuilder.getInstance(RemoveBLOBsPlugin.class).build());

            context.addPluginConfiguration(PluginConfigurationBuilder.getInstance(SqlMapPlugin.class).addProperty("isMergeable", "false").build());

            context.addPluginConfiguration(
                    PluginConfigurationBuilder.getInstance(SerializablePlugin.class).addProperty("suppressJavaInterface", "false").build());

            context.addPluginConfiguration(PluginConfigurationBuilder.getInstance(CommentPlugin.class).build());
            context.addPluginConfiguration(PluginConfigurationBuilder.getInstance(ModelExamplePlugin.class).build());
            context.addPluginConfiguration(PluginConfigurationBuilder.getInstance(MySqlPagePlugin.class).build());
            context.addPluginConfiguration(PluginConfigurationBuilder.getInstance(PostfixPlugin.class).build());
            context.addPluginConfiguration(PluginConfigurationBuilder.getInstance(NumberLikePlugin.class).build());
            context.addPluginConfiguration(PluginConfigurationBuilder.getInstance(BatchInsertSelectivePlugin.class).build());
            context.addPluginConfiguration(PluginConfigurationBuilder.getInstance(BatchUpdateByPrimaryKeySelectivePlugin.class).build());
            context.addPluginConfiguration(PluginConfigurationBuilder.getInstance(BatchDeleteByPrimaryKeyPlugin.class).build());
            context.addPluginConfiguration(PluginConfigurationBuilder.getInstance(BatchSelectByPrimaryKeyPlugin.class).build());
            context.addPluginConfiguration(PluginConfigurationBuilder.getInstance(MethodFilterPlugin.class)
                    .addProperty("exclude", "{\"link_queue\":[\"insert\",\"updateByExample\",\"updateByPrimaryKey\"]}").build());

            List<TableConfiguration> tcList = context.getTableConfigurations();
            if (null == tcList || tcList.isEmpty()) {
                continue;
            }
            tcList.forEach(tc -> {
                String tableName = tc.getTableName();
                String domainObjectName = tc.getDomainObjectName();
                if (StringUtils.isBlank(domainObjectName)) {
                    domainObjectName = JavaBeansUtil.getCamelCaseString(tableName, true) + "DO";
                    tc.setDomainObjectName(domainObjectName);
                }
                String mapperName = tc.getMapperName();
                if (StringUtils.isBlank(mapperName)) {
                    mapperName = JavaBeansUtil.getCamelCaseString(tableName, true) + "Mapper";
                    tc.setMapperName(mapperName);
                }
            });

        }

    }

    private String getAndRemove(Context context, String name) {
        String value = context.getProperty(name);
        if (null != value) {
            context.getProperties().remove(name);
        }
        return value;
    }

    private Configuration parseConfiguration(InputStream inputSource) throws IOException, XMLParserException {
        parseErrors.clear();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new ParserEntityResolver2());

            ParserErrorHandler handler = new ParserErrorHandler(warnings, parseErrors);
            builder.setErrorHandler(handler);

            Document document = null;
            try {
                document = builder.parse(new InputSource(inputSource));
            } catch (SAXParseException e) {
                throw new XMLParserException(parseErrors);
            } catch (SAXException e) {
                if (e.getException() == null) {
                    parseErrors.add(e.getMessage());
                } else {
                    parseErrors.add(e.getException().getMessage());
                }
            }

            if (parseErrors.size() > 0) {
                parseErrors.forEach(System.out::println);
                throw new XMLParserException(parseErrors);
            }

            Configuration config = null;
            Element rootNode = document.getDocumentElement();
            config = new MyBatisGeneratorConfigurationParser(null).parseConfiguration(rootNode);
            if (parseErrors.size() > 0) {
                throw new XMLParserException(parseErrors);
            }

            return config;
        } catch (ParserConfigurationException e) {
            parseErrors.add(e.getMessage());
            throw new XMLParserException(parseErrors);
        }
    }

    public static class PluginConfigurationBuilder {

        private Class               pluginClass;

        private Map<String, String> properties = new HashMap<>();

        private PluginConfigurationBuilder(Class pluginClass) {
            this.pluginClass = pluginClass;
        }

        public static PluginConfigurationBuilder getInstance(Class pluginClass) {
            return new PluginConfigurationBuilder(pluginClass);
        }

        public PluginConfigurationBuilder addProperty(String name, Object value) {
            properties.put(name, value.toString());
            return this;
        }

        public PluginConfiguration build() {
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType(pluginClass.getName());
            properties.forEach((k, v) -> pluginConfiguration.addProperty(k, v));
            return pluginConfiguration;
        }

    }

}
