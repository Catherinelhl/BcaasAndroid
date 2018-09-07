package io.bcaas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

import io.bcaas.R;

public class LineEditText extends EditText {

    private Paint mPaint;
    private int mLineColor;
    public LineEditText(Context context) {
        super(context);
    }

    public LineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context, attrs);
    }

    public LineEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context, attrs);
    }

    private void initData(Context context, AttributeSet attrs) {
        TypedArray attrArrays = context.obtainStyledAttributes(attrs, R.styleable.LineEditText);

        mPaint = new Paint();
        int length = attrArrays.getIndexCount();
        for (int i = 0; i < length; i++) {
            int index = attrArrays.getIndex(i);
            switch (index) {
                case R.styleable.LineEditText_lineColorEt:
                    mLineColor = attrArrays.getColor(index, 0xFFF);
                    break;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mLineColor);
        mPaint.setTextSize(1);
        canvas.drawLine(0, getHeight() - 1, getWidth() - 1, getHeight() - 1, mPaint);
    }
}