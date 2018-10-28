package io.bcaas.view.tv;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/2
 * 程序所用的不需要放大的layout
 *
 */
public class TVLinearLayout extends LinearLayout {
    public TVLinearLayout(Context context) {
        super(context);
    }

    public TVLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
