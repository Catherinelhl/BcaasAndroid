package io.bcaas.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import io.bcaas.R;
import io.bcaas.base.BCAASApplication;
import io.bcaas.bean.TypeSwitchingBean;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.StringTool;

import java.util.List;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/31
 * <p>
 * 「切換語言」、「TV版切換幣種」數據填充適配器
 */
public class TypeSwitchingAdapter extends RecyclerView.Adapter<TypeSwitchingAdapter.viewHolder> {

    private Context context;
    private List<TypeSwitchingBean> typeSwitchingBeans;
    private OnItemSelectListener settingItemSelectListener;

    public TypeSwitchingAdapter(Context context, List<TypeSwitchingBean> typeSwitchingBeans) {
        this.context = context;
        this.typeSwitchingBeans = typeSwitchingBeans;
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
        if (ListTool.isEmpty(typeSwitchingBeans)) {
            return;
        }
        TypeSwitchingBean typeSwitchingBean = typeSwitchingBeans.get(i);
        if (typeSwitchingBean == null) {
            return;
        }
        //如果當前是手機，那麼顯示紅色的勾選
        viewHolder.btnChoose.setBackground(context.getResources().getDrawable(BCAASApplication.isIsPhone() ? R.mipmap.icon_choose : R.mipmap.icon_choose_yellow));
        if (i == typeSwitchingBeans.size() - 1) {
            viewHolder.vLine.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.vLine.setVisibility(View.VISIBLE);
        }
        boolean isChoose = typeSwitchingBean.isChoose();
        String language = typeSwitchingBean.getLanguage();
        viewHolder.tvLanguage.setText(language);
        viewHolder.btnChoose.setVisibility(isChoose ? View.VISIBLE : View.INVISIBLE);
        viewHolder.btnChoose.setOnClickListener(v -> {
            if (!isChoose) {
                viewHolder.btnChoose.setVisibility(View.VISIBLE);
                updateData(typeSwitchingBean);
                settingItemSelectListener.onItemSelect(typeSwitchingBean, "");
            } else {
                settingItemSelectListener.changeItem(false);
            }
        });
        viewHolder.tvLanguage.setOnClickListener(v -> {
            if (!isChoose) {
                viewHolder.btnChoose.setVisibility(View.VISIBLE);
                updateData(typeSwitchingBean);
                settingItemSelectListener.onItemSelect(typeSwitchingBean, "");
            } else {
                settingItemSelectListener.changeItem(false);

            }
        });
        viewHolder.rlLanguageSwitch.setOnClickListener(v -> {
            if (!isChoose) {
                viewHolder.btnChoose.setVisibility(View.VISIBLE);
                settingItemSelectListener.onItemSelect(typeSwitchingBean, "");
                updateData(typeSwitchingBean);
            } else {
                settingItemSelectListener.changeItem(false);

            }
        });

    }

    /**
     * 根據當前選中的item，刷新其他數據
     *
     * @param typeSwitchingBean
     */
    private void updateData(TypeSwitchingBean typeSwitchingBean) {
        if (ListTool.isEmpty(typeSwitchingBeans)) {
            return;
        }
        for (TypeSwitchingBean typeSwitchingBeanNew : typeSwitchingBeans) {
            String type = typeSwitchingBeanNew.getType();
            String typeChoose = typeSwitchingBean.getType();
            if (StringTool.equals(type, typeChoose)) {
                typeSwitchingBeanNew.setChoose(true);
            } else {
                typeSwitchingBeanNew.setChoose(false);
            }
        }
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return typeSwitchingBeans.size();
    }


    class viewHolder extends RecyclerView.ViewHolder {
        TextView tvLanguage;
        Button btnChoose;
        View vLine;
        RelativeLayout rlLanguageSwitch;

        public viewHolder(View view) {
            super(view);
            tvLanguage = view.findViewById(R.id.tv_language);
            vLine = view.findViewById(R.id.v_line);
            btnChoose = view.findViewById(R.id.btn_choose);
            rlLanguageSwitch = view.findViewById(R.id.rl_language_switch);
        }
    }

}
