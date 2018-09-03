package io.bcaas.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_pop_list, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {
        if (popList == null) {
            return;
        }
        Address address = popList.get(i);
        if (address != null) {
            String addressName = address.getAddressName();
            if (StringTool.isEmpty(addressName)) {
                return;
            }
            viewHolder.tvContent.setText(addressName);
            viewHolder.tvContent.setOnClickListener(v -> onItemSelectListener.onItemSelect(address));
        }


    }

    @Override
    public int getItemCount() {
        return ListTool.isEmpty(popList) ? 0 : popList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        private TextView tvContent;

        public viewHolder(View view) {
            super(view);
            tvContent = view.findViewById(R.id.tv_content);
        }
    }


}
