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

import java.util.List;

import io.bcaas.R;
import io.bcaas.bean.ServerBean;
import io.bcaas.bean.ServerTypeBean;
import io.bcaas.constants.MessageConstants;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.StringTool;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/31
 * <p>
 * Debug模式下切換服務器數據填充顯示適配器
 */
public class ChangeServerAdapter extends RecyclerView.Adapter<ChangeServerAdapter.viewHolder> {

    private Context context;
    private List<ServerTypeBean> serverTypeBeans;
    private OnItemSelectListener settingItemSelectListener;

    private boolean isTV;

    public ChangeServerAdapter(Context context, List<ServerTypeBean> serverTypeBeans, boolean isTV) {
        this.context = context;
        this.serverTypeBeans = serverTypeBeans;
        this.isTV = isTV;
    }

    public void setSettingItemSelectListener(OnItemSelectListener settingItemSelectListener) {
        this.settingItemSelectListener = settingItemSelectListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(isTV ? R.layout.tv_item_change_server :
                R.layout.item_change_server, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {
        if (ListTool.isEmpty(serverTypeBeans)) {
            return;
        }
        ServerTypeBean serverTypeBean = serverTypeBeans.get(i);
        if (serverTypeBeans == null) {
            return;
        }
        boolean isChoose = serverTypeBean.isChoose();
        viewHolder.tvSFNServer.setText(serverTypeBean.getServerName());
        viewHolder.btnChoose.setVisibility(isChoose ? View.VISIBLE : View.INVISIBLE);
        viewHolder.btnChoose.setOnClickListener(v -> {
            if (!isChoose) {
                viewHolder.btnChoose.setVisibility(View.VISIBLE);
                updateData(serverTypeBean);
                settingItemSelectListener.onItemSelect(serverTypeBean, MessageConstants.Empty);
            } else {
                settingItemSelectListener.onItemSelect(serverTypeBean, MessageConstants.Empty);
            }
        });
        viewHolder.rlChangeServer.setOnClickListener(v -> {
            if (!isChoose) {
                viewHolder.btnChoose.setVisibility(View.VISIBLE);
                settingItemSelectListener.onItemSelect(serverTypeBean, MessageConstants.Empty);
                updateData(serverTypeBean);
            } else {
                settingItemSelectListener.onItemSelect(serverTypeBean, MessageConstants.Empty);
            }
        });

    }

    /**
     * 根據當前選中的item，刷新其他數據
     *
     * @param serverTypeBean
     */
    private void updateData(ServerTypeBean serverTypeBean) {
        if (ListTool.isEmpty(serverTypeBeans)) {
            return;
        }
        for (ServerTypeBean serverTypeBeanTemp : serverTypeBeans) {
            String server = serverTypeBeanTemp.getServerName();
            String serverChoose = serverTypeBean.getServerName();
            serverTypeBeanTemp.setChoose(StringTool.equals(server, serverChoose));
        }
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return serverTypeBeans.size();
    }


    class viewHolder extends RecyclerView.ViewHolder {
        TextView tvSFNServer;
        Button btnChoose;
        RelativeLayout rlChangeServer;

        public viewHolder(View view) {
            super(view);
            tvSFNServer = view.findViewById(R.id.tv_server);
            btnChoose = view.findViewById(R.id.btn_choose);
            rlChangeServer = view.findViewById(R.id.rl_change_server);
        }
    }

}
