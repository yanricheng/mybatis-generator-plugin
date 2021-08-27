package com.itfsw.mybatis.generator.plugins;

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
import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yrc
 * @date 2021/5/23
 */
public class GenerateRepositoryPlugin extends BasePlugin {

    private static final Map<String, Class<?>> namePrimitiveMap = new HashMap<>();
    private static final String BASE_JAVA_DIR = "baseJavaDir";
    private static final String DOMAIN_OBJ_EXCLUDE_FIELDS = "domainObjectExcludeFields";

    private static final FullyQualifiedJavaType serializable = new FullyQualifiedJavaType("java.io.Serializable");
    private static final FullyQualifiedJavaType getter = new FullyQualifiedJavaType("lombok.Getter");
    private static final FullyQualifiedJavaType setter = new FullyQualifiedJavaType("lombok.Setter");
    private static final FullyQualifiedJavaType toString = new FullyQualifiedJavaType("lombok.ToString");
    private static final FullyQualifiedJavaType superBuilder = new FullyQualifiedJavaType("lombok.experimental.SuperBuilder");
    private static final String GETTER = "@Getter";
    private static final String SETTER = "@Setter";
    private static final String TOSTRING = "@ToString";
    private static final String SUPERBUILDER = "@SuperBuilder";


    private static final String DO = "DO";
    private static final String DTO = "DTO";
    private static final String INSERT = "insert";
    private static final String ADD = "add";
    private static final String UPDATE = "update";
    private static final String MODIFY = "modify";
    private static final String SELECT = "select";
    private static final String GET = "get";

    private static final String BUILDER_SUBFIX = "Builder";
    private static final String CONVERT_SUBFIX = "Convert";
    private static final String CONTROLLER_SUBFIX = "Controller";
    private static final String MAPPER_SUBFIX = "Mapper";
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

    static {
        namePrimitiveMap.put("boolean", Boolean.class);
        namePrimitiveMap.put("Boolean", Boolean.class);
        namePrimitiveMap.put("byte", Byte.class);
        namePrimitiveMap.put("Byte", Byte.class);
        namePrimitiveMap.put("char", Character.class);
        namePrimitiveMap.put("Character", Character.class);
        namePrimitiveMap.put("short", Short.class);
        namePrimitiveMap.put("Short", Short.class);
        namePrimitiveMap.put("int", Integer.class);
        namePrimitiveMap.put("Integer", Integer.class);
        namePrimitiveMap.put("long", Long.class);
        namePrimitiveMap.put("Long", Long.class);
        namePrimitiveMap.put("double", Double.class);
        namePrimitiveMap.put("Double", Double.class);
        namePrimitiveMap.put("float", Float.class);
        namePrimitiveMap.put("Float", Float.class);
        namePrimitiveMap.put("void", Void.class);
    }

    private ShellCallback shellCallback = null;

    public GenerateRepositoryPlugin() {
        shellCallback = new DefaultShellCallback(false);
    }


    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    private GeneratedJavaFile genPojo(CompilationUnit dataObject, ClassType targetClassType, String targetFullPackage, String targetSimpleClassName) {
        FullyQualifiedJavaType dataObjFullType = dataObject.getType();
        String simpleClassName = dataObjFullType.getShortName();
        String targetFullyQualifiedName = String.format("%s.%s", targetFullPackage, targetSimpleClassName);
        //定义实现
        TopLevelClass targetClazz = new TopLevelClass(targetFullyQualifiedName);
        //添加父接口
        targetClazz.addSuperInterface(serializable);
        targetClazz.addImportedType(serializable);
        targetClazz.setVisibility(JavaVisibility.PUBLIC);

        //#####################
        if (targetClassType == ClassType.domainEntity) {
            //添加注释
            setCommonInfo(targetClazz, "该类继承了DataObject，是个领域对象，主要是描述域属性及行为");
            targetClazz.setSuperClass(dataObjFullType.getShortName());
            targetClazz.addImportedType(dataObjFullType);
        } else {
            TopLevelClass dataObjClazz = (TopLevelClass) dataObject;
            targetClazz.getImportedTypes().addAll(dataObjClazz.getImportedTypes());
            targetClazz.getFields().addAll(dataObjClazz.getFields());
            targetClazz.getAnnotations().addAll(dataObjClazz.getAnnotations());
            Set<String> excludeSet = Arrays.stream(domainObjectExcludeFields.split(",")).collect(Collectors.toSet());
            Set<Field> fieldSet = new HashSet<>();
            for (Field f : targetClazz.getFields()) {
                if (excludeSet.contains(f.getName())) {
                    fieldSet.add(f);
                }
            }
            targetClazz.getFields().removeAll(fieldSet);
        }

        targetClazz.getAnnotations().add(GETTER);
        targetClazz.getAnnotations().add(SETTER);
        targetClazz.getAnnotations().add(TOSTRING);
        targetClazz.getAnnotations().add(SUPERBUILDER);
        targetClazz.addImportedType(getter);
        targetClazz.addImportedType(setter);
        targetClazz.addImportedType(toString);
        targetClazz.addImportedType(superBuilder);

        //------------------

        GeneratedJavaFile domainJavaFile = new GeneratedJavaFile(targetClazz, baseJavaDir, javaFormatter);
        try {
            new File(shellCallback.getDirectory(baseJavaDir, targetFullPackage), domainJavaFile.getFileName());
            return domainJavaFile;
        } catch (ShellException e) {
            e.printStackTrace();
            return null;
        }
    }

    private GeneratedJavaFile genConverter(CompilationUnit classObj, String replaceTarget,
                                           String replacement, String builderTargetPackage, FullyQualifiedJavaType another, String parentClass) {
        JavaFormatter javaFormatter = context.getJavaFormatter();
        FullyQualifiedJavaType dataObjFullType = classObj.getType();
        String simpleClassName = dataObjFullType.getShortName();
//        String builderTargetPackage = String.format("%s.%s", basePackage, targetSubPackage);

        //#####################
        //构造 builder
        String parentBuilderFullyQualifiedName = String.format("%s<%s,%s>", parentClass, simpleClassName, another.getShortName());
        String builderFullyQualifiedName = String.format("%s.%s", builderTargetPackage, (replaceTarget == null || replaceTarget.trim().length() == 0) ? simpleClassName + replacement : simpleClassName.replace(replaceTarget, replacement));
        FullyQualifiedJavaType mapstructFullyQualifiedName = new FullyQualifiedJavaType("org.mapstruct.Mapper");

        Interface builderInterface = new Interface(builderFullyQualifiedName);
        setCommonInfo(builderInterface, builderFullyQualifiedName);
        builderInterface.addImportedType(mapstructFullyQualifiedName);
        builderInterface.addImportedType(dataObjFullType);
        builderInterface.addImportedType(another);
        builderInterface.addAnnotation("@Mapper(componentModel = \"spring\")");
        builderInterface.addSuperInterface(new FullyQualifiedJavaType(parentBuilderFullyQualifiedName));
        //------------------

        GeneratedJavaFile builderJavaFile = new GeneratedJavaFile(builderInterface, baseJavaDir, javaFormatter);
        try {
            new File(shellCallback.getDirectory(baseJavaDir, builderTargetPackage), builderJavaFile.getFileName());
            return builderJavaFile;
        } catch (ShellException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
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
                CompilationUnit dataObj = javaFile.getCompilationUnit();
                GeneratedJavaFile domainJavaFile = genPojo(dataObj, ClassType.domainEntity, domainPackage, dataObj.getType().getShortName().replace(DO, ""));
                files.add(domainJavaFile);

                GeneratedJavaFile dtoJavaFile = genPojo(javaFile.getCompilationUnit(), ClassType.domainAggregationDto, dtoPackage, dataObj.getType().getShortName().replace(DO, DTO));
                files.add(dtoJavaFile);


                GeneratedJavaFile builderJavaFile = genConverter(javaFile.getCompilationUnit(), DO, BUILDER_SUBFIX,
                        builderPackage, domainJavaFile.getCompilationUnit().getType(), parentBuilderClass);
                files.add(builderJavaFile);

                GeneratedJavaFile converterJavaFile = genConverter(dtoJavaFile.getCompilationUnit(), "", CONVERT_SUBFIX,
                        convertPackage, domainJavaFile.getCompilationUnit().getType(), parentConvertClass);
                files.add(converterJavaFile);


            } else if (simpleClassName.endsWith(MAPPER_SUBFIX)) {
                //############
                //----------- 生成Repository
                String repositoryInterfaceFullyName = String.format("%s.%s", repositoryPackage, simpleClassName.replace(MAPPER_SUBFIX, "Repository"));
                //定义接口
                Interface repositoryInterface = new Interface(repositoryInterfaceFullyName);
                setCommonInfo(repositoryInterface, repositoryInterfaceFullyName);
                Interface mapperInterface = (Interface) javaFile.getCompilationUnit();
                repositoryInterface.getImportedTypes().addAll(mapperInterface.getImportedTypes().stream().filter(
                        type -> !(type.getFullyQualifiedName().contains("spring")
                                || type.getFullyQualifiedName().contains("mybaits")
                                || type.getShortName().contains(DO)
                                || type.getFullyQualifiedName().contains("ibaits")))
                        .collect(Collectors.toSet()));
                repositoryInterface.getStaticImports().addAll(mapperInterface.getStaticImports());

                //将方法参数中xxxDO换成domain
                for (Method mapperMethod : mapperInterface.getMethods()) {
                    List<Parameter> parameterList = mapperMethod.getParameters();
                    Parameter[] parameters = new Parameter[parameterList.size()];
                    for (int i = 0, size = mapperMethod.getParameters().size(); i < size; i++) {
                        Parameter parameter = parameterList.get(i);
                        if (parameter.getType().getShortName().contains(DO)) {
                            String domainName = parameter.getType().getShortName().replace(DO, "");
                            String fullTypeSpecification = String.format("%s.%s", domainPackage, domainName);
                            repositoryInterface.addImportedType(new FullyQualifiedJavaType(fullTypeSpecification));
                            parameters[i] = new Parameter(new FullyQualifiedJavaType(domainName), FieldUtil.firstLower(domainName), false);
                        } else {
                            parameters[i] = parameter;
                        }
                    }
                    // parseValue 方法
                    Method method = JavaElementGeneratorTools.generateMethod(
                            mapperMethod.getName(),
                            JavaVisibility.PUBLIC,
                            mapperMethod.getReturnType(),
                            parameters);

//                    method.setReturnType(mapperMethod.getReturnType());
                    //将方法返回值中xxxDO换成domain
                    if (method.getReturnType().getShortName().contains(DO)) {
                        String returnTypeShortName = method.getReturnType().getShortName().replace(DO, "");
                        method.setReturnType(new FullyQualifiedJavaType(returnTypeShortName));
                    }

                    repositoryInterface.getMethods().add(method);
                }


                //-----------
                //----------- 生成Repository impl
                String repositoryClassFullyName = String.format("%s.%s", repositoryImplPackage, simpleClassName.replace(MAPPER_SUBFIX, "RepositoryImpl"));
                //定义实现
                TopLevelClass repositoryImplClazz = new TopLevelClass(repositoryClassFullyName);
                //添加注释
                setCommonInfo(repositoryImplClazz, repositoryClassFullyName);
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
                String builderClassName = simpleClassName.replace(MAPPER_SUBFIX, "") + "Builder";
                FullyQualifiedJavaType builderFullClass = new FullyQualifiedJavaType(String.format("%s.%s", builderPackage, builderClassName));
                repositoryImplClazz.addImportedType(builderFullClass);
                String bulderBeanName = FieldUtil.firstLower(builderClassName);
                Field builderField = new Field(bulderBeanName, builderFullClass);
                builderField.setVisibility(JavaVisibility.PRIVATE);
                builderField.addAnnotation("@Autowired");
                commentGenerator.addFieldComment(builderField, introspectedTable);
                repositoryImplClazz.addField(builderField);
                repositoryImplClazz.getImportedTypes().addAll(mapperInterface.getImportedTypes());
                repositoryImplClazz.getStaticImports().addAll(mapperInterface.getStaticImports());

                for (Method repositoryMethod : repositoryInterface.getMethods()) {
                    List<Parameter> parameterList = repositoryMethod.getParameters();
                    Parameter[] parameters = new Parameter[parameterList.size()];
                    for (int i = 0, size = repositoryMethod.getParameters().size(); i < size; i++) {
                        parameters[i] = parameterList.get(i);
                    }
                    // parseValue 方法
                    Method implMethod = JavaElementGeneratorTools.generateMethod(
                            repositoryMethod.getName(),
                            JavaVisibility.PUBLIC,
                            repositoryImplClazz.getType(),
                            parameters);
                    implMethod.setReturnType(repositoryMethod.getReturnType());
                    implMethod.addAnnotation("@Override");
                    List<Parameter> params = repositoryMethod.getParameters();
                    StringBuilder sbd = new StringBuilder();
                    for (Parameter p : params) {
                        if (sbd.length() > 0) {
                            sbd.append(",");
                        }
                        if (implMethod.getName().contains("insert") || implMethod.getName().contains("update")) {
                            //ecsUserMapper.insertEcsUser(ecsUserBuilder.toDataObj(ecsUserDO));
                            sbd.append(String.format("%s(%s)", bulderBeanName + ".toDataObj", p.getName()));
                        } else {
                            sbd.append(p.getName());
                        }
                    }

                    if (implMethod.getName().contains("select")) {
                        //ecsUserBuilder.toEntity(ecsUserMapper.selectByUserId(userId));
                        implMethod.addBodyLine(String.format("return %s(%s.%s(%s));", bulderBeanName + ".toEntity", simpleBeanName, repositoryMethod.getName(), sbd));
                    } else {
                        implMethod.addBodyLine(String.format("return %s.%s(%s);", simpleBeanName, repositoryMethod.getName(), sbd));
                    }
                    commentGenerator.addGeneralMethodComment(implMethod, introspectedTable);
                    FormatTools.addMethodWithBestPosition(repositoryImplClazz, implMethod);
                }
                //-----------

                //###############
                //----------- service 接口定义,以repository借口为模板生成service接口
                String serviceFullyQualifiedName = String.format("%s.%s", servicePackage, simpleClassName.replace(MAPPER_SUBFIX, "Service"));

                Interface serviceInterface = new Interface(new FullyQualifiedJavaType(serviceFullyQualifiedName));
                //添加注释
                setCommonInfo(serviceInterface, serviceFullyQualifiedName);
                serviceInterface.addImportedType(new FullyQualifiedJavaType(String.format("%s.%s", domainPackage, simpleClassName.replace(MAPPER_SUBFIX, ""))));
                for (Method m : repositoryInterface.getMethods()) {
                    String methodName = m.getName();
                    if (methodName.startsWith(INSERT)) {
                        methodName = methodName.replaceAll(INSERT, ADD);
                    } else if (methodName.startsWith(UPDATE)) {
                        methodName = methodName.replaceAll(UPDATE, MODIFY);
                    } else if (methodName.startsWith(SELECT)) {
                        methodName = methodName.replaceAll(SELECT, GET);
                    }

                    Method method = JavaElementGeneratorTools.generateMethod(
                            methodName,
                            JavaVisibility.PUBLIC,
                            repositoryImplClazz.getType(),
                            m.getParameters().toArray(new Parameter[m.getParameters().size()]));
                    method.setReturnType(m.getReturnType());

                    serviceInterface.addMethod(method);
                }

                //----------------------
                //----------------------
                //--- service impl 定义
                //定义实现
                String serviceImplFullyQualifiedName = String.format("%s.%s", serviceImplPackage, simpleClassName.replace(MAPPER_SUBFIX, "ServiceImpl"));
                TopLevelClass serviceImplClazz = new TopLevelClass(serviceImplFullyQualifiedName);
                //添加注释
                setCommonInfo(serviceImplClazz, serviceImplFullyQualifiedName);
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
                    Method implMethod = JavaElementGeneratorTools.generateMethod(
                            serviceMethod.getName(),
                            JavaVisibility.PUBLIC,
                            repositoryImplClazz.getType(),
                            parameters);
                    implMethod.setReturnType(serviceMethod.getReturnType());
                    implMethod.addAnnotation("@Override");
                    List<Parameter> params = serviceMethod.getParameters();
                    String methodName = serviceMethod.getName();
                    if (methodName.startsWith(ADD)) {
                        methodName = methodName.replaceAll(ADD, INSERT);
                    } else if (methodName.startsWith(MODIFY)) {
                        methodName = methodName.replaceAll(MODIFY, UPDATE);
                    } else if (methodName.startsWith(GET)) {
                        methodName = methodName.replaceAll(GET, SELECT);
                    }

                    StringBuilder sbd = new StringBuilder();
                    for (Parameter p : params) {
                        if (sbd.length() > 0) {
                            sbd.append(",");
                        }
                        sbd.append(p.getName());
                    }

                    implMethod.addBodyLine(String.format("return %s.%s(%s);", repositoryBeanName, methodName, sbd));
                    commentGenerator.addGeneralMethodComment(implMethod, introspectedTable);
                    FormatTools.addMethodWithBestPosition(serviceImplClazz, implMethod);
                }

                //#########################
                //------------ facade 层定义
                String facadeFullyQualifiedName = String.format("%s.%s", facadePackage, simpleClassName.replace(MAPPER_SUBFIX, "Facade"));
                String domainClassName = simpleClassName.replace(MAPPER_SUBFIX, "");
                Interface facadeInterface = new Interface(new FullyQualifiedJavaType(facadeFullyQualifiedName));
                //添加注释
                setCommonInfo(facadeInterface, facadeFullyQualifiedName);
                FullyQualifiedJavaType dtoFullType = new FullyQualifiedJavaType(String.format("%s.%s", dtoPackage, simpleClassName.replace(MAPPER_SUBFIX, "DTO")));
                facadeInterface.addImportedType(dtoFullType);
                facadeInterface.addImportedType(new FullyQualifiedJavaType("cn.com.servyou.xqy.framework.rpc.facade.SingleResult"));

                for (Method m : serviceInterface.getMethods()) {
                    Parameter[] parameters = new Parameter[m.getParameters().size()];
                    for (int i = 0, size = m.getParameters().size(); i < size; i++) {
                        Parameter p = m.getParameters().get(i);
                        if (p.getType().getShortName().contains(domainClassName)) {
                            p = new Parameter(dtoFullType, p.getName(), false);
                        }
                        parameters[i] = p;
                    }
                    FullyQualifiedJavaType returnType = m.getReturnType().getShortName().contains(domainClassName) ? dtoFullType : m.getReturnType();
                    if (namePrimitiveMap.get(returnType.getShortName()) != null) {
                        returnType = new FullyQualifiedJavaType(String.format("SingleResult<%s>", namePrimitiveMap.get(returnType.getShortName()).getSimpleName()));
                    } else {
                        returnType = new FullyQualifiedJavaType(String.format("SingleResult<%s>", returnType.getShortName()));
                    }
                    Method method = JavaElementGeneratorTools.generateMethod(
                            m.getName(),
                            JavaVisibility.PUBLIC,
                            returnType,
                            parameters);
                    facadeInterface.addMethod(method);
                }

                //----------------------
                //----------------------
                //--- facade impl 定义
                //定义实现
                String facadeImplFullyQualifiedName = String.format("%s.%s", facadeImplPackage, simpleClassName.replace(MAPPER_SUBFIX, "FacadeImpl"));
                TopLevelClass facadeImplClazz = new TopLevelClass(facadeImplFullyQualifiedName);
                //添加注释
                setCommonInfo(facadeImplClazz, facadeImplFullyQualifiedName);
                //添加父亲接口
                facadeImplClazz.addSuperInterface(facadeInterface.getType());
                facadeImplClazz.addAnnotation("@Service");
                //增加import
                facadeImplClazz.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
                facadeImplClazz.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));
                facadeImplClazz.addImportedType(new FullyQualifiedJavaType("cn.com.servyou.xqy.framework.rpc.facadeimpl.ResultServiceCheckCallback"));
                FullyQualifiedJavaType serviceTemplateFullType = new FullyQualifiedJavaType("cn.com.servyou.xqy.framework.rpc.facadeimpl.ServiceTemplate");
                facadeInterface.addImportedType(serviceTemplateFullType);
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

                    StringBuilder sbd = new StringBuilder();
                    for (Parameter p : params) {
                        if (sbd.length() > 0) {
                            sbd.append(",");
                        }
                        if (implMethod.getName().contains("add") || implMethod.getName().contains("modify")) {
                            //authorDTOConvert.toEntity(author)
                            sbd.append(String.format("%s(%s)", dtoConvertBeanName + ".toEntity", p.getName()));
                        } else {
                            sbd.append(p.getName());
                        }
                    }


                    implMethod.addBodyLine("return " + serviceTemplateBeanName + (implMethod.getName().startsWith("get") || implMethod.getName().startsWith("query") ? ".executeWithoutTx(" : ".executeWithTx("));
                    implMethod.addBodyLine(String.format("%s,new ResultServiceCheckCallback<%s>() {", "\"" + method.getName() + "\"", method.getReturnType()));
                    implMethod.addBodyLine("@Override public void check() {}");
                    implMethod.addBodyLine(String.format("@Override public %s createDefaultResult() { return SingleResult.success(null); }", method.getReturnType()));
                    implMethod.addBodyLine(String.format("@Override public void executeService(%s result) {", method.getReturnType()));
                    String returnVal = null;
                    if (implMethod.getName().contains("get")) {
                        //ecsUserBuilder.toEntity(ecsUserMapper.selectByUserId(userId));
                        returnVal = String.format("%s(%s.%s(%s))", dtoConvertBeanName + ".toDto", serviceBeanName, method.getName(), sbd);
                    } else {
                        returnVal = String.format("%s.%s(%s)", serviceBeanName, method.getName(), sbd);
                    }

                    implMethod.addBodyLine(String.format("result.setEntity(%s);}});", returnVal));

                    commentGenerator.addGeneralMethodComment(implMethod, introspectedTable);
                    FormatTools.addMethodWithBestPosition(facadeImplClazz, implMethod);
                }


                //----------------------
                //----------------------
                //--- controller impl 定义
                //定义实现
                String controllerFullyQualifiedName = String.format("%s.%s", controllerPackage, simpleClassName.replace(MAPPER_SUBFIX, CONTROLLER_SUBFIX));
                TopLevelClass controllerClazz = new TopLevelClass(controllerFullyQualifiedName);
                //添加注释
                setCommonInfo(controllerClazz, controllerFullyQualifiedName);
                //添加父亲接口
                controllerClazz.setSuperClass(new FullyQualifiedJavaType(parentControllerClass));
                controllerClazz.addAnnotation("@Controller");
                controllerClazz.addAnnotation("@ResponseBody");
                controllerClazz.addAnnotation("@ReturnApiResponse");
                controllerClazz.addAnnotation("@Validated");
                controllerClazz.addAnnotation(String.format("@RequestMapping(%s)", "\"" + FieldUtil.firstLower(simpleClassName) + "\""));
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


                for (Method method : facadeInterface.getMethods()) {
                    List<Parameter> parameterList = method.getParameters();
                    Parameter[] parameters = new Parameter[parameterList.size()];
                    for (int i = 0, size = method.getParameters().size(); i < size; i++) {
                        parameters[i] = parameterList.get(i);
                    }

                    FullyQualifiedJavaType returnType = method.getReturnType().getTypeArguments().get(0);
                    // parseValue 方法
                    Method m = JavaElementGeneratorTools.generateMethod(
                            method.getName(),
                            JavaVisibility.PUBLIC,
                            returnType,
                            parameters);
                    if (method.getName().startsWith("get")
                            || method.getName().startsWith("query")
                            || method.getName().startsWith("list")
                            || method.getName().startsWith("select")) {
                        m.addAnnotation("@GetMapping()");
                    } else {
                        m.addAnnotation("@PostMapping()");
                    }

                    List<Parameter> params = method.getParameters();

                    StringBuilder sbd = new StringBuilder();
                    for (Parameter p : params) {
                        if (sbd.length() > 0) {
                            sbd.append(",");
                        }
                        sbd.append(p.getName());
                    }
                    m.addBodyLine(String.format("return %s.%s(%s).getEntity();", facadeBeanName, method.getName(), sbd));

                    commentGenerator.addGeneralMethodComment(m, introspectedTable);
                    FormatTools.addMethodWithBestPosition(controllerClazz, m);
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


                try {
                    File repositoryDir = shellCallback.getDirectory(baseJavaDir, repositoryPackage);
                    File implDir = shellCallback.getDirectory(baseJavaDir, repositoryImplPackage);
                    File serviceDir = shellCallback.getDirectory(baseJavaDir, servicePackage);
                    File serviceImplDir = shellCallback.getDirectory(baseJavaDir, facadeImplPackage);
                    File facadeDir = shellCallback.getDirectory(baseJavaDir, facadePackage);
                    File facadeImplDir = shellCallback.getDirectory(baseJavaDir, facadeImplPackage);
                    File controllerImplDir = shellCallback.getDirectory(baseJavaDir, controllerPackage);

                    File repositoryFile = new File(repositoryDir, repositoryJavaFile.getFileName());
                    File implFile = new File(implDir, implJavaFile.getFileName());
                    File serviceFile = new File(serviceDir, serviceJavaFile.getFileName());
                    File serviceImplFile = new File(serviceImplDir, serviceImplJavaFile.getFileName());
                    File facadeFile = new File(facadeDir, facadeJavaFile.getFileName());
                    File facadeImplFile = new File(facadeImplDir, facadeImplJavaFile.getFileName());
                    File controllerImplFile = new File(controllerImplDir, controllerJavaFile.getFileName());

                    files.add(repositoryJavaFile);
                    files.add(implJavaFile);
                    files.add(serviceJavaFile);
                    files.add(serviceImplJavaFile);
                    files.add(facadeJavaFile);
                    files.add(facadeImplJavaFile);
                    files.add(controllerJavaFile);
                } catch (ShellException e) {
                    e.printStackTrace();
                }
            }
        }
        return files;
    }

    private void setCommonInfo(JavaElement ele, String doc) {
        ele.setVisibility(JavaVisibility.PUBLIC);
        ele.addJavaDocLine("/**");
        ele.addJavaDocLine(" * " + doc + "");
        ele.addJavaDocLine(" */");
    }
}
