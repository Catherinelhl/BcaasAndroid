package io.bcaas.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @projectName: BottomBar
 * @packageName: cn.catherine.bottombar
 * @author: catherine
 * @time: 2018/9/10
 * 自定義Viewpager：设置当前ViewPage不可滑动
 */
public class BcaasViewpager extends ViewPager {

    public BcaasViewpager(Context context) {
        super(context);
    }

    public BcaasViewpager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        //去除页面切换时的滑动翻页效果
        super.setCurrentItem(item, false);
    }


    //是否可以滑动
    private boolean isCanScroll = true;

    //----------禁止左右滑动------------------
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isCanScroll) {
            return super.onTouchEvent(ev);
        } else {
            return false;
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (isCanScroll) {
            return super.onInterceptTouchEvent(arg0);
        } else {
            return false;
        }

    }
    //-------------------------------------------

    /**
     * 设置 是否可以滑动
     *
     * @param isCanScroll
     */
    public void setCanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;

    }
}