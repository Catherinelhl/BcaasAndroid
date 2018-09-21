package io.bcaas.tools.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.bcaas.constants.Constants;

/**
 * @author Costa
 * @version 1.0.0
 * @since 2017-03-01
 */

public class RegexTool {

    private static final String EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String MONEY = "^\\d{n}$";
    private static final String PASSWORD = "^(?![0-9]+$)(?![a-zA-Z]+$)[a-zA-Z0-9!@#$%^&*_]{8,16}$";
    private static final String VERSION = "^-?[\\d.]+(?:e-?\\d+)?$";

    private static final String AUTHNODE_AUTHORIZE_KEY = "OrAanUgeTBlHocNkBOcaDasE";
    private static final String PC_AUTHORIZE_KEY = "OranPgeBlockBCcaas";
    private static final String MAC_AUTHORIZE_KEY = "OraMngeBAlockBCcaas";
    private static final String IOS_AUTHORIZE_KEY = "OrangeiBlockOBcaasS";
    private static final String ANDROID_AUTHORIZE_KEY = "OrAanNgeDBlRocOkBOcaIasD";


    private static Pattern getPattern(String regex) {

        Pattern pattern = Pattern.compile(regex);

        return pattern;
    }

    public static boolean isRightPassword(String version) {

        Pattern pattern = getPattern(PASSWORD);

        Matcher matcher = pattern.matcher(version);

        return matcher.matches();
    }

    public static boolean isRightMoney(String version) {

        Pattern pattern = getPattern(MONEY);

        Matcher matcher = pattern.matcher(version);

        return matcher.matches();
    }

    public static boolean isValidateVersion(String version) {

        Pattern pattern = getPattern(VERSION);

        Matcher matcher = pattern.matcher(version);

        return matcher.matches();
    }

    public static boolean isValidateAuthNodeKey(String authKey) {
        return authKey.equals(AUTHNODE_AUTHORIZE_KEY) ? true : false;
    }

    public static boolean isValidatePCKey(String authKey) {
        return authKey.equals(PC_AUTHORIZE_KEY) ? true : false;
    }

    public static boolean isValidateMacKey(String authKey) {
        return authKey.equals(MAC_AUTHORIZE_KEY) ? true : false;
    }

    public static boolean isValidateIOSKey(String authKey) {
        return authKey.equals(IOS_AUTHORIZE_KEY) ? true : false;
    }

    public static boolean isValidateAndroidKey(String authKey) {
        return authKey.equals(ANDROID_AUTHORIZE_KEY) ? true : false;
    }

    public static boolean isValidatePassword(String password) {

        Pattern pattern = getPattern(PASSWORD);

        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    /*判断是否是字符*/
    public static boolean isCharacter(String str) {
        if (str.matches(Constants.REGEX.PASSWORD)) {
            return true;
        }
        return false;
    }

    /*将当前的空格替换掉*/
    public static String replaceBlank(String src) {
        String dest = "";
        if (src != null) {
            Pattern pattern = Pattern.compile(Constants.REGEX.REPLACE_BLANK);
            Matcher matcher = pattern.matcher(src);
            dest = matcher.replaceAll("");
        }
        return dest;
    }

    //是否是中文
    public static boolean isChinese(String s) {
        return s.matches("[\u4e00-\u9fa5]+");
    }
}
