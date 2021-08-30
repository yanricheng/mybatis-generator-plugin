package com.itfsw.mybatis.generator.plugins.biz.curd;

import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;

/**
 * @author yrc
 * @date 2021/8/28
 */
public class XConverters {

    public static Interface genConverter(CompilationUnit classObj, String replaceTarget,
                                         String replacement, String builderTargetPackage,
                                         FullyQualifiedJavaType another, String parentClass) {
        FullyQualifiedJavaType dataObjFullType = classObj.getType();
        String simpleClassName = dataObjFullType.getShortName();
//        String builderTargetPackage = String.format("%s.%s", basePackage, targetSubPackage);

        //#####################
        //构造 builder
        String parentBuilderFullyQualifiedName = String.format("%s<%s,%s>", parentClass, simpleClassName, another.getShortName());
        String builderFullyQualifiedName = String.format("%s.%s", builderTargetPackage, (replaceTarget == null || replaceTarget.trim().length() == 0) ? simpleClassName + replacement : simpleClassName.replace(replaceTarget, replacement));
        FullyQualifiedJavaType mapstructFullyQualifiedName = new FullyQualifiedJavaType("org.mapstruct.Mapper");

        Interface builderInterface = new Interface(builderFullyQualifiedName);
        xCommons.setCommentInfo(builderInterface, builderFullyQualifiedName);
        builderInterface.addImportedType(mapstructFullyQualifiedName);
        builderInterface.addImportedType(dataObjFullType);
        builderInterface.addImportedType(another);
        builderInterface.addAnnotation("@Mapper(componentModel = \"spring\")");
        builderInterface.addSuperInterface(new FullyQualifiedJavaType(parentBuilderFullyQualifiedName));
        //------------------

        return builderInterface;

    }
}
