package io.bcaas.view.textview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import io.bcaas.R;
import io.bcaas.tools.StringTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/28
 * 自定義TextView：TV版带星星的文本
 */
public class TVWithStarTextView extends TextView {
    public TVWithStarTextView(Context context) {
        super(context);
    }

    public TVWithStarTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTextWithStar(String text) {
        if (StringTool.isEmpty(text)) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("* ");
        stringBuffer.append(text);
        SpannableStringBuilder style = new SpannableStringBuilder(stringBuffer);
        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue_0576FE)), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white_f1f1f1)), 3, stringBuffer.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.setText(style);
    }
}
