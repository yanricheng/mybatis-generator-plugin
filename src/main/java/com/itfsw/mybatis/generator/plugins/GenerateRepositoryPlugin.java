package com.itfsw.mybatis.generator.plugins;

import com.itfsw.mybatis.generator.plugins.utils.BasePlugin;
import com.itfsw.mybatis.generator.plugins.utils.FieldUtil;
import com.itfsw.mybatis.generator.plugins.utils.FormatTools;
import com.itfsw.mybatis.generator.plugins.utils.JavaElementGeneratorTools;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yrc
 * @date 2021/5/23
 */
public class GenerateRepositoryPlugin extends BasePlugin {

    private static final String PARENT_BUILDER = "parentBuilder";

    private ShellCallback shellCallback = null;

    public GenerateRepositoryPlugin() {
        shellCallback = new DefaultShellCallback(false);
    }


    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        JavaFormatter javaFormatter = context.getJavaFormatter();
        List<GeneratedJavaFile> files = new ArrayList<GeneratedJavaFile>();
        for (GeneratedJavaFile javaFile : introspectedTable.getGeneratedJavaFiles()) {
            FullyQualifiedJavaType compilationUnit = javaFile.getCompilationUnit().getType();
            String simpleClassName = compilationUnit.getShortName();
            String simpleBeanName = FieldUtil.firstLower(compilationUnit.getShortName());


            if (simpleClassName.endsWith("DO")) {
                String targetDir = introspectedTable.getContext().getJavaModelGeneratorConfiguration().getTargetProject();
                String dataObjTargetPackage = introspectedTable.getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
                String domainTargetPackage = String.format("%s.%s", dataObjTargetPackage.substring(0, dataObjTargetPackage.lastIndexOf(".")), "biz.domain");
                String domainSimpleName = simpleClassName.replace("DO", "");
                String domainFullyQualifiedName = String.format("%s.%s", domainTargetPackage, domainSimpleName);
                //定义实现
                TopLevelClass domainClazz = new TopLevelClass(domainFullyQualifiedName);
                //添加注释
                setCommonInfo(domainClazz, domainFullyQualifiedName);
                //添加父亲接口
                FullyQualifiedJavaType serializableFullyQualifiedName = new FullyQualifiedJavaType("java.io.Serializable");
                domainClazz.addSuperInterface(serializableFullyQualifiedName);
                domainClazz.addImportedType(serializableFullyQualifiedName);

                //构造domain
                TopLevelClass dataObjClazz = (TopLevelClass) javaFile.getCompilationUnit();
                domainClazz.getImportedTypes().addAll(dataObjClazz.getImportedTypes());
                domainClazz.getFields().addAll(dataObjClazz.getFields());
                domainClazz.getMethods().addAll(dataObjClazz.getMethods());

                //构造 builder
                Object parentBuilderConf = this.getProperties().get(PARENT_BUILDER);
                String parentBuilderFullyQualifiedName = String.format("%s<%s,%s>", parentBuilderConf != null ? parentBuilderConf.toString() : null, simpleClassName, domainSimpleName);
                String builderTargetPackage = String.format("%s.%s", dataObjTargetPackage.substring(0, dataObjTargetPackage.lastIndexOf(".")), "builder");
                String builderFullyQualifiedName = String.format("%s.%s", builderTargetPackage, simpleClassName.replace("DO", "Builder"));
                FullyQualifiedJavaType mapstructFullyQualifiedName = new FullyQualifiedJavaType("org.mapstruct.Mapper");

                Interface builderInterface = new Interface(builderFullyQualifiedName);
                setCommonInfo(builderInterface, builderFullyQualifiedName);
                builderInterface.addImportedType(mapstructFullyQualifiedName);
                builderInterface.addImportedType(dataObjClazz.getType());
                builderInterface.addImportedType(domainClazz.getType());
                builderInterface.addAnnotation("@Mapper(componentModel = \"spring\")");
                builderInterface.addSuperInterface(new FullyQualifiedJavaType(parentBuilderFullyQualifiedName));


                GeneratedJavaFile domainJavaFile = new GeneratedJavaFile(domainClazz, targetDir, javaFormatter);
                GeneratedJavaFile builderJavaFile = new GeneratedJavaFile(builderInterface, targetDir, javaFormatter);
                try {
                    new File(shellCallback.getDirectory(targetDir, dataObjTargetPackage), domainJavaFile.getFileName());
                    new File(shellCallback.getDirectory(targetDir, builderTargetPackage), builderJavaFile.getFileName());

                    files.add(domainJavaFile);
                    files.add(builderJavaFile);
                } catch (ShellException e) {
                    e.printStackTrace();
                }
            } else if (simpleClassName.endsWith("Mapper")) {

                //生成Repository
                String targetDir = introspectedTable.getContext().getJavaClientGeneratorConfiguration().getTargetProject();
                String mapperTargetPackage = introspectedTable.getContext().getJavaClientGeneratorConfiguration().getTargetPackage();
                String repositoryTargetPackage = String.format("%s.%s", mapperTargetPackage.substring(0, mapperTargetPackage.lastIndexOf(".")), "repository");
                String implTargetPackage = String.format("%s.%s", mapperTargetPackage.substring(0, mapperTargetPackage.lastIndexOf(".")), "repository.impl");
                String mapperFullyQualifiedName = String.format("%s.%s", repositoryTargetPackage, simpleClassName.replace("Mapper", "Repository"));
                String implFullyQualifiedName = String.format("%s.%s", implTargetPackage, simpleClassName.replace("Mapper", "RepositoryImpl"));

                //定义接口
                Interface repositoryInterface = new Interface(mapperFullyQualifiedName);
                setCommonInfo(repositoryInterface, mapperFullyQualifiedName);

                //定义实现
                TopLevelClass repositoryImplClazz = new TopLevelClass(implFullyQualifiedName);
                //添加注释
                setCommonInfo(repositoryImplClazz, implFullyQualifiedName);
                //添加父亲接口
                repositoryImplClazz.addSuperInterface(repositoryInterface.getType());
                repositoryImplClazz.addAnnotation("@Repository");
                //增加import
                repositoryImplClazz.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
                repositoryImplClazz.addImportedType(compilationUnit);

                //Autowired mapper
                Field mapperField = new Field(FieldUtil.firstLower(simpleClassName), compilationUnit);
                mapperField.setVisibility(JavaVisibility.PRIVATE);
                mapperField.addAnnotation("@Autowired");
                commentGenerator.addFieldComment(mapperField, introspectedTable);
                repositoryImplClazz.addField(mapperField);

                Interface mapperInterface = (Interface) javaFile.getCompilationUnit();
                repositoryInterface.getImportedTypes().addAll(mapperInterface.getImportedTypes().stream().filter(
                        type -> !(type.getFullyQualifiedName().contains("spring")
                                || type.getFullyQualifiedName().contains("mybaits"))
                                || type.getFullyQualifiedName().contains("ibaits"))
                        .collect(Collectors.toSet()));
                repositoryInterface.getStaticImports().addAll(mapperInterface.getStaticImports());
                repositoryInterface.getMethods().addAll(mapperInterface.getMethods());


                repositoryImplClazz.getImportedTypes().addAll(mapperInterface.getImportedTypes());
                repositoryImplClazz.getStaticImports().addAll(mapperInterface.getStaticImports());


                for (Method mapperMethod : mapperInterface.getMethods()) {
                    List<Parameter> parameterList = mapperMethod.getParameters();
                    Parameter[] parameters = new Parameter[parameterList.size()];
                    for (int i = 0, size = mapperMethod.getParameters().size(); i < size; i++) {
                        parameters[i] = parameterList.get(i);
                    }
                    // parseValue 方法
                    Method implMethod = JavaElementGeneratorTools.generateMethod(
                            mapperMethod.getName(),
                            JavaVisibility.PUBLIC,
                            repositoryImplClazz.getType(),
                            parameters);
                    implMethod.setReturnType(mapperMethod.getReturnType());
                    implMethod.addAnnotation("@Override");
                    List<Parameter> params = mapperMethod.getParameters();
                    StringBuilder sbd = new StringBuilder();
                    for (Parameter p : params) {
                        if (sbd.length() > 0) {
                            sbd.append(",");
                        }
                        sbd.append(p.getName());
                    }
                    implMethod.addBodyLine(String.format("return %s.%s(%s);", simpleBeanName, mapperMethod.getName(), sbd.toString()));
                    commentGenerator.addGeneralMethodComment(implMethod, introspectedTable);
                    FormatTools.addMethodWithBestPosition(repositoryImplClazz, implMethod);
                }

//                implTopLevelClass.getMethods().addAll(mapperInterface.getMethods());


                //增加mapper依赖
//                repositoryInterface.addField(new File);
//                repositoryInterface.addImportedType(baseModelJavaType.ge);
//                FullyQualifiedJavaType daoSuperType = new FullyQualifiedJavaType(expandDaoSuperClass);
//                repositoryInterface.addImportedType(daoSuperType);
//                repositoryInterface.addSuperInterface(daoSuperType);

                GeneratedJavaFile repositoryJavaFile = new GeneratedJavaFile(repositoryInterface, targetDir, javaFormatter);
                GeneratedJavaFile implJavaFile = new GeneratedJavaFile(repositoryImplClazz, targetDir, javaFormatter);
                try {
                    File mapperDir = shellCallback.getDirectory(targetDir, repositoryTargetPackage);
                    File implDir = shellCallback.getDirectory(targetDir, implTargetPackage);
                    File mapperFile = new File(mapperDir, repositoryJavaFile.getFileName());
                    File implFile = new File(implDir, implJavaFile.getFileName());

                    files.add(repositoryJavaFile);
                    files.add(implJavaFile);
                } catch (ShellException e) {
                    e.printStackTrace();
                }
            }
        }

//            else if (!shortName.endsWith("Example")) { // CRUD Mapper
//                Interface mapperInterface = new Interface(daoTargetPackage + "." + shortName + "Mapper");
//
//                mapperInterface.setVisibility(JavaVisibility.PUBLIC);
//                mapperInterface.addJavaDocLine("/**");
//                mapperInterface.addJavaDocLine(" * MyBatis Generator工具自动生成");
//                mapperInterface.addJavaDocLine(" */");
//
//                FullyQualifiedJavaType daoSuperType = new FullyQualifiedJavaType(daoSuperClass);
//                // 添加泛型支持
//                daoSuperType.addTypeArgument(baseModelJavaType);
//                mapperInterface.addImportedType(baseModelJavaType);
//                mapperInterface.addImportedType(daoSuperType);
//                mapperInterface.addSuperInterface(daoSuperType);
//
//                mapperJavafile = new GeneratedJavaFile(mapperInterface, daoTargetDir, javaFormatter);
//                mapperJavaFiles.add(mapperJavafile);
//
//            }
        return files;
    }

    private void setCommonInfo(JavaElement ele, String doc) {
        ele.setVisibility(JavaVisibility.PUBLIC);
        ele.addJavaDocLine("/**");
        ele.addJavaDocLine(" * " + doc + "");
        ele.addJavaDocLine(" */");
    }
}
