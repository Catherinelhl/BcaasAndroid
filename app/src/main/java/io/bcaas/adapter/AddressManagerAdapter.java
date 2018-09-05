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

import java.util.ArrayList;
import java.util.List;

import io.bcaas.R;
import io.bcaas.db.vo.AddressVO;
import io.bcaas.listener.OnItemSelectListener;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 地址管理适配容器
 */
public class AddressManagerAdapter extends RecyclerView.Adapter<AddressManagerAdapter.viewHolder> {

    private Context context;
    private List<AddressVO> addressVOBeans;
    private OnItemSelectListener onItemSelect;

    public AddressManagerAdapter(Context context) {
        this.context = context;
        addressVOBeans = new ArrayList<>();
    }

    public void addList(List<AddressVO> addressVOBeans) {
        this.addressVOBeans = addressVOBeans;
        notifyDataSetChanged();
    }

    public void setItemSelectListener(OnItemSelectListener settingItemSelectListener) {
        this.onItemSelect = settingItemSelectListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {
        if (addressVOBeans == null) {
            return;
        }
        AddressVO addressVOBean = addressVOBeans.get(i);
        if (addressVOBean == null) {
            return;
        }
        viewHolder.tvSettingType.setText(addressVOBean.getAddress());
        viewHolder.tvAlias.setText(addressVOBean.getAddressName());
        viewHolder.btnDelete.setOnClickListener(v -> onItemSelect.onItemSelect(addressVOBean));

    }

    @Override
    public int getItemCount() {
        return addressVOBeans.size();
    }


    class viewHolder extends RecyclerView.ViewHolder {
        TextView tvSettingType;
        TextView tvAlias;
        Button btnDelete;
        RelativeLayout rlSettingTypes;

        public viewHolder(View view) {
            super(view);
            tvSettingType = view.findViewById(R.id.tv_setting_type);
            tvAlias = view.findViewById(R.id.tv_alias);
            btnDelete = view.findViewById(R.id.btn_delete);
            rlSettingTypes = view.findViewById(R.id.rl_setting_types);
        }
    }

}
