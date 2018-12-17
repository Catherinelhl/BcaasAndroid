package io.bcaas.ui.activity.tv;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.adapter.ChangeServerAdapter;
import io.bcaas.base.BaseActivity;
import io.bcaas.bean.ServerTypeBean;
import io.bcaas.constants.Constants;
import io.bcaas.http.retrofit.RetrofitFactory;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.ServerTool;
import io.bcaas.tools.StringTool;
import io.bcaas.view.tv.FlyBroadLayout;
import io.bcaas.view.tv.MainUpLayout;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/17
 * Activity:TV版Debug模式下：切换服务器
 */
public class ChangeServerActivityTV extends BaseActivity {
    private String TAG = ChangeServerActivityTV.class.getSimpleName();
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
    @BindView(R.id.block_base_mainup)
    FlyBroadLayout blockBaseMainup;
    @BindView(R.id.block_base_content)
    MainUpLayout blockBaseContent;
    private List<ServerTypeBean> serverTypeBeans;
    private ChangeServerAdapter changeServerAdapter;

    @Override
    public boolean full() {
        return false;
    }

    @Override
    public int getContentView() {
        return R.layout.tv_activity_change_server;
    }

    @Override
    public void getArgs(Bundle bundle) {

    }

    @Override
    public void initViews() {
        ibBack.setVisibility(View.VISIBLE);
        tvTitle.setText(getResources().getString(R.string.change_server));
        serverTypeBeans = new ArrayList<>();
        initServerTypeInfo();
        initAdapter();
    }

    //初始化所有的服务器类别数据
    private void initServerTypeInfo() {
        ServerTypeBean serverTypeBeanSIT = new ServerTypeBean(Constants.ServerType.INTERNATIONAL_SIT,
                Constants.ServerTypeName.INTERNATIONAL_SIT, true);
        ServerTypeBean serverTypeBeanUAT = new ServerTypeBean(Constants.ServerType.INTERNATIONAL_UAT,
                Constants.ServerTypeName.INTERNATIONAL_UAT, false);
        ServerTypeBean serverTypeBeanPRD = new ServerTypeBean(Constants.ServerType.INTERNATIONAL_PRD,
                Constants.ServerTypeName.INTERNATIONAL_PRD, false);

        serverTypeBeans.add(serverTypeBeanSIT);
        serverTypeBeans.add(serverTypeBeanUAT);
        serverTypeBeans.add(serverTypeBeanPRD);


        String currentServerType = ServerTool.getServerType();
        if (StringTool.isEmpty(currentServerType)) {
            return;
        }
        // 1：比对当前在使用的服务器类别
        for (ServerTypeBean serverTypeBean : serverTypeBeans) {
            if (StringTool.equals(serverTypeBean.getServerType(), currentServerType)) {
                LogTool.d(TAG, serverTypeBean);
                //2：设置服务器选中
                serverTypeBean.setChoose(true);
            } else {
                serverTypeBean.setChoose(false);
            }
        }
    }

    private void initAdapter() {
        changeServerAdapter = new ChangeServerAdapter(this, serverTypeBeans, true);
        rvChangeServer.setHasFixedSize(true);
        rvChangeServer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvChangeServer.setAdapter(changeServerAdapter);
    }

    @Override
    public void initListener() {
        blockBaseContent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                blockBaseMainup.setFocusView(newFocus, oldFocus, 1.2f);
            }
        });
        changeServerAdapter.setSettingItemSelectListener(new OnItemSelectListener() {
            @Override
            public <T> void onItemSelect(T type, String from) {
                if (type == null) {
                    return;
                }

                if (serverTypeBeans.size() > 1) {
                    if (type instanceof ServerTypeBean) {
                        ServerTypeBean serverTypeBean = (ServerTypeBean) type;
                        if (serverTypeBean != null) {
                            //根据返回的数据重新设置服务器的数据
                            ServerTool.setServerType(serverTypeBean.getServerType());
                            ServerTool.initServerData();
                        }
                    }
                    //點擊切換服務器1：清空url
                    RetrofitFactory.clean();
                    //2:跳轉登錄,關閉當前頁面
                    intentToActivity(LoginActivityTV.class, true);
                }

            }

            @Override
            public void changeItem(boolean isChange) {

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
