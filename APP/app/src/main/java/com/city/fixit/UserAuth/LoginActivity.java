package com.city.fixit.UserAuth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.city.fixit.R;
import com.city.fixit.ServerCommunication.CustomOkHttpClient;
import com.city.fixit.ServerCommunication.JsonParser;
import com.city.fixit.Utils.Constants;
import com.city.fixit.Utils.FLog;
import com.city.fixit.Utils.Utils;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

public class LoginActivity extends Activity implements Callback {

    private static String TAG = "LoginActivity";

    private Context mContext;
    private Callback mCallback;

    private Button mBtnLogin;

    private EditText mEtEmail;
    private EditText mEtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;
        mCallback = this;

        mEtEmail = findViewById(R.id.etLoginEmail);
        mEtPassword = findViewById(R.id.etLoginPassword);

        mBtnLogin = findViewById(R.id.btnLogin);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FLog.d(TAG, "Starting login");
                isErrorState(false);
                String email = mEtEmail.getText().toString();
                String pass = mEtPassword.getText().toString();

                if(isInputValid(email, pass)) {
                    FLog.d(TAG, "Valid input, performing login!");
                    boolean ret = CustomOkHttpClient.sendLoginRequest(mContext, mCallback, email, pass);
                    if(!ret) {
                        showAlertMessage("Impossível conectar ao servidor,\n Verifique sua conexão!");
                    }
                }
            }
        });
    }

    private boolean isInputValid(String email, String pass) {
        ArrayList<String> s = new ArrayList<>();
        FLog.d(TAG, "EMIL: " + email + " pass: " + pass);
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            s.add("Email inválido!");
        }

        if(pass.isEmpty() || pass.length() < Constants.PASSWORD_MIN_LENGTH) {
            s.add("A senha deve contem pelo menos 4 caracteres!");
        }

        if(s.size() > 0) {
            FLog.d(TAG, "Stopped! User inserted invalid info!");
            isErrorState(true);
            Toast.makeText(mContext, Utils.prepareErrorMessage(s), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void isErrorState(boolean state) {
        int e = (state ? R.drawable.edit_text_error_shape : R.drawable.edit_text_shape);
        mEtEmail.setBackgroundResource(e);
        mEtPassword.setBackgroundResource(e);
    }

    private void showAlertMessage(String s) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Error!");
        dialog.setMessage(s);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing when user press ok
            }
        });
        LoginActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public void onResponse(Response response) throws IOException {
        if(response.isSuccessful()) {
            String token = JsonParser.getResponseToken(response.body().string());
            if(Utils.saveToken(mContext, token)) {
                FLog.d(TAG, "Token saved successfully and user logged in :)");
                //TODO: Startar a atividade de report
            }
            // TODO: Is this case possible?
        } else {
            String serverMessage = JsonParser.getResponseMessage(response.body().string());
            FLog.d(TAG, "Error! Server Response: " + serverMessage);
            showAlertMessage(serverMessage);
        }
    }

    @Override
    public void onFailure(Request request, IOException e) {
        FLog.d(TAG, "Device not able to connect with server! " + e.getMessage());
        showAlertMessage("Impossível conectar com o servidor!\n Por favor tente novamente mais tarde!");
    }
}