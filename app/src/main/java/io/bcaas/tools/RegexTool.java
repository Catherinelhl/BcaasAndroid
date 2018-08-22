package io.bcaas.tools;

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
}
