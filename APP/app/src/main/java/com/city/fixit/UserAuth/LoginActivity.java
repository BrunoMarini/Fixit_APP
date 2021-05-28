package com.city.fixit.UserAuth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.city.fixit.R;
import com.city.fixit.Report.HomeActivity;
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
    private Switch mSwitch;

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
        mSwitch = findViewById(R.id.switchRememberMe);

        mBtnLogin = findViewById(R.id.btnLogin);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FLog.d(TAG, "Starting login");
                loadingBarStatus(true);
                isErrorState(false);
                String email = mEtEmail.getText().toString();
                String pass = mEtPassword.getText().toString();

                if(isInputValid(email, pass)) {
                    FLog.d(TAG, "Valid input, performing login!");
                    boolean ret = CustomOkHttpClient.sendLoginRequest(mContext, mCallback, email, pass);
                    if(!ret) {
                        loadingBarStatus(false);
                        showAlertMessage("Impossível conectar ao servidor,\n Verifique sua conexão!");
                    }
                }
            }
        });
        loadingBarStatus(false);
    }

    private boolean isInputValid(String email, String pass) {
        ArrayList<String> s = new ArrayList<>();
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            s.add("Email inválido!");
        }

        if(pass.isEmpty() || pass.length() < Constants.PASSWORD_MIN_LENGTH) {
            s.add("A senha deve contem pelo menos 4 caracteres!");
        }

        if(s.size() > 0) {
            FLog.d(TAG, "Stopped! User inserted invalid info!");
            loadingBarStatus(false);
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
        loadingBarStatus(false);
        if(response.isSuccessful()) {
            String token = JsonParser.getResponseToken(response.body().string());
            if(Utils.saveToken(mContext, token)) {
                FLog.d(TAG, "Token saved successfully and user logged in :)");
                Utils.saveRememberMeOption(mContext, mSwitch.isChecked());
                startActivity(new Intent(mContext, HomeActivity.class));
                finish();
            }
            // TODO: Is this case possible?
        } else {
            String serverMessage = JsonParser.getResponseMessage(response.body().string());
            FLog.d(TAG, "Error! Server Response: " + "[" + response.code() + "] " + serverMessage);
            if(response.code() == Constants.HTTP_UNAUTHORIZED) {
                serverMessage += "\nEnviamos o email de confirmação novamente, não esqueça de " +
                                                                    "verificar sua caixa de SPAM";
            } else if(response.code() == Constants.HTTP_FORBIDDEN) {
                FLog.d(TAG, "Blocked user");
                serverMessage += "\nEsse usuário não sera capaz de realizar mais nenhum reporte!";
            }
            showAlertMessage(serverMessage);
        }
    }

    @Override
    public void onFailure(Request request, IOException e) {
        loadingBarStatus(false);
        FLog.d(TAG, "Device not able to connect with server! " + e.getMessage());
        showAlertMessage("Impossível conectar com o servidor!\n Por favor tente novamente mais tarde!");
    }

    private void loadingBarStatus(final Boolean status){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.loadingPanel).setVisibility(status ? View.VISIBLE : View.GONE);
                mEtEmail.setEnabled(!status);
                mEtPassword.setEnabled(!status);
                mBtnLogin.setEnabled(!status);
            }
        });
    }
}