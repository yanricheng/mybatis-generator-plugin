package com.itfsw.mybatis.generator.plugins;

import com.itfsw.mybatis.generator.plugins.utils.FieldUtil;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;

import java.text.MessageFormat;
import java.util.List;

public class SelectByColumnPlugin extends PluginAdapter {

    private final static String DELIMITER = ",";
    private final static String SELECT_BY = "selectBy";

    @Override
    public boolean validate(List<String> warnings) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
                                   IntrospectedTable introspectedTable) {
        String selectByColumn = introspectedTable.getTableConfigurationProperty(SELECT_BY);
        if (selectByColumn != null && !"".equals(selectByColumn)) {
            for (String column : selectByColumn.split(DELIMITER)) {
                for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
                    if (column.trim().equals(introspectedColumn.getActualColumnName())) {
                        addSelectByMethod(interfaze, introspectedTable, introspectedColumn);
                    }
                }
            }
        }
        return true;
    }

    private void addSelectByMethod(Interface interfaze, IntrospectedTable introspectedTable,
                                   IntrospectedColumn introspectedColumn) {
        String mName = "selectBy" + makeFieldName(introspectedColumn);
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
        List<Element> els = document.getRootElement().getElements();
        String paramType = "String";
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        String selectByColumn = introspectedTable.getTableConfigurationProperty(SELECT_BY);
        if (selectByColumn != null && !"".equals(selectByColumn)) {
            for (String column : selectByColumn.split(DELIMITER)) {
                for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
                    if (column.trim().equals(introspectedColumn.getActualColumnName())) {
                        String field = makeFieldName(introspectedColumn);
                        String returnType = introspectedTable.getBaseResultMapId();
                        String xml = MessageFormat.format(templateXml, field, paramType, returnType, column, tableName,
                                "#{" + introspectedColumn.getJavaProperty() + "}", System.currentTimeMillis());
                        els.add(new TextElement(xml));
                    }
                }
            }
        }
        return true;
    }

    private static final String templateXml = ""
            + "<select id=\"selectBy{0}\" parameterType=\"{1}\" resultMap=\"{2}\">\n"
            + "    select * from {4}\n"
            + "    where {3}={5}\n"
            + " </select>";

    // 首字母大写
    private String makeFieldName(IntrospectedColumn introspectedColumn) {
        String input = introspectedColumn.getJavaProperty();
        return FieldUtil.firstUpper(FieldUtil.Underline2hump(input));
    }
}
