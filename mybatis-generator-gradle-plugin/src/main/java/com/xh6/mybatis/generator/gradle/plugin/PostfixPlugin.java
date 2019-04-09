package com.xh6.mybatis.generator.gradle.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.config.PropertyRegistry;

/**
 * @author zhouxinghai
 * @description 扩展Mapper.java为MpapperExt.java
 * @2014年10月26日 下午3:50:19
 * @email 1093320666@qq.com
 */
public class PostfixPlugin extends PluginAdapter {

    /**
     * 注解
     */
    private static final String annotation_resource = "org.apache.ibatis.annotations.Mapper";

    /**
     * 后缀
     */
    private static final String postfix             = "DAO";

    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement root = document.getRootElement();
        Attribute namespaceAttribute = null;
        for (Attribute attribute : root.getAttributes()) {
            if (attribute.getName().equals("namespace")) {
                namespaceAttribute = attribute;
            }
        }
        root.getAttributes().remove(namespaceAttribute);

        String name = introspectedTable.getMyBatis3JavaMapperType();
        String extname = getExtName(name);
        root.getAttributes().add(new Attribute("namespace", extname));
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    // 生成XXExt.java
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        String name = introspectedTable.getMyBatis3JavaMapperType();
        String extname = getExtName(name);
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(extname);
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        context.getCommentGenerator().addJavaFileComment(interfaze);

        FullyQualifiedJavaType baseInterfaze = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
        interfaze.addSuperInterface(baseInterfaze);

        FullyQualifiedJavaType annotation = new FullyQualifiedJavaType(annotation_resource);
        interfaze.addAnnotation("@Mapper");
        interfaze.addImportedType(annotation);
        interfaze.addImportedType(baseInterfaze);

        // 备注
        String comment = CommentPlugin.queryTableComment(introspectedTable.getTableConfiguration().getTableName(), context);

        interfaze.addJavaDocLine(comment);

        CompilationUnit compilationUnits = interfaze;
        GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(compilationUnits,
                context.getJavaClientGeneratorConfiguration().getTargetProject(), context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                context.getJavaFormatter());

        if (isExistExtFile(generatedJavaFile.getTargetProject(), generatedJavaFile.getTargetPackage(), generatedJavaFile.getFileName())) {
            return super.contextGenerateAdditionalJavaFiles(introspectedTable);
        }
        List<GeneratedJavaFile> generatedJavaFiles = new ArrayList<GeneratedJavaFile>(1);
        generatedJavaFile.getFileName();
        generatedJavaFiles.add(generatedJavaFile);
        return generatedJavaFiles;
    }

    // 生成XXExt.xml
    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {
        String xmlname = introspectedTable.getMyBatis3XmlMapperFileName();
        String extxmlname = getExtName(xmlname);

        String extPackage = introspectedTable.getMyBatis3XmlMapperPackage();
        extPackage = extPackage.substring(0, extPackage.lastIndexOf("."));
        if (isExistExtFile(context.getSqlMapGeneratorConfiguration().getTargetProject(), extPackage, extxmlname)) {
            return super.contextGenerateAdditionalXmlFiles(introspectedTable);
        }

        Document document = new Document(XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID, XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);

        XmlElement root = new XmlElement("mapper");
        document.setRootElement(root);

        String name = introspectedTable.getMyBatis3JavaMapperType();
        String extname = getExtName(name);
        //<mapper namespace="com.xinghai.outway.dao.OutwaySyncItemsDAO" />
        root.addAttribute(new Attribute("namespace", extname));

        GeneratedXmlFile gxf = new GeneratedXmlFile(document, extxmlname, extPackage, context.getSqlMapGeneratorConfiguration().getTargetProject(),
                false, context.getXmlFormatter());

        List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>(1);
        answer.add(gxf);

        return answer;
    }

    private boolean isExistExtFile(String targetProject, String targetPackage, String fileName) {

        File project = new File(targetProject);
        if (!project.isDirectory()) {
            return true;
        }

        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(targetPackage, ".");
        while (st.hasMoreTokens()) {
            sb.append(st.nextToken());
            sb.append(File.separatorChar);
        }

        File directory = new File(project, sb.toString());
        if (!directory.isDirectory()) {
            boolean rc = directory.mkdirs();
            if (!rc) {
                return true;
            }
        }

        File testFile = new File(directory, fileName);
        if (testFile.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取扩展dao完整名称
     * @param name
     * @return
     */
    private String getExtName(String name) {
        String extname = name.replace(".mapper.", ".").replace("Mapper", postfix);
        return extname;
    }

}
