package io.bcaas.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import io.bcaas.R;
import io.bcaas.constants.MessageConstants;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.StringTool;
import io.bcaas.vo.PublicUnitVO;

import java.util.List;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * 用於顯示已經存在的所有幣種數據填充在PopWindow裡的適配器
 */
public class PopListCurrencyAdapter extends
        RecyclerView.Adapter<PopListCurrencyAdapter.viewHolder> {
    private String TAG = PopListCurrencyAdapter.class.getSimpleName();
    private Context context;
    private List<PublicUnitVO> publicUnitVOS;
    private OnItemSelectListener onItemSelectListener;


    public PopListCurrencyAdapter(Context context, List<PublicUnitVO> list) {
        this.context = context;
        this.publicUnitVOS = list;
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pop_currency_list, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {
        if (publicUnitVOS == null) {
            return;
        }
        String content = publicUnitVOS.get(i).getBlockService();
        if (StringTool.isEmpty(content)) {
            return;
        }
        viewHolder.tvContent.setText(content);
        viewHolder.tvContent.setOnClickListener(v -> onItemSelectListener.onItemSelect(content, MessageConstants.Empty));

    }

    @Override
    public int getItemCount() {
        return ListTool.isEmpty(publicUnitVOS) ? 0 : publicUnitVOS.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        private TextView tvContent;

        public viewHolder(View view) {
            super(view);
            tvContent = view.findViewById(R.id.tv_content);
        }
    }


}
