package com.itfsw.mybatis.generator.plugins;

import com.itfsw.mybatis.generator.plugins.utils.FieldUtil;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;

import java.util.ArrayList;
import java.util.List;

import static com.itfsw.mybatis.generator.plugins.biz.Constants.BASE_JAVA_DIR;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.GETTER;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.SETTER;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.TOSTRING;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.getter;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.setter;
import static com.itfsw.mybatis.generator.plugins.biz.Constants.toString;

public class SelectByPagePlugin extends PluginAdapter {

    private final static String DELIMITER = ",";
    private final static String SELECT_BY = "selectByPage";
    private static final FullyQualifiedJavaType basePageQueryDTO = new FullyQualifiedJavaType("cn.com.servyou.finance.ecs.facade.base.BasePageQueryDTO");
    //    private static final FullyQualifiedJavaType basePageResultDTO = new FullyQualifiedJavaType("cn.com.servyou.finance.ecs.facade.base.BasePageResultDTO");
    private static final String dtoPackageVal = "dtoPackage";
    private static final String dataOjbectPackageVal = "dataOjbectPackage";
    private static String dtoPackage = null;
    private static String dataOjbectPackage = null;
    private static String baseJavaDir = null;
    private static JavaFormatter javaFormatter = null;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    private TopLevelClass generate(String fullyQualifiedName, List<Field> queryDtoFields, FullyQualifiedJavaType supperClass) {
        //定义实现
        TopLevelClass pageQueryDto = new TopLevelClass(fullyQualifiedName);
        //添加父接口
        pageQueryDto.setSuperClass(supperClass.getShortName());
        pageQueryDto.addImportedType(supperClass);
        pageQueryDto.setVisibility(JavaVisibility.PUBLIC);
        pageQueryDto.getAnnotations().add(GETTER);
        pageQueryDto.getAnnotations().add(SETTER);
        pageQueryDto.getAnnotations().add(TOSTRING);
        pageQueryDto.addImportedType(getter);
        pageQueryDto.addImportedType(setter);
        pageQueryDto.addImportedType(toString);
        for (Field f : queryDtoFields) {
            pageQueryDto.addField(f);
            f.setVisibility(JavaVisibility.PRIVATE);
        }
        return pageQueryDto;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        if (javaFormatter == null) {
            javaFormatter = context.getJavaFormatter();
        }

        baseJavaDir = context.getProperty(BASE_JAVA_DIR);
        dtoPackage = context.getProperty(dtoPackageVal);
        if (dtoPackage == null) {
            dtoPackage = this.getProperties().getProperty(dtoPackageVal);
        }


        List<GeneratedJavaFile> files = new ArrayList<>();

        String domainName = introspectedTable.getMyBatis3JavaMapperType();
        domainName = domainName.substring(domainName.lastIndexOf(".") + 1).replace("Mapper", "");
        String pageQueryFullyQualifiedName = String.format("%s.%s%s", dtoPackage, domainName, "PageQueryDto");

        List<Field> queryDtoFields = new ArrayList<>();
        String selectByColumn = introspectedTable.getTableConfigurationProperty(SELECT_BY);
        if (selectByColumn != null && !"".equals(selectByColumn)) {
            for (String column : selectByColumn.split(DELIMITER)) {
                for (IntrospectedColumn col : introspectedTable.getAllColumns()) {
                    if (column.trim().equals(col.getActualColumnName())) {
                        queryDtoFields.add(new Field(col.getJavaProperty(), col.getFullyQualifiedJavaType()));
                    }
                }
            }
        }

//        String pageResultFullyQualifiedName = String.format("%s.%s%s", dtoPackage, domainName, "PageResultDto");
//        List<Field> resultDtoFields = new ArrayList<>();
//        for (IntrospectedColumn col : introspectedTable.getAllColumns()) {
//            resultDtoFields.add(new Field(col.getJavaProperty(), col.getFullyQualifiedJavaType()));
//        }

        files.add(new GeneratedJavaFile(generate(pageQueryFullyQualifiedName, queryDtoFields, basePageQueryDTO), baseJavaDir, javaFormatter));
//        files.add(new GeneratedJavaFile(generate(pageResultFullyQualifiedName, resultDtoFields, basePageResultDTO), baseJavaDir, javaFormatter));
        return files;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
                                   IntrospectedTable introspectedTable) {
        dtoPackage = context.getProperty(dtoPackageVal);
        if (dtoPackage == null) {
            dtoPackage = this.getProperties().getProperty(dtoPackageVal);
        }

        dataOjbectPackage = context.getProperty(dataOjbectPackageVal);
        if (dataOjbectPackage == null) {
            dataOjbectPackage = this.getProperties().getProperty(dataOjbectPackageVal);
        }

        String domainName = introspectedTable.getMyBatis3JavaMapperType();
        domainName = domainName.substring(domainName.lastIndexOf(".") + 1).replace("Mapper", "");
        String pageQueryFullyQualifiedName = String.format("%s.%s%s", dtoPackage, domainName, "PageQueryDto");
        String pageResultFullyQualifiedName = String.format("%s.%s%s", dataOjbectPackage, domainName, "DO");


        FullyQualifiedJavaType pageQueryType = new FullyQualifiedJavaType(pageQueryFullyQualifiedName);
        interfaze.addImportedType(pageQueryType);
        FullyQualifiedJavaType resultType = new FullyQualifiedJavaType(pageResultFullyQualifiedName);
        interfaze.addImportedType(resultType);
        interfaze.addImportedType(new FullyQualifiedJavaType("java.util.List"));


        Method pageMethod = new Method();
        pageMethod.setName("selectPage");
        pageMethod.setVisibility(JavaVisibility.PUBLIC);
        pageMethod.addParameter(new Parameter(pageQueryType, FieldUtil.firstLower(pageQueryType.getShortName())));
        pageMethod.setReturnType(new FullyQualifiedJavaType("java.util.List<" + resultType.getShortName() + ">"));
        interfaze.addMethod(pageMethod);

        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("java.lang.Long");
        Method countMethod = new Method();
        countMethod.setName("selectPageCount");
        countMethod.setVisibility(JavaVisibility.PUBLIC);
        countMethod.addParameter(new Parameter(pageQueryType, FieldUtil.firstLower(pageQueryType.getShortName())));
        countMethod.setReturnType(returnType);
        interfaze.addMethod(countMethod);
        return true;
    }

    private void addSelectByMethod(Interface interfaze, IntrospectedTable introspectedTable,
                                   IntrospectedColumn introspectedColumn) {
        String mName = "selectPage" + makeFieldName(introspectedColumn);

        FullyQualifiedJavaType paramType = FullyQualifiedJavaType.getStringInstance();
//        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType(
//                "java.util.List<" + introspectedTable.getBaseRecordType() + ">");
        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        Method method = new Method();
        method.setName(mName);
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addParameter(new Parameter(paramType, introspectedColumn.getJavaProperty()));
        method.setReturnType(returnType);
        interfaze.addMethod(method);
        String importedType = returnType.getFullyQualifiedName();
        if (!importedType.startsWith("java.lang")) {
//            interfaze.getImportedTypes().add(returnType);
            interfaze.addImportedType(returnType);
        }
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        dtoPackage = context.getProperty(dtoPackageVal);
        if (dtoPackage == null) {
            dtoPackage = this.getProperties().getProperty(dtoPackageVal);
        }
//
//        dataOjbectPackage = context.getProperty(dataOjbectPackageVal);
//        if (dataOjbectPackage == null) {
//            dataOjbectPackage = this.getProperties().getProperty(dataOjbectPackageVal);
//        }

        String domainName = introspectedTable.getMyBatis3JavaMapperType();
        domainName = domainName.substring(domainName.lastIndexOf(".") + 1).replace("Mapper", "");
        String pageQueryFullyQualifiedName = String.format("%s.%s%s", dtoPackage, domainName, "PageQueryDto");
//        String pageResultFullyQualifiedName = String.format("%s.%s%s", dataOjbectPackage, domainName, "DO");


        String selectPageXml = "\n"
                + "<select id=\"selectPage\" parameterType=\"" + pageQueryFullyQualifiedName + "\" resultMap=\"" + introspectedTable.getBaseResultMapId() + "\">\n"
                + "    select * from " + introspectedTable.getFullyQualifiedTableNameAtRuntime() + "\n"
                + "    <include refid=\"page_condition\" /> \n"
                + "      limit #{offset},#{pageSize} \n"
                + "      order by id DECS  \n"
                + " </select>\n";

        String selectPageCountXml = "\n"
                + "<select id=\"selectPageCount\" parameterType=\"" + pageQueryFullyQualifiedName + "\" resultMap=\"" + introspectedTable.getBaseResultMapId() + "\">\n"
                + "    select count(*) from " + introspectedTable.getFullyQualifiedTableNameAtRuntime() + "\n"
                + "    <include refid=\"page_condition\" /> \n"
                + " </select>\n";

        String condition = "\n<sql id=\"page_condition\">\n where is_delete=0 \n %s </sql>\n";

        List<Element> els = document.getRootElement().getElements();
        String paramType = "String";
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        String selectByColumn = introspectedTable.getTableConfigurationProperty(SELECT_BY);
        if (selectByColumn != null && !"".equals(selectByColumn)) {
            StringBuilder sb = new StringBuilder();
            for (String column : selectByColumn.split(DELIMITER)) {
                for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
                    if (column.trim().equals(introspectedColumn.getActualColumnName())) {
                        String field = makeFieldName(introspectedColumn);
                        sb.append(" and " + column + " = " + "#{" + introspectedColumn.getJavaProperty() + "} \n");
                    }
                }
            }
            String con = String.format(condition, sb);
            els.add(new TextElement(con));
            els.add(new TextElement(selectPageXml));
            els.add(new TextElement(selectPageCountXml));
        }
        return true;
    }

    // 首字母大写
    private String makeFieldName(IntrospectedColumn introspectedColumn) {
        String input = introspectedColumn.getJavaProperty();
        return FieldUtil.firstUpper(FieldUtil.Underline2hump(input));
    }
}
