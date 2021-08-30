package com.itfsw.mybatis.generator.plugins.biz;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

/**
 * @author yrc
 * @date 2021/8/28
 */
public class Constants {
    public static final String BASE_JAVA_DIR = "baseJavaDir";
    public static final String DOMAIN_OBJ_EXCLUDE_FIELDS = "domainObjectExcludeFields";

    public static final FullyQualifiedJavaType serializable = new FullyQualifiedJavaType("java.io.Serializable");
    public static final FullyQualifiedJavaType getter = new FullyQualifiedJavaType("lombok.Getter");
    public static final FullyQualifiedJavaType setter = new FullyQualifiedJavaType("lombok.Setter");
    public static final FullyQualifiedJavaType toString = new FullyQualifiedJavaType("lombok.ToString");
    public static final FullyQualifiedJavaType superBuilder = new FullyQualifiedJavaType("lombok.experimental.SuperBuilder");
    public static final String GETTER = "@Getter";
    public static final String SETTER = "@Setter";
    public static final String TOSTRING = "@ToString";
    public static final String SUPERBUILDER = "@SuperBuilder";


    public static final String DO = "DO";
    public static final String DTO = "DTO";
    public static final String INSERT = "insert";
    public static final String ADD = "add";
    public static final String UPDATE = "update";
    public static final String DELETE = "delete";
    public static final String REMOVE = "remove";
    public static final String MODIFY = "modify";
    public static final String SELECT = "select";
    public static final String GET = "get";

    public static final String BUILDER_SUBFIX = "Builder";
    public static final String CONVERT_SUBFIX = "Convert";
    public static final String CONTROLLER_SUBFIX = "Controller";
    public static final String MAPPER_SUBFIX = "Mapper";
}
