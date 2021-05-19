package com.city.fixit.UserAuth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.city.fixit.ServerCommunication.CustomOkHttpClient;
import com.city.fixit.Utils.Constants;
import com.city.fixit.Utils.FLog;
import com.city.fixit.Utils.Utils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class SplashScreen extends AppCompatActivity implements Callback {
    private static final String TAG = "SplashScreen";
    private Context mContext;
    private Callback mCallback;
    private Intent mIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        mCallback = this;
        mIntent = new Intent(mContext, MainActivity.class);

        if(Utils.loadRememberMeOption(mContext)) {
            FLog.d(TAG, "RememberMe = true");
            String token = Utils.loadToken(mContext);
            if(token != null) {
                FLog.d(TAG, "Token not null: " + token);
                boolean ret = CustomOkHttpClient.sendUserAuthRequest(mContext, mCallback, token);
                if(ret) {
                    FLog.d(TAG, "Request sent for user valid sent!");
                    return;
                }
            }
        }
        startActivity(mIntent);
    }

    @Override
    public void onFailure(Request request, IOException e) {
        FLog.d(TAG, "onFailure()");
        mIntent.putExtra(Constants.EXTRA_ON_FAILURE, "");
        startActivity(mIntent);
    }

    @Override
    public void onResponse(Response response) throws IOException {
        FLog.d(TAG, "onResponse()");
        if(response.isSuccessful()) {
            mIntent.putExtra(Constants.EXTRA_ON_SUCCESS, "");
        } else {
            FLog.d(TAG, "Response: " + response.body().string());
            mIntent.putExtra(Constants.EXTRA_ON_ERROR, "");
        }
        startActivity(mIntent);
    }
}
