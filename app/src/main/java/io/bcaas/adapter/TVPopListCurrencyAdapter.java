package io.bcaas.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import io.bcaas.R;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.StringTool;
import io.bcaas.vo.PublicUnitVO;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * <p>
 * TV展示币种列表的容器
 */
public class TVPopListCurrencyAdapter extends
        RecyclerView.Adapter<TVPopListCurrencyAdapter.viewHolder> {
    private String TAG = TVPopListCurrencyAdapter.class.getSimpleName();
    private Context context;
    private List<PublicUnitVO> publicUnitVOS;
    private OnItemSelectListener onItemSelectListener;


    public TVPopListCurrencyAdapter(Context context, List<PublicUnitVO> list) {
        this.context = context;
        this.publicUnitVOS = list;
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.tv_item_pop_currency_list, viewGroup, false);
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
        viewHolder.tvContent.setOnClickListener(v -> onItemSelectListener.onItemSelect(content));
        viewHolder.relativeLayout.setOnClickListener(v -> onItemSelectListener.onItemSelect(content));

    }

    @Override
    public int getItemCount() {
        return ListTool.isEmpty(publicUnitVOS) ? 0 : publicUnitVOS.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        private TextView tvContent;
        private RelativeLayout relativeLayout;

        public viewHolder(View view) {
            super(view);
            tvContent = view.findViewById(R.id.tv_content);
            relativeLayout = view.findViewById(R.id.rl_tv_show_currency);
        }
    }


}
