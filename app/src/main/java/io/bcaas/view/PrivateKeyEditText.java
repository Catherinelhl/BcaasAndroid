package io.bcaas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.InputType;
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
import io.bcaas.tools.StringTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/19
 * <p>
 * 自定义bcaas 密码输入框
 */
public class PrivateKeyEditText extends LinearLayout {

    @BindView(R.id.tvEtTitle)
    TextView tvEtTitle;
    @BindView(R.id.etPrivateKey)
    EditText etPrivateKey;
    @BindView(R.id.cbPwd)
    CheckBox cbPwd;

    private String title, hint;//声明需要显示的标题以及ethint
    private boolean showTitle, showHint;//是否需要暗示标题或者hint，默认是显示，若果不需要显示，则需要重新赋值

    public PrivateKeyEditText(Context context, @Nullable AttributeSet attrs) {
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
        cbPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etPrivateKey.setInputType(isChecked ?
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                        InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);//设置当前私钥显示不可见

            }
        });
    }

    //返回私钥文本
    public String getPrivateKey() {
        if (etPrivateKey == null) {
            return "";
        }
        return etPrivateKey.getText().toString();
    }


}
