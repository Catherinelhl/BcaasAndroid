package io.bcaas.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import io.bcaas.R;
import io.bcaas.bean.LanguageSwitchingBean;
import io.bcaas.bean.SettingsBean;
import io.bcaas.constants.Constants;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.StringTool;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/31
 * <p>
 * 切換語言
 */
public class LanguageSwitchingAdapter extends RecyclerView.Adapter<LanguageSwitchingAdapter.viewHolder> {

    private Context context;
    private List<LanguageSwitchingBean> languageSwitchingBeans;
    private OnItemSelectListener settingItemSelectListener;

    public LanguageSwitchingAdapter(Context context, List<LanguageSwitchingBean> languageSwitchingBeans) {
        this.context = context;
        this.languageSwitchingBeans = languageSwitchingBeans;
    }

    public void setSettingItemSelectListener(OnItemSelectListener settingItemSelectListener) {
        this.settingItemSelectListener = settingItemSelectListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_language_switch, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {
        if (ListTool.isEmpty(languageSwitchingBeans)) {
            return;
        }
        LanguageSwitchingBean languageSwitchingBean = languageSwitchingBeans.get(i);
        if (languageSwitchingBean == null) {
            return;
        }
        boolean isChoose = languageSwitchingBean.isChoose();
        String language = languageSwitchingBean.getLanguage();
        viewHolder.tvLanguage.setText(language);
        viewHolder.btnChoose.setVisibility(isChoose ? View.VISIBLE : View.INVISIBLE);
        viewHolder.btnChoose.setOnClickListener(v -> {
            if (!isChoose) {
                viewHolder.btnChoose.setVisibility(View.VISIBLE);
                updateData(languageSwitchingBean);
                settingItemSelectListener.onItemSelect(languageSwitchingBean);
            }
        });
        viewHolder.rlLanguageSwitch.setOnClickListener(v -> {
            if (!isChoose) {
                viewHolder.btnChoose.setVisibility(View.VISIBLE);
                settingItemSelectListener.onItemSelect(languageSwitchingBean);
                updateData(languageSwitchingBean);
            }
        });

    }

    /**
     * 根據當前選中的item，刷新其他數據
     *
     * @param languageSwitchingBean
     */
    private void updateData(LanguageSwitchingBean languageSwitchingBean) {
        if (ListTool.isEmpty(languageSwitchingBeans)) {
            return;
        }
        for (LanguageSwitchingBean languageSwitchingBeanNew : languageSwitchingBeans) {
            String type = languageSwitchingBeanNew.getType();
            String typeChoose = languageSwitchingBean.getType();
            if (StringTool.equals(type, typeChoose)) {
                languageSwitchingBeanNew.setChoose(true);
            } else {
                languageSwitchingBeanNew.setChoose(false);
            }
        }
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return languageSwitchingBeans.size();
    }


    class viewHolder extends RecyclerView.ViewHolder {
        TextView tvLanguage;
        Button btnChoose;
        RelativeLayout rlLanguageSwitch;

        public viewHolder(View view) {
            super(view);
            tvLanguage = view.findViewById(R.id.tv_language);
            btnChoose = view.findViewById(R.id.btn_choose);
            rlLanguageSwitch = view.findViewById(R.id.rl_language_switch);
        }
    }

}
