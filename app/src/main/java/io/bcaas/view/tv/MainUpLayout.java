package io.bcaas.view.tv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import io.bcaas.R;

/**
 * TV版：界面最外層包裹的View
 */
public class MainUpLayout extends RelativeLayout {
    private int position;
    private Context context;

    public MainUpLayout(Context context) {
        super(context, null);
    }

    public MainUpLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.context = context;
        init(context);
    }

    public MainUpLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init(Context context) {
        setClipChildren(false); //是否现限制其他控件在它周围绘制，这里我们要绘制边框，所以选择false
        setClipToPadding(false); //是否限制控件区域在padding里面，与上面的属性一起使用
        setChildrenDrawingOrderEnabled(true);//用于改变控件的绘制顺序，由于可能用到放大的空间，所以这里需要改变一下
        // 获取焦点，重绘item，防止控件放大被挡住的问题，但是问题是绘制频繁，会导致卡顿,不建议用
        // 最好的办法是看哪一个被挡住了，获取id，然后让控件重画，这样会好点。
		/*getViewTreeObserver()
		.addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
			@Override
			public void onGlobalFocusChanged(SpeedUpView oldFocus, SpeedUpView newFocus) {
				position = indexOfChild(newFocus);
				if (position != -1) {
					invalidate();
				}
			}
		});*/
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (position != -1) {
            if (i == childCount - 1) {
                return position;
            }
            if (i == position) //此为选中的，当让它最后一个绘画，倒叙
                return childCount - 1;
        }
        return i;

    }
}