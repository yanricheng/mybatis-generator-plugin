package com.itfsw.mybatis.generator.plugins.biz.curd;

import com.itfsw.mybatis.generator.plugins.biz.enums.ClassType;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static com.itfsw.mybatis.generator.plugins.biz.Constants.GETTER;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.SETTER;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.TOSTRING;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.getter;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.serializable;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.setter;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.toString;

/**
 * @author yrc
 * @date 2021/8/28
 */
public final class Xpojos {

    protected static final Logger logger = LoggerFactory.getLogger(Xpojos.class);

    public static TopLevelClass genPojo(CompilationUnit sourceDataObject,
                                        ClassType targetClassType,
                                        String targetFullyQualifiedName,
                                        Set<String> excludeFieldSet) {
        FullyQualifiedJavaType dataObjFullType = sourceDataObject.getType();

        //定义实现
        TopLevelClass targetClazz = new TopLevelClass(targetFullyQualifiedName);
        //添加父接口
        targetClazz.addSuperInterface(serializable);
        targetClazz.addImportedType(serializable);
        targetClazz.setVisibility(JavaVisibility.PUBLIC);

        //#####################
        if (targetClassType == ClassType.domainEntity) {
            //添加注释
            xCommons.setCommentInfo(targetClazz, "该类继承了DataObject，是个领域对象，主要是描述域属性及行为");
            targetClazz.setSuperClass(dataObjFullType.getShortName());
            targetClazz.addImportedType(dataObjFullType);
        } else if (targetClassType == ClassType.domainAggregationDto) {
            TopLevelClass dataObjClazz = (TopLevelClass) sourceDataObject;
            targetClazz.getImportedTypes().addAll(dataObjClazz.getImportedTypes());
            targetClazz.getFields().addAll(dataObjClazz.getFields());
            targetClazz.getAnnotations().addAll(dataObjClazz.getAnnotations());
        }

        if (excludeFieldSet != null) {
            Set<Field> fieldSet = new HashSet<>();
            for (Field f : targetClazz.getFields()) {
                if (excludeFieldSet.contains(f.getName())) {
                    fieldSet.add(f);
                }
            }
            targetClazz.getFields().removeAll(fieldSet);
        }

        targetClazz.getAnnotations().add(GETTER);
        targetClazz.getAnnotations().add(SETTER);
        targetClazz.getAnnotations().add(TOSTRING);
//        targetClazz.getAnnotations().add(SUPERBUILDER);
        targetClazz.addImportedType(getter);
        targetClazz.addImportedType(setter);
        targetClazz.addImportedType(toString);
//        targetClazz.addImportedType(superBuilder);

        //------------------
        return targetClazz;
    }
}
