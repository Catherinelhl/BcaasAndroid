package io.bcaas.listener;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * 描述 ：
 * 金额输入过滤器，限制小数点后输入位数
 * 默认限制小数点8位
 * 默认第一位输入小数点时，转换为0.
 * 如果起始位置为0,且第二位跟的不是".",则无法后续输入
 * 、
 */

public class AliasEditTextFilter implements InputFilter {

    private static final String TAG = AliasEditTextFilter.class.getSimpleName();

    //设置最大长度为20个英文字符或者10个中文汉字
    private final static int MAX_LENGTH = 20;

    /**
     * 获取字符数量 汉字占2个长度，英文占1个长度
     *
     * @param text
     * @return
     */
    private int getTextLength(String text) {
        int length = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) > 255) {
                length += 2;
            } else {
                length++;
            }
        }
        return length;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        // 判断EditText输入内容+已有内容长度是否超过设定值，超过则做处理
        if (getTextLength(dest.toString()) + getTextLength(source.toString()) > MAX_LENGTH) {
            // 输入框内已经有20个字符则返回空字符
            if (getTextLength(dest.toString()) >= 20) {
                return ""; // 如果输入框内没有字符，且输入的超过了20个字符，则截取前10个汉字
            } else if (getTextLength(dest.toString()) == 0) {
                return source.toString().substring(0, 10);
            } else {
                // 输入框已有的字符数为双数还是单数
                if (getTextLength(dest.toString()) % 2 == 0) {
                    return source.toString().substring(0, 10 - (getTextLength(dest.toString()) / 2));
                } else {
                    return source.toString().substring(0, 10 - (getTextLength(dest.toString()) / 2 + 1));

                }
            }
        }
        return null;
    }
}