package io.bcaas.constants;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/28
 * <p>
 * 正则管理
 */
public class RegexConstants {
    public static final String IS_CHINESE = "[\u4e00-\u9fa5]+";
    public static String PASSWORD = "^(?![0-9]+$)(?![a-zA-Z]+$)[a-zA-Z0-9!@#$%^&*_]{8,16}$";
    public static String REPLACE_BLANK = "\t|\r|\n|\\s*";
}
