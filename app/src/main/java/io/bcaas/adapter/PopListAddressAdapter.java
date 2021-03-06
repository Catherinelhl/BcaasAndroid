package io.bcaas.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.bcaas.R;
import io.bcaas.base.BCAASApplication;
import io.bcaas.constants.MessageConstants;
import io.bcaas.db.vo.AddressVO;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.StringTool;
import io.bcaas.tools.TextTool;

import java.util.List;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 用於顯示已經存在的所有地址數據填充在PopWindow裡的適配器
 */
public class PopListAddressAdapter extends
        RecyclerView.Adapter<PopListAddressAdapter.viewHolder> {
    private String TAG = PopListAddressAdapter.class.getSimpleName();
    private Context context;
    private List<AddressVO> addressVOList;
    private OnItemSelectListener onItemSelectListener;


    public PopListAddressAdapter(Context context, List<AddressVO> list) {
        this.context = context;
        this.addressVOList = list;
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
        if (addressVOList == null) {
            return;
        }
        AddressVO addressVOBean = addressVOList.get(i);
        if (addressVOBean != null) {
            String addressName = addressVOBean.getAddressName();
            String address = addressVOBean.getAddress();
            if (StringTool.isEmpty(addressName)) {
                return;
            }
            //1:获取屏幕的宽度
            int screenWidth = BCAASApplication.getScreenWidth();
            int nameWidth = (screenWidth - context.getResources().getDimensionPixelOffset(R.dimen.d5)) / 3;
            double width = screenWidth - nameWidth - context.getResources().getDimensionPixelOffset(R.dimen.d10);
            viewHolder.tvAddress.setText(TextTool.intelligentOmissionText(viewHolder.tvAddress, (int) width, address, true));
            viewHolder.tvAddressName.setText(TextTool.intelligentOmissionText(viewHolder.tvAddressName, nameWidth, addressName))
            ;
            viewHolder.tvAddressName.setOnClickListener(v -> onItemSelectListener.onItemSelect(addressVOBean, MessageConstants.Empty));
            viewHolder.llAddress.setOnClickListener(view -> onItemSelectListener.onItemSelect(addressVOBean, MessageConstants.Empty));
        }


    }

    @Override
    public int getItemCount() {
        return ListTool.isEmpty(addressVOList) ? 0 : addressVOList.size();
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
