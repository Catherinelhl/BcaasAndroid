package io.bcaas.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import io.bcaas.R;
import io.bcaas.db.vo.Address;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.StringTool;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 展示地址列表的容器
 */
public class PopListAddressAdapter extends
        RecyclerView.Adapter<PopListAddressAdapter.viewHolder> {
    private String TAG = PopListAddressAdapter.class.getSimpleName();
    private Context context;
    private List<Address> popList;
    private OnItemSelectListener onItemSelectListener;


    public PopListAddressAdapter(Context context, List<Address> list) {
        this.context = context;
        this.popList = list;
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pop_address_list, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {
        if (popList == null) {
            return;
        }
        Address addressBean = popList.get(i);
        if (addressBean != null) {
            String addressName = addressBean.getAddressName();
            String address = addressBean.getAddress();
            if (StringTool.isEmpty(addressName)) {
                return;
            }
            viewHolder.tvAddress.setText(address);
            viewHolder.tvAddressName.setText(addressName);
            viewHolder.tvAddressName.setOnClickListener(v -> onItemSelectListener.onItemSelect(address));
            viewHolder.llAddress.setOnClickListener(view -> onItemSelectListener.onItemSelect(address));
        }


    }

    @Override
    public int getItemCount() {
        return ListTool.isEmpty(popList) ? 0 : popList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        private TextView tvAddressName;
        private TextView tvAddress;
        private LinearLayout llAddress;

        public viewHolder(View view) {
            super(view);
            tvAddressName = view.findViewById(R.id.tv_address_name);
            tvAddress = view.findViewById(R.id.tv_address);
            llAddress = view.findViewById(R.id.ll_address);
        }
    }


}
