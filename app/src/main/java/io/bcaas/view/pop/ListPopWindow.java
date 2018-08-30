package io.bcaas.view.pop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;

import java.util.List;

import io.bcaas.R;
import io.bcaas.adapter.PopListAdapter;
import io.bcaas.base.BcaasApplication;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.tools.BcaasLog;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/30
 * <p>
 * 显示列表
 */
public class ListPopWindow extends PopupWindow {
    private String TAG = ListPopWindow.class.getSimpleName();

    private PopListAdapter adapter;
    private View popWindow;
    private RecyclerView recyclerView;//显示当前列表
    private OnItemSelectListener itemSelectListener;
    private Context context;
    private List<String> list;

    public ListPopWindow(Context context, OnItemSelectListener onItemSelectListener, List<String> list) {
        super(context);
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
        this.itemSelectListener = onItemSelectListener;
        this.context = context;
        this.list = list;
        setAdapter();
    }

    private void setAdapter() {
        adapter = new PopListAdapter(context, list);
        adapter.setOnItemSelectListener(onItemSelectListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false));
    }

    private OnItemSelectListener onItemSelectListener = new OnItemSelectListener() {
        @Override
        public <T> void onItemSelect(T type) {
            BcaasLog.d(TAG, type);
            dismiss();
            itemSelectListener.onItemSelect(type);
        }
    };
}
