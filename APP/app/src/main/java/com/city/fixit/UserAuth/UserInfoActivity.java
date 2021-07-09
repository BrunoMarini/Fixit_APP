package com.city.fixit.UserAuth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.city.fixit.R;
import com.city.fixit.ServerCommunication.CustomOkHttpClient;
import com.city.fixit.ServerCommunication.JsonParser;
import com.city.fixit.Utils.Constants;
import com.city.fixit.Utils.FLog;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class UserInfoActivity extends Activity implements Callback {

    private static final String TAG = "UserInfoActivity";

    private Context mContext;
    private Callback mCallback;

    private TextView mTvUserName;
    private TextView mTvUserEmail;
    private TextView mTvUserPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        mContext = this;
        mCallback = this;

        mTvUserName = findViewById(R.id.tv_user_name);
        mTvUserEmail = findViewById(R.id.tv_user_email);
        mTvUserPhone = findViewById(R.id.tv_user_phone);

        Button btnUpdateRegister = findViewById(R.id.btn_update_register);
        btnUpdateRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingBarStatus(true);
                Intent intent = new Intent(mContext, UpdateRegisterActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_NAME, mTvUserName.getText().toString());
                intent.putExtra(Constants.INTENT_EXTRA_PHONE, mTvUserPhone.getText().toString());
                startActivity(new Intent(mContext, UpdateRegisterActivity.class));
                loadingBarStatus(false);
                finish();
            }
        });

        requestUserInfo();
    }

    private void requestUserInfo() {
        if(!CustomOkHttpClient.sendGetUserInfoRequest(mContext, mCallback)) {
            loadingBarStatus(false);
            showAlertMessage("Impossível conectar ao servidor,\n Verifique sua conexão!");
        }
    }

    private void updateUi(final String name, final String email, final String phone) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvUserName.setText("Olá " + name);
                mTvUserEmail.setText(email);
                mTvUserPhone.setText(phone);
                loadingBarStatus(false);
            }
        });
    }

    @Override
    public void onFailure(Request request, IOException e) {
        FLog.d(TAG, "Device not able to connect with server! " + e.getMessage());
        loadingBarStatus(false);
        showAlertMessage("Impossível conectar com o servidor!\n Por favor tente novamente mais tarde!");
    }

    @Override
    public void onResponse(Response response) throws IOException {
        if(response.isSuccessful()) {
            FLog.d(TAG, "Response successful!");
            String res = response.body().string();
            String name = JsonParser.getResponseName(res);
            String email = JsonParser.getResponseEmail(res);
            String phone = JsonParser.getResponsePhone(res);
            updateUi(name, email, phone);
        } else {
            String serverMessage = JsonParser.getResponseMessage(response.body().string());
            FLog.d(TAG, "Error! Server Response: " + "[" + response.code() + "] " + serverMessage);
            loadingBarStatus(false);
            showAlertMessage(serverMessage);
        }
    }

    private void showAlertMessage(String s) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Erro!");
        dialog.setMessage(s);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing when user press ok
            }
        });
        UserInfoActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });
    }

    private void loadingBarStatus(final Boolean status){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            findViewById(R.id.loadingPanel).setVisibility(status ? View.VISIBLE : View.GONE);
            }
        });
    }
}