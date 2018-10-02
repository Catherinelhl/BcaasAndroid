package io.bcaas.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import io.bcaas.tools.LogTool;
import io.bcaas.tools.OttoTool;
import io.bcaas.tools.ServerTool;
import io.bcaas.tools.StringTool;
import io.reactivex.disposables.Disposable;

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
    @BindView(R.id.btn_open_international_server)
    Button btnOpenInternationalServer;
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
        setOpenInternationalServerStatus(ServerTool.openInternationalServer);
    }

    private void setOpenInternationalServerStatus(boolean isOpen) {
        btnOpenInternationalServer.setText(isOpen ? "关闭国际版" : "打开国际版");
        btnOpenInternationalServer.setBackground(getResources().getDrawable(isOpen ? R.drawable.bcaas_bg_button : R.drawable.bg_grey));
    }

    private void getAllSeedFullNodes() {
        serverBeans.clear();
        //得到所有的全節點信息
        serverBeans.addAll(ServerTool.seedFullNodeServerBeanList);
        ServerBean serverBeanDefault = ServerTool.getDefaultServer();
        if (serverBeanDefault == null) {
            return;
        }
        String currentSFNUrl = serverBeanDefault.getSfnServer();
        if (StringTool.isEmpty(currentSFNUrl)) {
            currentSFNUrl = SystemConstants.SFN_URL_DEFAULT;
        }
        for (ServerBean serverBean : serverBeans) {
            if (StringTool.equals(serverBean.getSfnServer(), currentSFNUrl)) {
                LogTool.d(TAG, serverBean);
                LogTool.d(TAG, currentSFNUrl);
                serverBean.setChoose(true);
                RetrofitFactory.clean();
                ServerTool.setDefaultServer(serverBean);
            } else {
                serverBean.setChoose(false);
            }
        }
    }

    private void initAdapter() {
        changeServerAdapter = new ChangeServerAdapter(this, serverBeans, false);
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
                            ServerTool.setDefaultServer(serverBean);
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

            @Override
            public void changeItem(boolean isChange) {

            }
        });

        ibBack.setOnClickListener(v -> finish());
        Disposable subscribeOpenInternationalServer = RxView.clicks(btnOpenInternationalServer)
                .throttleFirst(Constants.ValueMaps.sleepTime1000, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    String status = btnOpenInternationalServer.getText().toString();
                    setOpenInternationalServerStatus(ServerTool.openInternationalServer);
                    boolean isOpen = StringTool.equals(status, "打开国际版");
                    ServerTool.openInternationalServer = isOpen;
                    setOpenInternationalServerStatus(isOpen);
                    ServerTool.cleanServerInfo();
                    ServerTool.initServerData();
                    getAllSeedFullNodes();
                    changeServerAdapter.notifyDataSetChanged();
                });
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
