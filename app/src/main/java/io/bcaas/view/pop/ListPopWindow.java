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
import io.bcaas.adapter.PopListAddressAdapter;
import io.bcaas.adapter.PopListCurrencyAdapter;
import io.bcaas.db.vo.AddressVO;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.LogTool;
import io.bcaas.vo.PublicUnitVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/30
 * <p>
 * 显示列表
 */
public class ListPopWindow extends PopupWindow {
    private String TAG = ListPopWindow.class.getSimpleName();

    private View popWindow;
    private RecyclerView recyclerView;//显示当前列表
    private OnItemSelectListener itemSelectListener;
    private Context context;

    public ListPopWindow(Context context) {
        super(context);
        this.context = context;
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popWindow = inflater.inflate(R.layout.popwindow_show_list, null);
        setContentView(popWindow);
        recyclerView = popWindow.findViewById(R.id.rv_list);
    }

    public void addCurrencyList(OnItemSelectListener onItemSelectListener, List<PublicUnitVO> list) {
        this.itemSelectListener = onItemSelectListener;
        PopListCurrencyAdapter adapter = new PopListCurrencyAdapter(context, list);
        adapter.setOnItemSelectListener(popItemSelectListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false));

    }

    public void addListAddress(OnItemSelectListener onItemSelectListener, List<AddressVO> list) {
        this.itemSelectListener = onItemSelectListener;
        PopListAddressAdapter adapter = new PopListAddressAdapter(context, list);
        adapter.setOnItemSelectListener(popItemSelectListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false));

    }

    private OnItemSelectListener popItemSelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type) {
            dismiss();
            itemSelectListener.onItemSelect(type);
        }
    };
}
