package com.itfsw.mybatis.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import java.io.File;
import java.util.List;

/**
 * @author yrc
 * @date 2021/5/23
 */
public class mkDirPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        super.initialized(introspectedTable);
        Object dirsConf = this.getProperties().get("dirs");
        if (dirsConf != null) {
            String[] dirs = dirsConf.toString().split(",");
            for (String dir : dirs) {
                new File(dir).mkdirs();
            }
        }
    }


}
