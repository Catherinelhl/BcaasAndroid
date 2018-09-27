package io.bcaas.view.edittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.bcaas.R;
import io.bcaas.constants.Constants;
import io.bcaas.listener.PasswordWatcherListener;
import io.bcaas.tools.StringTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/19
 * <p>
 * TV版自定义bcaas 密码输入框
 */
public class TVPasswordEditText extends LinearLayout {
    private String TAG = TVPasswordEditText.class.getSimpleName();

    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.v_password_line)
    View vPasswordLine;

    @BindView(R.id.tv_et_title)
    TextView tvEtTitle;
    @BindView(R.id.cb_pwd)
    CheckBox cbPwd;
    /*声明需要显示的标题以及hint*/
    private String title, hint;
    /*是否需要暗示标题或者hint，默认是显示，若果不需要显示，则需要重新赋值*/
    private boolean showTitle, showHint;
    /*監聽當前密碼的輸入*/
    private PasswordWatcherListener passwordWatcherListener;

    public TVPasswordEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.tv_layout_password_edittext, this, true);
        ButterKnife.bind(view);
        //获取自定义属性的值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.privateKeyStyle);
        if (typedArray != null) {
            title = typedArray.getString(R.styleable.privateKeyStyle_title);
            hint = typedArray.getString(R.styleable.privateKeyStyle_hint);
            showHint = typedArray.getBoolean(R.styleable.privateKeyStyle_showHint, true);
            showTitle = typedArray.getBoolean(R.styleable.privateKeyStyle_showTitle, true);
            boolean showLine = typedArray.getBoolean(R.styleable.privateKeyStyle_showLine, true);
            int textColor = typedArray.getInteger(R.styleable.privateKeyStyle_textColor, context.getResources().getColor(R.color.black_1d2124));
            int hintColor = typedArray.getInteger(R.styleable.privateKeyStyle_hintColor, context.getResources().getColor(R.color.black30_1d2124));

            typedArray.recycle();
            if (StringTool.notEmpty(title)) {
                tvEtTitle.setText(title);
            }
            if (StringTool.notEmpty(hint)) {
                etPassword.setHint(hint);
            }
            vPasswordLine.setVisibility(showLine ? VISIBLE : INVISIBLE);
            tvEtTitle.setVisibility(showTitle ? VISIBLE : GONE);
            etPassword.setTextColor(textColor);
            tvEtTitle.setTextColor(textColor);
            etPassword.setHintTextColor(hintColor);
        }
        //初始化所有輸入框的初始狀態，设置弹出的键盘类型为空
        etPassword.setInputType(EditorInfo.TYPE_NULL);
        initView();
    }


    private void initView() {
        etPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                etPassword.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) etPassword.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(etPassword, 0);
            }
        });
        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String content = etPassword.getText().toString();
                    if (StringTool.isEmpty(content)) {
                        etPassword.setInputType(InputType.TYPE_NULL);
                    }
                }
            }
        });
        cbPwd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String text = etPassword.getText().toString();
            if (StringTool.isEmpty(text)) {
                return;
            }
            etPassword.setInputType(isChecked ?
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);//设置当前私钥显示不可见

        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    String password = s.toString();
                    if (StringTool.notEmpty(password)) {
                        if (password.length() >= Constants.PASSWORD_MIN_LENGTH) {
                            if (passwordWatcherListener != null) {
                                passwordWatcherListener.onComplete(password);
                            }
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setOnPasswordWatchListener(PasswordWatcherListener passwordWatchListener) {
        this.passwordWatcherListener = passwordWatchListener;
    }

    //返回私钥文本
    public String getPassword() {
        if (etPassword == null) {
            return null;
        }
        return etPassword.getText().toString();
    }

    //私钥文本
    public void setPassword(String privateKey) {
        etPassword.setText(privateKey);
    }

}
