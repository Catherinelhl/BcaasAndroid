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

import io.bcaas.constants.Constants;
import io.bcaas.constants.MessageConstants;
import io.bcaas.tools.LogTool;
import io.bcaas.tools.encryption.AESTool;
import io.bcaas.gson.ServerResponseJson;
import io.bcaas.tools.gson.GsonTool;

/**
 * @author Costa Peng
 * @version 1.0.0
 * @since 2018/07/01
 */

public class RequestServerConnection {

    private static String TAG = RequestServerConnection.class.getSimpleName();

    public static String postContentToServer(String json, String apiUrl) {

        HttpURLConnection conn = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        StringBuilder response = null;

        Gson gson = null;
        try {

            response = new StringBuilder();
            gson = GsonTool.getGson();

            String encodeJson = AESTool.encodeCBC_128(json);
            LogTool.d(TAG, "request :" + apiUrl + "\n " + json);
            LogTool.d(TAG, encodeJson);

            URL url = new URL(apiUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(Constants.ValueMaps.TIME_OUT_TIME);
            conn.setConnectTimeout(Constants.ValueMaps.TIME_OUT_TIME);
            conn.setRequestMethod(MessageConstants.REQUEST_MOTHOD_POST);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(encodeJson.getBytes().length);

            conn.setRequestProperty(MessageConstants.REQUEST_PROPERTY.CONTENT_TYPE, MessageConstants.REQUEST_PROPERTY.CONTENT_TYPE_VALUE);
            conn.setRequestProperty(MessageConstants.REQUEST_PROPERTY.REQUEST_WITH, MessageConstants.REQUEST_PROPERTY.REQUEST_WITH_VALUE);

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
                LogTool.d(TAG, "response:" + apiUrl + "\n" + response.toString());
            } else {
                response.append(gson.toJson(new ServerResponseJson(MessageConstants.STATUS_FAILURE, HttpResult,
                        MessageConstants.API_SERVER_NOT_RESPONSE)));
            }
        } catch (Exception e) {
            LogTool.d(TAG, e.getMessage().toString());
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
                LogTool.d(TAG, e.getMessage().toString());
            }
        }
        return response.toString();
    }

}
