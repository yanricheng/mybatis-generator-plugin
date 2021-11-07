package com.itfsw.mybatis.generator.plugins.biz.curd;

import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Parameter;

import java.util.List;

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

    public static void setCommentInfo(JavaElement ele, String desc, List<Parameter> params,boolean isImpl) {
        ele.setVisibility(JavaVisibility.PUBLIC);
        ele.addJavaDocLine("/**");
        ele.addJavaDocLine(" * " + desc + "");
        if(isImpl){
            ele.addJavaDocLine(" * 请补充实现原理!!");
        }
        for (Parameter param : params) {
            ele.addJavaDocLine(" * @param " + param.getName() + "");
        }
        ele.addJavaDocLine(" * @return ");
        ele.addJavaDocLine(" */");
    }
}
