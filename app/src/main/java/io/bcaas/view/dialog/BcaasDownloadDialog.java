package io.bcaas.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.bcaas.R;
import io.bcaas.constants.Constants;

/**
 * @author catherine.brainwilliam
 * @since 2018/10/11
 * <p>
 *  自定義Dialog：显示当前的下载状态
 */
public class BcaasDownloadDialog extends Dialog {
    private String TAG = BcaasDownloadDialog.class.getSimpleName();

    private ProgressBar progressBar;
    private TextView tvProgress;

    public BcaasDownloadDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    public BcaasDownloadDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    private void initView() {
        setContentView(R.layout.download_dialog);
        progressBar = findViewById(R.id.down_progress);
        tvProgress = findViewById(R.id.tv_progress);
    }


    //设置进度
    public void setProgress(int progress) {
        if (tvProgress != null) {
            tvProgress.setText(progress + Constants.PROGRESS_MAX);
        }
        if (progressBar != null) {
            progressBar.setProgress(progress);
        }
    }
}
