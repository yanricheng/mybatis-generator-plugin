package com.itfsw.mybatis.generator.plugins.biz.curd;

import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.dom.java.JavaVisibility;

/**
 * @author yrc
 * @date 2021/8/28
 */
public class xCommons {

    public static void setCommentInfo(JavaElement ele, String doc) {
        ele.setVisibility(JavaVisibility.PUBLIC);
        ele.addJavaDocLine("/**");
        ele.addJavaDocLine(" * " + doc + "");
        ele.addJavaDocLine(" */");
    }
}
