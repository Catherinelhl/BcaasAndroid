package io.bcaas.tools;

import android.text.TextPaint;
import android.widget.TextView;

import io.bcaas.base.BCAASApplication;
import io.bcaas.constants.Constants;

/**
 * @projectName: BcaasAndroid
 * @packageName: io.bcaas.tools
 * @author: catherine
 * @time: 2018/9/8
 * <p>
 * 工具類：文本內容判斷工具
 */
public class TextTool {


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
        return intelligentOmissionText(view, measuredWidth, content, false);
    }

    public static String intelligentOmissionText(TextView view, int measuredWidth, String content, boolean containChinese) {
        if (StringTool.isEmpty(content)) {
            return "";
        }
        if (measuredWidth == 0) {
            return content;
        }

        // textView getPaint measureText 获得控件的TextView的对象
        TextPaint textPaint = view.getPaint();
        // 获得输入的text 的宽度
        float textPaintWidth = textPaint.measureText(content);
//        LogTool.d(TAG, content, textPaintWidth + "===" + measuredWidth + "===textSize:" + textPaint.getTextSize());
        //先判断文本是否超过2行
        if (textPaintWidth < measuredWidth) {
            return content;//能显示完全我们直接返回就行了。无需操作
        }
        //当前的textview 的textSize为15sp 其实很明显文字大小不同，每个字符占用的长度也是不同的，这里假设为15。
        // 我通过日志知道：".",0,"a","A","好"，“ ” 等。这些分别占用的数值为：8，10，16，17，30，30。
        // 所以说其实挺麻烦的，因为区别很大。这里明显中文的显示是最大的为30。所以我们长度给一个最低范围-30。
        // 首先计算一共能显示多少个字符：
        //如果当前囊括中文，那么就增大字号
        //如果当前是TV，那么就显示本身的字体大小
        float textSize = BCAASApplication.isIsPhone() ? containChinese ? 28 : 24 : textPaint.getTextSize();
        float num = (measuredWidth / textSize);
        int halfShow = (int) ((num - 3) / 2);
        int contentLength = content.length();
//        LogTool.d(TAG, contentLength + "===" + halfShow);
        if (halfShow > contentLength) {
            return content;
        }
        //取文本的一半
        String pre = content.substring(0, halfShow - 1);
        String last;
        int contentHalfLength = contentLength / 2;
        // 如果可以显示的内容一半的区域大于内容的一半长度，代表内容不够显示，那么就取
        if (halfShow > contentHalfLength) {
            last = content.substring(content.length() - contentHalfLength - 3, contentLength);
        } else {
            last = content.substring(contentLength - halfShow, contentLength);

        }
        return pre + Constants.ValueMaps.THREE_STAR + last;
    }

    //获取传入文本，获取所占用控件的大小
    public static float getViewWidth(TextView view, String content) {
        // textView getPaint measureText 获得控件的TextView的对象
        TextPaint textPaint = view.getPaint();
        // 获得输入的text 的宽度
        return textPaint.measureText(content);
    }
}
