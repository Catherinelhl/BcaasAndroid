package io.bcaas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

import io.bcaas.R;

/**
 * @projectName: BottomBar
 * @packageName: cn.catherine.bottombar
 * @author: catherine
 * @time: 2018/9/10
 * <p>
 * 自定义RadioButton:首页底部栏按鈕的样式
 */
public class BcaasRadioButton extends RadioButton {
    //图片大小
    //private int drawableSize;

    public BcaasRadioButton(Context context) {
        this(context, null);
    }

    public BcaasRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BcaasRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.BcaasRadioButton);
        //drawableSize = a.getDimensionPixelSize(R.styleable.MyRadioButton_rbDrawableTopSize, 50);
        Drawable drawableTop = typedArray.getDrawable(R.styleable.BcaasRadioButton_rbDrawableTop);

        //释放资源
        typedArray.recycle();
        if (drawableTop != null) {
            drawableTop.setBounds(0, 0, getResources().getDimensionPixelOffset(R.dimen.d30), getResources().getDimensionPixelOffset(R.dimen.d30));//第一0是距左右边距离，第二0是距上下边距离，第三长度,第四宽度
            setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);

        }
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        if (top != null) {
            //这里只要改后面两个参数就好了，一个宽一个是高，如果想知道为什么可以查找源码
            top.setBounds(0, 0, getResources().getDimensionPixelOffset(R.dimen.d30), getResources().getDimensionPixelOffset(R.dimen.d30));
        }
        setCompoundDrawables(left, top, right, bottom);
    }
}