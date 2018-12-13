package io.bcaas.view.pop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import java.util.List;

import io.bcaas.R;
import io.bcaas.adapter.PopListCurrencyAdapter;
import io.bcaas.listener.OnCurrencyItemSelectListener;
import io.bcaas.tools.ecc.WalletTool;
import io.bcaas.vo.PublicUnitVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/30
 * <p>
 * 自定義PopWindow：显示「幣種」列表,点击显示，会比对当前默认的币种，然后标记出当前的默认的币种
 */
public class BlockServicesPopWindow extends PopupWindow {

    private View popWindow;
    private RecyclerView recyclerView;//显示当前列表
    private OnCurrencyItemSelectListener onCurrencyItemSelectListener;
    //标记来自于哪里
    private Context context;
    private String fromWhere;

    public BlockServicesPopWindow(Context context) {
        super(context);
        this.context = context;
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popWindow = inflater.inflate(R.layout.popwindow_blockservices, null);
        setContentView(popWindow);
        recyclerView = popWindow.findViewById(R.id.rv_list);
    }

    public void addCurrencyList(OnCurrencyItemSelectListener onCurrencyItemSelectListener, String from) {
        this.onCurrencyItemSelectListener = onCurrencyItemSelectListener;
        this.fromWhere = from;
        List<PublicUnitVO> list = WalletTool.getPublicUnitVO();
        PopListCurrencyAdapter adapter = new PopListCurrencyAdapter(context, list);
        adapter.setOnItemSelectListener(currencyItemSelectListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false));

    }

    private OnCurrencyItemSelectListener currencyItemSelectListener = new OnCurrencyItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type, String from) {
            dismiss();
            onCurrencyItemSelectListener.onItemSelect(type, fromWhere);
        }
    };
}
