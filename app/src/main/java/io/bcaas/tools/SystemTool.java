package io.bcaas.tools;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 工具類：系統權限
 */
public class SystemTool {
    private static String TAG = SystemTool.class.getSimpleName();

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @param command 命令：String apkRoot="chmod 777 "+getPackageCodePath();
     * @return 0 命令执行成功
     */
    public static int RootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            int i = process.waitFor();

            LogTool.d(TAG, "i:" + i);
            return i;
        } catch (Exception e) {
            LogTool.d(TAG, e.getMessage());
            return -1;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                LogTool.e(TAG, e.getMessage());
            }
        }
    }

    /**
     * 提升读写权限
     *
     * @param filePath 文件路径
     * @return
     * @throws IOException
     */
    public static void setPermission(String filePath) {
        String command = "chmod " + "777" + " " + filePath;
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}