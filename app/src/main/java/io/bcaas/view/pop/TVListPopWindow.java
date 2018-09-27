package io.bcaas.view.pop;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.PopupWindow;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.bcaas.R;
import io.bcaas.adapter.PopListCurrencyAdapter;
import io.bcaas.adapter.TVPopListCurrencyAdapter;
import io.bcaas.listener.OnItemSelectListener;
import io.bcaas.view.tv.FlyBroadLayout;
import io.bcaas.view.tv.MainUpLayout;
import io.bcaas.vo.PublicUnitVO;

/**
 * @author catherine.brainwilliam
 * @since 2018/8/30
 * <p>
 * TV显示列表
 */
public class TVListPopWindow extends PopupWindow {
    private String TAG = TVListPopWindow.class.getSimpleName();

    private View popWindow;
    private RecyclerView recyclerView;//显示当前列表
    private OnItemSelectListener itemSelectListener;
    private Context context;
    FlyBroadLayout blockBaseMainup;
    MainUpLayout blockBaseContent;

    public TVListPopWindow(Context context) {
        super(context);
        this.context = context;
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popWindow = inflater.inflate(R.layout.tv_popwindow_show_list, null);
        setContentView(popWindow);

        recyclerView = popWindow.findViewById(R.id.rv_list);
        blockBaseMainup = popWindow.findViewById(R.id.block_base_mainup);
        blockBaseContent = popWindow.findViewById(R.id.block_base_content);
        blockBaseContent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                blockBaseMainup.setFocusView(newFocus, oldFocus, 1f);
            }
        });
    }

    public void addCurrencyList(OnItemSelectListener onItemSelectListener, List<PublicUnitVO> list) {
        this.itemSelectListener = onItemSelectListener;
        TVPopListCurrencyAdapter adapter = new TVPopListCurrencyAdapter(context, list);
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

        @Override
        public void changeItem(boolean isChange) {

        }
    };
}
