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
import io.bcaas.bean.LanguageSwitchingBean;
import io.bcaas.bean.ServerBean;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.StringTool;


/**
 * @author catherine.brainwilliam
 * @since 2018/8/31
 * <p>
 * 切換服務器
 */
public class ChangeServerAdapter extends RecyclerView.Adapter<ChangeServerAdapter.viewHolder> {

    private Context context;
    private List<ServerBean> serverBeans;
    private OnItemSelectListener settingItemSelectListener;

    public ChangeServerAdapter(Context context, List<ServerBean> serverBeans) {
        this.context = context;
        this.serverBeans = serverBeans;
    }

    public void setSettingItemSelectListener(OnItemSelectListener settingItemSelectListener) {
        this.settingItemSelectListener = settingItemSelectListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_change_server, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {
        if (ListTool.isEmpty(serverBeans)) {
            return;
        }
        ServerBean serverBean = serverBeans.get(i);
        if (serverBeans == null) {
            return;
        }
        boolean isChoose = serverBean.isChoose();
        String server = serverBean.getServer();
        viewHolder.tvServer.setText(server);
        viewHolder.btnChoose.setVisibility(isChoose ? View.VISIBLE : View.INVISIBLE);
        viewHolder.btnChoose.setOnClickListener(v -> {
            if (!isChoose) {
                viewHolder.btnChoose.setVisibility(View.VISIBLE);
                updateData(serverBean);
                settingItemSelectListener.onItemSelect(serverBean);
            }
        });
        viewHolder.rlChangeServer.setOnClickListener(v -> {
            if (!isChoose) {
                viewHolder.btnChoose.setVisibility(View.VISIBLE);
                settingItemSelectListener.onItemSelect(serverBean);
                updateData(serverBean);
            }
        });

    }

    /**
     * 根據當前選中的item，刷新其他數據
     *
     * @param serverBean
     */
    private void updateData(ServerBean serverBean) {
        if (ListTool.isEmpty(serverBeans)) {
            return;
        }
        for (ServerBean serverBeanTemp : serverBeans) {
            String server = serverBeanTemp.getServer();
            String serverChoose = serverBean.getServer();
            serverBeanTemp.setChoose(StringTool.equals(server, serverChoose));
        }
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return serverBeans.size();
    }


    class viewHolder extends RecyclerView.ViewHolder {
        TextView tvServer;
        Button btnChoose;
        RelativeLayout rlChangeServer;

        public viewHolder(View view) {
            super(view);
            tvServer = view.findViewById(R.id.tv_server);
            btnChoose = view.findViewById(R.id.btn_choose);
            rlChangeServer = view.findViewById(R.id.rl_change_server);
        }
    }

}
