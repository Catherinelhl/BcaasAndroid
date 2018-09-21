package io.bcaas.view.edittext;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

import io.bcaas.tools.regex.RegexTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/21
 *
 * 不能输入中文的输入框
 */
public class BcaasEditText extends EditText {
    public BcaasEditText(Context context) {
        super(context);
    }

    public BcaasEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new BcaasInputConnection(super.onCreateInputConnection(outAttrs), false);
    }

    class BcaasInputConnection extends InputConnectionWrapper implements InputConnection {
        public BcaasInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        /**
         * 对输入的内容进行拦截
         */
        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            // 不能输入汉字
            if (RegexTool.isChinese(text.toString())) {
                return false;
            }
            return super.commitText(text, newCursorPosition);
        }

    }

}
