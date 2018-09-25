package io.bcaas.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.adapter.ChangeServerAdapter;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BcaasApplication;
import io.bcaas.bean.ServerBean;
import io.bcaas.constants.Constants;
import io.bcaas.constants.SystemConstants;
import io.bcaas.event.LogoutEvent;
import io.bcaas.http.retrofit.RetrofitFactory;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.StringTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/17
 * 切换服务器
 */
public class ChangeServerActivity extends BaseActivity {
    @BindView(R.id.rv_change_server)
    RecyclerView rvChangeServer;
    @BindView(R.id.ib_back)
    ImageButton ibBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ib_right)
    ImageButton ibRight;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;

    private List<ServerBean> serverBeans;
    private ChangeServerAdapter changeServerAdapter;

    @Override
    public boolean full() {
        return false;
    }

    @Override
    public int getContentView() {
        return R.layout.activity_change_server;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public void initViews() {
        ibBack.setVisibility(View.VISIBLE);
        tvTitle.setText(getResources().getString(R.string.change_server));
        serverBeans = new ArrayList<>();
        getAllSeedFullNodes();
        initAdapter();

    }

    private void getAllSeedFullNodes() {
        //得到所有的全節點信息
        serverBeans = SystemConstants.seedFullNodeServerBeanList;
        String currentUrl = BcaasApplication.getSFNServer();
        if (StringTool.isEmpty(currentUrl)) {
            currentUrl = SystemConstants.SEEDFULLNODE_URL_DEFAULT_1;
        }
        for (ServerBean serverBean : serverBeans) {
            if (StringTool.equals(serverBean.getServer(), currentUrl)) {
                serverBean.setChoose(true);
            }
        }
    }

    private void initAdapter() {
        changeServerAdapter = new ChangeServerAdapter(this, serverBeans);
        rvChangeServer.setHasFixedSize(true);
        rvChangeServer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvChangeServer.setAdapter(changeServerAdapter);
    }

    @Override
    public void initListener() {
        changeServerAdapter.setSettingItemSelectListener(new OnItemSelectListener() {
            @Override
            public <T> void onItemSelect(T type) {
                if (type == null) {
                    return;
                }
                if (serverBeans.size() > 1) {
                    if (type instanceof ServerBean) {
                        ServerBean serverBean = (ServerBean) type;
                        if (serverBean != null) {
                            BcaasApplication.setSFNServer(serverBean.getServer());
                        }
                    }
                    //點擊切換服務器1：清空url
                    RetrofitFactory.clean();
                    //2:跳轉登錄
                    OttoTool.getInstance().post(new LogoutEvent());
                    //3:關閉當前頁面
                    finish();
                }

            }
        });

        ibBack.setOnClickListener(v -> finish());

    }

    @Override
    public void showLoading() {
        if (!checkActivityState()) {
            return;
        }
        showLoadingDialog();
    }

    @Override
    public void hideLoading() {
        if (!checkActivityState()) {
            return;
        }
        hideLoadingDialog();
    }

}
