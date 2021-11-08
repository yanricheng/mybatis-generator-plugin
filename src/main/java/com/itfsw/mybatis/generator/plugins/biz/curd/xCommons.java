package com.itfsw.mybatis.generator.plugins.biz.curd;

import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Parameter;

import java.util.Date;
import java.util.List;

/**
 * @author yrc
 * @date 2021/8/28
 */
public class xCommons {

    public static void setClassCommet(JavaElement ele, String bizDesc, String author) {
        ele.setVisibility(JavaVisibility.PUBLIC);
        ele.addJavaDocLine("/**");
        ele.addJavaDocLine(" * 业务描述：" + bizDesc + "");
        ele.addJavaDocLine(" * @author " + author + "");
        ele.addJavaDocLine(" * @date " + new Date().toLocaleString() + "");
        ele.addJavaDocLine(" */");
    }

    public static void seMethodCommet(JavaElement ele, String desc, List<Parameter> params, boolean isImpl, boolean needBiz) {
        ele.setVisibility(JavaVisibility.PUBLIC);
        ele.addJavaDocLine("/**");
        ele.addJavaDocLine(" * 业务场景： " + desc + "");
        if (needBiz) {
            ele.addJavaDocLine(" * 调用方： 请补充!");
            ele.addJavaDocLine(" * 调用频次： 请补充!");
        }
        if (isImpl) {
            ele.addJavaDocLine(" * 实现概述：请补充!");
        }
        for (Parameter param : params) {
            ele.addJavaDocLine(" * @param " + param.getName() + "");
        }
        ele.addJavaDocLine(" * @return ");
        ele.addJavaDocLine(" */");
    }
}
