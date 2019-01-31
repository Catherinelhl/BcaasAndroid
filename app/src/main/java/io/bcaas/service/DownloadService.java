package io.bcaas.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.LongSparseArray;

import java.io.File;

import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.tools.InstallTool;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.SystemTool;
import io.bcaas.tools.language.LanguageTool;

/**
 * 服務：開啟一個在应用内監聽更新APP下載進度的服務
 */
public class DownloadService extends Service {

    private String TAG = DownloadService.class.getSimpleName();
    //管理下载
    private DownloadManager mDownloadManager;
    private DownloadBinder mBinder = new DownloadBinder();
    private LongSparseArray<String> mApkPaths;
    private DownloadFinishReceiver downloadFinishReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        mApkPaths = new LongSparseArray<>();
        //注册下载完成的广播
        downloadFinishReceiver = new DownloadFinishReceiver();
        registerReceiver(downloadFinishReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(downloadFinishReceiver);//取消注册广播接收者
        super.onDestroy();
    }

    public class DownloadBinder extends Binder {

        /**
         * 下载
         *
         * @param apkUrl 下载的url
         */
        public long startDownload(String apkUrl) {
            //删除原有的APK
            clearApk(DownloadService.this, Constants.ValueMaps.DOWNLOAD_APK_NAME);
            //使用DownLoadManager来下载
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
            //将文件下载到自己的Download文件夹下,必须是External的
            //这是DownloadManager的限制
            File file = new File(getExternalFilesDir(Constants.ValueMaps.BCAAS_FILE_DIR).getAbsolutePath(), Constants.ValueMaps.DOWNLOAD_APK_NAME);
            request.setDestinationUri(Uri.fromFile(file));

            //添加请求 开始下载
            long downloadId = mDownloadManager.enqueue(request);
            LogTool.d(TAG, file.getAbsolutePath());
            mApkPaths.put(downloadId, file.getAbsolutePath());
            return downloadId;
        }

        /**
         * 获取进度信息
         *
         * @param downloadId 要获取下载的id
         * @return 进度信息 max-100
         */
        public int getProgress(long downloadId) {
            //查询进度
            DownloadManager.Query query = new DownloadManager.Query()
                    .setFilterById(downloadId);
            Cursor cursor = null;
            int progress = 0;
            try {
                cursor = mDownloadManager.query(query);//获得游标
                if (cursor != null && cursor.moveToFirst()) {
                    //当前的下载量
                    int downloadSoFar = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    //文件总大小
                    int totalBytes = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    progress = (int) (downloadSoFar * 1.0f / totalBytes * 100);
                }
            } finally {
                if (cursor != null) {

                    cursor.close();
                }
            }

            return progress;
        }

    }

    //下载完成的广播
    private class DownloadFinishReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //下载完成的广播接收者
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            String apkPath = mApkPaths.get(completeDownloadId);
            LogTool.d(TAG, MessageConstants.DOWNLOAD_FINISH_RECEIVER + apkPath);
            if (apkPath == null) {
                apkPath = getExternalFilesDir(Constants.ValueMaps.BCAAS_FILE_DIR).getAbsolutePath() + Constants.ValueMaps.DOWNLOAD_APK_NAME;
            }
            if (!apkPath.isEmpty()) {
                SystemTool.setPermission(apkPath);//提升读写权限,否则可能出现解析异常
                InstallTool.installAndroidAPK(context, apkPath);
            } else {
                LogTool.e(TAG, MessageConstants.APK_PATH_IS_NULL);
            }
        }
    }

    /**
     * 删除之前的apk
     *
     * @param apkName apk名字
     * @return
     */
    public static File clearApk(Context context, String apkName) {
        File apkFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), apkName);
        if (apkFile.exists()) {
            apkFile.delete();
        }
        return apkFile;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageTool.setLocal(base));
    }

}
