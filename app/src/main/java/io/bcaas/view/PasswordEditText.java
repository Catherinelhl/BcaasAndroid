package io.bcaas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import io.bcaas.R;
import io.bcaas.listener.PasswordWatcherListener;
import io.bcaas.tools.StringTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/19
 * <p>
 * 自定义bcaas 密码输入框
 */
public class PasswordEditText extends LinearLayout {

    @BindView(R.id.tvEtTitle)
    TextView tvEtTitle;
    @BindView(R.id.etPrivateKey)
    EditText etPrivateKey;
    @BindView(R.id.cbPwd)
    CheckBox cbPwd;
    /*声明需要显示的标题以及hint*/
    private String title, hint;
    /*是否需要暗示标题或者hint，默认是显示，若果不需要显示，则需要重新赋值*/
    private boolean showTitle, showHint;
    /*監聽當前密碼的輸入*/
    private PasswordWatcherListener passwordWatcherListener;

    public PasswordEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_private_key_edittext, this, true);
        ButterKnife.bind(view);
        //获取自定义属性的值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.privateKeyStyle);
        if (typedArray != null) {
            title = typedArray.getString(R.styleable.privateKeyStyle_title);
            hint = typedArray.getString(R.styleable.privateKeyStyle_hint);
            showHint = typedArray.getBoolean(R.styleable.privateKeyStyle_showHint, true);
            showTitle = typedArray.getBoolean(R.styleable.privateKeyStyle_showTitle, true);
            typedArray.recycle();
            if (StringTool.notEmpty(title)) {
                tvEtTitle.setText(title);
            }
            if (StringTool.notEmpty(hint)) {
                etPrivateKey.setHint(hint);
            }
            if (showTitle) {
                tvEtTitle.setVisibility(showTitle ? VISIBLE : INVISIBLE);
            }
        }

        initView();
    }


    private void initView() {
        cbPwd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            etPrivateKey.setInputType(isChecked ?
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);//设置当前私钥显示不可见

        });
        etPrivateKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString();
                if (StringTool.notEmpty(password)) {
                    if (password.length() == 8) {
                        if (passwordWatcherListener == null) return;
                        passwordWatcherListener.onComplete(password);
                    }
                }

            }
        });
    }

    public void setOnPasswordWatchListener(PasswordWatcherListener passwordWatchListener) {
        this.passwordWatcherListener = passwordWatchListener;
    }

    //返回私钥文本
    public String getPrivateKey() {
        if (etPrivateKey == null) {
            return null;
        }
        return etPrivateKey.getText().toString();
    }


}