package io.bcaas.ui.frg;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import butterknife.BindView;
import io.bcaas.R;
import io.bcaas.base.BaseActivity;
import io.bcaas.base.BaseFragment;
import io.bcaas.constants.Constants;
import io.bcaas.event.UpdateAddressEvent;
import io.bcaas.ui.aty.SendToConfirmPwdActivity;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/15
 * 发送页面
 */
public class SendFragment extends BaseFragment {
    @BindView(R.id.tvMyAccountAddressValue)
    TextView tvMyAccountAddressValue;//我的账户地址显示容器
    @BindView(R.id.tvBalance)
    TextView tvBalance;
    @BindView(R.id.sp_select)
    Spinner spSelect;////选择当前查询显示的币种
    @BindView(R.id.v_line)
    View vLine;
    @BindView(R.id.spSelectAccountAddress)
    Spinner spSelectAccountAddress;//选择收款账户地址
    @BindView(R.id.spSelectReceiveCurrency)
    Spinner spSelectReceiveCurrency;//选择交易发送的币种
    @BindView(R.id.etTransactionAmount)
    EditText etTransactionAmount;//我的交易数额
    @BindView(R.id.btnSend)
    Button btnSend;


    private String myAccountAddress;//得到当前的账户地址
    private ArrayAdapter currencyAdapter;//声明用于填充币种的适配
    private ArrayAdapter allAccountAddressAdapter;//声明用于填充所有可选账户的地址

    private String receiveAddress;//收款的账户地址
    private String receiveCurrency;//收款的币种

    public static SendFragment newInstance() {
        SendFragment sendFragment=new SendFragment();
        return sendFragment;
    }
    @Override
    public int getLayoutRes() {
        return R.layout.frg_send;
    }

    @Override
    public void initViews(View view) {
        myAccountAddress = "3672783jshadgvhbnjbvjf===";
        tvMyAccountAddressValue.setText(myAccountAddress);
        initData();

    }

    private void initData() {
        initSelectDisplaySpinnerAdapter();
        initReceiveAccountAddressSpinnerAdapter();

    }

    /*初始化选择显示当前想要发送的账户的数据；这里有三种方式：1：可以手动输入；2：通过扫描对方的code；3：通过选择自己本地的交易过的账户列表*/
    private void initReceiveAccountAddressSpinnerAdapter() {
        //将可选内容与ArrayAdapter连接起来
        allAccountAddressAdapter = new ArrayAdapter<>(this.context, R.layout.spinner_item, getCurrency());
        //设置下拉列表的风格
        allAccountAddressAdapter.setDropDownViewResource(R.layout.dropdown_style);
        //将adapter 添加到spinner中
        spSelectAccountAddress.setAdapter(allAccountAddressAdapter);
    }

    /*初始化选择显示当前币种的数据*/
    private void initSelectDisplaySpinnerAdapter() {
        //将可选内容与ArrayAdapter连接起来
        currencyAdapter = new ArrayAdapter<>(this.context, R.layout.spinner_item, getCurrency());
        //设置下拉列表的风格
        currencyAdapter.setDropDownViewResource(R.layout.dropdown_style);
        //将adapter 添加到spinner中
        spSelect.setAdapter(currencyAdapter);
        spSelectReceiveCurrency.setAdapter(currencyAdapter);
    }


    @Override
    public void initListener() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO  如果当前页面用户进行了点击切换其他的页面，是否需要保存当前的数据状态
                //将当前页面的数据传输到下一个页面进行失焦显示
                Bundle bundle = new Bundle();
                bundle.putString(Constants.KeyMaps.RECEIVEADDRESS, receiveAddress);
                bundle.putString(Constants.KeyMaps.RECEIVECURRENCY, receiveCurrency);
                bundle.putString(Constants.KeyMaps.TRANSACTIONAMOUNT, etTransactionAmount.getText().toString());
                intentToActivity(bundle, SendToConfirmPwdActivity.class, false);
            }
        });
        spSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                tvCurrency.setText(String.valueOf(currencyAdapter.getItem(position)));
                //TODO  餘額顯示保留6個精度，如果當前顯示不下文本的長度，長按文本彈出浮窗進行顯示
                tvBalance.setText(getAllTransactionData().get(position).getBalance());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spSelectReceiveCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //存储当前选中的用于交易的币种信息
                receiveCurrency = String.valueOf(currencyAdapter.getItem(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spSelectAccountAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                receiveAddress = String.valueOf(allAccountAddressAdapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Subscribe
    public void updateAddressEvent(UpdateAddressEvent updateAddressEvent) {
        System.out.println("UpdateAddressEvent" + updateAddressEvent);
        if (updateAddressEvent == null) return;
        String result = updateAddressEvent.getResult();
        ((BaseActivity) activity).showToast(result);
        showToast(result);
    }

}
