package com.city.fixit.UserAuth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.city.fixit.R;
import com.city.fixit.ServerCommunication.CustomOkHttpClient;
import com.city.fixit.ServerCommunication.JsonParser;
import com.city.fixit.Utils.FLog;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class UserStatisticsActivity extends Activity implements Callback {

    private static final String TAG = "UserStatisticsActivity";

    private Context mContext;
    private Callback mCallback;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_statistics);

        mContext = this;
        mCallback = this;

        mTextView = findViewById(R.id.tvText);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mTextView.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

        requestUserStatistics();
    }

    private void requestUserStatistics() {
        if(!CustomOkHttpClient.sendGetUserStatistics(mContext, mCallback)) {
            loadingBarStatus(false);
            showAlertMessage("Impossível conectar ao servidor,\n Verifique sua conexão!");
        }
    }

    private void updateUi(final int views, final int solved, final int reports) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(solved > 0)
                    mTextView.setText(Html.fromHtml(getString(R.string.message_resolved, reports, views, solved)));
                else
                    mTextView.setText(Html.fromHtml(getString(R.string.message_zero_resolved, reports, views)));
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
            int views = JsonParser.getResponseViews(res);
            int solved = JsonParser.getResponseSolved(res);
            int reports = JsonParser.getResponseReports(res);
            updateUi(views, solved, reports);
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
        UserStatisticsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });
    }

    private void loadingBarStatus(final Boolean status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            findViewById(R.id.loadingPanel).setVisibility(status ? View.VISIBLE : View.GONE);
            }
        });
    }
}