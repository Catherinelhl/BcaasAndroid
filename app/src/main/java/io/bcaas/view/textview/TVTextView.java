package io.bcaas.view.textview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;


/**
 * @author catherine.brainwilliam
 * @since 2018/9/25
 * 聲明不需要放大的文本嗯
 */
public class TVTextView extends TextView {
    public TVTextView(Context context) {
        super(context);
    }

    public TVTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
