package io.choerodon.test.manager.infra.common.utils;

/**
 * @author shinan.chen
 * @since 2019/1/22
 */
public class NameUtil {

    public static final String UNDERLINE = "_";
    public static final String MIDDLELINE = "-";

    /***
     * 下划线命名转为驼峰命名
     *
     * @param para 下划线命名的字符串
     */
    public static String UnderlineToHump(String para) {
        StringBuilder result = new StringBuilder();
        String a[] = para.split(UNDERLINE);
        for (String s : a) {
            if (!para.contains(UNDERLINE)) {
                result.append(s);
                continue;
            }
            if (result.length() == 0) {
                result.append(s.toLowerCase());
            } else {
                result.append(s.substring(0, 1).toUpperCase());
                result.append(s.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }


    /***
     * 驼峰命名转为中划线命名
     *
     * @param para 驼峰命名的字符串
     */
    public static String HumpToMiddleline(String para) {
        StringBuilder sb = new StringBuilder(para);
        int temp = 0;
        if (!para.contains(MIDDLELINE)) {
            for (int i = 0; i < para.length(); i++) {
                if (Character.isUpperCase(para.charAt(i))) {
                    sb.insert(i + temp, MIDDLELINE);
                    temp += 1;
                }
            }
        }
        return sb.toString().toLowerCase();
    }
}
