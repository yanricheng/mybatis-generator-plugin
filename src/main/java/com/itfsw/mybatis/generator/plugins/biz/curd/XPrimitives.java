package com.itfsw.mybatis.generator.plugins.biz.curd;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yrc
 * @date 2021/8/28
 */
public class XPrimitives {
    private static final Map<String, Class<?>> namePrimitiveMap = new HashMap<>();

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

    public static Class get(String primitiveType) {
        return namePrimitiveMap.get(primitiveType);
    }

}
