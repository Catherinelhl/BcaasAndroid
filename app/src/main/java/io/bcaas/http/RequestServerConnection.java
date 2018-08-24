package io.bcaas.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.bcaas.constants.MessageConstants;
import io.bcaas.encryption.AES;
import io.bcaas.gson.ServerResponseJson;
import io.bcaas.tools.BcaasLog;
import io.bcaas.tools.GsonTool;

/**
 * @author Costa Peng
 * @version 1.0.0
 * @since 2018/07/01
 */

public class RequestServerConnection {

    private static String TAG = "RequestServerConnection";

    public static String postContentToServer(String json, String apiUrl) {

        HttpURLConnection conn = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        StringBuilder response = null;

        Gson gson = null;

        // Request server
        try {

            response = new StringBuilder();
            gson = GsonTool.getGson();

            String encodeJson = AES.encodeCBC_128(json);
            BcaasLog.d(TAG, "ApiUrl==={} , requestJson==={}", apiUrl, json);
            BcaasLog.d(TAG, "encodeJson = " + encodeJson);

            URL url = new URL(apiUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(encodeJson.getBytes().length);

            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            conn.connect();

            outputStream = new BufferedOutputStream(conn.getOutputStream());
            outputStream.write(encodeJson.getBytes());
            outputStream.flush();

            int HttpResult = conn.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) { // Server response 200
                inputStream = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                BcaasLog.d(TAG, "ApiUrl：" + apiUrl + ", Response：", response.toString());
            } else {
                response.append(gson.toJson(new ServerResponseJson(MessageConstants.STATUS_FAILURE, HttpResult,
                        MessageConstants.API_SERVER_NOT_RESPONSE)));
            }
        } catch (Exception e) {
            BcaasLog.d(TAG, e.getMessage().toString());
            response.delete(0, response.length());
            return response.append(gson.toJson(new ServerResponseJson(MessageConstants.STATUS_FAILURE, MessageConstants.CODE_400,
                    MessageConstants.API_SERVER_NOT_RESPONSE))).toString();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                conn.disconnect();
            } catch (Exception e) {
                BcaasLog.d(TAG, e.getMessage().toString());
            }
        }
        return response.toString();
    }

}
