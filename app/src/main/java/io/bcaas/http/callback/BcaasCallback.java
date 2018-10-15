package io.bcaas.http.callback;

import io.bcaas.constants.MessageConstants;
import io.bcaas.tools.LogTool;
import retrofit2.Call;
import retrofit2.Response;

public abstract class BcaasCallback<T extends Object> implements retrofit2.Callback<T> {
    private String TAG = BcaasCallback.class.getSimpleName();

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.raw().code() == MessageConstants.CODE_404) {
            LogTool.d(TAG, "internet response: " + MessageConstants.CODE_404);
            onNotFound();
        } else {
            if (response.raw().isSuccessful()) {
//                LogTool.d(TAG, "internet response", "200");
                onSuccess(response);
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {

    }


    public abstract void onSuccess(Response<T> response);

    public void onNotFound() {
        return;
    }
}