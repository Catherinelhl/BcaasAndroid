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
import io.bcaas.bean.ServerTypeBean;
import io.bcaas.constants.Constants;
import io.bcaas.http.retrofit.RetrofitFactory;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.ServerTool;
import io.bcaas.tools.StringTool;

/**
 * @author catherine.brainwilliam
 * @since 2018/9/17
 * 切换服务器
 */
public class ChangeServerActivity extends BaseActivity {
    private String TAG = ChangeServerActivity.class.getSimpleName();
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

    private List<ServerTypeBean> serverTypeBeansList;
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
        serverTypeBeansList = new ArrayList<>();
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
        ServerTypeBean serverTypeBeanCHINA = new ServerTypeBean(Constants.ServerType.CHINA,
                Constants.ServerTypeName.CHINA);
        serverTypeBeansList.add(serverTypeBeanSIT);
        serverTypeBeansList.add(serverTypeBeanUAT);
        serverTypeBeansList.add(serverTypeBeanPRD);
//        serverTypeBeansList.add(serverTypeBeanCHINA);

        String currentServerType = ServerTool.getServerType();
        if (StringTool.isEmpty(currentServerType)) {
            return;
        }
        // 1：比对当前在使用的服务器类别
        for (ServerTypeBean serverTypeBean : serverTypeBeansList) {
            if (StringTool.equals(serverTypeBean.getServerType(), currentServerType)) {
                LogTool.d(TAG, serverTypeBean);
                //2：设置服务器选中
                serverTypeBean.setChoose(true);
//                //3：清除网络连接缓存
//                RetrofitFactory.clean();
//                //4：根据当前切换的服务器，设置默认请求数据
//                ServerTool.setServerBeanListByServerType(currentServerType);
            } else {
                serverTypeBean.setChoose(false);

            }
        }
    }

    private void initAdapter() {
        changeServerAdapter = new ChangeServerAdapter(this, serverTypeBeansList, false);
        rvChangeServer.setHasFixedSize(true);
        rvChangeServer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvChangeServer.setAdapter(changeServerAdapter);
    }

    @Override
    public void initListener() {
        changeServerAdapter.setSettingItemSelectListener(new OnItemSelectListener() {
            @Override
            public <T> void onItemSelect(T type, String from) {
                if (type == null) {
                    return;
                }
                if (serverTypeBeansList.size() > 1) {
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
                    finish();
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
