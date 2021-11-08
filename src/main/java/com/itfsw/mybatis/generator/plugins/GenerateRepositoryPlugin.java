package com.itfsw.mybatis.generator.plugins;

import com.itfsw.mybatis.generator.plugins.biz.curd.XConverters;
import com.itfsw.mybatis.generator.plugins.biz.curd.XPrimitives;
import com.itfsw.mybatis.generator.plugins.biz.curd.Xpojos;
import com.itfsw.mybatis.generator.plugins.biz.curd.xCommons;
import com.itfsw.mybatis.generator.plugins.biz.enums.ClassType;
import com.itfsw.mybatis.generator.plugins.utils.BasePlugin;
import com.itfsw.mybatis.generator.plugins.utils.FieldUtil;
import com.itfsw.mybatis.generator.plugins.utils.FormatTools;
import com.itfsw.mybatis.generator.plugins.utils.JavaElementGeneratorTools;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.itfsw.mybatis.generator.plugins.biz.Constants.ADD;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.BASE_JAVA_DIR;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.CONTROLLER_SUBFIX;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.CONVERT_SUBFIX;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.DELETE;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.DO;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.DOMAIN_OBJ_EXCLUDE_FIELDS;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.DTO;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.GET;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.INSERT;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.MAPPER_SUBFIX;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.MODIFY;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.REMOVE;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.SELECT;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.UPDATE;

/**
 * @author yrc
 * @date 2021/5/23
 */
public class GenerateRepositoryPlugin extends BasePlugin {


    private static String dataOjbectPackage = null;
    private static String mapperJavaPackage = null;
    private static String baseJavaDir = null;
    private static String repositoryPackage = null;
    private static String repositoryImplPackage = null;
    private static String builderPackage = null;
    private static String servicePackage = null;
    private static String serviceImplPackage = null;
    private static String domainPackage = null;
    private static String facadePackage = null;
    private static String facadeImplPackage = null;
    private static String dtoPackage = null;
    private static String convertPackage = null;
    private static String controllerPackage = null;
    private static String parentBuilderClass = null;
    private static String parentConvertClass = null;
    private static String parentControllerClass = null;
    private static JavaFormatter javaFormatter = null;
    private static String domainObjectExcludeFields = null;


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

        String tableDesc = introspectedTable.getTableConfigurationProperty("tableDesc");
        String author = introspectedTable.getTableConfigurationProperty("author");

        if (javaFormatter == null) {
            javaFormatter = context.getJavaFormatter();
        }

        baseJavaDir = context.getProperty(BASE_JAVA_DIR);

        domainObjectExcludeFields = context.getProperty(DOMAIN_OBJ_EXCLUDE_FIELDS);
        if (domainObjectExcludeFields == null) {
            domainObjectExcludeFields = this.getProperties().getProperty(DOMAIN_OBJ_EXCLUDE_FIELDS);
        }

        parentBuilderClass = context.getProperty("parentBuilderClass");
        if (parentBuilderClass == null) {
            parentBuilderClass = this.getProperties().getProperty("parentBuilderClass");
        }

        parentConvertClass = context.getProperty("parentConvertClass");
        if (parentConvertClass == null) {
            parentConvertClass = this.getProperties().getProperty("parentConvertClass");
        }

        parentControllerClass = context.getProperty("parentControllerClass");
        if (parentControllerClass == null) {
            parentControllerClass = this.getProperties().getProperty("parentControllerClass");
        }


        repositoryPackage = context.getProperty("repositoryPackage");
        if (repositoryPackage == null) {
            repositoryPackage = this.getProperties().getProperty("repositoryPackage");
        }
        repositoryImplPackage = context.getProperty("repositoryImplPackage");
        if (repositoryImplPackage == null) {
            repositoryImplPackage = this.getProperties().getProperty("repositoryImplPackage");
        }
        builderPackage = context.getProperty("builderPackage");
        if (builderPackage == null) {
            builderPackage = this.getProperties().getProperty("builderPackage");
        }
        servicePackage = context.getProperty("servicePackage");
        if (servicePackage == null) {
            servicePackage = this.getProperties().getProperty("servicePackage");
        }
        serviceImplPackage = context.getProperty("serviceImplPackage");
        if (serviceImplPackage == null) {
            serviceImplPackage = this.getProperties().getProperty("serviceImplPackage");
        }
        domainPackage = context.getProperty("domainPackage");
        if (domainPackage == null) {
            domainPackage = this.getProperties().getProperty("domainPackage");
        }

        facadePackage = context.getProperty("facadePackage");
        if (facadePackage == null) {
            facadePackage = this.getProperties().getProperty("facadePackage");
        }
        facadeImplPackage = context.getProperty("facadeImplPackage");
        if (facadeImplPackage == null) {
            facadeImplPackage = this.getProperties().getProperty("facadeImplPackage");
        }
        dtoPackage = context.getProperty("dtoPackage");
        if (dtoPackage == null) {
            dtoPackage = this.getProperties().getProperty("dtoPackage");
        }
        convertPackage = context.getProperty("convertPackage");
        if (convertPackage == null) {
            convertPackage = this.getProperties().getProperty("convertPackage");
        }
        controllerPackage = context.getProperty("controllerPackage");
        if (controllerPackage == null) {
            controllerPackage = this.getProperties().getProperty("controllerPackage");
        }

        mapperJavaPackage = context.getProperty("mapperJavaPackage");
        if (mapperJavaPackage == null) {
            mapperJavaPackage = this.getProperties().getProperty("mapperJavaPackage");
        }

        dataOjbectPackage = context.getProperty("dataOjbectPackage");
        if (dataOjbectPackage == null) {
            dataOjbectPackage = this.getProperties().getProperty("dataOjbectPackage");
        }


        List<GeneratedJavaFile> files = new ArrayList<GeneratedJavaFile>();
        for (GeneratedJavaFile javaFile : introspectedTable.getGeneratedJavaFiles()) {
            FullyQualifiedJavaType compilationUnit = javaFile.getCompilationUnit().getType();
            String simpleClassName = compilationUnit.getShortName();
            String simpleBeanName = FieldUtil.firstLower(compilationUnit.getShortName());


            if (simpleClassName.endsWith(DO)) {
                // 生成domain
                CompilationUnit dataObj = javaFile.getCompilationUnit();
                String domainFullyQualifiedName = String.format("%s.%s", domainPackage, dataObj.getType().getShortName().replace(DO, ""));
                TopLevelClass domainClass = Xpojos.genPojo(dataObj, ClassType.domainEntity, domainFullyQualifiedName, new HashSet<>());
                GeneratedJavaFile domainJavaFile = new GeneratedJavaFile(domainClass, baseJavaDir, javaFormatter);
//                try {
//                    new File(shellCallback.getDirectory(baseJavaDir, domainFullyQualifiedName), domainJavaFile.getFileName());
//                } catch (ShellException e) {
//                    logger.error("generate domain error! targetFullyQualifiedName:{}", domainFullyQualifiedName, e);
//                }
                files.add(domainJavaFile);


                //生成dto
                String dtoFullyQualifiedName = String.format("%s.%s", dtoPackage, dataObj.getType().getShortName().replace(DO, DTO));
                TopLevelClass dtoClass = Xpojos.genPojo(dataObj, ClassType.domainAggregationDto, dtoFullyQualifiedName
                        , Arrays.stream(domainObjectExcludeFields.split(",")).collect(Collectors.toSet()));
                GeneratedJavaFile dtoJavaFile = new GeneratedJavaFile(dtoClass, baseJavaDir, javaFormatter);
//                try {
//                    new File(shellCallback.getDirectory(baseJavaDir, dtoFullyQualifiedName), dtoJavaFile.getFileName());
//                } catch (ShellException e) {
//                    logger.error("generate domain error! targetFullyQualifiedName:{}", domainFullyQualifiedName, e);
//                }
                files.add(dtoJavaFile);


                //生成builder
//                Interface builderInterface = XConverters.genConverter(javaFile.getCompilationUnit(), DO, BUILDER_SUBFIX,
//                        builderPackage, domainJavaFile.getCompilationUnit().getType(), parentBuilderClass);
//                GeneratedJavaFile builderJavaFile = new GeneratedJavaFile(builderInterface, baseJavaDir, javaFormatter);
//                files.add(builderJavaFile);


                //生成convert
                Interface converterInterface = XConverters.genConverter(dtoJavaFile.getCompilationUnit(), "", CONVERT_SUBFIX,
                        convertPackage, javaFile.getCompilationUnit().getType(), parentConvertClass);
                GeneratedJavaFile converterJavaFile = new GeneratedJavaFile(converterInterface, baseJavaDir, javaFormatter);
                files.add(converterJavaFile);


            } else if (simpleClassName.endsWith(MAPPER_SUBFIX)) {
                //----------- 生成Repository
                String repositoryInterfaceFullyName = String.format("%s.%s", repositoryPackage, simpleClassName.replace(MAPPER_SUBFIX, "Repository"));
                //定义接口
                Interface repositoryInterface = new Interface(repositoryInterfaceFullyName);
                xCommons.setClassCommet(repositoryInterface, tableDesc + repositoryInterface.getType().getShortName(), author);
                Interface mapperInterface = (Interface) javaFile.getCompilationUnit();

                repositoryInterface.getImportedTypes().addAll(mapperInterface.getImportedTypes().stream().filter(
                        type -> !(type.getFullyQualifiedName().contains("spring")
                                || type.getFullyQualifiedName().contains("mybaits")
//                                || type.getShortName().contains(DO)
                                || type.getFullyQualifiedName().contains("ibaits")))
                        .collect(Collectors.toSet()));
                repositoryInterface.getStaticImports().addAll(mapperInterface.getStaticImports());
                repositoryInterface.getMethods().addAll(mapperInterface.getMethods());

                //将方法参数中xxxDO换成domain
//                for (Method mapperMethod : mapperInterface.getMethods()) {
//                    List<Parameter> parameterList = mapperMethod.getParameters();
//                    Parameter[] parameters = new Parameter[parameterList.size()];
//                    for (int i = 0, size = mapperMethod.getParameters().size(); i < size; i++) {
//                        Parameter parameter = parameterList.get(i);
//                        if (parameter.getType().getShortName().contains(DO)) {
//                            String domainName = parameter.getType().getShortName().replace(DO, "");
//                            String fullTypeSpecification = String.format("%s.%s", domainPackage, domainName);
//                            repositoryInterface.addImportedType(new FullyQualifiedJavaType(fullTypeSpecification));
//                            parameters[i] = new Parameter(new FullyQualifiedJavaType(domainName), FieldUtil.firstLower(domainName), false);
//                        } else {
//                            parameters[i] = parameter;
//                        }
//                    }
//                    // parseValue 方法
//                    Method method = JavaElementGeneratorTools.generateMethod(
//                            mapperMethod.getName(),
//                            JavaVisibility.PUBLIC,
//                            mapperMethod.getReturnType(),
//                            parameters);
//
////                    method.setReturnType(mapperMethod.getReturnType());
//                    //将方法返回值中xxxDO换成domain
//                    if (method.getReturnType().getShortName().contains(DO)) {
//                        String returnTypeShortName = method.getReturnType().getShortName().replace(DO, "");
//                        method.setReturnType(new FullyQualifiedJavaType(returnTypeShortName));
//                    }
//
//                    repositoryInterface.getMethods().add(method);
//                }


                //-----------
                //----------- 生成Repository impl
                String repositoryClassFullyName = String.format("%s.%s", repositoryImplPackage, simpleClassName.replace(MAPPER_SUBFIX, "RepositoryImpl"));
                //定义实现
                TopLevelClass repositoryImplClazz = new TopLevelClass(repositoryClassFullyName);
                //添加注释
                xCommons.setClassCommet(repositoryImplClazz, tableDesc + repositoryImplClazz.getType().getShortName(), author);
                //添加父亲接口
                repositoryImplClazz.addSuperInterface(repositoryInterface.getType());
                repositoryImplClazz.addAnnotation("@Repository");
                //增加import
                repositoryImplClazz.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
                repositoryImplClazz.addImportedType(compilationUnit);
                repositoryImplClazz.addImportedTypes(repositoryInterface.getImportedTypes());

                //Autowired mapper
                Field mapperField = new Field(FieldUtil.firstLower(simpleClassName), compilationUnit);
                mapperField.setVisibility(JavaVisibility.PRIVATE);
                mapperField.addAnnotation("@Autowired");
                commentGenerator.addFieldComment(mapperField, introspectedTable);
                repositoryImplClazz.addField(mapperField);
                //----- Autowired builder
//                String builderClassName = simpleClassName.replace(MAPPER_SUBFIX, "") + "Builder";
//                FullyQualifiedJavaType builderFullClass = new FullyQualifiedJavaType(String.format("%s.%s", builderPackage, builderClassName));
//                repositoryImplClazz.addImportedType(builderFullClass);
//                String bulderBeanName = FieldUtil.firstLower(builderClassName);
//                Field builderField = new Field(bulderBeanName, builderFullClass);
//                builderField.setVisibility(JavaVisibility.PRIVATE);
//                builderField.addAnnotation("@Autowired");
//                commentGenerator.addFieldComment(builderField, introspectedTable);
//                repositoryImplClazz.addField(builderField);
                repositoryImplClazz.getImportedTypes().addAll(mapperInterface.getImportedTypes());
                repositoryImplClazz.getStaticImports().addAll(mapperInterface.getStaticImports());

                for (Method repMethod : repositoryInterface.getMethods()) {
                    xCommons.seMethodCommet(repMethod, getOpName(repMethod.getName()) + tableDesc, repMethod.getParameters(), false, false);
                    List<Parameter> parameterList = repMethod.getParameters();
                    Parameter[] parameters = new Parameter[parameterList.size()];
                    for (int i = 0, size = repMethod.getParameters().size(); i < size; i++) {
                        parameters[i] = parameterList.get(i);
                    }
                    // parseValue 方法
                    Method repImplMethod = JavaElementGeneratorTools.generateMethod(
                            repMethod.getName(),
                            JavaVisibility.PUBLIC,
                            repositoryImplClazz.getType(),
                            parameters);
                    repImplMethod.setReturnType(repMethod.getReturnType());
                    repImplMethod.addAnnotation("@Override");
                    List<Parameter> params = repMethod.getParameters();
                    StringBuilder sbd = new StringBuilder();
                    for (Parameter p : params) {
                        if (sbd.length() > 0) {
                            sbd.append(",");
                        }
//                        if (implMethod.getName().contains("insert") || implMethod.getName().contains("update")) {
//                            //ecsUserMapper.insertEcsUser(ecsUserBuilder.toDataObj(ecsUserDO));
//                            sbd.append(String.format("%s(%s)", bulderBeanName + ".toDataObj", p.getName()));
//                        } else {
//                            sbd.append(p.getName());
//                        }
                        sbd.append(p.getName());
                    }

//                    if (implMethod.getName().contains("select")) {
//                        //ecsUserBuilder.toEntity(ecsUserMapper.selectByUserId(userId));
//                        implMethod.addBodyLine(String.format("return %s(%s.%s(%s));", bulderBeanName + ".toEntity", simpleBeanName, repositoryMethod.getName(), sbd));
//                    } else {
//                        implMethod.addBodyLine(String.format("return %s.%s(%s);", simpleBeanName, repositoryMethod.getName(), sbd));
//                    }

                    repImplMethod.addBodyLine(String.format("return %s.%s(%s);", simpleBeanName, repMethod.getName(), sbd));
                    xCommons.seMethodCommet(repImplMethod, getOpName(repImplMethod.getName()) + tableDesc, repImplMethod.getParameters(), true, false);
//                    commentGenerator.addGeneralMethodComment(implMethod, introspectedTable);
                    FormatTools.addMethodWithBestPosition(repositoryImplClazz, repImplMethod);
                }
                //-----------

                //###############
                //----------- service 接口定义,以repository借口为模板生成service接口
                String serviceFullyQualifiedName = String.format("%s.%s", servicePackage, simpleClassName.replace(MAPPER_SUBFIX, "Service"));

                Interface serviceInterface = new Interface(new FullyQualifiedJavaType(serviceFullyQualifiedName));
                //添加注释
                xCommons.setClassCommet(serviceInterface, tableDesc + serviceInterface.getType().getShortName(), author);

                serviceInterface.getImportedTypes().addAll(repositoryInterface.getImportedTypes().stream().filter(
                        type -> !(type.getFullyQualifiedName().contains("Mapper")))
                        .collect(Collectors.toSet()));
                serviceInterface.getStaticImports().addAll(repositoryImplClazz.getStaticImports());

//                serviceInterface.addImportedType(new FullyQualifiedJavaType(String.format("%s.%s", domainPackage, simpleClassName.replace(MAPPER_SUBFIX, ""))));
                for (Method m : repositoryInterface.getMethods()) {
                    String methodName = m.getName();
                    if (methodName.startsWith(INSERT)) {
                        methodName = methodName.replaceAll(INSERT, ADD);
                    } else if (methodName.startsWith(DELETE)) {
                        methodName = methodName.replaceAll(DELETE, REMOVE);
                    } else if (methodName.startsWith(UPDATE)) {
                        methodName = methodName.replaceAll(UPDATE, MODIFY);
                    } else if (methodName.startsWith(SELECT)) {
                        methodName = methodName.replaceAll(SELECT, GET);
                    }

                    methodName = methodName.replaceAll("PrimaryKey", "Id");


                    Method srvMethod = JavaElementGeneratorTools.generateMethod(
                            methodName,
                            m.getVisibility(),
                            m.getReturnType(),
                            m.getParameters().toArray(new Parameter[m.getParameters().size()]));
                    srvMethod.setReturnType(m.getReturnType());
                    xCommons.seMethodCommet(srvMethod, getOpName(srvMethod.getName()) + tableDesc, srvMethod.getParameters(), false, false);
                    serviceInterface.addMethod(srvMethod);
                }

                //----------------------
                //----------------------
                //--- service impl 定义
                //定义实现
                String serviceImplFullyQualifiedName = String.format("%s.%s", serviceImplPackage, simpleClassName.replace(MAPPER_SUBFIX, "ServiceImpl"));
                TopLevelClass serviceImplClazz = new TopLevelClass(serviceImplFullyQualifiedName);
                //添加注释
                xCommons.setClassCommet(serviceImplClazz, tableDesc + serviceImplClazz.getType().getShortName(), author);
                //添加父亲接口
                serviceImplClazz.addSuperInterface(serviceInterface.getType());
                serviceImplClazz.addAnnotation("@Service");
                //增加import
                serviceImplClazz.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));
                serviceImplClazz.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
                serviceImplClazz.getImportedTypes().addAll(serviceInterface.getImportedTypes());
                serviceImplClazz.addImportedType(serviceInterface.getType());
                serviceImplClazz.addImportedType(repositoryInterface.getType());

                //Autowired mapper
                String repositoryBeanName = FieldUtil.firstLower(repositoryInterface.getType().getShortName());
                Field respositoryField = new Field(FieldUtil.firstLower(repositoryBeanName), repositoryInterface.getType());
                respositoryField.setVisibility(JavaVisibility.PRIVATE);
                respositoryField.addAnnotation("@Autowired");
                commentGenerator.addFieldComment(respositoryField, introspectedTable);
                serviceImplClazz.addField(respositoryField);

                for (Method serviceMethod : serviceInterface.getMethods()) {
                    List<Parameter> parameterList = serviceMethod.getParameters();
                    Parameter[] parameters = new Parameter[parameterList.size()];
                    for (int i = 0, size = serviceMethod.getParameters().size(); i < size; i++) {
                        parameters[i] = parameterList.get(i);
                    }
                    // parseValue 方法
                    Method srvImplMethod = JavaElementGeneratorTools.generateMethod(
                            serviceMethod.getName(),
                            JavaVisibility.PUBLIC,
                            repositoryImplClazz.getType(),
                            parameters);
                    srvImplMethod.setReturnType(serviceMethod.getReturnType());
                    srvImplMethod.addAnnotation("@Override");
                    List<Parameter> params = serviceMethod.getParameters();
                    String methodName = serviceMethod.getName();
                    if (methodName.startsWith(ADD)) {
                        methodName = methodName.replaceAll(ADD, INSERT);
                    } else if (methodName.startsWith(MODIFY)) {
                        methodName = methodName.replaceAll(MODIFY, UPDATE);
                    } else if (methodName.startsWith(GET)) {
                        methodName = methodName.replaceAll(GET, SELECT);
                    } else if (methodName.startsWith(REMOVE)) {
                        methodName = methodName.replaceAll(REMOVE, DELETE);
                    }

                    methodName = methodName.replaceAll("Id", "PrimaryKey");

                    StringBuilder sbd = new StringBuilder();
                    for (Parameter p : params) {
                        if (sbd.length() > 0) {
                            sbd.append(",");
                        }
                        sbd.append(p.getName());
                    }

                    srvImplMethod.addBodyLine(String.format("return %s.%s(%s);", repositoryBeanName, methodName, sbd));
//                    commentGenerator.addGeneralMethodComment(implMethod, introspectedTable);
                    xCommons.seMethodCommet(srvImplMethod, getOpName(srvImplMethod.getName()) + tableDesc, srvImplMethod.getParameters(), true, false);
                    FormatTools.addMethodWithBestPosition(serviceImplClazz, srvImplMethod);
                }

                //#########################
                //------------ facade 层定义
                String domainName = introspectedTable.getMyBatis3JavaMapperType();
                domainName = domainName.substring(domainName.lastIndexOf(".") + 1).replace("Mapper", "");
                FullyQualifiedJavaType pageQueryType = new FullyQualifiedJavaType(String.format("%s.%s%s", dtoPackage, domainName, "PageQueryDto"));


                String facadeFullyQualifiedName = String.format("%s.%s", facadePackage, simpleClassName.replace(MAPPER_SUBFIX, "Facade"));
                String domainClassName = simpleClassName.replace(MAPPER_SUBFIX, "");
                Interface facadeInterface = new Interface(new FullyQualifiedJavaType(facadeFullyQualifiedName));
                //添加注释
                xCommons.setClassCommet(facadeInterface, tableDesc + facadeInterface.getType().getShortName(), author);
                FullyQualifiedJavaType dtoFullType = new FullyQualifiedJavaType(String.format("%s.%s", dtoPackage, simpleClassName.replace(MAPPER_SUBFIX, "DTO")));
                facadeInterface.addImportedType(dtoFullType);
                facadeInterface.addImportedType(pageQueryType);
                facadeInterface.addImportedType(new FullyQualifiedJavaType("cn.com.servyou.xqy.framework.rpc.facade.SingleResult"));
                facadeInterface.addImportedType(new FullyQualifiedJavaType("cn.com.servyou.finance.ecs.facade.base.BasePageResultDTO"));


                //单独增加分页方法 ====== start
                Parameter[] pageParameters = new Parameter[]{new Parameter(pageQueryType, FieldUtil.firstLower(pageQueryType.getShortName()))};
                Method pageMethod = JavaElementGeneratorTools.generateMethod(
                        "queryWithPage",
                        JavaVisibility.PUBLIC,
                        new FullyQualifiedJavaType(String.format("SingleResult<BasePageResultDTO<%s>>", dtoFullType.getShortName())),
                        pageParameters);
                facadeInterface.addMethod(pageMethod);
                xCommons.seMethodCommet(pageMethod, getOpName(pageMethod.getName()) + tableDesc, pageMethod.getParameters(), false, true);
                //单独增加分页方法 ====== end

                for (Method m : serviceInterface.getMethods()) {
                    if (m.getName().contains("page") || m.getName().contains("Page")) {
                        continue;
                    }
                    Parameter[] parameters = new Parameter[m.getParameters().size()];
                    for (int i = 0, size = m.getParameters().size(); i < size; i++) {
                        Parameter p = m.getParameters().get(i);
                        if (p.getType().getShortName().contains(domainClassName)) {
                            p = new Parameter(dtoFullType, p.getName(), false);
                        }
                        parameters[i] = p;
                    }
                    FullyQualifiedJavaType returnType = m.getReturnType().getShortName().contains(domainClassName) ? dtoFullType : m.getReturnType();
                    if (XPrimitives.get(returnType.getShortName()) != null) {
                        returnType = new FullyQualifiedJavaType(String.format("SingleResult<%s>", XPrimitives.get(returnType.getShortName()).getSimpleName()));
                    } else {
                        returnType = new FullyQualifiedJavaType(String.format("SingleResult<%s>", returnType.getShortName()));
                    }
                    Method facadeMethod = JavaElementGeneratorTools.generateMethod(
                            m.getName(),
                            JavaVisibility.PUBLIC,
                            returnType,
                            parameters);
                    facadeInterface.addMethod(facadeMethod);
                    xCommons.seMethodCommet(facadeMethod, getOpName(facadeMethod.getName()) + tableDesc, m.getParameters(), false, true);
                }

                //----------------------
                //----------------------
                //--- facade impl 定义
                //定义实现
                String facadeImplFullyQualifiedName = String.format("%s.%s", facadeImplPackage, simpleClassName.replace(MAPPER_SUBFIX, "FacadeImpl"));
                TopLevelClass facadeImplClazz = new TopLevelClass(facadeImplFullyQualifiedName);
                //添加注释
                xCommons.setClassCommet(facadeImplClazz, tableDesc + facadeImplClazz.getType().getShortName(), author);
                //添加父亲接口
                facadeImplClazz.addSuperInterface(facadeInterface.getType());
                facadeImplClazz.addAnnotation("@Service");
                //增加import
                facadeImplClazz.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
                facadeImplClazz.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));
                facadeImplClazz.addImportedType(new FullyQualifiedJavaType("cn.com.servyou.xqy.framework.rpc.facadeimpl.ResultServiceCheckCallback"));
                FullyQualifiedJavaType serviceTemplateFullType = new FullyQualifiedJavaType("cn.com.servyou.xqy.framework.rpc.facadeimpl.ServiceTemplate");
                facadeImplClazz.addImportedType(serviceTemplateFullType);
                facadeImplClazz.getImportedTypes().addAll(facadeInterface.getImportedTypes());
                facadeImplClazz.addImportedType(facadeInterface.getType());
                facadeImplClazz.addImportedType(serviceInterface.getType());

                //Autowired service
                String serviceBeanName = FieldUtil.firstLower(serviceInterface.getType().getShortName());
                Field serviceField = new Field(FieldUtil.firstLower(serviceBeanName), serviceInterface.getType());
                serviceField.setVisibility(JavaVisibility.PRIVATE);
                serviceField.addAnnotation("@Autowired");
                commentGenerator.addFieldComment(serviceField, introspectedTable);
                facadeImplClazz.addField(serviceField);

                //Autowired  ServiceTemplate
                String serviceTemplateBeanName = FieldUtil.firstLower(serviceTemplateFullType.getShortName());
                Field serviceTemplateField = new Field(FieldUtil.firstLower(serviceTemplateBeanName), serviceTemplateFullType);
                serviceTemplateField.setVisibility(JavaVisibility.PRIVATE);
                serviceTemplateField.addAnnotation("@Autowired");
                commentGenerator.addFieldComment(serviceTemplateField, introspectedTable);
                facadeImplClazz.addField(serviceTemplateField);

                //Autowired convert
                FullyQualifiedJavaType dtoConvertFullType = new FullyQualifiedJavaType(convertPackage + "." + simpleClassName.replace(MAPPER_SUBFIX, "DTOConvert"));
                String dtoConvertClassName = dtoConvertFullType.getShortName();
                String dtoConvertBeanName = FieldUtil.firstLower(dtoConvertClassName);
                Field convertField = new Field(dtoConvertBeanName, dtoConvertFullType);
                convertField.setVisibility(JavaVisibility.PRIVATE);
                convertField.addAnnotation("@Autowired");
                commentGenerator.addFieldComment(convertField, introspectedTable);
                facadeImplClazz.addField(convertField);
                facadeImplClazz.addImportedType(dtoConvertFullType);
                facadeImplClazz.addImportedType(pageQueryType);


                for (Method method : facadeInterface.getMethods()) {
                    List<Parameter> parameterList = method.getParameters();
                    Parameter[] parameters = new Parameter[parameterList.size()];
                    for (int i = 0, size = method.getParameters().size(); i < size; i++) {
                        parameters[i] = parameterList.get(i);
                    }
                    // parseValue 方法
                    Method implMethod = JavaElementGeneratorTools.generateMethod(
                            method.getName(),
                            JavaVisibility.PUBLIC,
                            repositoryImplClazz.getType(),
                            parameters);
                    implMethod.setReturnType(method.getReturnType());
                    implMethod.addAnnotation("@Override");
                    List<Parameter> params = method.getParameters();

                    StringBuilder paramSbd = new StringBuilder();
                    for (Parameter p : params) {
                        if (paramSbd.length() > 0) {
                            paramSbd.append(",");
                        }
                        if (implMethod.getName().contains("add") || implMethod.getName().contains("modify")) {
                            //authorDTOConvert.toEntity(author)
                            paramSbd.append(String.format("%s(%s)", dtoConvertBeanName + ".toEntity", p.getName()));
                        } else {
                            paramSbd.append(p.getName());
                        }
                    }


                    implMethod.addBodyLine("return " + serviceTemplateBeanName + ((implMethod.getName().startsWith("get") || implMethod.getName().startsWith("query")) ? ".executeWithoutTx(" : ".executeWithTx("));
                    implMethod.addBodyLine(String.format("%s,new ResultServiceCheckCallback<%s>() {", "\"请补充业务描述！\"", method.getReturnType()));
                    implMethod.addBodyLine("@Override public void check() {}");
                    implMethod.addBodyLine(String.format("@Override public %s createDefaultResult() { return SingleResult.success(null); }", method.getReturnType()));
                    implMethod.addBodyLine(String.format("@Override public void executeService(%s result) {", method.getReturnType()));

                    String returnVal = null;
                    if (implMethod.getName().contains("get")) {
                        returnVal = String.format("%s(%s.%s(%s))", dtoConvertBeanName + ".toDto", serviceBeanName, method.getName(), paramSbd);
                        implMethod.addBodyLine(String.format("result.setEntity(%s);}});", returnVal));
                    } else if (implMethod.getName().contains("page") || implMethod.getName().contains("Page")) {
                        implMethod.addBodyLine("BasePageResultDTO<" + dtoFullType.getShortName() + "> resultDto = new BasePageResultDTO();");
                        implMethod.addBodyLine("resultDto.setTotalCount(" + serviceBeanName + ".getPageCount(" + paramSbd + "));");
                        implMethod.addBodyLine("resultDto.setPageData(" + dtoConvertBeanName + ".toDtoList(" + serviceBeanName + ".getPage(userMailPageQueryDto)));");
                        implMethod.addBodyLine("result.setEntity(resultDto);}});");
                    } else {
                        returnVal = String.format("%s.%s(%s)", serviceBeanName, method.getName(), paramSbd);
                        implMethod.addBodyLine(String.format("result.setEntity(%s);}});", returnVal));
                    }

                    xCommons.seMethodCommet(implMethod, getOpName(implMethod.getName()) + tableDesc, implMethod.getParameters(), true, false);
                    FormatTools.addMethodWithBestPosition(facadeImplClazz, implMethod);
                }


                //----------------------
                //----------------------
                //--- controller impl 定义
                //定义实现
                String controllerSimpleName = simpleClassName.replace(MAPPER_SUBFIX, CONTROLLER_SUBFIX);
                String controllerFullyQualifiedName = String.format("%s.%s", controllerPackage, controllerSimpleName);
                TopLevelClass controllerClazz = new TopLevelClass(controllerFullyQualifiedName);
                //添加注释
                xCommons.setClassCommet(controllerClazz, tableDesc + controllerClazz.getType().getShortName(), author);
                //添加父亲接口
                controllerClazz.setSuperClass(new FullyQualifiedJavaType(parentControllerClass));
                controllerClazz.addAnnotation("@Controller");
                controllerClazz.addAnnotation("@ResponseBody");
                controllerClazz.addAnnotation("@ReturnApiResponse");
                controllerClazz.addAnnotation("@Validated");
                controllerClazz.addAnnotation(String.format("@RequestMapping(%s)", "\"" + FieldUtil.firstLower(controllerSimpleName.replaceAll(CONTROLLER_SUBFIX, "")) + "\""));
                //增加import
                controllerClazz.addImportedType(new FullyQualifiedJavaType("cn.com.servyou.finance.ecs.common.web.ReturnApiResponse"));
                controllerClazz.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
                controllerClazz.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Controller"));
                controllerClazz.addImportedType(new FullyQualifiedJavaType("org.springframework.web.bind.annotation.GetMapping"));
                controllerClazz.addImportedType(new FullyQualifiedJavaType("org.springframework.web.bind.annotation.PostMapping"));
                controllerClazz.addImportedType(new FullyQualifiedJavaType("org.springframework.web.bind.annotation.RequestBody"));
                controllerClazz.addImportedType(new FullyQualifiedJavaType("org.springframework.web.bind.annotation.RequestMapping"));
                controllerClazz.addImportedType(new FullyQualifiedJavaType("org.springframework.web.bind.annotation.RequestParam"));
                controllerClazz.addImportedType(new FullyQualifiedJavaType("org.springframework.web.bind.annotation.ResponseBody"));
                controllerClazz.addImportedType(new FullyQualifiedJavaType("org.springframework.validation.annotation.Validated"));
                controllerClazz.addImportedType(facadeInterface.getType());
                controllerClazz.getImportedTypes().addAll(facadeInterface.getImportedTypes());


                //Autowired service
                String facadeBeanName = FieldUtil.firstLower(facadeInterface.getType().getShortName());
                Field facadeField = new Field(FieldUtil.firstLower(facadeBeanName), facadeInterface.getType());
                facadeField.setVisibility(JavaVisibility.PRIVATE);
                facadeField.addAnnotation("@Autowired");
                commentGenerator.addFieldComment(facadeField, introspectedTable);
                controllerClazz.addField(facadeField);


                for (Method facadeMethod : facadeInterface.getMethods()) {
                    List<Parameter> parameterList = facadeMethod.getParameters();
                    Parameter[] parameters = new Parameter[parameterList.size()];
                    for (int i = 0, size = facadeMethod.getParameters().size(); i < size; i++) {
                        parameters[i] = parameterList.get(i);
                    }

                    FullyQualifiedJavaType returnType = facadeMethod.getReturnType().getTypeArguments().get(0);
                    // parseValue 方法
                    Method webMethod = JavaElementGeneratorTools.generateMethod(
                            facadeMethod.getName(),
                            JavaVisibility.PUBLIC,
                            returnType,
                            parameters);
                    if (facadeMethod.getName().startsWith("get")
                            || facadeMethod.getName().startsWith("query")
                            || facadeMethod.getName().startsWith("list")
                            || facadeMethod.getName().startsWith("select")) {
                        webMethod.addAnnotation("@GetMapping(\"" + facadeMethod.getName() + "\")");
                    } else {
                        webMethod.addAnnotation("@PostMapping(\"" + facadeMethod.getName() + "\")");
                    }

                    List<Parameter> params = facadeMethod.getParameters();

                    StringBuilder sbd = new StringBuilder();
                    for (Parameter p : params) {
                        if (sbd.length() > 0) {
                            sbd.append(",");
                        }
                        sbd.append(p.getName());
                    }
                    webMethod.addBodyLine(String.format("return %s.%s(%s).getEntity();", facadeBeanName, facadeMethod.getName(), sbd));

//                    commentGenerator.addGeneralMethodComment(m, introspectedTable);
                    xCommons.seMethodCommet(webMethod, getOpName(webMethod.getName()) + tableDesc, webMethod.getParameters(), false, true);
                    FormatTools.addMethodWithBestPosition(controllerClazz, webMethod);
                }

                //--------------------------
                //--------------------------

                GeneratedJavaFile repositoryJavaFile = new GeneratedJavaFile(repositoryInterface, baseJavaDir, javaFormatter);
                GeneratedJavaFile implJavaFile = new GeneratedJavaFile(repositoryImplClazz, baseJavaDir, javaFormatter);
                GeneratedJavaFile serviceJavaFile = new GeneratedJavaFile(serviceInterface, baseJavaDir, javaFormatter);
                GeneratedJavaFile serviceImplJavaFile = new GeneratedJavaFile(serviceImplClazz, baseJavaDir, javaFormatter);
                GeneratedJavaFile facadeJavaFile = new GeneratedJavaFile(facadeInterface, baseJavaDir, javaFormatter);
                GeneratedJavaFile facadeImplJavaFile = new GeneratedJavaFile(facadeImplClazz, baseJavaDir, javaFormatter);
                GeneratedJavaFile controllerJavaFile = new GeneratedJavaFile(controllerClazz, baseJavaDir, javaFormatter);


//                try {
//                    File repositoryDir = shellCallback.getDirectory(baseJavaDir, repositoryPackage);
//                    File implDir = shellCallback.getDirectory(baseJavaDir, repositoryImplPackage);
//                    File serviceDir = shellCallback.getDirectory(baseJavaDir, servicePackage);
//                    File serviceImplDir = shellCallback.getDirectory(baseJavaDir, facadeImplPackage);
//                    File facadeDir = shellCallback.getDirectory(baseJavaDir, facadePackage);
//                    File facadeImplDir = shellCallback.getDirectory(baseJavaDir, facadeImplPackage);
//                    File controllerImplDir = shellCallback.getDirectory(baseJavaDir, controllerPackage);
//
//                    File repositoryFile = new File(repositoryDir, repositoryJavaFile.getFileName());
//                    File implFile = new File(implDir, implJavaFile.getFileName());
//                    File serviceFile = new File(serviceDir, serviceJavaFile.getFileName());
//                    File serviceImplFile = new File(serviceImplDir, serviceImplJavaFile.getFileName());
//                    File facadeFile = new File(facadeDir, facadeJavaFile.getFileName());
//                    File facadeImplFile = new File(facadeImplDir, facadeImplJavaFile.getFileName());
//                    File controllerImplFile = new File(controllerImplDir, controllerJavaFile.getFileName());

                files.add(repositoryJavaFile);
                files.add(implJavaFile);
                files.add(serviceJavaFile);
                files.add(serviceImplJavaFile);
                files.add(facadeJavaFile);
                files.add(facadeImplJavaFile);
                files.add(controllerJavaFile);
//                } catch (ShellException e) {
//                    e.printStackTrace();
//                }
            }
        }
        return files;
    }


    String getOpName(String methodName) {
        if (methodName.startsWith("add") || methodName.startsWith("insert") || methodName.startsWith("create")) {
            return "新增";
        } else if (methodName.startsWith("delete") || methodName.startsWith("remove")) {
            return "删除";
        } else if (methodName.startsWith("update") || methodName.startsWith("edit") || methodName.startsWith("modify")) {
            return "修改";
        } else {
            if (methodName.contains("page") || methodName.contains("Page")) {
                return "分页查询";
            } else {
                return "查询";
            }
        }
    }
}
