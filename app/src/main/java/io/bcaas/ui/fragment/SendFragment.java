package io.bcaas.ui.fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.adapter.AllCurrencyListAdapter;
import io.bcaas.base.BCAASApplication;
import io.bcaas.base.BaseFragment;
import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.event.RefreshBlockServiceEvent;
import io.bcaas.event.RequestBlockServiceEvent;
import io.bcaas.listener.OnCurrencyItemSelectListener;
import io.bcaas.tools.ListTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.StringTool;
import io.bcaas.ui.activity.MainActivity;
import io.bcaas.vo.PublicUnitVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * Fragment：「交易发送」一级页面，选择当前任意币种进行交易
 */
public class SendFragment extends BaseFragment {
    @BindView(R.id.tv_address_key)
    TextView tvAddressKey;
    @BindView(R.id.ib_copy)
    ImageButton ibCopy;
    @BindView(R.id.tv_account_address_value)
    TextView tvAccountAddressValue;
    @BindView(R.id.rl_amount_info)
    RelativeLayout rlAmountInfo;
    @BindView(R.id.tv_transaction_block_title)
    TextView tvTransactionBlockTitle;
    @BindView(R.id.v_line)
    View vLine;
    @BindView(R.id.rv_all_currency)
    RecyclerView rvAllCurrency;
    @BindView(R.id.srl_all_currency)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.ll_transaction_info)
    LinearLayout llTransactionInfo;
    @BindView(R.id.ll_send)
    LinearLayout llSend;
    private String TAG = SendFragment.class.getSimpleName();


    private AllCurrencyListAdapter allCurrencyListAdapter;

    public static SendFragment newInstance() {
        SendFragment sendFragment = new SendFragment();
        Bundle bundle = new Bundle();
        sendFragment.setArguments(bundle);
        return sendFragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_send;
    }

    @Override
    public void getArgs(Bundle bundle) {
    }

    @Override
    public void initViews(View view) {
        tvAccountAddressValue.setText(BCAASApplication.getWalletAddress());
        swipeRefreshLayout.setColorSchemeResources(
                R.color.button_right_color,
                R.color.button_right_color

        );
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        initAllCurrencyListAdapter();
    }

    private void initAllCurrencyListAdapter() {
        allCurrencyListAdapter = new AllCurrencyListAdapter(this.context, BCAASApplication.getPublicUnitVOList());
        allCurrencyListAdapter.setOnItemSelectListener(onCurrencyItemSelectListener);
        rvAllCurrency.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false);
        rvAllCurrency.setLayoutManager(linearLayoutManager);
        rvAllCurrency.setAdapter(allCurrencyListAdapter);
    }

    private OnCurrencyItemSelectListener onCurrencyItemSelectListener = new OnCurrencyItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type, String from) {
            String blockService = type.toString();
            //判断是否为空
            if (StringTool.notEmpty(blockService)) {
                //判断是否和默认的币种一致
                if (!StringTool.equals(blockService, BCAASApplication.getBlockService())) {
                    //如果不一致，那么保存最新的，且发起验证
                    BCAASApplication.setBlockService(blockService);
                    if (activity != null) {
                        ((MainActivity) activity).verify();
                    }
                    //重置余额
                    BCAASApplication.resetWalletBalance();
                }
                // 通知首页跳转到发送填写信息页面
                ((MainActivity) activity).intentToSendFillInActivity(MessageConstants.Empty);
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initListener() {
        rlAmountInfo.setOnTouchListener((v, event) -> true);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            //判断如果当前没有币种，那么就暂时不能刷新数据
            if (StringTool.isEmpty(BCAASApplication.getBlockService())) {
                return;
            }
            ((MainActivity) activity).requestBlockService(new RequestBlockServiceEvent(Constants.From.SEND_FRAGMENT));
        });
        ibCopy.setOnClickListener(v -> {
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText(Constants.KeyMaps.COPY_ADDRESS, BCAASApplication.getWalletAddress());
            // 将ClipData内容放到系统剪贴板里。
            if (cm != null) {
                cm.setPrimaryClip(mClipData);
                showToast(getString(R.string.successfully_copied));
            }
        });
    }

    @Subscribe
    public void refreshBlockService(RefreshBlockServiceEvent refreshBlockServiceEvent) {
        if (refreshBlockServiceEvent != null) {
            List<PublicUnitVO> publicUnitVOS = BCAASApplication.getPublicUnitVOList();
            LogTool.d(TAG, "refreshBlockService:" + publicUnitVOS);

            if (ListTool.noEmpty(publicUnitVOS)) {
                if (allCurrencyListAdapter != null) {
                    allCurrencyListAdapter.addList(publicUnitVOS);
                }
            }
        }
    }
}
