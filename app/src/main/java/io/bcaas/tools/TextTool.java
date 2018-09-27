package io.bcaas.tools;

import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

import io.bcaas.base.BcaasApplication;
import io.bcaas.constants.Constants;

/**
 * @projectName: BcaasAndroid
 * @packageName: io.bcaas.tools
 * @author: catherine
 * @time: 2018/9/8
 * <p>
 * 文本工具
 */
public class TextTool {

    private static String TAG = TextTool.class.getSimpleName();

    /*得到保留前后四位*/
    public static String keepFourText(String content) {
        if (StringTool.notEmpty(content)) {
            String pre = content.substring(0, 4);
            String last = content.substring(content.length() - 5, content.length() - 1);
            String result = pre + Constants.ValueMaps.THREE_STAR + last;
            return result;
        }
        return null;
    }

    /**
     * 智能变换文本显示
     *
     * @param view
     * @param content 获取我们需要展示的文本内容
     * @return
     */
    public static String intelligentOmissionText(TextView view, int measuredWidth, String content) {
        if (StringTool.isEmpty(content)) {
            return "";
        }
        if (measuredWidth == 0) {
            return content;
        }

        // textView getPaint measureText 获得控件的TextView的对象
        TextPaint textPaint = view.getPaint();
        textPaint.getTextSize();
        // 获得输入的text 的宽度
        float textPaintWidth = textPaint.measureText(content);
        LogTool.d(TAG, textPaintWidth + "+++" + measuredWidth);
        //先判断文本是否超过2行
        if (textPaintWidth < measuredWidth) {
            return content;//能显示完全我们直接返回就行了。无需操作
        }
        //当前的textview 的textSize为15sp 其实很明显文字大小不同，每个字符占用的长度也是不同的，这里假设为15。
        // 我通过日志知道：".",0,"a","A","好"，“ ” 等。这些分别占用的数值为：8，10，16，17，30，30。
        // 所以说其实挺麻烦的，因为区别很大。这里明显中文的显示是最大的为30。所以我们长度给一个最低范围-30。
        // 首先计算一共能显示多少个字符：
        float num = (measuredWidth / textPaint.getTextSize());
        int show = (int) ((num - 3) / 2);
        int contentLength = content.length();
        if (show > contentLength) {
            return content;
        }
        String pre = content.substring(0, show - 1);
        String last;
        if (show > contentLength / 2) {
            last = content.substring(content.length() - 4, contentLength);
        } else {
            last = content.substring(contentLength - show, contentLength);

        }
        return pre + Constants.ValueMaps.THREE_STAR + last;
    }
}
