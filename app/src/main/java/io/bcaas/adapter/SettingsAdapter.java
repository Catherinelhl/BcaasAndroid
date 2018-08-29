package io.bcaas.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.List;

import io.bcaas.R;
import io.bcaas.bean.SettingsBean;
import io.bcaas.constants.Constants;
import io.bcaas.listener.OnItemSelectListener;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 显示设置里面所有的选项
 */
public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.viewHolder> {

    private Context context;
    private List<SettingsBean> settingTypes;

    private OnItemSelectListener settingItemSelectListener;

    public SettingsAdapter(Context context, List<SettingsBean> settingTypes) {
        this.context = context;
        this.settingTypes = settingTypes;
    }

    public void setSettingItemSelectListener(OnItemSelectListener settingItemSelectListener) {
        this.settingItemSelectListener = settingItemSelectListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_setting, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {
        if (settingTypes == null) return;
        final SettingsBean types = settingTypes.get(i);
        if (types == null) return;
        String type = types.getType();
        Constants.SettingType tag = types.getTag();
        Drawable drawableLeft = context.getResources().getDrawable(
                R.mipmap.icon_check_wallet_info);
        switch (tag) {
            case CHECK_WALLET_INFO:
                drawableLeft = context.getResources().getDrawable(
                        R.mipmap.icon_check_wallet_info);
                break;
            case MODIFY_PASSWORD:
                drawableLeft = context.getResources().getDrawable(
                        R.mipmap.icon_modify_password);
                break;
            case MODIFY_AUTH:
                drawableLeft = context.getResources().getDrawable(
                        R.mipmap.icon_modify_representative);
                break;
            case ADDRESS_MANAGE:
                drawableLeft = context.getResources().getDrawable(
                        R.mipmap.icon_address_management);
                break;
            case LANGUAGE_SWITCHING:
                drawableLeft = context.getResources().getDrawable(
                        R.mipmap.icon_switch);
                break;
        }
        viewHolder.tvSettingType.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                null, null, null);
        viewHolder.tvSettingType.setCompoundDrawablePadding(context.getResources().getDimensionPixelOffset(R.dimen.d16));

        viewHolder.tvSettingType.setText(type);
        viewHolder.ibDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingItemSelectListener.onItemSelect(types);
            }
        });
        viewHolder.rlSettingTypes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingItemSelectListener.onItemSelect(types);


            }
        });

    }

    @Override
    public int getItemCount() {
        return settingTypes.size();
    }


    class viewHolder extends RecyclerView.ViewHolder {
        TextView tvSettingType;
        ImageButton ibDetail;
        RelativeLayout rlSettingTypes;

        public viewHolder(View view) {
            super(view);
            tvSettingType = view.findViewById(R.id.tv_setting_type);
            ibDetail = view.findViewById(R.id.ibDetail);
            rlSettingTypes = view.findViewById(R.id.rl_setting_types);
        }
    }

}
