package io.bcaas.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/20
 */
public class RegexTool {

    //判断是否是字符
    public static boolean isCharacter(String str) {
        if (str.matches("^[a-zA-Z]*")) {
            return true;
        }
        return false;
    }

    //将当前的空格替换掉
    public static String replaceBlank(String src) {
        String dest = "";
        if (src != null) {
            Pattern pattern = Pattern.compile("\t|\r|\n|\\s*");
            Matcher matcher = pattern.matcher(src);
            dest = matcher.replaceAll("");
        }
        return dest;
    }

    //是否符合8位只是字母和数字
    public static boolean isEightBits(String str) {
        String regex = "^[0-9a-zA-Z]{8}$";
        return str.matches(regex);
    }


}
