package com.itfsw.mybatis.generator.plugins.utils;

public class FieldUtil {
    /**
     * 下划线转驼峰
     *
     * @param input
     * @return
     */
    public static String Underline2hump(String input) {
        StringBuilder sb = new StringBuilder();
        String[] arr = input.split("_");
        sb.append(arr[0]);
        for (int i = 1; i < arr.length; i++) {
            String temp = arr[i].substring(0, 1).toUpperCase() + arr[i].substring(1, arr[i].length());
            sb.append(temp);
        }
        return sb.toString();
    }

    /**
     * 首字母大写
     *
     * @param input
     * @return
     */
    public static String firstUpper(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1, input.length());
    }

    /**
     * 首字母小写
     *
     * @param input
     * @return
     */
    public static String firstLower(String input) {
        return input.substring(0, 1).toLowerCase() + input.substring(1, input.length());
    }

    public static void main(String[] args) throws Exception {
        System.out.println(FieldUtil.Underline2hump("est"));
    }
}
